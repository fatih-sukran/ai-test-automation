---
name: scaffold-from-swagger
description: Scaffold a complete test module (models + API client + Cucumber feature + step definitions) from an OpenAPI/Swagger spec URL for a specific tag. Downloads the spec, identifies endpoints under the tag, then generates all test artifacts following this project's Kotlin + RestAssured + Cucumber conventions. Use when the user provides a swagger/openapi URL and asks to scaffold tests, generate from swagger, or bulk-create tests for a resource tag.
arguments: [url, tag]
argument-hint: "<swagger-url> <tag>"
allowed-tools: Read Write Edit Grep Glob WebFetch
---

# Scaffold From Swagger

Generate a full test module from the OpenAPI spec at **$0** for tag **$1**.

## Spec Snapshot

Swagger spec (tag + paths summary, pre-fetched):

```!
curl -s "$0" | python3 -c "
import json, sys
spec = json.load(sys.stdin)
tag = '$1'.lower()
print('Endpoints tagged as ' + tag + ':')
for path, methods in (spec.get('paths') or {}).items():
    for method, op in methods.items():
        tags = [t.lower() for t in (op.get('tags') or [])]
        if tag in tags:
            params = [p.get('name') for p in (op.get('parameters') or [])]
            body = 'has body' if op.get('parameters') and any(p.get('in')=='body' for p in op.get('parameters')) else 'no body'
            print(f'  {method.upper():6s} {path:40s}  params={params}  ({body})')
print()
print('Definitions referenced:')
for name in sorted((spec.get('definitions') or {}).keys()):
    print(f'  - {name}')
"
```

## Procedure

### 1. Parse the tag's endpoints
- Using the snapshot above, list every `METHOD PATH` under tag `$1`.
- For each, note path parameters, query parameters, and whether a request body is present.

### 2. Generate required models
- For each definition referenced by the tagged endpoints, create a Kotlin data class under `src/test/kotlin/com/petstore/automation/models/`.
- Nullable defaults, `@JsonInclude(JsonInclude.Include.NON_NULL)`, imported types for nested refs.
- Reuse existing models (Category, Tag) if they appear in the schema — check with `Grep` first.

### 3. Generate the API client
- Create `src/test/kotlin/com/petstore/automation/api/<Tag-PascalCase>ApiClient.kt` as an `object`.
- One method per endpoint, with the following naming:
  - `POST /<res>` → `create<Res>`
  - `GET /<res>/{id}` → `get<Res>ById`
  - `PUT /<res>` → `update<Res>`
  - `DELETE /<res>/{id}` → `delete<Res>ById`
  - Others: `<verb><Description>` (e.g. `findByStatus`, `logIn`, `logOut`).
- Follow the pattern in `PetApiClient.kt`: `RestAssured.given(RequestSpecFactory.default())` + `pathParam` / `body` / `queryParam`.

### 4. Generate the feature files
- One feature file PER HTTP method under `src/test/resources/features/<tag>/<tag>_<method>.feature` (method lowercase).
- Each file's `Feature:` header names the method (e.g. `Feature: Pet Store API - Create Order`).
- One happy-path scenario per endpoint inside its matching method file. If the tag has multiple `GET` endpoints (e.g. `GET /store/order/{id}` and `GET /store/inventory`), put both scenarios in `<tag>_get.feature`.
- Scenario title: `Scenario: <business action> with <METHOD> <PATH>`.
- Reuse shared steps (status code, response body contains) from `PetSteps.kt` where possible.

### 5. Generate step definitions
- File: `src/test/kotlin/com/petstore/automation/steps/<Tag-PascalCase>Steps.kt`
- Constructor-inject `ScenarioContext`. Add tag-specific nullable fields to `ScenarioContext.kt` if new state is required (e.g. `createdOrder`, `createdUser`).
- Strict Given/When/Then separation.

### 6. Report
- List all files that were created.
- Highlight endpoints where the Petstore demo API is known to be flaky (form upload, array body).
- Suggest `./gradlew build` followed by `./gradlew test`.

## Conventions

Follow the project overview in `CLAUDE.md` and the rules under `.claude/rules/`. Do NOT introduce new libraries or patterns (Kotest, AssertJ, coroutines). Stick to RestAssured + JUnit 5 Assertions + Jackson + Cucumber.

## Example Invocations

- `/scaffold-from-swagger https://petstore.swagger.io/v2/swagger.json store` → scaffolds `Order` model + `StoreApiClient` (createOrder, getOrderById, deleteOrderById, getInventory) + `features/store/store_post.feature` + `store_get.feature` + `store_delete.feature` + `StoreSteps`.
- `/scaffold-from-swagger https://petstore.swagger.io/v2/swagger.json user` → scaffolds `User` model + `UserApiClient` + `features/user/user_post.feature` + `user_get.feature` + `user_put.feature` + `user_delete.feature` + `UserSteps`.
