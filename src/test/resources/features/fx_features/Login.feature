#Feature: Login
#  Scenario: Registering as a valid user
#    Given I am on the login screen
#    When I register with username "uniqueUsername"
#    Then I am logged in successfully
#
#  Scenario: Registering with an invalid username
#    Given I am on the login screen
#    When I register with username ""
#    Then I am not logged in
#
#  Scenario: Registering with an invalid username
#    Given I am on the login screen
#    When I register with username "#$*"
#    Then I am not logged in
#
#  Scenario: Registering with an invalid username
#    Given I am on the login screen
#    When I register with username "abcdefghijklmnopqrstu"
#    Then I am not logged in