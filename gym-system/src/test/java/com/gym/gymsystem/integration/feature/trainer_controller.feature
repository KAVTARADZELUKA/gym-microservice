Feature: Trainer Management
  As an admin or trainer
  I want to manage trainer profiles
  So that I can register, update, and view trainer information

  Background:
    Given I have a valid JWT token for "ROLE_ADMIN"

  Scenario: Register a new trainer (positive)
    When I send a POST request to trainer with valid registration details
    Then I should receive a 201 status code
    And the response should contain the registered username and password

  Scenario: Register a new trainer with missing fields (negative)
    When I send a POST request to trainer with missing required fields
    Then I should receive a 400 status code
    And the response should indicate the missing fields

  Scenario: Get trainer profile as admin (positive)
    Given I have a valid JWT token for "ROLE_ADMIN"
    When I send a POST request to trainer with valid registration details
    When I send a GET request to "/trainer/{findUsername}" with an existing trainer's username
    Then I should receive a 200 status code
    And the response should contain the trainer's profile details

  Scenario: Get trainer profile as unauthorized user (negative)
    Given I have a valid JWT token for a non-admin user
    When I send a POST request to trainer with valid registration details
    When I send a GET request to "/trainer/{findUsername}" with an existing trainer's username
    Then I should receive a 403 status code
    And the response should indicate access is denied

  Scenario: Update trainer profile as admin (positive)
    Given I have a valid JWT token for "ROLE_ADMIN"
    When I send a POST request to trainer with valid registration details
    When I send a PUT request to "/trainer/{trainerUsername}" with valid update details
    Then I should receive a 200 status code
    And the response should contain the trainer's profile details

  Scenario: Update trainer profile as unauthorized user (negative)
    Given I have a valid JWT token for a non-admin user
    When I send a POST request to trainer with valid registration details
    When I send a PUT request to "/trainer/{trainerUsername}" with valid update details
    Then I should receive a 403 status code
    And the response should indicate access is denied

  Scenario: Update trainer status (positive)
    Given I have a valid JWT token for "ROLE_ADMIN"
    When I send a POST request to trainer with valid registration details
    When I send a PATCH request to "/trainer/status/{findUsername}" with "isActive" set to true
    Then I should receive a 200 status code
    And the response should indicate the trainer has been activated successfully

  Scenario: Update trainer status for non-existing user (negative)
    Given I have a valid JWT token for "ROLE_ADMIN"
    When I send a PATCH request to "/trainer/status/{findUsername}" with "isActive" set to true
    Then I should receive a 404 status code
    And the response should indicate the trainer was not found
