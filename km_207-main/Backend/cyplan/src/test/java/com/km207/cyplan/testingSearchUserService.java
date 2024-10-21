package com.km207.cyplan;

import com.km207.cyplan.repository.PlanRepository;
import com.km207.cyplan.repository.UserRepository;
import com.km207.cyplan.services.userServices.deleteService;
import com.km207.cyplan.services.userServices.searchUserService;
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
public class testingSearchUserService {
    @TestConfiguration
    static class SearchServiceTestConfiguration { // can be named whatever
        @Bean
        PlanRepository getRepo() {
            return mock(PlanRepository.class);
        }

        @Bean
        public searchUserService sService() {
            return new searchUserService();
        }
    }
    @Autowired
    private PlanRepository planRepo;
    @Autowired
    private searchUserService sService;
    @Test
    public void testSearchUserService(){
        when(planRepo.countUser("TEST@iastate.edu")).thenReturn(1);
        int expectedResult = 1;
        int actualResult = sService.countUsersByEmail("TEST@iastate.edu");
        assertEquals(expectedResult,actualResult);
    }
}
