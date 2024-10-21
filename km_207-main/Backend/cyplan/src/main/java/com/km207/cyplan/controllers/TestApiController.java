package com.km207.cyplan.controllers;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "Test Controller", description = "a controller that contains a test endpoint we use to tell if our app's backend is running properly")
@RestController
public class TestApiController {
    @GetMapping("/test")
    public String testEndpoint(){ return "Test end point is working";}
}
