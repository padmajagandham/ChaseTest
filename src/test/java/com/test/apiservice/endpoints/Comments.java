package com.test.apiservice.endpoints;

import com.google.gson.Gson;
import com.test.apiservice.models.CommentDataModel;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Comments {
    private String comments = "/comments";
    private String specificComment= "/comments/<id>";
    private String allCommentsForAPost = "/comments?postId=<postId>";
    Gson gson = new Gson();

    public Response submitACommentForAPost(RequestSpecification rs, CommentDataModel req){
        return given(rs).body(req).post(comments);
    }

    public Response getListOfCommentsForAPost(RequestSpecification rs, int postId){
        return given(rs).get(allCommentsForAPost.replace("<postId>",String.valueOf(postId)));
    }

    public Response getASpecificComment(RequestSpecification rs,int commentId){
        return given(rs).get(specificComment.replace("<id>",String.valueOf(commentId)));
    }

    public Response getAllComments(RequestSpecification rs){
        return given(rs).get(comments);
    }
//
//    public Response modifyAComment(RequestSpecification rs){
//
//    }
//
//    public Response deleteAComment(RequestSpecification rs){
//
//    }
}
