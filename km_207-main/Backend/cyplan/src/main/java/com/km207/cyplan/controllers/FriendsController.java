package com.km207.cyplan.controllers;

import com.km207.cyplan.models.user;
import com.km207.cyplan.repository.UserRepository;
import com.km207.cyplan.services.userServices.searchUserService;
import com.km207.cyplan.services.friendUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import com.km207.cyplan.services.userServices.getService;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Friends Controller", description = "class for all endpoints relating the friends feature of cyplan including updating database & displaying information about friends")
@RestController
@RequestMapping("/friends")
public class FriendsController {

    @Autowired
    searchUserService searchUserService;

    @Autowired
    friendUserService friendUserService;

    @Autowired
    private UserRepository userRepository;
@Autowired
getService getService;
    //Display Friends
    @GetMapping("/showFriends")
   public ResponseEntity<Map<String, Object>> showStudentsByEmail(@RequestParam("email") String email){
        List<String> friends = friendUserService.showFriends(email);
        List<String> nonNullFriends = friends.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();

        if (!nonNullFriends.isEmpty()) {
            response.put("friends", nonNullFriends);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
//    //show the pending requests
//    @GetMapping("/pendingRequests")
//    public ResponseEntity<Map<String, Object>> showPendingRequests(@RequestParam("email") String email){
//        List<String> friends = friendUserService.showPendingFriends(email);
//        Map<String, Object> response = new HashMap<>();
//        if(!friends.isEmpty()){
//            response.put("Pending", friends);
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
    @GetMapping("/pendingRequests")
    public ResponseEntity<Map<String, Object>> showPendingRequests(@RequestParam("email") String email){
        List<String> friends = friendUserService.showPendingFriends(email);

        // Filter out null values
        List<String> nonNullFriends = friends.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        if (!nonNullFriends.isEmpty()) {
            response.put("Pending", nonNullFriends);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //Add Friends SIMPLE
    @PostMapping("/addFriend")
    public ResponseEntity addFriend(@RequestParam("currentUserEmail") String currentUserEmail, @RequestParam("pendingFriendEmail") String pendingFriendEmail) {
        int result = friendUserService.confirmFriendRequest(currentUserEmail, pendingFriendEmail);
        if(result != 1){
            return new ResponseEntity<>("Friend request confirmation failed", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("Friend request confirmed successfully", HttpStatus.OK);
        }
    }

    //Add Friends (goes to pending request)
    //Delete a friend out of the request
    @DeleteMapping("/deleteFriend")
    public ResponseEntity deleteFriend(@RequestParam("currentUserEmail") String currentUserEmail, @RequestParam("friendEmail") String friendEmail){
        if(currentUserEmail.isEmpty() || friendEmail.isEmpty()){
            return new ResponseEntity<>("Please Complete all Fields", HttpStatus.BAD_REQUEST);
        }
        int result = friendUserService.deleteFriend(currentUserEmail, friendEmail);
        if(result != 1){
            return  new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        }
    }
    // Confirming a friend request, does the same thing as add friend so you dont
//    @PostMapping("/confirmFriendRequest")
//    public ResponseEntity confirmFriendRequest(@RequestParam("currentUserEmail") String currentUserEmail, @RequestParam("friendEmail") String friendEmail) {
//        if(currentUserEmail.isEmpty() || friendEmail.isEmpty()){
//            return new ResponseEntity<>("Please Complete all Fields", HttpStatus.BAD_REQUEST);
//        }
//        int result = friendUserService.requestFriend(currentUserEmail, friendEmail);
//        if(result != 1){
//            return  new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
//        } else {
//            return new ResponseEntity<>("Successful", HttpStatus.OK);
//        }
//    }

    // Requesting a friend
    @PostMapping("/requestFriend")
    public ResponseEntity requestFriend(@RequestParam("currentUserEmail") String currentUserEmail, @RequestParam("pendingFriendEmail") String pendingFriendEmail) {
        if(currentUserEmail.isEmpty() || pendingFriendEmail.isEmpty()){
            return new ResponseEntity<>("Please Complete all Fields", HttpStatus.BAD_REQUEST);
        }
        int result = friendUserService.requestFriend(currentUserEmail, pendingFriendEmail);
        if(result != 1){
            return  new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        }
    }
    
    @GetMapping("/getUserInfo")
    public ResponseEntity<Map<String, Object>> getUserID(@RequestParam("email") String email){
        Optional<String> name = getService.findFirstNameByEmail(email);
        Optional<String> major = getService.findMajorByEmail(email);
        Optional<Integer> userID = getService.findIDByEmail(email);

        if (name.isPresent() && major.isPresent() && userID.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("name", name.get());
            response.put("major", major.get());
            response.put("userID", userID.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //Logan is using getCred from the profile update to search the students
//    @GetMapping("/getChats")
//    public List<String> getChats(@RequestParam("courseCode") String courseCode){
//        List<String> courseComments = commentRepository.getCourseComments(courseCode);
//        return courseComments;
//    }
    @GetMapping("/yourRequests")
    public ResponseEntity<Map<String, Object>> yourRequests(@RequestParam("currentUserEmail") String currentUserEmail){
        List<String> friends = friendUserService.showFriendRequests(currentUserEmail);
        Map<String, Object> response = new HashMap<>();
        if(!friends.isEmpty()){
            response.put("Requested You", friends);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
