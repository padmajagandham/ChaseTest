package com.test.apiservice.endpoints;

import com.google.gson.Gson;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Users {
    private String users = "/users";
    private String allPostsForAUser = "/users/<userId>/posts";
    private String specificUser = "/users/<userId>";

    public Response getAllUsers(RequestSpecification rs){
        return given(rs).get(users);
    }

    public Response getAllPostsMadeByUser(RequestSpecification rs, int userId){
        return given(rs).get(allPostsForAUser.replace("<userId>",String.valueOf(userId)));
    }

    public Response getASpecificUser(RequestSpecification rs, int userId){
        return given(rs).get(specificUser.replace("<userId>",String.valueOf(userId)));
    }
}
