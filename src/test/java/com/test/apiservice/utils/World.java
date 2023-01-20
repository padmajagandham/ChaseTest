package com.test.apiservice.utils;

import com.test.apiservice.models.CommentDataModel;
import com.test.apiservice.models.PostDataModel;
import com.test.apiservice.models.UserDataModel;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;


//This is the test context to be shared between different step definition files
@Data
public class World {
    private RequestSpecification rs;
    private UserDataModel userDataModel;
    private PostDataModel postDataModel;
    private CommentDataModel commentDataModel;
    private Response postResponse;

    public World(){

        //Set up the request specification here
        this.rs = new RequestSpecBuilder()
                .setBaseUri((System.getProperty("baseURL") != null)?System.getProperty("baseURL"):"https://jsonplaceholder.typicode.com") //Read this from a command line
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
}
