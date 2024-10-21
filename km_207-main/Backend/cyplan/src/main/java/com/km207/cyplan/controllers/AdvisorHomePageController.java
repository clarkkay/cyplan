package com.km207.cyplan.controllers;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.km207.cyplan.services.userServices.searchUserService;
import com.km207.cyplan.services.advisorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name ="AdvisorHomePage Controller", description = "Contains all endpoints related to getting and setting advisor home page information")
@RestController
@RequestMapping("/advisor/homepage")
public class AdvisorHomePageController {
    @Autowired
    searchUserService searchUserService;
    @Autowired
    advisorService advisorService;
    //Get mapping for checking if a user exists in the database
    @GetMapping("/getStudent")
    public ResponseEntity getStudentCountByEmail(@RequestParam String email) {
        int count = searchUserService.countUsersByEmail(email);

        if (count != 1) {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("User Exists! There is " + count +" users with that email", HttpStatus.OK);
        }
    }
    @GetMapping("/getChats")
    public ResponseEntity<Map<String, Object>> getChats(@RequestParam("adviseeEmail") String adviseeEmail, @RequestParam("plan_name") String plan_name){
        List<String> chats = advisorService.getChats(adviseeEmail, plan_name);
        Map<String, Object> response = new HashMap<>();
        if(!chats.isEmpty()){
            response.put("Message", chats);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/sendChat")
    public ResponseEntity sendChat(@RequestParam("advisorEmail") String advisorEmail, @RequestParam("adviseeEmail") String adviseeEmail, @RequestParam("chat") String chat, @RequestParam("plan_name") String plan_name){
        int count = advisorService.sendChat(advisorEmail, adviseeEmail, chat, plan_name);
        if (count != 1) {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("Successful" , HttpStatus.OK);
        }
    }
}
