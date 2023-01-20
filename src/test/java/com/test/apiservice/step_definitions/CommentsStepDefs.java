package com.test.apiservice.step_definitions;

import com.google.gson.Gson;
import com.test.apiservice.endpoints.Comments;
import com.test.apiservice.endpoints.Users;
import com.test.apiservice.models.CommentDataModel;
import com.test.apiservice.utils.World;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.test.apiservice.utils.Helpers.getRamdomInt;
import static com.test.apiservice.utils.Hooks.sc;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommentsStepDefs {

    World world;
    Gson gson = new Gson();
    Comments commentsEndPoint = new Comments();
    CommentDataModel commentRequest;
    Response commentResponse;

    public CommentsStepDefs(World world){
        this.world = world;
    }

    /**submits a comment to POST /comment endpoint and saves the response to 'commentResponse'**/
    @When("any random user tries to add a comment to above post")
    public void the_user_tries_to_add_a_comment_to_above_post() {
        commentRequest  = CommentDataModel.builder().postId(world.getPostDataModel().getId())
                .name("some random name")
                .email("somerandom@gmail.com")
                .body("this is a brand new comment").build();
        sc.log("JSON Req:"+gson.toJson(commentRequest));

        commentResponse = commentsEndPoint.submitACommentForAPost(world.getRs(),commentRequest);
    }

    /**asserts that the 'commentResponse' is as expected. **/
    @Then("^the comment must be (added|deleted|modified|returned) successfully$")
    public void the_comment_must_be_added_successfully(String flag) {
        CommentDataModel comment = gson.fromJson(commentResponse.getBody().asString(),CommentDataModel.class);

        sc.log("JSON Res:"+gson.toJson(comment));

        switch (flag) {
            case "added":
                assertEquals(201, commentResponse.statusCode());
                assertEquals(world.getPostDataModel().getId(), comment.getPostId());
                break;
            default:
                assertEquals(200, commentResponse.statusCode());
                break;
        }

        if(!flag.equals("returned") && !flag.equals("deleted")){
//      Json Schema validation is recommended to be done at this stage if available
            assertNotNull(comment.getId());
            assertEquals(commentRequest.getBody(), comment.getBody());
            assertEquals(commentRequest.getEmail(), comment.getEmail());
            assertEquals(commentRequest.getName(), comment.getName());

//        add additional assertions to verify in DB or /comments response to make sure the newly added or modified post is present.
//        But currently this cannot be done as a new entry is not being added or an existing entry is not being modified in the list of existing comments
        }
        //for deleted and Get queries , additional assertions must have to be done against DB when available
    }

    /**queries GET /comment?postId=<postId> endpoint and saves the response to 'commentResponse' also sets the test context**/
    @When("comments endpoint is queried by above post id")
    public void comments_endpoint_is_queried_by_above_post_id() {
        sc.log("This is the post Id used to query /comments?postId="+world.getPostDataModel().getId());
        commentResponse = commentsEndPoint.getListOfCommentsForAPost(world.getRs(),world.getPostDataModel().getId());
        world.setPostResponse(commentResponse);
    }

    /**retrieves the set of comments against a selected post and selects a random one from the list and sets test context**/
    @Given("has comments against above post")
    public void has_comments_against_above_post() {
        comments_endpoint_is_queried_by_above_post_id();
        assertEquals(commentResponse.getStatusCode(),200);
        CommentDataModel[] listOfCommentsForAPost = gson.fromJson(commentResponse.getBody().asString(), CommentDataModel[].class);

        int x = getRamdomInt(listOfCommentsForAPost.length);
        world.setCommentDataModel(listOfCommentsForAPost[x]);
        sc.log("This is the comment data selected for the test"+gson.toJson(listOfCommentsForAPost[x]));
    }

    /**queries GET /comment with a specific or invalid comment id and saves to 'commentsResponse'**/
    @When("^comments endpoint is queried by a (|invalid )comment id$")
    public void comments_endpoint_is_queried_by_a_comment_id(String flag) {
        if(!flag.contains("invalid")) {
            sc.log("This is the comment Id used to query /comments/"+world.getCommentDataModel().getId());
            commentResponse = commentsEndPoint.getASpecificComment(world.getRs(), world.getCommentDataModel().getId());
        }
        else {
            sc.log("This is the comment Id used to query /comments/-1");
            commentResponse = commentsEndPoint.getASpecificComment(world.getRs(), -1);
        }
    }

    /**asserts GET /comment returns 404 when targeted comment is not found**/
    @Then("the comment must not be returned successfully")
    public void the_comment_must_not_be_returned_successfully() {
        assertEquals(commentResponse.getStatusCode(),404);
        sc.log("JSON Res"+ commentResponse.getBody().asString());
        assertEquals(commentResponse.getBody().asString(),"{}");
    }


}
