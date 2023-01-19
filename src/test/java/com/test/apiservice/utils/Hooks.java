package com.test.apiservice.utils;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

public class Hooks {

    public static Scenario sc;

    @Before
    public void setup(Scenario scenario){
//        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        sc = scenario;
    }

//    @After
//    public void cleanup(Scenario scenario){
//        //Currently as the data is locked down there is no need to delete any newly created data by the test
//    }
}
