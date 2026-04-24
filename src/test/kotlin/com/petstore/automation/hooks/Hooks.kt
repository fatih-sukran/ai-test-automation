package com.petstore.automation.hooks

import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.Scenario
import org.slf4j.LoggerFactory

class Hooks {

    private val log = LoggerFactory.getLogger(Hooks::class.java)

    @Before
    fun logScenarioStart(scenario: Scenario) {
        log.info("Starting scenario: {}", scenario.name)
    }

    @After
    fun logScenarioEnd(scenario: Scenario) {
        log.info("Finished scenario: {} [{}]", scenario.name, scenario.status)
    }
}
