# Petstore API Test Automation

Kotlin + Gradle + RestAssured + Cucumber API test automation against the [Petstore Swagger API](https://petstore.swagger.io/v2). Built as a university presentation example of classical, pre-AI test automation in Phase 1, then extended with Claude Code tooling in Phase 2.

## Scope

Four scenarios — one happy-path test per HTTP method:

| Scenario | Endpoint | Method |
|---|---|---|
| Create a new pet | `/pet` | POST |
| Retrieve an existing pet | `/pet/{petId}` | GET |
| Update an existing pet | `/pet` | PUT |
| Delete an existing pet | `/pet/{petId}` | DELETE |

## Tech Stack

- **Kotlin** 1.9.23 (JVM 17)
- **Gradle** 8.5 (Kotlin DSL, wrapper included)
- **RestAssured** 5.4 — HTTP client
- **Cucumber** 7.15 — BDD framework
- **JUnit 5** + `cucumber-junit-platform-engine` — runner
- **Jackson** (kotlin module) — JSON serialization
- **Owner** 1.0.12 — type-safe configuration
- **Allure** 2.25 — reporting
- **SLF4J + Logback** — logging

## Prerequisites

- **JDK 17** (Temurin, Zulu, or OpenJDK)
  - macOS (Homebrew): `brew install --cask temurin@17`
  - SDKMAN: `sdk install java 17.0.10-tem`
- `JAVA_HOME` set:
  ```bash
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  ```

Gradle does not need to be installed separately — the project ships with a Gradle wrapper (`./gradlew`).

## Running

```bash
# Run tests
./gradlew test

# Generate Allure report (build/reports/allure-report/allureReport/index.html)
./gradlew allureReport

# Open Allure report in a browser (embedded server)
./gradlew allureServe
```

## Project Layout

```
src/test/
├── kotlin/com/petstore/automation/
│   ├── api/              # PetApiClient — RestAssured wrapper
│   ├── config/           # PetstoreConfig (Owner), RequestSpecFactory
│   ├── context/          # ScenarioContext — scenario-scoped state
│   ├── hooks/            # Hooks — @Before/@After
│   ├── models/           # Pet, Category, Tag (data classes)
│   ├── runners/          # CucumberTestRunner (JUnit 5 Suite)
│   └── steps/            # PetSteps — Given/When/Then
└── resources/
    ├── features/pet/
    │   ├── pet_post.feature
    │   ├── pet_get.feature
    │   ├── pet_put.feature
    │   └── pet_delete.feature
    ├── config.properties
    ├── allure.properties
    ├── junit-platform.properties
    └── logback-test.xml
```

## Configuration

`src/test/resources/config.properties` drives `baseUrl`, `requestTimeoutMs`, and `logRequestResponse`. Values are read via [Owner](http://owner.aeonbits.org/) in a type-safe way (`PetstoreConfig` interface).

## Phase 2 — Claude Code AI Tooling

Phase 1 produced the classical, hand-written automation project. Phase 2 layers Claude Code's three core mechanisms on top, used in the live presentation demo.

### 1. Rules — `CLAUDE.md` and `.claude/rules/`

Project instructions auto-loaded into every Claude session:

- `CLAUDE.md` (project root) — tech stack, directory map, conventions, do/don't list.
- `.claude/rules/feature-files.md` — path-scoped: activates when editing `*.feature` files.
- `.claude/rules/step-definitions.md` — path-scoped: step classes.
- `.claude/rules/models-and-clients.md` — path-scoped: model and API client files.

### 2. Skills — `.claude/skills/`

Reusable slash-command workflows:

| Command | What it does |
|---|---|
| `/add-endpoint-test <METHOD> <PATH>` | Scaffolds model + client + feature + steps for a new endpoint |
| `/generate-negative-tests <feature>` | Adds 400/404/405 negative scenarios to an existing feature |
| `/scaffold-from-swagger <url> <tag>` | Generates a full test module for a tag from an OpenAPI spec |
| `/review-bdd` | Reviews feature files and step definitions for BDD quality (report only) |
| `/commit-tests [msg]` | Commits test changes using a `test: ...` message template |

### 3. MCP — `.mcp.json`

`.mcp.json` declares three MCP servers. Prerequisites:

```bash
# Node/npx (Context7 and GitHub)
node --version    # v18+

# uv (for the Python-based Fetch MCP)
brew install uv   # or: curl -LsSf https://astral.sh/uv/install.sh | sh

# GitHub MCP requires a personal access token (repo + PR scopes)
export GITHUB_TOKEN=<your-token>
```

Declared servers:

| Server | Package | Role |
|---|---|---|
| `context7` | `@upstash/context7-mcp` (npx) | Up-to-date library docs (RestAssured, Cucumber, Owner) |
| `fetch` | `mcp-server-fetch` (uvx) | HTTP fetch — live Swagger/OpenAPI spec retrieval |
| `github` | `@modelcontextprotocol/server-github` (npx) | Repo / issue / PR workflow |

The first time Claude Code opens the project it prompts to approve `.mcp.json`. After approval, verify connections with `claude mcp list`.

### Pre-demo Checklist

- [ ] `/memory` shows `CLAUDE.md` and the three rules files loaded
- [ ] `/` menu lists all five custom skills (`/add-endpoint-test`, etc.)
- [ ] `claude mcp list` reports context7, fetch, github as **connected**
- [ ] `GITHUB_TOKEN` is exported
- [ ] `./gradlew build` completes cleanly (Java 17 active)

## Notes

- Petstore is a demo API and can be flaky (429 rate limiting, 503, loose state persistence). Scenarios are designed to be independent, each setting up the data it needs.
- Phase 1: hand-written baseline, no AI. Phase 2: AI-assisted development via Claude Code rules + skills + MCP.
