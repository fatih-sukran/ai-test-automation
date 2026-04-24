# Petstore API Test Automation — Claude Instructions

Kotlin + Gradle + RestAssured + Cucumber API test automation against the [Petstore Swagger API](https://petstore.swagger.io/v2). This file holds persistent instructions Claude Code loads in every session for this project.

## Tech Stack

- **Kotlin** 1.9.23, JVM target 17
- **Gradle** 8.5 (Kotlin DSL, wrapper via `./gradlew`)
- **RestAssured** 5.4 — HTTP client
- **Cucumber** 7.15 + `cucumber-junit-platform-engine`
- **JUnit 5** — runner
- **Jackson** (`jackson-module-kotlin`) — JSON
- **Owner** 1.0.12 — type-safe config
- **Allure** 2.25 — reporting

Base URL: `https://petstore.swagger.io/v2` (override via `src/test/resources/config.properties`).

## Directory Map

```
src/test/
├── kotlin/com/petstore/automation/
│   ├── api/          PetApiClient — RestAssured method wrappers
│   ├── config/       PetstoreConfig (Owner), RequestSpecFactory
│   ├── context/      ScenarioContext — Cucumber-scoped state
│   ├── hooks/        Hooks — @Before/@After lifecycle
│   ├── models/       Pet, Category, Tag — data classes
│   ├── runners/      CucumberTestRunner (JUnit Suite)
│   └── steps/        PetSteps — @Given/@When/@Then
└── resources/
    ├── features/     *.feature — Gherkin scenarios (English keywords)
    ├── config.properties, allure.properties, junit-platform.properties
    └── logback-test.xml
```

## How to Add a New Endpoint Test

Follow this order when adding a test for a new endpoint:

1. **Model**: If needed, add a data class under `models/`. Nullable defaults + `@JsonInclude(JsonInclude.Include.NON_NULL)`.
2. **Client**: Add a method to the appropriate `*ApiClient` object (create one if missing) using `RestAssured.given(RequestSpecFactory.default())`. Return type is `Response`.
3. **Feature scenario**: Add a scenario to `features/<resource>/<resource>_<method>.feature` — one feature file per HTTP method (e.g. `features/store/store_post.feature`). Create the file if missing. The scenario title must include the endpoint (e.g. `Scenario: Create a new order with POST /store/order`).
4. **Step definitions**: In `steps/<Resource>Steps.kt`. REUSE existing steps (`the response status code is {int}`, etc.) whenever possible. Respect `@Given`/`@When`/`@Then` separation.
5. **ScenarioContext**: If the scenario carries state between steps, add nullable properties to `ScenarioContext.kt`.

## Conventions

**Kotlin style**
- 4-space indent, `kotlin.code.style=official`
- Data class properties nullable with defaults (e.g. `val id: Long? = null`)
- `@JsonInclude(JsonInclude.Include.NON_NULL)` on every serialized data class

**Feature files**
- English `Given` / `When` / `Then` keywords
- Scenarios are independent (no cross-scenario shared state). Use `Background` only for genuinely shared setup.
- Declarative: scenario titles and step text describe business intent, not implementation

**Step definitions**
- `Given` = setup / prerequisite, `When` = HTTP call, `Then` = assertion
- State only via `ScenarioContext` (constructor injection via picocontainer)
- Assertions from `org.junit.jupiter.api.Assertions`
- Null guard with `requireNotNull(context.lastResponse) { "..." }`

## Do / Don't

Do
- Use `PetApiClient.*` / `*ApiClient.*` for HTTP calls
- Build the request spec from `RequestSpecFactory.default()`
- Read config via `petstoreConfig.baseUrl()` (Owner)

Don't
- Don't use `Thread.sleep` — scenarios are independent, no sync needed
- Don't hardcode pet ids or order ids — use `System.currentTimeMillis()` or read from the response
- Don't call `RestAssured.given()` directly in step definitions — go through the client layer
- Don't mock RestAssured — tests hit the live Petstore demo API

## Commands

```bash
./gradlew test               # Run all tests
./gradlew test --tests "*"   # Filter by pattern
./gradlew allureReport       # Build/reports/allure-report/
./gradlew allureServe        # Open report in browser
./gradlew build              # Compile + test
```

## AI Workflow Notes

- Custom slash commands available: `/add-endpoint-test`, `/generate-negative-tests`, `/scaffold-from-swagger`, `/review-bdd`, `/commit-tests`. Prefer the matching skill over ad-hoc prompts.
- Path-scoped rules live in `.claude/rules/`: feature-files rules activate when editing `*.feature`, step-definition rules when editing step classes, model/client rules when editing models or API clients.
- MCP servers: `context7` (library docs), `fetch` (HTTP / Swagger), `github` (PR workflow).
