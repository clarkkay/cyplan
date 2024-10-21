package com.km207.cyplan.controllers;

import com.km207.cyplan.models.basePlan;
import com.km207.cyplan.models.cluster;
import com.km207.cyplan.models.schedule;
import com.km207.cyplan.repository.CommentRepository;
import com.km207.cyplan.repository.CoursesRepository;
import com.km207.cyplan.repository.PlanRepository;
import com.km207.cyplan.services.planService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.json.JSONObject;

@Tag(name="HomeApi Controller", description = "Class that has all endpoints relating to the homepage including most stuff related to the creation & editing of student plans")
@RestController
@RequestMapping("/home")
public class HomeApiController {
    @Autowired
    PlanRepository planRepository;
    @Autowired
    CoursesRepository coursesRepository;
    @Autowired
    planService plan_service;
    @Autowired
    CommentRepository commentRepository;

    //Function that returns a students plan given their ID and the ID of the plan they want
    @GetMapping("")
    public List<String> getUserPlan(@RequestParam("userID")int userID, @RequestParam("planName")String planName){
        List<String> stringPlan = planRepository.getUserPlan(userID, planName);
        return stringPlan;
    }

    //Takes a userID and returns the code of the degree they are getting may not need to be a get mapping. (Instead may only need to exist as a function)
    @GetMapping("/getMajor")
    public String getUserMajor(@RequestParam("userID") int userID){
        String userMajor = planRepository.getUserMajor(userID);
        return userMajor;
    }

    // returns 1 if the course exists, return 0 if not
    @GetMapping("/course")
    public int getCourse(@RequestParam("courseID") String courseCode) {
        Long count = coursesRepository.countByCourseCode(courseCode);
        if (count != null && count > 0) {
            return 1;
        } else {
            return 0;
        }
    }



    //Takes a userID and returns the  default plan for the degree that the user is persuing
    @GetMapping("/getBasePlan")
    public basePlan getUserDefaultPlan(@RequestParam("userID") int userID){
        //Get the users Major
        String userMajor = planRepository.getUserMajor(userID);

        //Get the base plan for the users major
        basePlan userBasePlan = new basePlan();
        userBasePlan.setMajor(userMajor);
        userBasePlan.setClasses(planRepository.getDegreeBasePlanCourses(userMajor));
        userBasePlan.setClusters(plan_service.getDegreeBasePlanClusters(userMajor));
        return userBasePlan;
    }

    @GetMapping("/TEST")
    public List<String> TEST(@RequestParam("maj") String maj){
        return planRepository.getRawDegreeBasePlanClusters(maj);
    }

    // takes in a courseID and gives the description for the course
    @GetMapping("/getCourseDescription")
    public String getDescription(@RequestParam("courseCode") String courseCode){
        String description = planRepository.getCourseDescription(courseCode);
        return description;
    }


    //function to update a users plan
    @PutMapping("/updatePlan")
    public ResponseEntity<String> updateUserPlan(@RequestBody schedule request) {
        //get info from the schedule in the request body
        int userID = request.getUserID();
        String planName = request.getPlanName();
        List<String> classes = request.getClasses();

        //get the planID from the plan that is going to be deleted
        int planID = planRepository.getPlanIDfromName(userID, planName);

        //delete all entries with userID and planName
        int deleteResult = planRepository.deleteUserPlan(userID, planName);

        if (deleteResult == 1) {
            //TODO: log the issue
            return ResponseEntity.internalServerError().body("error deleting plan in updateUserPlan()"); //IDK if this works, but it is supposed to return this error when something goes wrong when updating the plan
        }

        //go through the given list of classes and add it to the database
        int planSize = classes.size();
        String[] curClass; //variable to store the entireSchedule entry at an index of the schedule arrayList
        String curCourseCode;   //variable to store the courseCode of the schedule entry at an index of the schedule arrayList
        int curSemesterTaken;
        int statusCode;
        for (int i = 0; i<planSize; i++){
            curClass = classes.get(i).split(","); //split apart the given schedule entry
            curCourseCode = curClass[0];
            curSemesterTaken = Integer.parseInt(curClass[1].strip());
            planRepository.insertPlanEntry(userID, curCourseCode, curSemesterTaken, planID, planName); //TODO: maybe encase this in an if statement and return an error if it returns a bad number
        }
        // Your code to process the received JSON object (request) goes here
        return ResponseEntity.ok("plan updated successfully!");
    }

    //Deletes all records related to a given users plan from the plan database. Takes a userID and the name of the plan they want to delete from the database
    @DeleteMapping("/deletePlan")
    public ResponseEntity<String> deleteUserPlan(@RequestParam("userID") int userID, @RequestParam("planNames") String planNames){
        String[] listOfPlanNames = planNames.split(",");
        int resultStatus;
        for (String curPlanName : listOfPlanNames){
            int deleteResult = planRepository.deleteUserPlan(userID, curPlanName);
            if (deleteResult != 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete plan: " + curPlanName);
            }
        }
        return ResponseEntity.ok("plans deleted successfully");
    }

    // @Tanner: I changed this to send back a JSON object because the front end was confused
    @PostMapping("/addPlan")
    public ResponseEntity<JSONObject> addPlan(@RequestBody schedule request) {
        //get info from the schedule in the request body
        int userID = request.getUserID();
        String planName = request.getPlanName();
        List<String> classes = request.getClasses();

        // check if planName already exists in database
        Integer isPlan = planRepository.getPlanIDfromName(userID, planName);
        if (isPlan != null) {
            // get rid of the plan and then continue on as normal
            planRepository.deleteUserPlan(userID, planName);
        }

        //get the next available planID for the given user
        Integer newPlanID = planRepository.getHighestPlanID(userID);
        if (newPlanID == null) {
            newPlanID = 0;
        } else {
            newPlanID += 1;
        }

        //go through the given list of classes and add it to the database
        int planSize = classes.size();
        String[] curClass; //variable to store the entireSchedule entry at an index of the schedule arrayList
        String curCourseCode;   //variable to store the courseCode of the schedule entry at an index of the schedule arrayList
        int curSemesterTaken;
        for (int i = 0; i<planSize; i++){
            curClass = classes.get(i).split(","); //split apart the given schedule entry
            curCourseCode = curClass[0];
            curSemesterTaken = Integer.parseInt(curClass[1].strip()); //TODO: check if I need to throw an error if this doesn't work
            planRepository.insertPlanEntry(userID, curCourseCode, curSemesterTaken, newPlanID, planName); //TODO: maybe encase this in an if statment and return an error if it returns a bad number
        }
        JSONObject responseJson = new JSONObject();
        try {
            responseJson.put("message", "Plan added successfully!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(responseJson);
    }

    @ApiOperation(value = "Get list of Students in the System ", response = Iterable.class, tags = "getSchedules")
    @GetMapping("/getSchedules")
    // findAllByUserId(@Param("userID") int userID)
    public List<String> getUserSchedules(@RequestParam("userID") int userID){
        List<String> schedules = planRepository.findAllByUserId(userID);
        return schedules;
    }

    //Function to check whether all the coReqs and Prereqs on a given schedule are satisfied.
    //Returns success message if the check is successful
    //Returns the prereq or coreq and course that failed the test, and the semester you needed to take the coreq/prereq if the check is unsuccessful
    @PutMapping("/checkPrereqsAndCoreqs")
    public ResponseEntity<Map<String, Object>> checkPrereqsAndCoreqs(@RequestBody schedule request) {
        List<String> plan = request.getClasses();
        Map<String, Object> response = new HashMap<>();

        for (String curCourse : plan){
            int curCourseSem = Integer.parseInt(curCourse.split(",")[1].strip());
            String curCourseCode = curCourse.split(",")[0].strip();
            if (curCourseSem == 0){
                continue;           //If course was taken semester 0, preReqs and Coreqs are waived.
            }
            //PREREQUISITE CHECK
            String[] prereqs = coursesRepository.getPrereqs(curCourseCode).split(",");//get prereqs
            for (String curPrereq : prereqs) {
                curPrereq = curPrereq.strip();
                if (curPrereq.equals("")) {
                    break; //if the prereqs list is empty, break and go to the next course
                }
                //CHECK IF THERE ARE COPREREQS
                if (curPrereq.contains(";")) {
                    String[] coPrereqs = curPrereq.split(";");
                    boolean coPrereqFound = false; //variable to tell whether the plan contains the correct coPreReq
                    for (String curCoPrereq : coPrereqs) {
                        if (plan_service.takenPrior(plan, curCoPrereq, curCourseSem)) {
                            coPrereqFound = true; //If you find even a single coPrereq in the plan, you are good, so set coPreReqFound to true and break.
                            break;
                        }
                    }
                    if (coPrereqFound == false) { //Only executes if you didn't find a single coPrereq from the chain
                        response.put("status", "failed");
                        response.put("message", "coPreReq check Failed");
                        response.put("Reason", curCourseCode + " needs one of " + coPrereqs.toString() + " taken before semester " + curCourseSem);
                        // Assume coPrereqs and curCourseCode are already defined

                        String[] alertCourses = new String[coPrereqs.length + 1];
                        System.arraycopy(coPrereqs, 0, alertCourses, 0, coPrereqs.length);
                        alertCourses[coPrereqs.length] = curCourseCode;
                        response.put("alertCourses", alertCourses);
                        return ResponseEntity.badRequest().body(response); //TODO: If they went through and didn't ever find one of the coPreReqs, return false. MAKE THIS RETURN BAD REQUEST W/ BODY
                    }
                }
                //IF NOT A COPREREQ CHAIN, JUST CHECK IF YOU HAVE THE GIVEN PREREQ
                else if (!plan_service.takenPrior(plan, curPrereq, curCourseSem)) {
                    response.put("status", "failed");
                    response.put("message", "Prereq check Failed");
                    response.put("Reason", curCourseCode + " needs " + curPrereq + " taken before semester " + curCourseSem);
                    String[] alertCourses = {curCourseCode, curPrereq};
                    response.put("alertCourses", alertCourses);
                    return ResponseEntity.badRequest().body(response);
                }
            }

            //COREQUISITE CHECK
            String[] coreqs = coursesRepository.getCoreqs(curCourseCode).split(",");//get coreqs
            for (String curCoreq: coreqs) {
                curCoreq = curCoreq.strip();
                if (curCoreq.equals("")) {
                    break; //if the coreq list is empty, break and go to the next course
                }
                //CHECK IF THERE ARE COCOREQS
                if (curCoreq.contains(";")) {
                    String[] coCoreqs = curCoreq.split(";");
                    boolean coCoreqFound = false; //variable to tell whether the plan contains the correct coPreReq
                    for (String curCoCoreq : coCoreqs) {
                        if (plan_service.takenPrior(plan, curCoCoreq, curCourseSem + 1)) {
                            coCoreqFound = true; //If you find even a single coCoreq in the plan, you are good, so set coCoReqFound to true and break.
                            break;
                        }
                    }
                    if (coCoreqFound == false) { //Only executes if you didn't find a single cocoreq from the chain
                        response.put("status", "failed");
                        response.put("message", "coCoReq check Failed");
                        response.put("Reason", curCourseCode + " needs one of " + coCoreqs.toString() + " taken before semester OR durring " + curCourseSem);
                        String[] alertCourses = new String[coCoreqs.length + 1];
                        System.arraycopy(coCoreqs, 0, alertCourses, 0, coCoreqs.length);
                        alertCourses[coCoreqs.length] = curCourseCode;
                        response.put("alertCourses", alertCourses);
                        return ResponseEntity.badRequest().body(response); //TODO: If they went through and didn't ever find one of the coCoReqs, return false. MAKE THIS RETURN BAD REQUEST W/ BODY
                    }
                }
                //IF NOT A COCOREQ CHAIN, JUST CHECK IF YOU HAVE THE GIVEN PREREQ
                else if (!plan_service.takenPrior(plan, curCoreq, curCourseSem + 1)) {
                    response.put("status", "failed");
                    response.put("message", "Coreq check Failed");
                    response.put("Reason", curCourseCode + " needs " + curCoreq + " taken before OR durring semester " + curCourseSem);
                    String[] alertCourses = {curCourseCode, curCoreq};
                    response.put("alertCourses", alertCourses);
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        response.put("status", "success");
        response.put("message", "Prereq and Coreq check successful");
        return ResponseEntity.ok(response);
    }

    /*
     * Function to tell if a given plan satisfies all degree requirements for the users degree
     */
    @PutMapping("/checkDegreeRequirements")
    public ResponseEntity<Map<String, Object>> checkDegreeRequirements(@RequestBody schedule request) {
        Map<String, Object> response = new HashMap<>(); //object to be returned
        List<String> plan = request.getClasses();
        String userMajor = planRepository.getUserMajor(request.getUserID());
        List<Integer> degreeReqCodes = planRepository.getDegreeReqCodes(userMajor);
        int reqCount = degreeReqCodes.size();
        int[] reqFulfillments = new int[reqCount]; //initialize an array of integers to keep track of how much each fulfillment has been fulfilled (array[i] keeps track of how much fulfillment i has been fulifilled)

        //GET DEGREE REQ FULFILLMENT AMMOUNTS OF THE GIVEN SCHEDULE
        for (String curCourse : plan){
            String curClassCode = curCourse.split(",")[0].strip(); //get just the course code from the courseCode,semester pair
            for (int i = 0; i<reqCount; i++){
                int curReqCode = degreeReqCodes.get(i);
                if (planRepository.doesCourseFulfillReq(curClassCode, curReqCode, userMajor) >= 1) { //Check if the curCourse fulfills the curReq
                    String reqFulfillmentType = planRepository.getFulfillmentType(userMajor, curReqCode);
                    //figure out how MUCH the course counts towards fulfilling the req
                    if (reqFulfillmentType.equals("credit")){
                        reqFulfillments[i] += coursesRepository.getNumCredits(curClassCode);
                    }else if (reqFulfillmentType.equals("course")){
                        reqFulfillments[i] += 1; //if the req is counting num courses, then each course is worth 1
                    }else{
                        //Throw an error because we don't know how to handle any other fulfill type other than 'credit' or 'course'
                        response.put("status", "failed");
                        response.put("message", "error: unknown fulfillment type");
                        response.put("Reason", "fulfillment type of '" + reqFulfillmentType + "' is unhandled in checkDegreeRequirements() function" );
                        return ResponseEntity.badRequest().body(response);
                    }
                }
            }
        }

        //CHECK IF REQUIREMENTS HAVE BEEN FULFILLED
        List<Integer> failedReqCodes = new ArrayList<Integer>();
        for (int i=0; i<reqCount; i++){
            if (reqFulfillments[i] < planRepository.getFulfillmentAmntRequired(userMajor, degreeReqCodes.get(i))){
                failedReqCodes.add(degreeReqCodes.get(i));
            }
        }
        if(failedReqCodes.size() == 0){ //none of the reqs failed so return success
            response.put("status", "success");
            response.put("message", "degree Requirement check successful");
            return ResponseEntity.ok(response);
        }else{ //at least one of the reqs failed so send back which failed (can return multiple failures)
            response.put("status", "failed");
            response.put("message", "degreeReqCheck failed");
            response.put("Reason", "The following requirements were not fulfilled: " + failedReqCodes);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/getCourseComments")
    public List<String> getCourseComments(@RequestParam("courseCode") String courseCode){
        List<String> courseComments = commentRepository.getCourseComments(courseCode);
        return courseComments;
    }

    @GetMapping("/getCourseRating")
    public String getCourseRating(@RequestParam("courseCode") String courseCode){
        int numRatings = coursesRepository.getNumRatings(courseCode);
        float totalRating = coursesRepository.getTotalRating(courseCode);

        //calculate rating
        float rating = (totalRating) / (float)numRatings;
        return String.valueOf(rating);
    }

    /*
     * endpoint to update a courses rating. Takes a courseCode of the course you are updating and a new rating to add OR delete
     * The addSubFlag tells whether you want to add the userRating to the total rating in the database, or remove it
     * addSubFlag = 1 means it will add to database, addSubFlag = -1 means it will subtract from database. DO NOT ANYTHING OTHER THAN 1 OR -1
     */
    @PutMapping("/addCourseRating")
    public ResponseEntity<String> addCourseRating(@RequestParam("courseCode") String courseCode, @RequestParam("userRating") float userRating){
        // Throw error if the course doesn't exist in the database
        if (coursesRepository.countByCourseCode(courseCode) == null || coursesRepository.countByCourseCode(courseCode) == 0){
            return ResponseEntity.badRequest().body("Failed to add Rating - Course with code " + courseCode + " does not exist");
        }

        // Get old rating values from the database
        float oldRatingSum = coursesRepository.getTotalRating(courseCode); // This should be the sum of all ratings
        int oldNumRatings = coursesRepository.getNumRatings(courseCode);

        // Calculate new rating values
        int newRatingCount = oldNumRatings + 1;
        float newRatingSum = oldRatingSum + userRating; // Update the total sum of ratings

        // Update the database with the new sum of ratings and the new count
        int result = coursesRepository.updateCourseRating(courseCode, newRatingSum, newRatingCount);

        if (result == 0){
            return ResponseEntity.badRequest().body("Failed to update rating");
        }
        return ResponseEntity.ok("Rating updated successfully");
    }


    @PutMapping("/resetCourseRating")
    public ResponseEntity<String> resetCourseRating(@RequestParam("courseCode") String courseCode){
        int result = coursesRepository.updateCourseRating(courseCode, 0, 0);
        if (result == 0){
            return ResponseEntity.badRequest().body("Failed to reset rating");
        }
        return ResponseEntity.ok("successfully reset rating for " + courseCode);
    }


    @PostMapping("/addCourseComment")
    public ResponseEntity<String> getCourseComments(@RequestParam("commentInput") String commentInput,@RequestParam("courseCode") String courseCode){
        int response = commentRepository.addUserComment(courseCode, commentInput);
        //TODO: use the response to see if the comment fails to add.
        if (response == 0){
            return ResponseEntity.ok("Failed to add Comment");
        }
        return ResponseEntity.ok("Successfully added comment");
    }


}
