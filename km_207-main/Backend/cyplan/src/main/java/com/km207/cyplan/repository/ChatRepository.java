package com.km207.cyplan.repository;

import com.km207.cyplan.models.chat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends CrudRepository<chat, Integer> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO chats(username, chat) VALUES (:username, :chat)", nativeQuery = true)
    int addChat(@Param("username") String username, @Param("chat") String chat);

    @Query(value = "SELECT chat FROM chat WHERE username = :username", nativeQuery = true)
    List<String> getChars(@Param("username") String username);
    //send chat for advisors
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO advisorChats(advisorEmail, adviseeEmail, chat, plan_name) VALUES (:advisorEmail, :adviseeEmail, :chat, :plan_name)", nativeQuery = true)
    int sendChat(@Param("advisorEmail") String advisorEmail, @Param("adviseeEmail")String adviseeEmail, @Param("chat") String chat, @Param("plan_name") String plan_name);
    //getChats from a username
    @Query(value = "SELECT chat FROM advisorChats WHERE adviseeEmail = :adviseeEmail AND plan_name = :plan_name", nativeQuery = true)
    List<String> getChats(@Param("adviseeEmail") String adviseeEmail, @Param("plan_name") String plan_name);
}
