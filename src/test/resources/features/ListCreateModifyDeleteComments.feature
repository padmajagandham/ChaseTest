@regression
Feature: Create/update/delete/get a Comments

#  There is no reference to userId in comments, so inferring that any user even not registered on the social networking site will be able to comment
  Scenario: Verify any user can successfully create a comment on an existing post
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When any random user tries to add a comment to above post
    Then the comment must be added successfully

  Scenario: Verify that all comments related to a post are returned when queried by postId
    Given a user exists on the social networking site
    And has already posted on the social networking site
    When comments endpoint is queried by above post id
    Then all the comments for selected post are returned successfully

  Scenario: Verify a specific comment is returned when queried by commentId
    Given a user exists on the social networking site
    And has already posted on the social networking site
    And has comments against above post
    When comments endpoint is queried by a comment id
    Then the comment must be returned successfully

  Scenario: Verify HTTP 404 Not Found response is returned when trying to retrieve using invalid commentId
    When comments endpoint is queried by a invalid comment id
    Then the comment must not be returned successfully

  @ignore @to_be_automated
  Scenario: Verify a user is able to modify an existing comment
    Given a user exists on the social networking site
    And has already posted on the social networking site
    And has comments against above post
    When the comment is modified with new body
    Then the comment must be modified successfully

  @ignore @to_be_automated
  Scenario: Verify a user is able to patch an existing comment using PATCH /comment/<id> endpoint
    Given a user exists on the social networking site
    And has already posted on the social networking site
    And has comments against above post
    When the comment is modified with new title and body
    Then the comment must be patched successfully with new title and body and all other fields must remain unchanged

  @ignore @to_be_automated
  Scenario: Verify a user is able to delete a comment
    Given a user exists on the social networking site
    And has already posted on the social networking site
    And has comments against above post
    When user attempts to delete the comment
    Then the comment must be deleted successfully

  @ignore @to_be_automated
  Scenario Outline: Verify HTTP error 400 bad response is returned if request is invalid : <TestCase>
    When a comment is created/modified with below data
      | postId   | name   | email   | body   |
      | <postId> | <name> | <email> | <body> |
    Then the request must not be successful

    Examples:
      | TestCase                            | postId | name      | email         | body      |
      | Invalid postId                      | 9999   |           |               |           |
      | No postId field, as it is mandatory |        | ssss      | test@test.com | some body |
      | No email id field                   | 7      | some name | tset@test.com | some body |
      | invalid email id                    | 7      | some name | test          |           |

##    feature not supported
#  Scenario: Verify a user is unable to modify or delete comments made by other users