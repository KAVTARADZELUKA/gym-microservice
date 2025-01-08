Feature: Trainer Workload Management

  Scenario: Successfully update trainer workload
    Given the user is authenticated as "ADMIN"
    And a valid workload request is provided with action "ADD"
    When the workload update request is sent
    Then the response status should be 200 OK

  Scenario: Fail to update workload due to insufficient permissions
    Given the user is authenticated as "TRAINER"
    And a valid workload request is provided with action "ADD"
    When the workload update request is sent
    Then the response status should be 403 FORBIDDEN

  Scenario: Fail to update workload with invalid date format
    Given the user is authenticated as "ADMIN"
    And a workload request is provided with an invalid date "2025/01/07"
    When the workload update request is sent
    Then an error message "trainingDate=Invalid date format. Use yyyy-MM-dd" should be returned