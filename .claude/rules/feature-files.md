---
paths:
  - "src/test/resources/features/**/*.feature"
---

# Cucumber Feature File Rules

This file activates when Claude is working on `.feature` files.

## File Layout

- One feature file per HTTP method, grouped by resource in its own subdirectory:
  ```
  src/test/resources/features/
  ├── pet/
  │   ├── pet_post.feature
  │   ├── pet_get.feature
  │   ├── pet_put.feature
  │   └── pet_delete.feature
  ├── store/
  │   ├── store_post.feature
  │   └── ...
  └── user/
      └── ...
  ```
- Path pattern: `features/<resource>/<resource>_<method>.feature` (method lowercase: `post`, `get`, `put`, `delete`, `patch`).
- Each file covers ONE method for ONE resource. Keep feature files small and focused.
- Cucumber runner scans `features/` recursively — subdirectories are picked up automatically.

## Language and Keywords

- Gherkin keywords in English: `Feature`, `Scenario`, `Given`, `When`, `Then`, `And`, `But`
- Feature description in role-action-value format, 2-3 lines (optional). Example:
  ```gherkin
  Feature: Pet Store API - CRUD Operations
    As an API consumer of the Petstore service
    I want to create, read, update, and delete pets
    So that the pet inventory stays accurate
  ```

## Scenario Naming

- Scenario titles must include the HTTP method and path:
  - Good: `Scenario: Create a new pet with POST /pet`
  - Good: `Scenario: Retrieve an existing pet with GET /pet/{petId}`
  - Bad: `Scenario: Test create` (too generic)
  - Bad: `Scenario: POST /pet returns 200` (implementation detail)

## Scenario Independence

- Every scenario is independent — one scenario's success or failure must not affect another.
- Use `Background` only for genuinely shared setup; otherwise embed setup steps such as `Given an existing pet has been created in the store` inside the scenario.
- Do not share state between scenarios outside `@ScenarioScope`.

## Declarative Over Imperative

- Steps describe what is being tested, not how:
  - Good: `When I send a POST request to create the pet`
  - Bad: `When I open POST connection to "/pet" with JSON body and content-type "application/json"`
- Keep implementation details inside step definitions; feature files should read clearly.

## Happy Path Plus Negative Scenarios

- For each HTTP method, aim for 1 happy-path scenario plus 1-2 negatives (400 invalid payload, 404 missing resource, 405 wrong method).
- Use `/generate-negative-tests <feature>` to add negatives.

## Data Tables and Scenario Outlines

- Use `Scenario Outline` + `Examples:` when three or more rows of repeating data are needed.
- For one-off special cases, prefer a plain `Scenario` over an outline with a single row.

## Step Reuse

- Before adding a new step, search existing step classes for a reusable match (`Grep` for `@Given`, `@When`, `@Then`).
- Prefer parameters (`{int}`, `{string}`, `{word}`) over writing a new step with slightly different wording.
