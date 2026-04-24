Feature: Pet Store API - Update Pet
  As an API consumer of the Petstore service
  I want to update an existing pet
  So that its details stay accurate

  Scenario: Update an existing pet with PUT /pet
    Given an existing pet has been created in the store
    When I send a PUT request changing the status to "sold"
    Then the response status code is 200
    And the response pet status is "sold"
