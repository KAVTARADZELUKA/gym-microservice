Feature: Training Management in Gym System
  As an admin, trainer, or trainee
  I want to manage trainings
  So that trainees can be assigned to trainers and have their training details updated or retrieved

  Background:
    Given I have a valid JWT token for "ROLE_ADMIN"

  Scenario: Get not assigned active trainers for a trainee (Positive Scenario)
    When I send a POST request to "/trainee" with valid registration details
    When I request the list of active trainers not assigned to trainee "trainee123"
    Then I should receive a list of trainers not assigned to the trainee

  Scenario: Get not assigned active trainers for a trainee without permission (Negative Scenario)
    Given I have a valid JWT token for a trainee user
    When I send a POST request to "/trainee" with valid registration details
    When I request the list of active trainers not assigned to trainee "trainee123"
    Then I should receive a 403 status code
    And the response should indicate access is denied

  Scenario: Update trainee's trainers (Positive Scenario)
    Given  I send a POST request to trainer with valid registration details
    When I send a POST request to "/trainee" with valid registration details
    When I send a request to add the new training
    And I send a POST request to trainer with valid registration details
    When I send a request to update trainers for trainee
    Then I should receive a 200 status code
    Then the response should contain the updated list of trainers assigned to the trainee

  Scenario: Update trainee's trainers without permission (Negative Scenario)
    Given I have a valid JWT token for a non-admin user
    When  I send a POST request to trainer with valid registration details
    When I send a POST request to "/trainee" with valid registration details
    When I send a request to add the new training
    And I send a POST request to trainer with valid registration details
    When I send a request to update trainers for trainee
    Then I should receive a 403 status code
    And the response should indicate access is denied

  Scenario: Get trainings for a trainee within a specific period (Positive Scenario)
    Given  I send a POST request to trainer with valid registration details
    When I send a POST request to "/trainee" with valid registration details
    When I send a request to add the new training
    And I request the list of trainings for trainee from "2024-01-01" to "2028-12-31"
    Then I should receive a 200 status code
    Then I should receive the list of trainings matching the criteria

  Scenario: Get trainings for a trainee without permission (Negative Scenario)
    Given I have a valid JWT token for a trainee user
    Given  I send a POST request to trainer with valid registration details
    When I send a POST request to "/trainee" with valid registration details
    When I send a request to add the new training
    And I request the list of trainings for trainee from "2024-01-01" to "2028-12-31"
    Then I should receive a 403 status code
    And the response should indicate access is denied

  Scenario: Add a new training (Positive Scenario)
    Given  I send a POST request to trainer with valid registration details
    When I send a POST request to "/trainee" with valid registration details
    When I send a request to add the new training
    Then I should receive a success message "Training added successfully"

  Scenario: Add a new training without permission (Negative Scenario)
    Given I have a valid JWT token for a non-admin user
    When  I send a POST request to trainer with valid registration details
    When I send a POST request to "/trainee" with valid registration details
    When I send a request to add the new training
    Then I should receive a 403 status code
    And the response should indicate access is denied
