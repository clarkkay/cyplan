package com.km207.cyplan;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
public class testingTestApiController {
@LocalServerPort
    @Before
    public void setUp(){
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testEndpointWorking(){
        Response response = RestAssured.given().when().get("/test");
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
    }
}
