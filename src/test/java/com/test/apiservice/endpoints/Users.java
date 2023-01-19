package com.test.apiservice.endpoints;

import com.google.gson.Gson;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Users {
    private String users = "/users";
    private String allPostsForAUser = "/users/<userId>/posts";
    Gson gson = new Gson();

    public Response getAllUsers(RequestSpecification rs){
        return given(rs).get(users);
    }

    public Response getAllPostsMadeByUser(RequestSpecification rs, int userId){
        return given(rs).get(allPostsForAUser.replace("<userId>",String.valueOf(userId)));
    }
}
