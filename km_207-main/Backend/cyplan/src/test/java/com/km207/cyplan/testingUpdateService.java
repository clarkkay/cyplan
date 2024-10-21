package com.km207.cyplan;

import com.km207.cyplan.repository.UserRepository;
import com.km207.cyplan.services.userServices.updateService;
import org.h2.engine.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class testingUpdateService {
    @TestConfiguration
    static class UpdateServiceTestConfiguration {
        @Bean
        UserRepository getRepo() {
            return mock(UserRepository.class);
        }

        @Bean
        public updateService uService() {
            return new updateService();
        }
    }
        @Autowired
        private UserRepository userRepo;
        @Autowired
        private updateService uService;
        @Test
        public void testingUpdateService(){
            when(userRepo.updateUserInformation("TEST@iastate.edu", "test", "COM S")).thenReturn(1);
            int expectedResult = 1;
            int actualResult = uService.updateUserServiceMethod("TEST@iastate.edu", "test", "COM S");
            assertEquals(expectedResult,actualResult);
        }
}
