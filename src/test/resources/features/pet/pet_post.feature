Feature: Pet Store API - Create Pet
  As an API consumer of the Petstore service
  I want to add a new pet to the store
  So that the pet becomes available in the inventory

  Scenario: Create a new pet with POST /pet
    Given a pet payload with name "Rex" and status "available"
    When I send a POST request to create the pet
    Then the response status code is 200
    And the response body contains a generated pet id
    And the response pet name is "Rex"
