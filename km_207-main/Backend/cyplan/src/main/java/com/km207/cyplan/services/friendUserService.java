package com.km207.cyplan.services;
import com.km207.cyplan.models.friends;

import com.km207.cyplan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class friendUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JavaMailSender javaMailSender;
    //show list of friends
    public List<String> showFriends(String currentUserEmail){
        return userRepository.findFriendsByEmail(currentUserEmail);
    }
    //Show list of pending Friends
    public List<String> showPendingFriends(String currentUserEmail){
        return userRepository.findPendingFriendsByEmail(currentUserEmail);
    }
    public List<String> showFriendRequests(String currentUserEmail){
        return userRepository.findRequestsByEmail(currentUserEmail);
    }
    //Request Friend
    public int requestFriend(String currentUserEmail, String friendEmail){
        //send friend email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("km207f2023@gmail.com");
        message.setTo(friendEmail);
        message.setText(friendEmail +"\n\n"+currentUserEmail+" has requested you as a friend on Cyplan!");
        message.setSubject("Advisor Check");
        javaMailSender.send(message);
        return userRepository.requestFriend(currentUserEmail, friendEmail);

    }

    //Add Friend
//    public int addFriend(String currentUserEmail, String pendingFriendEmail){
//        //send friend email
////        SimpleMailMessage message = new SimpleMailMessage();
////        message.setFrom("km207f2023@gmail.com");
////        message.setTo(friendEmail);
////        message.setText(friendEmail +"\n\n"+currentUserEmail+" has added you as a friend on Cyplan!");
////        message.setSubject("Advisor Check");
////        javaMailSender.send(message);
//        return userRepository.addFriend(currentUserEmail, pendingFriendEmail);
//
//    }
    public int addFriend(String currentUserEmail, String pendingFriendEmail) {
        //send friend email
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("km207f2023@gmail.com");
//        message.setTo(friendEmail);
//        message.setText(friendEmail +"\n\n"+currentUserEmail+" has added you as a friend on Cyplan!");
//        message.setSubject("Advisor Check");
//        javaMailSender.send(message);
        return userRepository.addFriend(currentUserEmail, pendingFriendEmail);
    }

    //Delete Friend
    public int deleteFriend(String currentUserEmail, String friendEmail){
        return userRepository.deleteFriend(currentUserEmail, friendEmail);
    }
    //Confirm a friend
    public int confirmFriendRequest(String currentUserEmail, String friendEmail) {
        return userRepository.confirmFriendRequest(currentUserEmail, friendEmail);
    }


}
