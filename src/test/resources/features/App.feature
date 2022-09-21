Feature: Login
  Scenario: Logging in
    Given I am on the login page
    When I enter a name
    Then I login and my account is created

  Scenario: Opening profile
    Given I am logged in on the main page
    When I click on the profile button
    Then My user profile is displayed

  Scenario: Profile with registered vehicle
    Given I am logged in on the main page with a vehicle registered
    When I click on the profile button
    Then My user profile is displayed with the vehicle

  Scenario: Invalid year
    Given I am logged in on the main page
    When I enter an invalid year
    Then The vehicle isn't saved and the year error message is displayed

  Scenario: Invalid make
    Given I am logged in on the main page
    When I enter an invalid make
    Then The vehicle isn't saved and the make error message is displayed

  Scenario: Invalid model
    Given I am logged in on the main page
    When I enter an invalid model
    Then The vehicle isn't saved and the model error message is displayed

  Scenario: Write note
    Given I am logged in on the main page
    When I write a note for a station
    Then The note is saved to the database