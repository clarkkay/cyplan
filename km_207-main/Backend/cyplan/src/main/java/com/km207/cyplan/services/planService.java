package com.km207.cyplan.services;

import com.km207.cyplan.models.cluster;
import com.km207.cyplan.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class planService {
    @Autowired
    PlanRepository planRepository;

    /*
     * Function to tell whether a schedule contains a course before a given semester.
     * used by entering a course's prereq as the keyCourseCode and the semester that
     * the original course was taken to make sure the prereq exists in the plan prior
     * to the semester the course itself
     */
    public boolean takenPrior(List<String> plan, String keyCourseCode, int keySemester){
        for (String curCourse : plan){ //for each course in plan
            String curCourseCode = curCourse.split(",")[0].strip();
            int curCourseSem;

            //may not be able to parse the semester & course if the string was invalidly formatted
            try {
                curCourseSem = Integer.parseInt(curCourse.split(",")[1].strip());
            }catch (IndexOutOfBoundsException e){
                System.out.println("INVALID PLAN FORMAT (may be missing comma between course and semester taken)");
                return false;
            }

            if (curCourseCode.equals(keyCourseCode) && curCourseSem < keySemester){
                return true;
            }
        }
        return false;
    }

    /*
     * Function to create a list of cluster objects that hold all the clusters on a given majors
     * base plan. These cluster objects hold the title of the cluster, the semester the cluster
     * should appear at on the schedule, and all the classes that could be taken to fulfill the cluster.
     */
    public List<cluster> getDegreeBasePlanClusters(String major){
        List<cluster> clusters = new ArrayList<cluster>();

        List<String> rawClusterInfo = planRepository.getRawDegreeBasePlanClusters(major); //a list of the reqs in a cluster where the last integer is the semester it needs to be taken
        for (String curClusterInfo : rawClusterInfo){
            cluster newCluster = new cluster(); //create a new cluster that will be added to the list of clusters

            //parse through the cluster info
            String[] curClusterInfoSplit = curClusterInfo.split(",");
            int semester = Integer.parseInt(curClusterInfoSplit[curClusterInfoSplit.length-1]); //the last integer in the list is the semester the cluster needs to be taken

            //getting a string of just the reqCodes of the cluster, excluding the semester number at the end by copying all but the last element to a temp array, then stringifying it
            String[] temp = new String[curClusterInfoSplit.length-1];
            for (int i = 0; i<curClusterInfoSplit.length-1; i++){
                temp[i] = curClusterInfoSplit[i];
            }
            //rebuilding string from array using stringbuilder
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < temp.length; i++) {
                stringBuilder.append(temp[i]);
                if (i < temp.length - 1) {
                    stringBuilder.append(",");
                }
            }
            String curClusterReqs = stringBuilder.toString();             // Convert StringBuilder to String
            
            //put cluster info into the new cluster
            newCluster.setSemester(semester);
            newCluster.setClasses(getClusterClasses(major, curClusterReqs));
            newCluster.setTitle(getClusterTitle(major, curClusterReqs));

            //add new cluster into the clusterList
            clusters.add(newCluster);
        }
        return clusters;
    }

    /*
    * Function to take a string of cluster information/requirements (formatted like: "1" or "1,3" or "1;4")
    * Where the numbers are requirements that are to be fulfilled. If there is a comma between them, then it is
    * an OR and if there is a semicolon between them, it is an AND (so like classes that count as a social science AND IP).
    * The function returns the list of classes that would be offered in that cluster
    */
    public List<String> getClusterClasses(String major, String clusterInfo){
        List<String> clusterClasses = new ArrayList<String>();
        if (clusterInfo.contains(",")){
            String[] multiReq = clusterInfo.split(",");
            for (String curReq : multiReq){
                if (curReq.contains(";")){  //clusterInfo contained both ',' AND ';'. (ex. "1;4,1;5")
                    clusterClasses.addAll(getFulfillmentClassesAND(major, curReq));
                }else{  //clusterInfo contained only ',' (ex. "1,4,6")
                    int reqInt = Integer.parseInt(curReq.strip());
                    clusterClasses.addAll(planRepository.getFulfillmentClasses(major, reqInt));
                }
            }
        }else if (clusterInfo.contains(";")){       //clusterInfo only contains ';'. (ex. "1;4;2")
            clusterClasses.addAll(getFulfillmentClassesAND(major, clusterInfo.strip()));
        }else{ //clusterInfo doesn't contain ',' OR ';' so it must be a single requirement (otherwise there is an error)
            int reqInt = Integer.parseInt(clusterInfo.strip());
            clusterClasses.addAll(planRepository.getFulfillmentClasses(major, reqInt));
        }

        // Using a Set to remove duplicates
        Set<String> uniqueClusterClasses = new HashSet<>(clusterClasses);
        clusterClasses = new ArrayList<>(uniqueClusterClasses);

        return clusterClasses;
    }

    /*
     * Function that takes in a major and a string of req codes comprised of reqCodes (integers) seperated
     * only by ';'. Returns only courses that fulfill ALL the req codes that are between the ';'s. (so if
     * we get the string "1;2;5" it returns only the courses that fulfill requirements 1 AND 2 AND 5 at the same time).
     */
    public List<String> getFulfillmentClassesAND(String major, String reqCodes){
        String[] reqCodesSplit = reqCodes.split(";");
        int curReqCode = Integer.parseInt(reqCodesSplit[0].strip());
        List<String> clusterClasses = planRepository.getFulfillmentClasses(major, curReqCode);

        //go through every reqCode (not including the first because its already been dealt with)
        //only keeping courses that appear in all the searches
        for (int i=1; i < reqCodesSplit.length; i++){
            curReqCode = Integer.parseInt(reqCodesSplit[i].strip());
            clusterClasses.retainAll(planRepository.getFulfillmentClasses(major, curReqCode)); //retainAll() keeps only common elements between the 2 lists
        }
        return clusterClasses;
    }

    /*
     * function to take in a course cluster's complex string of requirement codes seperated by ',' and ';' where "1,2" signifys you can
     * take a course that fulfills req 1 OR 2 and "1;2" signifys you can take a course that fulfills req 1 AND 2 (things
     * can get more complex like "1;2,1;3". This function then returns the title of the cluster (what will be printed to the user on
     * the box that they select the course from).
     */
    public String getClusterTitle(String major, String complexReqCode){
        String ANDchar = " + ";
        String ORchar = " / ";
        String clusterTitle = "";
        if (complexReqCode.contains(";") && !complexReqCode.contains(",")){
            //complexReqCode only contains ";" (ex "1;3;4")
            String[] singleReqs = complexReqCode.split(";");
            for (int i=0 ; i<singleReqs.length; i++){
                clusterTitle += planRepository.getReqName(major, Integer.parseInt(singleReqs[i].strip()));
                if (i != singleReqs.length-1){
                    clusterTitle += ANDchar;
                }

            }
        }else{
            String[] reqCodes = complexReqCode.split(",");
            for (int i=0 ; i<reqCodes.length; i++){
                String curReqCode = reqCodes[i];
                if(curReqCode.contains(";")){
                    String[] singleReqs = curReqCode.split(";");
                    for (int j=0; j<singleReqs.length; j++){ //At this point, all the elements of singleReqs are just numbers (no more ',' or ';')
                        clusterTitle += planRepository.getReqName(major, Integer.parseInt(singleReqs[j].strip()));
                        if (j != singleReqs.length-1){
                            clusterTitle += ANDchar;
                        }
                    }
                }else{ //there is no ';' so its a single req
                    clusterTitle += planRepository.getReqName(major, Integer.parseInt(curReqCode.strip()));
                }
                if (i != reqCodes.length-1){
                    clusterTitle += ORchar;
                }
            }
        }
        return clusterTitle;
    }

}
