package com.km207.cyplan.services.userServices;

import com.km207.cyplan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
@Service
public class RegisterUserService {
        @Autowired
        private JavaMailSender javaMailSender;

        @Autowired
        UserRepository userRepository;
        public int registerNewUserServiceMethod(String fname, String email, String major, String user_type, String password){
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("km207f2023@gmail.com");
            message.setTo(email);
            message.setText("Dear " + fname + "\nWelcome to Cyplan!\n\n We're excited to have you on board. Your account has been successfully created, and you're all set to explore the features we have in store for you.\n\nWarm regards, \nKayley Clark\nCyplan");
            message.setSubject("Welcome to Cyplan!");
            javaMailSender.send(message);
            return userRepository.registerNewUser(fname, email, major, user_type, password);
        }
}
