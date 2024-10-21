package com.km207.cyplan;

import com.km207.cyplan.repository.UserRepository;
import com.km207.cyplan.services.userServices.deleteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class TestingDeleteService {
    @TestConfiguration
    static class DeleteServiceTestConfiguration { // can be named whatever
        @Bean
        UserRepository getRepo() {
            return mock(UserRepository.class);
        }

        @Bean
        public deleteService dService() {
            return new deleteService();
        }
    }

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private deleteService dService;

    @Test
    public void testDeleteUser(){
        //MOCK db
        when(userRepo.deleteUser("TEST@iastate.edu")).thenReturn(1);

        //calculate results
        int expectedResult = 1;
        int actualResult = dService.deleteUser("TEST@iastate.edu");
        assertEquals(expectedResult, actualResult);
    }
}
