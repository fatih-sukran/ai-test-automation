---
paths:
  - "src/test/kotlin/com/petstore/automation/steps/**/*.kt"
---

# Step Definition Rules

This file activates when Claude is working on step-definition files under `steps/`.

## Given / When / Then Separation

- `@Given` — prerequisites, test-data setup, preparatory API calls
- `@When` — the actual action under test (typically a single HTTP call)
- `@Then` / `@And` — response assertions

Do not mix roles within a single step. Examples:
- Bad: `@When fun createAndVerify()` that sends a request and also asserts
- Good: `@When fun sendCreateRequest()` plus `@Then fun verifyStatus(expected: Int)`

## State Management

- State lives in `ScenarioContext` only. Inject via constructor:
  ```kotlin
  class PetSteps(private val context: ScenarioContext) { ... }
  ```
- Cucumber picocontainer DI wires it automatically (`cucumber-picocontainer` is on the classpath).
- Do not use top-level `var`, `companion object`, or singleton state — these break scenario parallelism.

## HTTP Calls

- Go through the `api/` client objects; do not call `RestAssured.given()` directly.
  - Good: `PetApiClient.createPet(pet)`
  - Bad: `RestAssured.given().body(pet).post("/pet")`
- If no client method exists, add one to the appropriate `*ApiClient` first, then invoke it from the step.

## Assertions

- Library: `org.junit.jupiter.api.Assertions` (`assertEquals`, `assertTrue`, `assertNotNull`).
- Parse responses with `response.as(Klass::class.java)` — the Jackson Kotlin module is configured in `Hooks`.
- Always guard nullable context: `val pet = requireNotNull(context.lastResponse) { "No response captured" }.as(Pet::class.java)`.
- Do not pull in AssertJ, Hamcrest, or other assertion libraries — JUnit Assertions are enough.

## String Parameters

- Use Cucumber placeholders: `{int}`, `{string}`, `{word}`.
- Quote strings in the feature file, bind as `String` in Kotlin:
  ```gherkin
  Given a pet payload with name "Rex" and status "available"
  ```
  ```kotlin
  @Given("a pet payload with name {string} and status {string}")
  fun preparePetPayload(name: String, status: String) { ... }
  ```

## Logging and Debugging

- Use the SLF4J logger pattern in `Hooks.kt`; do not use `println`.
- RestAssured request/response logging is already wired via `RequestLoggingFilter` / `ResponseLoggingFilter` (toggled in config).
- The `AllureRestAssured()` filter in `RequestSpecFactory` automatically attaches requests/responses to Allure.
