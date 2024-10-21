package com.km207.cyplan.repository;

import com.km207.cyplan.models.course;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursesRepository extends CrudRepository<course, Integer> {
    @Query(value = "SELECT credits FROM courses WHERE course_code = :givenCourseCode", nativeQuery = true)
    int getNumCredits(@Param("givenCourseCode") String givenCourseCode);

    @Query(value = "SELECT prereqs FROM courses WHERE course_code = :courseCode", nativeQuery = true)
    String getPrereqs(@Param("courseCode") String courseCode);

    @Transactional
    @Modifying
    @Query(value = "UPDATE courses SET prereqs = :newPrereqs WHERE course_code = :courseCode", nativeQuery = true)
    int updatePrereqs(@Param("courseCode") String courseCode,
                     @Param("newPrereqs") String newPrereqs);

    @Query(value = "SELECT coreqs FROM courses WHERE course_code = :courseCode", nativeQuery = true)
    String getCoreqs(@Param("courseCode") String courseCode);

    @Query(value = "SELECT total_rating FROM courses WHERE course_code = :courseCode", nativeQuery = true)
    float getTotalRating(@Param("courseCode") String courseCode);

    @Query(value = "SELECT num_ratings FROM courses WHERE course_code = :courseCode", nativeQuery = true)
    int getNumRatings(@Param("courseCode") String courseCode);

    @Transactional
    @Modifying
    @Query(value = "UPDATE courses SET total_rating = :newTotalRating, num_ratings = :newNumRatings WHERE course_code = :courseCode", nativeQuery = true)
    int updateCourseRating(@Param("courseCode") String courseCode, @Param("newTotalRating") float newTotalRating, @Param("newNumRatings") int newNumRatings);

    @Query(value = "SELECT COUNT(*) FROM courses WHERE course_code = :courseCode", nativeQuery = true)
    Long countByCourseCode(@Param("courseCode") String courseCode);

}
