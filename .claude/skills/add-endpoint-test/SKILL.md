---
name: add-endpoint-test
description: Scaffold a full API test for a new endpoint (data class model + ApiClient method + Cucumber feature scenario + step definitions) following this project's Kotlin + RestAssured + Cucumber conventions. Use when the user asks to add a test for an endpoint like "POST /store/order", "GET /user/{username}", "add endpoint test", "new api test".
arguments: [method, path]
argument-hint: "<METHOD> <PATH>"
allowed-tools: Read Write Edit Grep Glob
---

# Add Endpoint Test

Create a complete happy-path test for endpoint **$0 $1**.

## Inputs

- **HTTP method:** `$0` (GET, POST, PUT, DELETE, PATCH)
- **Path:** `$1` (e.g. `/store/order`, `/user/{username}`, `/pet/{petId}/uploadImage`)

## Procedure

### 1. Analyze the endpoint
- Determine the resource name from the path's first segment (`/store/...` → `store`, `/user/...` → `user`).
- Identify path parameters (wrapped in `{}`) and the required request body shape.
- Consult `CLAUDE.md` for the directory map, `.claude/rules/models-and-clients.md` for client conventions, and `.claude/rules/feature-files.md` for scenario style.
- If a Swagger spec is available (https://petstore.swagger.io/v2/swagger.json), use the `fetch` MCP or `WebFetch` to read the response schema; otherwise infer from method + path.

### 2. Add or update the model (if the request or response type is not yet defined)
- Location: `src/test/kotlin/com/petstore/automation/models/<Type>.kt`
- Kotlin `data class`, all properties nullable with `= null` defaults, `@JsonInclude(JsonInclude.Include.NON_NULL)` annotation.
- Reuse existing types (Category, Tag) — search with `Grep` before creating duplicates.

### 3. Add a client method
- Location: `src/test/kotlin/com/petstore/automation/api/<Resource>ApiClient.kt`
- Create the `*ApiClient` object if it does not exist (follow the pattern of `PetApiClient.kt`).
- Method signature returns `io.restassured.response.Response`.
- Start the builder with `RestAssured.given(RequestSpecFactory.default())`.
- Path params via `.pathParam("name", value)`, body via `.body(dto)`.

### 4. Add or update the feature file
- Location: `src/test/resources/features/<resource>/<resource>_<method>.feature` (method lowercase: `post`, `get`, `put`, `delete`). For path `/store/order` with method `POST` the file is `features/store/store_post.feature`.
- Create the file if missing; otherwise append a new `Scenario:` block.
- File-level `Feature:` header should match the method (e.g. `Feature: Pet Store API - Create Order`).
- Scenario title pattern: `Scenario: <business action> with <METHOD> <PATH>`.
- Reuse shared steps (`the response status code is {int}`, `an existing pet has been created in the store`) when applicable.

### 5. Add or update step definitions
- Location: `src/test/kotlin/com/petstore/automation/steps/<Resource>Steps.kt`
- Reuse `ScenarioContext` via constructor injection; add nullable fields to `ScenarioContext.kt` when new state is required.
- Enforce Given/When/Then role separation: prerequisite → action → assertion.

### 6. Verify
- Report a summary of new and modified files.
- Suggest the user run `./gradlew build` and `./gradlew test --tests "*CucumberTestRunner*"`.
- Do NOT run tests yourself unless explicitly asked.

## Convention Reminders

- Kotlin 4-space indent, nullable-plus-default properties, `@JsonInclude(NON_NULL)` on serialized classes.
- Feature keywords in English, scenarios independent, declarative language.
- No `Thread.sleep`, no hardcoded IDs (use `System.currentTimeMillis()` or the id returned by a previous response), no direct `RestAssured.given()` inside step definitions.
- See full conventions in `CLAUDE.md` and `.claude/rules/`.

## Examples

- `/add-endpoint-test POST /store/order` — creates `Order.kt`, `StoreApiClient.kt` with `createOrder()`, writes `features/store/store_post.feature`, updates `StoreSteps.kt`.
- `/add-endpoint-test GET /user/{username}` — reuses `User.kt` if present, adds `UserApiClient.getUserByUsername()`, writes `features/user/user_get.feature` + steps.
- `/add-endpoint-test DELETE /store/order/{orderId}` — adds `StoreApiClient.deleteOrderById()` + `features/store/store_delete.feature`, reusing `the response status code is {int}`.
