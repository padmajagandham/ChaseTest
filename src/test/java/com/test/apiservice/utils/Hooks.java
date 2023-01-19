package com.test.apiservice.utils;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    public static Scenario sc;

    @Before
    public void setup(Scenario scenario){
        Logger logger = LoggerFactory.getLogger(Hooks.class);
        if(logger.isDebugEnabled())
            RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        sc = scenario;
    }

//    @After
//    public void cleanup(Scenario scenario){
//        //Currently as the data is locked down there is no need to delete any newly created data by the test
//    }
}
