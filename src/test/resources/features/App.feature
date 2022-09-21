Feature: User Profile
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

  Scenario: Invalid vehicle
    Given I am logged in on the main page
    When I enter an invalid year
    Then The vehicle isn't saved and the year error message is displayed