package com.petstore.automation.context

import com.petstore.automation.models.Pet
import io.restassured.response.Response

class ScenarioContext {
    var pendingPet: Pet? = null
    var createdPet: Pet? = null
    var lastResponse: Response? = null
}
