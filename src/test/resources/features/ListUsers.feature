@regression
Feature: List users

  Scenario: Verify all users can be retrieved successfully
    When a GET users request is submitted
    Then all the users available on the system are returned successfully

  Scenario: Verify a specific user details can be retrieved using endpoint /user/<userId>
    Given a user exists on the social networking site
    When a GET request is submitted to retrieve above user details
    Then all the details of the specific user are returned

  Scenario: Verify all posts for a user are retrieved successfully using endpoint /users/<userId>/posts
    Given a user exists on the social networking site
    When a GET request is submitted to retrieve all posts of above user
    Then all posts made by the user are returned successfully

  @ignore @to_be_automated
  Scenario: Verify a new user can be added onto the system
    When a new user create request is submitted with valid details
    Then the user is created successfully

  @ignore @to_be_automated
  Scenario: Verify that the address details can be modified for an existing user using PATCH /users

  @ignore @to_be_automated
  Scenario: Verify all the details of the existing user are replaced using PUT /users

  @ignore @to_be_automated
  Scenario: Verify user creation must fail when mandatory details like "email" are malformed or missing

