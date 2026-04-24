Feature: Pet Store API - Delete Pet
  As an API consumer of the Petstore service
  I want to delete an existing pet
  So that stale records are removed from the inventory

  Scenario: Delete an existing pet with DELETE /pet/{petId}
    Given an existing pet has been created in the store
    When I send a DELETE request for that pet id
    Then the response status code is 200
