Feature: Users feature api tests

  Background:
    Given url baseUrl
    * def usersRequest = read('classpath:features/users/UsersRequest.json')

  Scenario: Test insert user
    Given path "/users"
    And request usersRequest.insertUserRequest
    And method post
    Then status 200

  Scenario: Test get all users
    Given path "/users"
    And method get
    Then status 200
    And match response contains
