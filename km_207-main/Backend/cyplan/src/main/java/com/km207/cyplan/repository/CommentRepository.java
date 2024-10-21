package com.km207.cyplan.repository;

import com.km207.cyplan.models.comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<comment, Integer>  {
    @Query(value = "SELECT user_comment FROM comments WHERE course_code = :courseCode", nativeQuery = true)
    List<String> getCourseComments(@Param("courseCode") String courseCode);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO comments(course_code, user_comment) VALUES (:courseCode, :userComment)", nativeQuery = true)
    int addUserComment(@Param("courseCode") String courseCode,
                      @Param("userComment") String userComment);

}
