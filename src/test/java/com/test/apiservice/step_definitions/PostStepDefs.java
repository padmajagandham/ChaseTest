package com.test.apiservice.step_definitions;

import com.google.gson.Gson;
import com.test.apiservice.endpoints.Comments;
import com.test.apiservice.endpoints.Posts;
import com.test.apiservice.models.CommentDataModel;
import com.test.apiservice.models.PostDataModel;
import com.test.apiservice.utils.Hooks;
import com.test.apiservice.utils.World;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

    @When("a user submits a post with random title and body")
    public void a_user_submits_a_post_with_necessary_and() {
        postRequest = PostDataModel.builder()
                .userId(world.getUserDataModel().getId())
                .title("some title")
                .body("some body")
                .build();

        sc.log("JSON Req"+gson.toJson(postRequest));

        postResponse = postEndpoint
                .submitAPostForAUser(world.getRs(), postRequest);
        world.setPostDataModel(gson.fromJson(postResponse.getBody().asString(), PostDataModel.class));
    }

    @Given("above user successfully submits a post")
    public void above_user_successfully_submits_a_post() {
        a_user_submits_a_post_with_necessary_and();
        assertEquals(201,postResponse.statusCode());
        world.setPostDataModel(gson.fromJson(postResponse.getBody().asString(), PostDataModel.class));
    }

    @Given("has already posted on the social networking site")
    public void has_already_posted_on_the_social_networking_site() {
       //Get all posts by userId and choose a random one to modify
        Response res = postEndpoint.getAllPostsByAUser(world.getRs(), world.getUserDataModel().getId());
        PostDataModel[] listOfPostsByUser = gson.fromJson(res.getBody().asString(), PostDataModel[].class);
//        Random rand = new Random();
//        int x = rand.nextInt(listOfPostsByUser.length);

        int x = getRamdomInt(listOfPostsByUser.length);

        sc.log("Selected this post of test user"+"\n"+gson.toJson(listOfPostsByUser[x]));
        world.setPostDataModel(listOfPostsByUser[x]);
    }

    @When("the user tries to modify above post's title and body")
    public void the_user_tries_to_modify_above_post_s_title_and_body() {
        postRequest = PostDataModel.builder()
                .title("some modified title")
                .body("some modified body")
                .build();
        System.out.println(gson.toJson(postRequest));
        sc.log("JSON Req:"+gson.toJson(postRequest));

        postResponse = postEndpoint
                .modifyAPostByAUser(world.getRs(),postRequest,world.getPostDataModel().getId());

    }

    @Then("^the post must be (created|modified|deleted|returned) successfully$")
    public void the_post_must_be_created_successfully(String flag) {
        PostDataModel post = gson.fromJson(postResponse.getBody().asString(), PostDataModel.class);

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
//      ToDo:Json Schema validation

            assertNotNull(post.getId());
            assertEquals(postRequest.getBody(), post.getBody());
            assertEquals(postRequest.getTitle(), post.getTitle());

//        add additional assertions to verify in DB or /posts response to make sure the newly added or modified post is present.
//        But currently this cannot be done as a new entry is not being added or an existing entry is not being modified in the list of existing posts
        }
    }

    @When("a user submits a post with below data")
    public void a_user_submits_a_post_with_below_data(List<PostDataModel> postReq) {
        postRequest = postReq.get(0);
        postResponse = postEndpoint
                .submitAPostForAUser(world.getRs(), postRequest);
    }

    @Then("^the post must not be (returned|created)$")
    public void the_post_must_not_be_created_successfully(String flag) {
        if(flag.equals("Created"))
            assertEquals(400, postResponse.statusCode());
        else
            assertEquals(404, postResponse.statusCode());
    }

    @When("the user tries to delete above post")
    public void the_user_tries_to_delete_above_post() {
        sc.log("Trying to delete post with Id :"+world.getPostDataModel().getId());
        postResponse = postEndpoint.deleteAPostMadeByAUser(world.getRs(),world.getPostDataModel().getId());
    }

    @When("^the post is queried by (invalid |)id$")
    public void the_post_is_queried_by_id(String flag) {
        if(flag.contains("invalid")) {
            sc.log("Trying to query post with Id : -1");
            postResponse = postEndpoint.getAPostsById(world.getRs(), -1);
        }
        else{
            sc.log("Trying to query post with Id :"+world.getPostDataModel().getId());
            postResponse = postEndpoint.getAPostsById(world.getRs(),world.getPostDataModel().getId());
        }
    }

    @When("^the post is queried by (invalid |)userId$")
    public void the_post_is_queried_by_user_id(String flag) {
        if(!flag.contains("invalid")) {
            sc.log("Trying to retrieve posts for userId:" + world.getUserDataModel().getId());
            //Get all posts by userId and choose a random one to modify
            postResponse = postEndpoint.getAllPostsByAUser(world.getRs(), world.getUserDataModel().getId());
        }
        else {
            sc.log("Trying to retrieve posts for userId:-1");
            postResponse = postEndpoint.getAllPostsByAUser(world.getRs(), -1);
        }
    }
    @Then("the all posts made by the user are returned successfully")
    public void the_all_posts_made_by_the_user_are_returned_successfully() {
        PostDataModel[] listOfPostsByUser = gson.fromJson(postResponse.getBody().asString(), PostDataModel[].class);

        //assert by retrieving all available posts and filtering them by userId
        //or can also be asserted against a DB using a query when available
        Response res = postEndpoint.getAllPosts(world.getRs());
        PostDataModel[] allPosts = gson.fromJson(res.getBody().asString(), PostDataModel[].class);
        List<PostDataModel> list = Arrays.stream(allPosts).filter(x->x.getUserId().equals(world.getUserDataModel().getId())).collect(Collectors.toList());

        assertEquals(list, Arrays.asList(listOfPostsByUser));
    }

    @Then("no posts are returned")
    public void no_posts_are_returned() {
        PostDataModel[] listOfPostsByUser = gson.fromJson(postResponse.getBody().asString(), PostDataModel[].class);
        assertEquals(0,listOfPostsByUser.length);
    }

    @When("the user wants to retrieve all comments for a post")
    public void the_user_wants_to_retrieve_all_comments_for_a_post() {
        sc.log("Trying to retrieve comments for post:"+world.getPostDataModel().getId());
        postResponse = postEndpoint.getAllCommentsForAPost(world.getRs(),world.getPostDataModel().getId());
    }

    @Then("all the comments for selected post are returned successfully")
    public void all_the_comments_for_selected_post_are_returned_successfully() {
        CommentDataModel[] listOfCommentsForAPost = gson.fromJson(postResponse.getBody().asString(), CommentDataModel[].class);

        //assert by retrieving all available posts and filtering them by userId
        //or can also be asserted against a DB using a query when available
        Response res = commentsEndPoint.getAllComments(world.getRs());
        CommentDataModel[] allComments = gson.fromJson(res.getBody().asString(), CommentDataModel[].class);
        List<CommentDataModel> list = Arrays.stream(allComments).filter(x->x.getPostId().equals(world.getPostDataModel().getId())).collect(Collectors.toList());

        assertEquals(list, Arrays.asList(listOfCommentsForAPost));
    }


}
