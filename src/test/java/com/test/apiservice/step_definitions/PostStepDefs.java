package com.test.apiservice.step_definitions;

import com.google.gson.Gson;
import com.test.apiservice.endpoints.Comments;
import com.test.apiservice.endpoints.Posts;
import com.test.apiservice.models.CommentDataModel;
import com.test.apiservice.models.PostDataModel;
import com.test.apiservice.models.UserDataModel;
import com.test.apiservice.utils.World;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.test.apiservice.utils.Helpers.getRamdomInt;
import static com.test.apiservice.utils.Hooks.sc;
import static org.junit.Assert.*;

public class PostStepDefs {

    World world;
    Posts postEndpoint = new Posts();
    Comments commentsEndPoint = new Comments();
    Gson gson = new Gson();
    PostDataModel postRequest;
    Response postResponse;
    Logger log;

    public PostStepDefs(World world){
        this.world = world;
        log = LoggerFactory.getLogger(PostStepDefs.class);
    }

    /** constructs a "post" request json with dummy data and performs POST to /posts endpoint
     response is saved to postResponse variable to be asserted in Then steps **/
    @When("a user submits a post with random title and body")
    public void a_user_submits_a_post_with_necessary_and() {
        postRequest = PostDataModel.builder()
                .userId(world.getUserDataModel().getId())
                .title("some title")
                .body("some body")
                .build();

        sc.log("JSON Req"+gson.toJson(postRequest));
        log.debug("JSON Req"+gson.toJson(postRequest));

        postResponse = postEndpoint
                .submitAPostForAUser(world.getRs(), postRequest);
        world.setPostDataModel(gson.fromJson(postResponse.getBody().asString(), PostDataModel.class));
    }

    /** constructs a "post" request json with dummy data and performs POST to /posts endpoint
     and also asserts for success HTTP code **/
    @Given("above user successfully submits a post")
    public void above_user_successfully_submits_a_post() {
        a_user_submits_a_post_with_necessary_and();
        assertEquals(201,postResponse.statusCode());
        world.setPostDataModel(gson.fromJson(postResponse.getBody().asString(), PostDataModel.class));
    }

    /** Retrieves a random postId from the list of available posts and sets the post detail in test context
     ready to be used by When and Then steps **/
    @Given("has already posted on the social networking site")
    public void has_already_posted_on_the_social_networking_site() {
       //Get all posts by userId and choose a random one to modify
        Response res = postEndpoint.getAllPostsByAUser(world.getRs(), world.getUserDataModel().getId());
        PostDataModel[] listOfPostsByUser = gson.fromJson(res.getBody().asString(), PostDataModel[].class);

        int x = getRamdomInt(listOfPostsByUser.length);

        sc.log("Selected this post of test user"+"\n"+gson.toJson(listOfPostsByUser[x]));
        world.setPostDataModel(listOfPostsByUser[x]);
    }

    /** constructs a post request with dummy data and modifies the post set in test context
     world. world.setPostDataModel() must have been done before calling this step. submits the req
     to PUT /posts endpoint and saves response to postResponse **/
    @When("the user tries to modify above post's title and body")
    public void the_user_tries_to_modify_above_post_s_title_and_body() {
        postRequest = PostDataModel.builder()
                .title("some modified title")
                .body("some modified body")
                .build();
        log.debug("JSON Req:"+gson.toJson(postRequest));
        sc.log("JSON Req:"+gson.toJson(postRequest));

        postResponse = postEndpoint
                .modifyAPostByAUser(world.getRs(),postRequest,world.getPostDataModel().getId());

    }

    /**Asserts the postResponse set in When Steps, for GET, PUT, POST and DELETE operations**/
    @Then("^the post must be (created|modified|deleted|returned) successfully$")
    public void the_post_must_be_created_successfully(String flag) {
        PostDataModel post = gson.fromJson(postResponse.getBody().asString(), PostDataModel.class);

        log.debug("JSON Res"+gson.toJson(post));
        sc.log("JSON Res"+gson.toJson(post));

        switch (flag) {
            case "created":
                assertEquals(201, postResponse.statusCode());
                assertEquals(world.getUserDataModel().getId(), post.getUserId());
                break;
            default:
                assertEquals(200, postResponse.statusCode());
                break;
        }

        if(!flag.equals("returned") && !flag.equals("deleted")){
//       If a Json schema is available, make sure the response body is asserted against it so that contract errors can be caught early

            assertNotNull(post.getId());
            assertEquals(postRequest.getBody(), post.getBody());
            assertEquals(postRequest.getTitle(), post.getTitle());

//        add additional assertions to verify in DB or /posts response to make sure the newly added or modified post is present.
//        But currently this cannot be done as a new entry is not being added or an existing entry is not being modified in the list of existing posts
        }
    }

    /**submits the postReq to /posts endpoint directly as read from the cucumber data table, which here is transformed to postReq
     and saves response to 'postResponse' **/
    @When("a user submits a post with below data")
    public void a_user_submits_a_post_with_below_data(List<PostDataModel> postReq) {
        world.setPostDataModel(postReq.get(0));
        world.setUserDataModel(UserDataModel.builder().id(postReq.get(0).getUserId()).build());
        postRequest = postReq.get(0);
        postResponse = postEndpoint
                .submitAPostForAUser(world.getRs(), postRequest);
    }

    /**asserts the error response when an invalid req payload is submitted or when the post is not found
     *Note: 'postResponse' must be set in When steps **/
    @Then("^the post must not be (returned|created)$")
    public void the_post_must_not_be_created_successfully(String flag) {
        if(flag.equals("Created"))
            assertEquals(400, postResponse.statusCode()); //additional DB checks can be done if available to make sure no record is created
        else
            assertEquals(404, postResponse.statusCode());
    }

    /** world.setPostDataModel() must have been setup before calling this step. submits the req
    to DELETE /posts endpoint and saves response to postResponse **/
    @When("the user tries to delete above post")
    public void the_user_tries_to_delete_above_post() {
        log.debug("Trying to delete post with Id :"+world.getPostDataModel().getId());
        sc.log("Trying to delete post with Id :"+world.getPostDataModel().getId());
        postResponse = postEndpoint.deleteAPostMadeByAUser(world.getRs(),world.getPostDataModel().getId());
    }

    /** world.setPostDataModel() must have been setup before calling this step. submits a request to GET /posts/<id> with a valid id or -1. **/
    @When("^the post is queried by (invalid |)id$")
    public void the_post_is_queried_by_id(String flag) {
        if(flag.contains("invalid")) {
            log.debug("Trying to query post with Id : -1");
            sc.log("Trying to query post with Id : -1");
            postResponse = postEndpoint.getAPostsById(world.getRs(), -1);
        }
        else{
            log.debug("Trying to query post with Id :"+world.getPostDataModel().getId());
            sc.log("Trying to query post with Id :"+world.getPostDataModel().getId());
            postResponse = postEndpoint.getAPostsById(world.getRs(),world.getPostDataModel().getId());
        }
    }

    /** world.setUserDataModel() must have been setup before calling this step. submits a request to GET /posts?userId=<id> with a valid id or -1. **/
    @When("^the post is queried by (invalid |)userId$")
    public void the_post_is_queried_by_user_id(String flag) {
        if(!flag.contains("invalid")) {
            log.debug("Trying to retrieve posts for userId:" + world.getUserDataModel().getId());
            sc.log("Trying to retrieve posts for userId:" + world.getUserDataModel().getId());
            //Get all posts by userId and choose a random one to modify
            postResponse = postEndpoint.getAllPostsByAUser(world.getRs(), world.getUserDataModel().getId());
            world.setPostResponse(postResponse);
        }
        else {
            log.debug("Trying to retrieve posts for userId:-1");
            sc.log("Trying to retrieve posts for userId:-1");
            postResponse = postEndpoint.getAllPostsByAUser(world.getRs(), -1);
        }
    }

    /** asserts that the posts retrieved from GET /posts?userId=<userId> endpoint or GET /users/<userId>/posts
     are retrieved correctly. 'postResponse' must be set test context in When steps.
     **/
    @Then("all posts made by the user are returned successfully")
    public void the_all_posts_made_by_the_user_are_returned_successfully() {
        assertEquals(world.getPostResponse().getStatusCode(),200);
        PostDataModel[] listOfPostsByUser = gson.fromJson(world.getPostResponse().getBody().asString(), PostDataModel[].class);

        //assert by retrieving all available posts and filtering them by userId
        //or can also be asserted against a DB using a query when available
        Response res = postEndpoint.getAllPosts(world.getRs());
        PostDataModel[] allPosts = gson.fromJson(res.getBody().asString(), PostDataModel[].class);
        List<PostDataModel> list = Arrays.stream(allPosts).filter(x->x.getUserId().equals(world.getUserDataModel().getId())).collect(Collectors.toList());

        assertEquals(list, Arrays.asList(listOfPostsByUser));
    }

    /** asserts that no posts retrieved from GET /posts?userId=<userId> . 'postResponse' must be set in When steps.
     **/
    @Then("no posts are returned")
    public void no_posts_are_returned() {
        assertEquals(postResponse.getStatusCode(),200);
        PostDataModel[] listOfPostsByUser = gson.fromJson(postResponse.getBody().asString(), PostDataModel[].class);
        assertEquals(0,listOfPostsByUser.length);
    }

    /**world.setPostDataModel() must have been setup before calling this step. submits a request to GET /posts/<postId>/comments
     * and saves 'postResponse' in test context **/
    @When("the user wants to retrieve all comments for a post")
    public void the_user_wants_to_retrieve_all_comments_for_a_post() {
        sc.log("Trying to retrieve comments for post:"+world.getPostDataModel().getId());
        postResponse = postEndpoint.getAllCommentsForAPost(world.getRs(),world.getPostDataModel().getId());
        world.setPostResponse(postResponse);
    }

    /** asserts that the comments retrieved from GET /posts/<postId>/comments endpoint or GET /comments?postId=<postId>
     are retrieved correctly. 'postResponse' must be set test context in When steps.
     **/
    @Then("all the comments for selected post are returned successfully")
    public void all_the_comments_for_selected_post_are_returned_successfully() {
        assertEquals(world.getPostResponse().getStatusCode(),200);
        CommentDataModel[] listOfCommentsForAPost = gson.fromJson(world.getPostResponse().getBody().asString(), CommentDataModel[].class);
//        CommentDataModel[] listOfCommentsForAPost = gson.fromJson(postResponse.getBody().asString(), CommentDataModel[].class);

        //assert by retrieving all available posts and filtering them by userId
        //or can also be asserted against a DB using a query when available
        Response res = commentsEndPoint.getAllComments(world.getRs());
        CommentDataModel[] allComments = gson.fromJson(res.getBody().asString(), CommentDataModel[].class);
        List<CommentDataModel> list = Arrays.stream(allComments).filter(x->x.getPostId().equals(world.getPostDataModel().getId())).collect(Collectors.toList());

        assertEquals(list, Arrays.asList(listOfCommentsForAPost));
    }

    /** submits a request to PATCH /post/<postId> endpoint and saves to 'postResponse'**/
    @When("the user tries to patch above post's title")
    public void the_user_tries_to_patch_above_post_s_title() {
        postRequest = PostDataModel.builder()
                .title("some modified title")
                .build();
        log.debug("JSON Req:"+gson.toJson(postRequest));
        sc.log("JSON Req:"+gson.toJson(postRequest));

        postResponse = postEndpoint
                .patchAPost(world.getRs(),postRequest,world.getPostDataModel().getId());
    }

    /** asserts that the PATCH request only updates the specified field and other values remain unchanged**/
    @Then("the post's must be modified successfully and all other values must remain unchanged")
    public void the_post_s_must_be_modified_successfully_and_all_other_values_must_remain_unchanged() {

        assertEquals(postResponse.getStatusCode(),200);
        PostDataModel post = gson.fromJson(postResponse.getBody().asString(), PostDataModel.class);

        log.debug("JSON Req:"+gson.toJson(post));
        sc.log("JSON Req:"+gson.toJson(post));

        assertEquals(world.getPostDataModel().getId(),post.getId());
        assertEquals(world.getPostDataModel().getUserId(),post.getUserId());
        assertEquals(world.getPostDataModel().getBody(),post.getBody());
        assertEquals(postRequest.getTitle(),post.getTitle());
    }



}
