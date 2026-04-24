package com.petstore.automation.api

import com.petstore.automation.config.RequestSpecFactory
import com.petstore.automation.models.Pet
import io.restassured.RestAssured
import io.restassured.response.Response

object PetApiClient {

    fun createPet(pet: Pet): Response =
        RestAssured.given(RequestSpecFactory.default())
            .body(pet)
            .post("/pet")

    fun getPetById(petId: Long): Response =
        RestAssured.given(RequestSpecFactory.default())
            .pathParam("petId", petId)
            .get("/pet/{petId}")

    fun updatePet(pet: Pet): Response =
        RestAssured.given(RequestSpecFactory.default())
            .body(pet)
            .put("/pet")

    fun deletePetById(petId: Long): Response =
        RestAssured.given(RequestSpecFactory.default())
            .pathParam("petId", petId)
            .delete("/pet/{petId}")
}
