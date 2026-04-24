package com.petstore.automation.steps

import com.petstore.automation.api.PetApiClient
import com.petstore.automation.context.ScenarioContext
import com.petstore.automation.models.Category
import com.petstore.automation.models.Pet
import com.petstore.automation.models.Tag
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

class PetSteps(private val context: ScenarioContext) {

    @Given("a pet payload with name {string} and status {string}")
    fun preparePetPayload(name: String, status: String) {
        context.pendingPet = Pet(
            id = System.currentTimeMillis(),
            category = Category(id = 1, name = "dogs"),
            name = name,
            photoUrls = listOf("https://example.com/rex.png"),
            tags = listOf(Tag(id = 1, name = "friendly")),
            status = status,
        )
    }

    @Given("an existing pet has been created in the store")
    fun createPetPrerequisite() {
        val pet = Pet(
            id = System.currentTimeMillis(),
            category = Category(id = 1, name = "dogs"),
            name = "Rex",
            photoUrls = listOf("https://example.com/rex.png"),
            tags = listOf(Tag(id = 1, name = "friendly")),
            status = "available",
        )
        val response = PetApiClient.createPet(pet)
        assertEquals(200, response.statusCode, "Setup: pet creation failed")
        context.createdPet = response.`as`(Pet::class.java)
    }

    @When("I send a POST request to create the pet")
    fun sendCreatePetRequest() {
        val pet = requireNotNull(context.pendingPet) { "pendingPet not prepared" }
        context.lastResponse = PetApiClient.createPet(pet)
    }

    @When("I send a GET request for that pet id")
    fun sendGetPetRequest() {
        val id = requireNotNull(context.createdPet?.id) { "createdPet.id missing" }
        context.lastResponse = PetApiClient.getPetById(id)
    }

    @When("I send a PUT request changing the status to {string}")
    fun sendUpdatePetRequest(newStatus: String) {
        val existing = requireNotNull(context.createdPet) { "createdPet missing" }
        context.lastResponse = PetApiClient.updatePet(existing.copy(status = newStatus))
    }

    @When("I send a DELETE request for that pet id")
    fun sendDeletePetRequest() {
        val id = requireNotNull(context.createdPet?.id) { "createdPet.id missing" }
        context.lastResponse = PetApiClient.deletePetById(id)
    }

    @Then("the response status code is {int}")
    fun verifyStatusCode(expected: Int) {
        val response = requireNotNull(context.lastResponse) { "No response captured" }
        assertEquals(expected, response.statusCode)
    }

    @Then("the response body contains a generated pet id")
    fun verifyGeneratedId() {
        val pet = requireNotNull(context.lastResponse).`as`(Pet::class.java)
        assertNotNull(pet.id, "Response pet id is null")
        assertTrue(pet.id!! > 0, "Response pet id is not positive")
    }

    @Then("the response pet name is {string}")
    fun verifyPetName(expected: String) {
        val pet = requireNotNull(context.lastResponse).`as`(Pet::class.java)
        assertEquals(expected, pet.name)
    }

    @Then("the response pet id matches the created pet")
    fun verifyPetIdMatches() {
        val pet = requireNotNull(context.lastResponse).`as`(Pet::class.java)
        assertEquals(context.createdPet?.id, pet.id)
    }

    @Then("the response pet status is {string}")
    fun verifyPetStatus(expected: String) {
        val pet = requireNotNull(context.lastResponse).`as`(Pet::class.java)
        assertEquals(expected, pet.status)
    }
}
