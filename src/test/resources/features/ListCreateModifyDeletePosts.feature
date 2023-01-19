@regression @focus
Feature: Create/update/delete/get a Post

#  Assumptions: From the response of GET /posts or GET /posts/<postId>, it can be inferred that a post has a reference to unique userId's
#  so it can be safely assumed that a user has to exist in the Social Networking site to make a post
  Scenario: Verify a user can submit a post successfully with valid data
    Given a user exists on the social networking site
    When a user submits a post with random title and body
    Then the post must be created successfully

#  verifying PUT, can also be modified to verify PATCH
  Scenario: Verify a user can modify an existing post made by him
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When the user tries to modify above post's title and body
    Then the post must be modified successfully

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
    Then the all posts made by the user are returned successfully

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
      | userId is a String instead of int |        |                    |                 |
      | title is > allowed char limit     |        |                    |                 |
      | body is > allowed char limit      |        |                    |                 |


# This cannot be automated currently, as the API does not validate the user Id input and accepts any value given
#  Scenario: User that does not exist on the social networking site, must not be allowed to make a post

#  API does not currently support this
#  Scenario: Verify a user cannot delete or modify other users posts i.e. a PUT or PATCH request must not be allowed to modify userId of a post
