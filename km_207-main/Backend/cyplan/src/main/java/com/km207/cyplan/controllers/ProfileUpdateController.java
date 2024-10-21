package com.km207.cyplan.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.km207.cyplan.services.userServices.updateService;
import com.km207.cyplan.services.userServices.getService;
import com.km207.cyplan.services.userServices.deleteService;
import com.km207.cyplan.services.userServices.passwordService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Profile Update Controller", description = "controller to deal with all the endpoints relating to updating and displaying user profile information")
@RestController
@RequestMapping("/profile")
public class ProfileUpdateController {
    @Autowired
    updateService updateService;
    @Autowired
    getService getService;
    @Autowired
    passwordService passwordService;
    @Autowired
    deleteService deleteService;
    //Get name and major with email
    @GetMapping("/getCred")
    public ResponseEntity<Map<String, Object>> getUserDetailsByEmail(@RequestParam String email){
        //These are the return objects, if a get request is returning an object then it must be made like this
        Optional<String> firstName = getService.findFirstNameByEmail(email);
        Optional<String> major = getService.findMajorByEmail(email);

        if(firstName.isPresent() && major.isPresent())
        {
            Map<String, Object> response = new HashMap<>();
            response.put("first_name", firstName.get());
            response.put("major", major.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    //Update the user, takes in the first name and major
    @PutMapping("/update")
    public ResponseEntity updateUser(@RequestParam("email") String email,
                                     @RequestParam("first_name") String first_name,
                                     @RequestParam("major") String major) {
        int result = updateService.updateUserServiceMethod(email, first_name, major);

        if (result != 1) {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Successful", HttpStatus.OK);
    }
    //Delete user based off of email
    @DeleteMapping("/delete")
    public ResponseEntity deleteUser(@RequestParam("email")String email){
        int result = deleteService.deleteUser(email);
        if(result != 1){
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Successful", HttpStatus.OK);
    }
    @PutMapping("/passwordChange")
    public ResponseEntity changePassword(@RequestParam("email") String email, @RequestParam("password") String oldPassword){
        int result = passwordService.updatePassword(email, oldPassword);
        if (result != 1) {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Successful", HttpStatus.OK);
    }
}
