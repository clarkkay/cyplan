package com.km207.cyplan;

import com.km207.cyplan.repository.UserRepository;
import com.km207.cyplan.services.userServices.passwordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class TestingPasswordService {
    @TestConfiguration
    static class PasswordServiceTestConfiguration { // can be named whatever
        @Bean
        UserRepository getRepo() {
            return mock(UserRepository.class);
        }
        @Bean
        public passwordService pService() {
            return new passwordService();
        }
    }

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private passwordService pService;

    @Test
    public void testUpdatePassword(){
        //mock db
        when(userRepo.updatePassword("TEST@iastate.edu", "123")).thenReturn(1);

        //calculate results
        int expectedResult = 1;
        int actualResult = pService.updatePassword("TEST@iastate.edu", "123");
        assertEquals(expectedResult, actualResult);
    }
}
