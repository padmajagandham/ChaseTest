package com.test.apiservice.step_definitions;

import com.google.gson.Gson;
import com.test.apiservice.endpoints.Users;
import com.test.apiservice.models.UserDataModel;
import com.test.apiservice.utils.World;
import io.cucumber.java.en.Given;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.test.apiservice.utils.Helpers.getRamdomInt;
import static com.test.apiservice.utils.Hooks.sc;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserStepDefs {

    World world;
    Users usersEndpoint = new Users();
    Gson gson = new Gson();
    Logger log;

    public UserStepDefs(World world){
        this.world = world;
        log = LoggerFactory.getLogger(UserStepDefs.class);
    }

    @Given("a user exists on the social networking site")
    public void aUserExistsOnTheSocialNetworkingSite() {
        Response res = usersEndpoint.getAllUsers(world.getRs());

        assertEquals(res.statusCode(),200);

        UserDataModel[] listOfUsers = gson.fromJson(res.getBody().asString(), UserDataModel[].class);
        assertTrue("No users to post content on the Messaging site", listOfUsers.length>0);

        int x = getRamdomInt(listOfUsers.length);

        log.debug("Select a random user from the available list:"+x);
        sc.log("Selected this user from the available list for the test"+"\n"+gson.toJson(listOfUsers[x]));
        world.setUserDataModel(listOfUsers[x]);
    }
}
