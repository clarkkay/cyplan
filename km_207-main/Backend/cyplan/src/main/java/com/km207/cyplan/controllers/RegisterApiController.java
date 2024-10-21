package com.km207.cyplan.controllers;

import com.km207.cyplan.services.userServices.RegisterUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.regex.Pattern;
@Tag(name = "Register Controller", description = "controller to manage user registration endpoints including registering a new user into the database and loading there information")
@RestController
//change post demo
public class RegisterApiController {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    RegisterUserService userService;
    @PostMapping("/user/register")
    public ResponseEntity registerNewUser(@RequestParam("first_name")String first_name,
                                          @RequestParam("email")String email,
                                          @RequestParam("major")String major,
                                          @RequestParam("user_type") String user_type,
                                          @RequestParam("password")String password){
        if(first_name.isEmpty()  || email.isEmpty() || password.isEmpty()){
            return new ResponseEntity<>("Please Complete all Fields", HttpStatus.BAD_REQUEST);
        }
        //SQL Injection checker
        if (containsSpecialCharacter(password)) {
            return ResponseEntity.badRequest().body("Password contains invalid characters.");
        }
        //advisor check
        if(user_type.equals("A")){
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("km207f2023@gmail.com");
            message.setTo("km207f2023@gmail.com");
            message.setText(first_name + " has signed up as an advisor! There email is: " + email);
            message.setSubject("Advisor Check");
            javaMailSender.send(message);
        }
        //Hash Password:
        //String hashed_pass = BCrypt.hashpw(password, BCrypt.gensalt());

        //Register New User
        int result = userService.registerNewUserServiceMethod(first_name, email, major, user_type, password);

        if(result != 1){
            return new ResponseEntity<>("Failed ", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(" Successful", HttpStatus.OK);
    }

    //SQL Injection helper
    public boolean containsSpecialCharacter(String s) {
        return Pattern.compile("[!@#$%^&*()~`=_+\\[\\]{}|;:'\",.<>?/\\\\]").matcher(s).find();
    }
}
