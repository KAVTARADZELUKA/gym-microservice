Feature: Trainee Management

  Background:
    Given I have a valid JWT token for "ROLE_ADMIN"

  Scenario: Register a new trainee (positive)
#    When DB
    When I send a POST request to "/trainee" with valid registration details
    Then I should receive a 201 status code
    And the response should contain the registered username and password

  Scenario: Register a new trainee with missing fields (negative)
    When I send a POST request to "/trainee" with invalid registration details
    Then I should receive a 400 status code
    And the response should indicate the missing fields

  Scenario: Get trainee profile as an admin (positive)
    Given I send a POST request
    When I send a GET request
    Then I should receive a 200 status code
    And the response should contain the trainee's profile information

  Scenario: Get trainee profile without proper authorization (negative)
    Given I have a valid JWT token for "ROLE_TRAINEE"
    When I send a GET request
    Then I should receive a 403 status code
    And the response should indicate access is denied

  Scenario: Update trainee profile (positive)
    Given I send a POST request
    When I send a PUT request with valid update details
    Then I should receive a 200 status code
    And the response should indicate the profile was updated

  Scenario: Delete a trainee (positive)
    Given I send a POST request
    When I send a DELETE request
    Then I should receive a 200 status code
    And the response should indicate the trainee was deleted