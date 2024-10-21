package com.km207.cyplan.services;

import com.km207.cyplan.models.user;
import org.springframework.beans.factory.annotation.Autowired;
import com.km207.cyplan.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class loginUserService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    UserRepository userRepository;

    public int loginUserServiceMethod(String email, String password){
        return userRepository.loginUser(email, password);
    }
    public Optional<Integer> findUserIDByEmail(String email){
        return userRepository.findByEmail(email).map(user::getUser_id);
    }
    public Optional<String> findNameByEmail(String email){
        return userRepository.findByEmail(email).map(user::getFirst_name);
    }
    public Optional<String> findUserTypeByEmail(String email){
        return userRepository.findByEmail(email).map(user::getUser_type);
    }
    public Optional<String> returnEmail(String email){
        return userRepository.findByEmail(email).map(user::getEmail);
    }
    public Optional<String> returnMajor(String email){
        return userRepository.findByEmail(email).map(user::getMajor);
    }
    //Forgot Password Methods
    public int forgotPasswordMethod(String email) throws Exception{
        Optional<user> userOptional = userRepository.findByEmail(email);
        if(!userOptional.isPresent()){
            throw new Exception("User not found with email:" + email);
        }
        user user = userOptional.get();
        //creating password
        String newPassword = generateNewPass(8);

        user.setPassword(newPassword);
        userRepository.save(user);

        SimpleMailMessage test = new SimpleMailMessage();
        test.setFrom("km207f2023@gmail.com");
        test.setTo(email);
        test.setText("Dear " + email + "\nWe see you have forgot your password! Mistakes happen to the best of us, please login with this password: \n\n" + newPassword);
        test.setSubject("Password Reset");
        javaMailSender.send(test);

        return 1;
    }
    public String generateNewPass(int length){
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for(int i = 0; i < length; i++){
            int index = random.nextInt(allowedChars.length());
            password.append(allowedChars.charAt(index));
        }
        return password.toString();
    }
}
