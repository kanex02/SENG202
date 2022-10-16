Feature: Login
  Scenario: Registering as a valid user
    Given I am on the login screen
    When I register with username "uniqueUsername"
    Then I am logged in successfully

  Scenario: Registering as empty user
    Given I am on the login screen
    When I register with username ""
    Then I am not logged in