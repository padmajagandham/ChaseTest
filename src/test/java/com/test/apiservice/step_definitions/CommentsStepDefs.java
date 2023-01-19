package com.test.apiservice.step_definitions;

import com.google.gson.Gson;
import com.test.apiservice.endpoints.Comments;
import com.test.apiservice.endpoints.Users;
import com.test.apiservice.models.CommentDataModel;
import com.test.apiservice.utils.Hooks;
import com.test.apiservice.utils.World;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.test.apiservice.utils.Hooks.sc;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommentsStepDefs {

    World world;
    Users usersEndpoint = new Users();
    Gson gson = new Gson();
    Comments commentsEndPoint = new Comments();
    CommentDataModel commentRequest;
    Response commentResponse;

    public CommentsStepDefs(World world){
        this.world = world;
    }

    @When("any random user tries to add a comment to above post")
    public void the_user_tries_to_add_a_comment_to_above_post() {
        commentRequest  = CommentDataModel.builder().postId(world.getPostDataModel().getId())
                .name("some random name")
                .email("somerandom@gmail.com")
                .body("this is a brand new comment").build();
        sc.log("JSON Req:"+gson.toJson(commentRequest));

        commentResponse = commentsEndPoint.submitACommentForAPost(world.getRs(),commentRequest);
    }

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
//      ToDo:Json Schema validation
            assertNotNull(comment.getId());
            assertEquals(commentRequest.getBody(), comment.getBody());
            assertEquals(commentRequest.getEmail(), comment.getEmail());
            assertEquals(commentRequest.getName(), comment.getName());

//        add additional assertions to verify in DB or /comments response to make sure the newly added or modified post is present.
//        But currently this cannot be done as a new entry is not being added or an existing entry is not being modified in the list of existing comments
        }
    }


}
