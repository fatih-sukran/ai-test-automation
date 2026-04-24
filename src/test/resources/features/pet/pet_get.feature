Feature: Pet Store API - Retrieve Pet
  As an API consumer of the Petstore service
  I want to retrieve an existing pet by id
  So that I can view its current details

  Scenario: Retrieve an existing pet with GET /pet/{petId}
    Given an existing pet has been created in the store
    When I send a GET request for that pet id
    Then the response status code is 200
    And the response pet id matches the created pet
