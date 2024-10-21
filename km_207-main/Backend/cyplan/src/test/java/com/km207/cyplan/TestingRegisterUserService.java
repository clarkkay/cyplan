package com.km207.cyplan;

import com.km207.cyplan.repository.UserRepository;
import com.km207.cyplan.services.userServices.RegisterUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
public class TestingRegisterUserService {
    @TestConfiguration
    static class RegisterUserServiceTestConfiguration { // can be named whatever
        @Bean
        UserRepository getRepo() {
            return mock(UserRepository.class);
        }
        @Bean
        public RegisterUserService regUserService() {return new RegisterUserService();}

        @Bean
        public JavaMailSender getJavaMailSender() {return mock(JavaMailSender.class);}
    }

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RegisterUserService regUserService;
    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    public void testregisterNewUserServiceMethod(){

        //mock db
        when(userRepo.registerNewUser("TESTfname", "TEST@iastate.edu", "TEST", "T", "TEST123")).thenReturn(1);

        //testing
        int expectedResult = 1;
        int actualResult = regUserService.registerNewUserServiceMethod("TESTfname", "TEST@iastate.edu", "TEST", "T", "TEST123");
        assertEquals(expectedResult, actualResult);
    }
}
