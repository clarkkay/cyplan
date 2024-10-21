package com.km207.cyplan.controllers;

import com.km207.cyplan.services.loginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Login Controller", description = "simple user login endpoint for testing and complex login to return credentials for shared preferences")
@RestController
public class LoginApiController {

    @Autowired
    loginUserService loginService;

    @PostMapping ("/user/login")
    public ResponseEntity loginUser(@RequestParam("email") String email, @RequestParam("password") String password){
        if(email.isEmpty() || password.isEmpty()){
            new ResponseEntity<>("Please complete fields", HttpStatus.BAD_REQUEST);
        }
        //SQL Injection checker
        if (containsSpecialCharacter(password)) {
            return ResponseEntity.badRequest().body("Password contains invalid characters.");
        }
        int result = loginService.loginUserServiceMethod(email, password);

        if(result != 1){
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(" Successful", HttpStatus.OK);
    }
    //SQL Injection helper
    public boolean containsSpecialCharacter(String s) {
        return Pattern.compile("[!@#$%^&*()~`=_+\\[\\]{}|;:'\",.<>?/\\\\]").matcher(s).find();
    }
    //complex login, return the usertype, user email, name
    @PostMapping("/user/loginCred")
    public ResponseEntity<Map<String, Object>> getLoginDetails(@RequestParam("email")String email, @RequestParam("password")String password) {
        if (email.isEmpty() || password.isEmpty()) {
            new ResponseEntity<>("Please complete fields", HttpStatus.BAD_REQUEST);
        }
        int result = loginService.loginUserServiceMethod(email, password);
        if (result != 1) {
            return ResponseEntity.notFound().build();
        } else {
            //Return Objects
            Optional<String> firstName = loginService.findNameByEmail(email);
            Optional<String> userType = loginService.findUserTypeByEmail(email);
            Optional<String> returnEmail = loginService.returnEmail(email);
            Optional<String> returnMajor = loginService.returnMajor(email);
            Optional<Integer> userID = loginService.findUserIDByEmail(email);
            Map<String, Object> response = new HashMap<>();
            response.put("first_name", firstName.get());
            response.put("user_type", userType.get());
            response.put("email", returnEmail.get());
            response.put("major", returnMajor.get());
            response.put("user_id", userID.get());
            return ResponseEntity.ok(response);
        }

    }
    //Forgot Password Implementation
    @PostMapping("/user/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) throws Exception {
        if(email.isEmpty()){
            new ResponseEntity<>("Please complete fields", HttpStatus.BAD_REQUEST);
        }
        int result = loginService.forgotPasswordMethod(email);

        if(result != 1){
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(" Successful", HttpStatus.OK);
    }
}
