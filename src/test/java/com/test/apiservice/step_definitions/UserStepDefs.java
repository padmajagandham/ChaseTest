package com.test.apiservice.step_definitions;

import com.google.gson.Gson;
import com.test.apiservice.endpoints.Users;
import com.test.apiservice.models.UserDataModel;
import com.test.apiservice.utils.World;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.test.apiservice.utils.Helpers.getRamdomInt;
import static com.test.apiservice.utils.Hooks.sc;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserStepDefs {

    World world;
    Users usersEndpoint = new Users();
    Gson gson = new Gson();
    Logger log;
    Response usersResponse;
    UserDataModel userReq;

    public UserStepDefs(World world){
        this.world = world;
        log = LoggerFactory.getLogger(UserStepDefs.class);
    }

    /** Retrieves a user from the list of users and saves it in test context for other steps to use **/
    @Given("a user exists on the social networking site")
    public void aUserExistsOnTheSocialNetworkingSite() {
//        UserDataModel.builder().address(new UserDataModel.Address())
        usersResponse = usersEndpoint.getAllUsers(world.getRs());

        assertEquals(usersResponse.statusCode(),200);

        UserDataModel[] listOfUsers = gson.fromJson(usersResponse.getBody().asString(), UserDataModel[].class);
        assertTrue("No users to post content on the Messaging site", listOfUsers.length>0);

        int x = getRamdomInt(listOfUsers.length);

        log.debug("Select a random user from the available list:"+x);
        sc.log("Selected this user from the available list for the test"+"\n"+gson.toJson(listOfUsers[x]));
        world.setUserDataModel(listOfUsers[x]);
    }

    /** submits GET /users/<userId> request for the selected user in test context **/
    @When("a GET request is submitted to retrieve above user details")
    public void a_get_request_is_submitted_to_retrieve_above_user_details() {
        usersResponse = usersEndpoint.getASpecificUser(world.getRs(),world.getUserDataModel().getId());
    }

    /** asserts that all the details of the specific user are returned correctly**/
    @Then("all the details of the specific user are returned")
    public void all_the_details_of_the_specific_user_are_returned() {
        assertEquals(usersResponse.statusCode(),200);
        UserDataModel actual = gson.fromJson(usersResponse.getBody().asString(), UserDataModel.class);

        Response response = usersEndpoint.getAllUsers(world.getRs());
        assertEquals(response.statusCode(),200);

        UserDataModel[] listOfUsers = gson.fromJson(response.getBody().asString(), UserDataModel[].class);
        UserDataModel expected = Arrays.stream(listOfUsers).filter(x->x.getId().equals(world.getUserDataModel().getId())).findFirst().get();

        assertEquals(expected,actual);

        //additional assertions must be included for schema when available and db validations
    }

    /**submits GET /users/<userId>/posts request and sets the test context**/
    @When("a GET request is submitted to retrieve all posts of above user")
    public void a_get_request_is_submitted_to_retrieve_all_posts_of_above_user() {
        usersResponse = usersEndpoint.getAllPostsMadeByUser(world.getRs(),world.getUserDataModel().getId());
        world.setPostResponse(usersResponse);
    }

    /**submits GET /users/<userId> request  **/
    @When("a GET users request is submitted")
    public void a_get_users_request_is_submitted() {
        usersResponse = usersEndpoint.getAllUsers(world.getRs());
    }

    /** asserts that tht http status code is 200 for GET /users and users list is non-empty **/
    @Then("all the users available on the system are returned successfully")
    public void all_the_users_available_on_the_system_are_returned_successfully() {
        assertEquals(usersResponse.statusCode(),200);

        UserDataModel[] listOfUsers = gson.fromJson(usersResponse.getBody().asString(), UserDataModel[].class);
        assertTrue("No users to post content on the Messaging site", listOfUsers.length>0);

        //additional assertions must be included for schema when available and db validations
    }

}
