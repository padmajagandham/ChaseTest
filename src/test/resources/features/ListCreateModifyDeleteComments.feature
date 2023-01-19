@regression
Feature: Create/update/delete/get a Comments

#  There is no reference to userId in comments, so inferring that any user even not registered on the social networking site will be able to comment
  Scenario: Verify any user can successfully create a comment on an existing post
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When any random user tries to add a comment to above post
    Then the comment must be added successfully

  @focus
  Scenario: Verify that all comments related to a post are returned when queried by postId
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When comments endpoint is queried by above post id
    Then all the comments related to the post are returned

#  Scenario: Verify a specific comment is returned when queried by commentId
#  Scenario: Verify HTTP 404 Not Found response is returned when trying to retrieve using invalid commentId

#  Scenario: Verify a user is able to modify an existing comment
#  Scenario: Verify a user is able to delete a comment
#  Scenario Outline: Verify HTTP error 400 bad response is returned if request is invalid
#    Examples:
#
##    feature not supported
#  Scenario: Verify a user is unable to modify or delete comments made by other users