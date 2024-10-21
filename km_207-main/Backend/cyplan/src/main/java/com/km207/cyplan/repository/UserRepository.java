package com.km207.cyplan.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.km207.cyplan.models.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<user, Integer> {
    //insert user
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO users(first_name, email, major, user_type, password) VALUES(:first_name, :email, :major, :user_type, :password)", nativeQuery = true)
    int registerNewUser(@Param("first_name") String first_name,
                        @Param("email") String email,
                        @Param("major") String major,
                        @Param("user_type") String user_type,
                        @Param("password") String password);
    //update
    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET first_name = :first_name, major = :major WHERE email = :email", nativeQuery = true)
    int updateUserInformation(@Param("email") String email,
                              @Param("first_name") String first_name,
                              @Param("major") String major);
    //update password
    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET password = :password WHERE email = :email", nativeQuery = true)
    int updatePassword(@Param("email") String email, @Param("password") String password);
    // get query for the profile
    Optional<user> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "DELETE from users WHERE email = :email", nativeQuery = true)
    int deleteUser(@Param("email") String email);

    //login
    @Query(value = "SELECT COUNT(*) FROM users WHERE email = :email and password = :password", nativeQuery = true)
    int loginUser(@Param("email") String email, @Param("password")String password);

    //Add Friend
    // Confirm friend request
    @Transactional
    @Modifying
    @Query(value = "UPDATE friends SET friendEmail = :currentUserEmail, currentUseremail : pendingFriendEmail, status = 'CONFIRMED', pendingFriendEmail = NULL WHERE currentUserEmail = :pendingFriendEmail AND pendingFriendEmail = :currentUserEmail AND status = 'PENDING'", nativeQuery = true)
    int addFriend(@Param("currentUserEmail") String currentUserEmail, @Param("pendingFriendEmail") String pendingFriendEmail);
    //Request friend
//    @Transactional
//    @Modifying
//    @Query(value = "INSERT INTO friends(currentUserEmail, pendingFriendEmail) VALUES(:currentUserEmail, :pendingFriendEmail)", nativeQuery = true)
//    int requestFriend(@Param("currentUserEmail") String currentUserEmail, @Param("pendingFriendEmail") String pendingFriendEmail);
    //Show friends by Email
    @Query(value = "(SELECT friendEmail FROM friends WHERE currentUserEmail = :email AND status = 'CONFIRMED') UNION (SELECT currentUserEmail FROM friends WHERE friendEmail = :email AND status = 'CONFIRMED')", nativeQuery = true)
    List<String> findFriendsByEmail(String email);
    //Show pending friends by email
    @Query(value = "SELECT pendingFriendEmail FROM friends f WHERE f.currentUseremail = :email", nativeQuery = true)
    List<String> findPendingFriendsByEmail(String email);
    //Show friend requests FOR YOU
    @Query(value = "SELECT currentUseremail from friends f where f.pendingFriendEmail = :currentUseremail", nativeQuery = true)
    List<String> findRequestsByEmail(String currentUseremail);
    //delete friend
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM friends WHERE ((status = 'PENDING' AND (currentUseremail = :currentUserEmail OR pendingFriendEmail = :currentUserEmail)) OR (status = 'CONFIRMED' AND ((currentUseremail = :currentUserEmail AND friendEmail = :friendEmail) OR (currentUseremail = :friendEmail AND friendEmail = :currentUserEmail))))", nativeQuery = true)
    int deleteFriend(@Param("currentUserEmail") String currentUserEmail, @Param("friendEmail") String friendEmail);

    // Request friend
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO friends(currentUserEmail, pendingFriendEmail, status) VALUES(:currentUserEmail, :pendingFriendEmail, 'PENDING')", nativeQuery = true)
    int requestFriend(@Param("currentUserEmail") String currentUserEmail, @Param("pendingFriendEmail") String pendingFriendEmail);

    // Confirm friend request
    @Transactional
    @Modifying
    @Query(value = "UPDATE friends SET friendEmail = :friendEmail, status = 'CONFIRMED', pendingFriendEmail = NULL WHERE currentUserEmail = :currentUserEmail AND pendingFriendEmail = :friendEmail", nativeQuery = true)
    int confirmFriendRequest(@Param("currentUserEmail") String currentUserEmail, @Param("friendEmail") String friendEmail);


//    @Query(value = "SELECT user_id FROM users WHERE email = :email", nativeQuery = true)
//    int getUserID(@Param("email") String email);
    @Query(value = "SELECT user_id, major, first_name from users where email =:email", nativeQuery = true)
    List<String> getUserInformation(String email);
}
