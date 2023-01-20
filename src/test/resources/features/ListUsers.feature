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
  Scenario: Verify that the address details can be modified for an existing user using PATCH /users/<userId>
    Given a user exists on the social networking site
    When new address details are submitted to PATCH /users/<userId> endpoint
    Then address details are modified successfully and all other details remain unchanged

  @ignore @to_be_automated
  Scenario: Verify all the details of the existing user are replaced using PUT /users/<userId>
    Given a user exists on the social networking site
    When a new user request is submitted with all mandatory data fields , like "email","name" etc
    Then the entire user resource is replaced by above details and none of the old details are persisted

  @ignore @to_be_automated
  Scenario: Verify user creation must fail when mandatory details like "email" are malformed or missing
    When a new user request is submitted with mandatory data like "email" missing
    Then user must not be created successfully

  @ignore @to_be_automated
  Scenario: Verify GET /users/<userId> returns HTTP 404 Not found response when userId is not found
    When GET /users/<userId> is queried using an invalid id
    Then a HTTP 404 response must be returned

