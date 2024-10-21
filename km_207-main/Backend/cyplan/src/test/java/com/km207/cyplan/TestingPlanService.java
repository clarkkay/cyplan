package com.km207.cyplan;

import com.km207.cyplan.models.cluster;
import com.km207.cyplan.repository.PlanRepository;
import com.km207.cyplan.services.planService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestingPlanService {
    @TestConfiguration
    static class PlanServiceTestConfiguration { // can be named whatever

        @Bean
        public planService pService() {
            return new planService();
        }

        @Bean
        PlanRepository getRepo() {
            return mock(PlanRepository.class);
        }
    }

    @Autowired
    private planService pService;

    @Autowired
    private PlanRepository planRepo;

    @Test
    public void testTakenPrior()  {
        List<String> plan = List.of("COM S 101, 1", "COM S 102, 2", "COM S 103, 3");
        String keyCourseCode = "COM S 102";

        //Test when course being taken before the key semester
        int keySemester = 3;
        assertEquals(true, pService.takenPrior(plan, keyCourseCode, keySemester));

        //Test when course taken durring the key semester
        keySemester = 2;
        assertEquals(false, pService.takenPrior(plan, keyCourseCode, keySemester));

        //Test when course taken after the key semester
        keySemester = 1;
        assertEquals(false, pService.takenPrior(plan, keyCourseCode, keySemester));

        //Test when plan entered has improper formatting (missing a comma for the semester in second string)
        plan = List.of("COM S 101, 1", "COM S 102", "COM S 103, 3");
        assertEquals(false, pService.takenPrior(plan, keyCourseCode, keySemester));
    }

    @Test
    public void testGetDegreeBasePlanClusters1(){
        //Assumptions:
        //The TEST baseplan has 3 req clusters (0, 1AND2, 1OR2)
        //TEST101 and TEST102 fulfill req 0
        //TEST103 and TEST104 fulfill req 1
        //TEST103 and TEST105 fulfill req 2

        //MOCK REPO RESPONSE
        List<String> mockList = List.of("0,1","1,2,2","1;2,3");
        when(planRepo.getRawDegreeBasePlanClusters("TEST")).thenReturn(mockList);

        //create array lists for req classes (using List.of() is immutable and so I can't call "retainALl" on it, thus I have to create array lists this way)
        List<String> req0Classes = new ArrayList<String>(); req0Classes.add("TEST 101"); req0Classes.add("TEST 102");
        List<String> req1Classes = new ArrayList<String>(); req1Classes.add("TEST 103"); req1Classes.add("TEST 104");
        List<String> req2Classes = new ArrayList<String>(); req2Classes.add("TEST 103"); req2Classes.add("TEST 105");

        //MOCKING METHODS
        when(planRepo.getFulfillmentClasses("TEST", 0)).thenReturn(req0Classes);
        when(planRepo.getFulfillmentClasses("TEST", 1)).thenReturn(req1Classes);
        when(planRepo.getFulfillmentClasses("TEST",2)).thenReturn(req2Classes);
        when(planRepo.getReqName("TEST", 0)).thenReturn("test0");
        when(planRepo.getReqName("TEST", 1)).thenReturn("test1");
        when(planRepo.getReqName("TEST", 2)).thenReturn("test2");

        //CREATE EXPECTED RESPONSE
        List<cluster> expectedResponse = new ArrayList<cluster>();
        List<String> c1classes = new ArrayList<String>(); c1classes.add("TEST 101"); c1classes.add("TEST 102");
        List<String> c2classes = new ArrayList<String>(); c2classes.add("TEST 103"); c2classes.add("TEST 104"); c2classes.add("TEST 105");
        List<String> c3classes = new ArrayList<String>(); c3classes.add("TEST 103");
        expectedResponse.add(new cluster("test0", c1classes, 1));
        expectedResponse.add(new cluster("test1 / test2", c2classes, 2));
        expectedResponse.add(new cluster("test1 + test2", c3classes,3));

        //GET ACTUAL RESPONSE
        List<cluster> actualResponse = pService.getDegreeBasePlanClusters("TEST");

        //TEST
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetClusterClasses1(){ //testing getClusterClasses with "0" as reqCode
        //Assumptions:
        //TEST101 and TEST102 fulfill req 0
        //TEST103 and TEST104 fulfill req 1
        //TEST103 and TEST105 fulfill req 2

        String major = "TEST";
        String clusterInfo = "0";

        when(planRepo.getFulfillmentClasses("TEST", 0)).thenReturn(List.of("TEST 101", "TEST 102"));

        List<String> expectedResult = List.of("TEST 102", "TEST 101");
        List<String> actualResult = pService.getClusterClasses("TEST", "0");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetClusterClasses2(){ //testing getClusterClasses with "1,2" as reqCode
        //Assumptions:
        //TEST101 and TEST102 fulfill req 0
        //TEST103 and TEST104 fulfill req 1
        //TEST103 and TEST105 fulfill req 2

        when(planRepo.getFulfillmentClasses("TEST", 1)).thenReturn(List.of("TEST 103", "TEST 104"));
        when(planRepo.getFulfillmentClasses("TEST",2)).thenReturn(List.of("TEST 103", "TEST 105"));

        List<String> expectedResult = List.of("TEST 103", "TEST 104", "TEST 105");
        List<String> actualResult = pService.getClusterClasses("TEST", "1,2");
        Collections.sort(actualResult); //for some reason the actualResult isn't sorted, but order doesn't matter so just sort it

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetClusterClasses3(){ //testing getClusterClasses with "1;2" as reqCode
        //Assumptions:
        //TEST101 and TEST102 fulfill req 0
        //TEST103 and TEST104 fulfill req 1
        //TEST103 and TEST105 fulfill req 2

        //create array lists for req classes (using List.of() is immutable and so I can't call "retainALl" on it, thus I have to create array lists this way)
        List<String> req1Classes = new ArrayList<String>(); req1Classes.add("TEST 103"); req1Classes.add("TEST 104");
        List<String> req2Classes = new ArrayList<String>(); req2Classes.add("TEST 103"); req2Classes.add("TEST 105");

        //MOCKING METHODS
        when(planRepo.getFulfillmentClasses("TEST", 1)).thenReturn(req1Classes);
        when(planRepo.getFulfillmentClasses("TEST",2)).thenReturn(req2Classes);

        //CALCULATING RESULTS
        List<String> expectedResult = List.of("TEST 103");
        List<String> actualResult = pService.getClusterClasses("TEST", "1;2");
        Collections.sort(actualResult); //for some reason the actualResult isn't sorted, but order doesn't matter so just sort it

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetClusterClasses4(){ //testing getClusterClasses with "0,1;2" as reqCode
        //Assumptions:
        //TEST101 and TEST102 fulfill req 0
        //TEST103 and TEST104 fulfill req 1
        //TEST103 and TEST105 fulfill req 2

        //create array lists for req classes (using List.of() is immutable and so I can't call "retainALl" on it, thus I have to create array lists this way)
        List<String> req0Classes = new ArrayList<String>(); req0Classes.add("TEST 101"); req0Classes.add("TEST 102");
        List<String> req1Classes = new ArrayList<String>(); req1Classes.add("TEST 103"); req1Classes.add("TEST 104");
        List<String> req2Classes = new ArrayList<String>(); req2Classes.add("TEST 103"); req2Classes.add("TEST 105");

        //MOCKING METHODS
        when(planRepo.getFulfillmentClasses("TEST", 0)).thenReturn(req0Classes);
        when(planRepo.getFulfillmentClasses("TEST", 1)).thenReturn(req1Classes);
        when(planRepo.getFulfillmentClasses("TEST",2)).thenReturn(req2Classes);

        //CALCULATING RESULTS
        List<String> expectedResult = List.of("TEST 101", "TEST 102", "TEST 103");
        List<String> actualResult = pService.getClusterClasses("TEST", "0,1;2");
        Collections.sort(actualResult); //for some reason the actualResult isn't sorted, but order doesn't matter so just sort it

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetFulfillmentClassesAND(){
        //Assumptions:
        //TEST101 and TEST102 fulfill req 0
        //TEST103 and TEST104 fulfill req 1
        //TEST103 and TEST105 fulfill req 2
        //TEST101 and TEST103 fulfill req 3

        //create array lists for req classes (using List.of() is immutable and so I can't call "retainALl" on it, thus I have to create array lists this way)
        List<String> req0Classes = new ArrayList<String>(); req0Classes.add("TEST 101"); req0Classes.add("TEST 102");
        List<String> req1Classes = new ArrayList<String>(); req1Classes.add("TEST 103"); req1Classes.add("TEST 104");
        List<String> req2Classes = new ArrayList<String>(); req2Classes.add("TEST 103"); req2Classes.add("TEST 105");
        List<String> req3Classes = new ArrayList<String>(); req3Classes.add("TEST 101"); req3Classes.add("TEST 103");


        //MOCKING METHODS
        when(planRepo.getFulfillmentClasses("TEST", 0)).thenReturn(req0Classes);
        when(planRepo.getFulfillmentClasses("TEST", 1)).thenReturn(req1Classes);
        when(planRepo.getFulfillmentClasses("TEST",2)).thenReturn(req2Classes);
        when(planRepo.getFulfillmentClasses("TEST", 3)).thenReturn(req3Classes);

        //Testing with 2 reqs who have a class in common
        List<String> expectedResult = List.of("TEST 103");
        List<String> actualResult = pService.getFulfillmentClassesAND("TEST", "1;2");
        Collections.sort(actualResult); //for some reason the actualResult isn't sorted, but order doesn't matter so just sort it
        assertEquals(expectedResult, actualResult);

        //Testing with 2 reqs who DONT have a class in common
        expectedResult = new ArrayList<String>();
        actualResult = pService.getFulfillmentClassesAND("TEST", "0;1");
        Collections.sort(actualResult);
        assertEquals(expectedResult, actualResult);

        //Testing with 3 reqs that have a class in common
        expectedResult = List.of("TEST 103");
        actualResult = pService.getFulfillmentClassesAND("TEST", "1;2;3");
        Collections.sort(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetClusterTitle(){
        //Assumptions:
        //Req "n" is titled "testn"

        //MOCKING db calls
        when(planRepo.getReqName("TEST", 0)).thenReturn("test0");
        when(planRepo.getReqName("TEST", 1)).thenReturn("test1");
        when(planRepo.getReqName("TEST", 2)).thenReturn("test2");

        //Testing with single req
        String expectedResult = "test0";
        String actualResult = pService.getClusterTitle("TEST", "0");
        assertEquals(expectedResult, actualResult);

        //Testing with OR req
        expectedResult = "test0 / test1";
        actualResult = pService.getClusterTitle("TEST", "0,1");
        assertEquals(expectedResult, actualResult);

        //Testing with AND req
        expectedResult = "test0 + test1";
        actualResult = pService.getClusterTitle("TEST", "0;1");
        assertEquals(expectedResult, actualResult);


        //Testing with req that contains OR and AND
        expectedResult = "test0 + test1 / test2";
        actualResult = pService.getClusterTitle("TEST", "0;1,2");
        assertEquals(expectedResult, actualResult);

        expectedResult = "test0 + test1 / test2 + test1";
        actualResult = pService.getClusterTitle("TEST", "0;1,2;1");
        assertEquals(expectedResult, actualResult);

        expectedResult = "test1 / test0 + test1 / test2";
        actualResult = pService.getClusterTitle("TEST", "1,0;1,2");
        assertEquals(expectedResult, actualResult);
    }
}
