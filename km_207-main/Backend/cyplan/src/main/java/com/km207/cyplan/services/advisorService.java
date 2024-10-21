package com.km207.cyplan.services;

import com.km207.cyplan.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class advisorService {
    @Autowired
    ChatRepository chatRepository;
    public int sendChat(String advisorEmail, String adviseeEmail, String chat, String plan_name){
        return chatRepository.sendChat(advisorEmail, adviseeEmail, chat, plan_name);
    }
    public List<String> getChats(String adviseeEmail, String plan_name){
        return chatRepository.getChats(adviseeEmail, plan_name);
    }
}
