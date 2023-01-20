@regression
Feature: Create/update/delete/get a Post

#  Assumptions: From the response of GET /posts or GET /posts/<postId>, it can be inferred that a post has a reference to unique userId's
#  so it can be safely assumed that a user has to exist in the Social Networking site to make a post
#  This scenario is written to fetch data dynamically at run time and perform the test
  Scenario: Verify a user can submit a post successfully with valid data
    Given a user exists on the social networking site
    When a user submits a post with random title and body
    Then the post must be created successfully

#  Scenario can also be written to make use of static data
#  in this case the required data for the test has to be setup in "Before" hooks or a test env pre-populated with definitive set of data
#  Example of a test with static data
  Scenario: Verify a user can submit a post successfully with valid data as specified in the test
    When a user submits a post with below data
      | userId | title           | body           |
      | 1      | Test some title | Test some body |
    Then the post must be created successfully

#  verifying PUT, PUT replaces the entire resource
  Scenario: Verify a user can modify an existing post made by him
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When the user tries to modify above post's title and body
    Then the post must be modified successfully

#    Verifying PATCH, PATCH modifies only the field set in the request and the other values remain unchanged
  Scenario: Verify a user can modify only a targeted field in a post
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When the user tries to patch above post's title
    Then the post's must be modified successfully and all other values must remain unchanged

  Scenario: Verify a user can delete an existing post made by him
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When the user tries to delete above post
    Then the post must be deleted successfully

  Scenario: Verify a an existing post is returned successfully when queried by Id
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When the post is queried by id
    Then the post must be returned successfully

  Scenario: Verify all posts of a user are correctly returned when queried by userId
    Given a user exists on the social networking site
    When the post is queried by userId
    Then all posts made by the user are returned successfully

  Scenario: Verify all comments for a specific post can be retrieved using /posts/<postId>/comments endpoint
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When the user wants to retrieve all comments for a post
    Then all the comments for selected post are returned successfully

  Scenario: Verify a Not Found response 404 is returned when queried by invalid Id
    When the post is queried by invalid id
    Then the post must not be returned

  Scenario: Verify no posts are returned when queried by invalid userId
    When the post is queried by invalid userId
    Then no posts are returned

  #    Most of the cases in this test will fail as currently there are no validations on any of the request fields
  @ignore
  Scenario Outline: Verify the post endpoint returns an error, when a post is submitted with invalid data : <TestCase>
    When a user submits a post with below data
      | userId   | title   | body   |
      | <userId> | <title> | <body> |
    Then the post must not be created

#    listing few examples, can be extended to include request type and mandatory field validations too
    Examples:
      | TestCase                          | userId | title              | body            |
      | Invalid userId                    | 100    | fghhfghfgh hfghfgh | gffghh fgdhgfgh |
      | No user Id field                  |        | fghg               | fghfgh          |
      | userId is a String instead of int | abc    |                    |                 |
#      tried with a string with 2000 character long , was still able to submit. So depending on the specification below data can be set.
      | title is > allowed char limit     | 7      |                    |                 |
      | body is > allowed char limit      | 7      |                    |                 |

# API does not currently support this, as the API does not validate the user Id input and accepts any value given
#  below test will fail
  @ignore
  Scenario: User that does not exist on the social networking site, must not be allowed to make a post i.e. a post must not be accepted with a invalid user id
    When a user submits a post with below data
      | userId | title      | body |
      | 501    | some_title |      |
    Then the post must not be created

#  API does not currently support this
  @ignore @to_be_automated
  Scenario: Verify a user cannot delete or modify other users posts i.e. a PUT or PATCH request must not be allowed to modify userId of a post
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When the user tries to modify above post's userId
    Then the post must not be modified

  @ignore @to_be_automated
  Scenario: Verify a when user attempts to modify a post that does not exist using PUT /post/<postId> , it must return a HTTP 404 response
    When the user tries to modify a post that does not exist targeting PUT /post/<postId>
    #this is currently failing with a 500 error , when tried from postman
    Then the request must fail with a HTTP 404 error