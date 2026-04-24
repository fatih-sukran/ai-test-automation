---
name: generate-negative-tests
description: Add negative test scenarios (invalid payload 400, non-existent id 404, unauthorized 401, wrong method 405) to an existing Cucumber feature file, reusing existing step definitions where possible. Use when the user asks to add negative tests, error cases, failure scenarios, robustness tests, edge cases, or unhappy paths for an API test feature.
arguments: [feature]
argument-hint: "<feature-file>"
allowed-tools: Read Write Edit Grep Glob
---

# Generate Negative Tests

Add negative (error-path) scenarios to feature **$0**, reusing existing step definitions whenever possible.

## Inputs

- **Target feature file:** `$0` (e.g. `pet_post.feature`, or a full path like `src/test/resources/features/pet/pet_post.feature`). Feature files are organized as `features/<resource>/<resource>_<method>.feature` — one file per HTTP method.

## Procedure

### 1. Read the target feature file
- Resolve path: if `$0` is a bare filename, search `src/test/resources/features/` recursively.
- Read all existing scenarios. Note the resource (pet, store, user) and the HTTP method this file covers.

### 2. Read the related step definitions
- For each resource, inspect `src/test/kotlin/com/petstore/automation/steps/<Resource>Steps.kt`.
- Enumerate existing `@Given`, `@When`, `@Then`, `@And` patterns — these form your reuse pool.

### 3. Plan the negative scenarios
Generate **three negative scenarios** per resource, using this rubric (skip any that do not apply to the endpoint's method):

| # | Trigger | Expected status | Example |
|---|---|---|---|
| 1 | Invalid / malformed payload (missing required field, wrong type) | 400 | POST /pet with body missing `name` |
| 2 | Non-existent resource id | 404 | GET /pet/999999999 |
| 3 | Unsupported HTTP method or malformed path param | 405 / 400 | PUT /pet with negative id |

Note: the Petstore demo API is lenient about 400s — it often returns 200 with a generic body. Tag such scenarios with `@flaky` and either assert a status-code range (e.g. `the response status code is between 400 and 500`) or keep the specific expected code and accept that the test may need adjustment after the first run.

### 4. Extend step definitions ONLY if necessary
- Prefer reuse. Only add new steps if no existing step expresses the negative case.
- Examples of new negative steps:
  - `Given a pet payload missing the required name field`
  - `When I send a GET request for a non-existent pet id {int}`
  - `Then the response body contains an error message`

### 5. Append to the feature file
- Insert new `Scenario:` blocks AFTER existing happy-path scenarios.
- Title pattern: `Scenario: <what fails> returns <status> for <METHOD> <PATH>`.
- Example:
  ```gherkin
  Scenario: Retrieving a non-existent pet returns 404 for GET /pet/{petId}
    When I send a GET request for a non-existent pet id 999999999
    Then the response status code is 404
  ```

### 6. Report
- List added scenarios and any new step definitions.
- Flag Petstore-specific flakiness (e.g. "API may return 200 for invalid payloads — revisit if the test fails").
- Suggest `./gradlew test` to run.

## Conventions

- English Gherkin keywords. See `.claude/rules/feature-files.md`.
- Reuse over creation. See `.claude/rules/step-definitions.md`.
- Use a large unlikely number like `999999999` for non-existent ids rather than `-1` (some APIs treat negatives as valid).

## Examples

- `/generate-negative-tests pet_post.feature` — adds negatives for Create Pet (400 invalid payload, 405 wrong method).
- `/generate-negative-tests pet_get.feature` — adds negatives for Retrieve Pet (404 non-existent id, 400 malformed id).
- `/generate-negative-tests store_delete.feature` — adds delete-specific negatives.
