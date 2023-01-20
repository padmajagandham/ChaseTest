package com.test.apiservice.endpoints;

import com.google.gson.Gson;
import com.test.apiservice.models.PostDataModel;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class Posts {
    private String posts = "/posts";
    private String specificPost = "/posts/<id>";
    private String allPostsByUserId = "/posts?userId=<userId>";
    private String allCommentsForaPost = "/posts/<id>/comments";
    Gson gson = new Gson();

    public Response getAllPosts(RequestSpecification rs){
        return given(rs).get(posts);
    }

    public Response getAPostsById(RequestSpecification rs,int id){
        return given(rs).get(specificPost.replace("<id>",String.valueOf(id)));
    }

    public Response getAllCommentsForAPost(RequestSpecification rs,int id){
        return given(rs).get(allCommentsForaPost.replace("<id>",String.valueOf(id)));
    }

    public Response getAllPostsByAUser(RequestSpecification rs, int userId){
        return given(rs).get(allPostsByUserId.replace("<userId>",String.valueOf(userId)));
    }

    public Response submitAPostForAUser(RequestSpecification rs, PostDataModel req){
//        QueryableRequestSpecification queryable = SpecificationQuerier.query(rs);
//        String headerValue = queryable.getHeaders().getValue("Token");
//        System.out.println(headerValue);

        return given(rs).body(req).post(posts);
    }

    public Response modifyAPostByAUser(RequestSpecification rs, PostDataModel req, int id){
        return given(rs).body(req).put(specificPost.replace("<id>",String.valueOf(id)));
    }

    public Response patchAPost(RequestSpecification rs, PostDataModel req, int id){
        return given(rs).body(req).patch(specificPost.replace("<id>",String.valueOf(id)));
    }

    public Response deleteAPostMadeByAUser(RequestSpecification rs, int postId){
        return given(rs).delete(specificPost.replace("<id>",String.valueOf(postId)));
    }
}
