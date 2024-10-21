package com.km207.cyplan.repository;

import com.km207.cyplan.models.plan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends CrudRepository<plan, Integer> {
    /*
    SELECT QUERIES
     */
    //Get User Count
    @Query(value = "SELECT COUNT(*) FROM users WHERE email = :email", nativeQuery = true)
    int countUser(@Param("email") String email);

    //Query to get student plan
    @Query(value = "SELECT course_code, semester_taken FROM plans WHERE user_id = :userID AND plan_name = :planName", nativeQuery = true)
    List<String> getUserPlan(@Param("userID") int userID, @Param("planName") String planName);

    //Query to get user's degree code
    @Query(value = "SELECT major FROM users WHERE user_id = :userID", nativeQuery = true)
    String getUserMajor(@Param("userID") int userID);

    //Query to get users degree level
    @Query(value = "SELECT degree_level FROM enrollments WHERE user_id = :userID", nativeQuery = true)
    String getUserDegreeLevel(@Param("userID") int userID);

    //Query to get a degree's base plan courses
    @Query(value = "SELECT course_code, semester_taken FROM basePlanCourses WHERE major = :userMajor", nativeQuery = true)
    List<String> getDegreeBasePlanCourses(@Param("userMajor") String userMajor);

    //Query to get a degree's base plan clusters
    @Query(value = "SELECT req_codes, semester_taken FROM basePlanClusters WHERE major = :userMajor", nativeQuery = true)
    List<String> getRawDegreeBasePlanClusters(@Param("userMajor") String userMajor);

    //Query to get a course description
    @Query(value = "SELECT description FROM courses WHERE course_code = :courseCode", nativeQuery = true)
    String getCourseDescription(@Param("courseCode") String courseCode);

    //query to get when student enrolled in a degree. MAY WANT TO MAKE THIS ONLY TAKE THE MOST RECENT "SINCE" IF THE USER HAS ENROLLED IN MULTIPLE DEGREES IN THE PAST (can a user enroll in multiple degrees?)
    @Query(value = "SELECT since FROM enrolls WHERE user_id = :userID", nativeQuery = true)
    Date getEnrollmentDate(@Param("userID") int userID);

    //query to get highest plan ID
    // Note for @Tanner: changed the return type to Integer so it can handle null values
    @Query(value = "SELECT plan_id FROM plans WHERE user_id = :userID ORDER BY plan_id DESC LIMIT 1", nativeQuery = true)
    Integer getHighestPlanID(@Param("userID") int userID);

    // @Tanner: same note as other
    @Query(value = "SELECT plan_id FROM plans WHERE plan_name = :planName AND user_id = :userID LIMIT 1", nativeQuery = true)
    Integer getPlanIDfromName(@Param("userID") int userID, @Param("planName") String planName);

    //query to get the distinct students plan names
    @Query(value = "SELECT DISTINCT plan_name FROM plans WHERE user_id = :userID", nativeQuery = true)
    List<String> findAllByUserId(@Param("userID") int userID);

    @Query(value = "SELECT course_code FROM fulfillments WHERE major = :userMajor AND req_code = :reqCode", nativeQuery = true)
    List<String> getFulfillmentClasses(@Param("userMajor") String userMajor, @Param("reqCode") int reqCode);

    @Query(value = "SELECT req_name FROM requirements WHERE major = :givenMajor AND req_code = :givenReqCode", nativeQuery = true)
    String getReqName(@Param("givenMajor") String userMajor, @Param("givenReqCode") int givenReqCode);

    @Query(value = "SELECT COUNT(*) FROM requirements WHERE major = :givenMajor", nativeQuery = true)
    int countDegreeRequirements(@Param("givenMajor") String givenMajor);

    @Query(value = "SELECT req_code FROM requirements WHERE major = :givenMajor", nativeQuery = true)
    List<Integer> getDegreeReqCodes(@Param("givenMajor") String givenMajor);

    //query to tell if a course counts towards fulfilling a given degree requirment
    @Query(value = "SELECT COUNT(*) FROM fulfillments WHERE course_code = :givenCourseCode AND req_code = :givenReqCode AND major = :givenMajor ", nativeQuery = true)
    int doesCourseFulfillReq(@Param("givenCourseCode") String givenCourseCode, @Param("givenReqCode") int givenReqCode, @Param("givenMajor") String givenMajor);

    @Query(value = "SELECT to_fulfill_type FROM requirements WHERE major = :givenMajor AND req_code = :givenReqCode", nativeQuery = true)
    String getFulfillmentType(@Param("givenMajor") String givenMajor, @Param("givenReqCode") int givenReqCode);

    @Query(value = "SELECT to_fulfill FROM requirements WHERE major = :givenMajor AND req_code = :givenReqCode", nativeQuery = true)
    int getFulfillmentAmntRequired(@Param("givenMajor") String givenMajor, @Param("givenReqCode") int givenReqCode);

    /*
    UPDATING/INSERTING/DELETING QUERIES
     */

    //Inserts a new plan into database
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO plans(user_id, course_code, semester_taken, plan_id, plan_name) VALUES(:user_id, :course_code, :semester_taken, :plan_id, :plan_name)", nativeQuery = true)
    int insertPlanEntry(@Param("user_id") int user_id ,
                        @Param("course_code") String course_code,
                        @Param("semester_taken") int semester_taken,
                        @Param("plan_id") int plan_id,
                        @Param("plan_name") String plan_name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM plans WHERE user_id = :userID AND plan_name = :planName", nativeQuery = true)
    int deleteUserPlan(@Param("userID") int userID, @Param("planName") String planName);
}