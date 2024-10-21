package com.km207.cyplan;

import com.km207.cyplan.repository.ChatRepository;
import com.km207.cyplan.repository.UserRepository;
import com.km207.cyplan.services.advisorService;
import com.km207.cyplan.services.userServices.getService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class testingAdvisorService {
    @TestConfiguration
    static class GetServiceTestConfiguration {
        @Bean
        ChatRepository advRepo() {
            return mock(ChatRepository.class);
        }

        @Bean
        public advisorService gService() {
            return new advisorService();
        }
    }
        @Autowired
        private ChatRepository advRepo;
        @Autowired
        private advisorService aService;
        @Test
        public void testingAdvisorService(){
            when(advRepo.sendChat("krclark@iastate.edu", "lte@iastate.edu","HIYA!","Logans Plan 1")).thenReturn(1);
            int expectedResult = 1;
            int actualResult = aService.sendChat("krclark@iastate.edu", "lte@iastate.edu","HIYA!","Logans Plan 1");
            assertEquals(expectedResult,actualResult);
        }
        @Test
    public void getChatsTest(){
            List<String> chats = new ArrayList<String>();
            chats.add("HIYA!");
            when(advRepo.getChats("lte@iastate.edu", "Logans Plan 1")).thenReturn(chats);
            List<String> expectedResult = List.of("HIYA!");
            List<String> actualResult = aService.getChats("lte@iastate.edu", "Logans Plan 1");
            assertEquals(expectedResult,actualResult);
        }
}
