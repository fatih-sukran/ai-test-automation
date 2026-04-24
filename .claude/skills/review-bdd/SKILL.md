---
name: review-bdd
description: Review all Cucumber feature files and step definitions in this project for BDD best practices. Flags imperative language, duplicate/near-duplicate steps, weak assertions, coupled scenarios, missing negative paths, and scenario titles that don't follow convention. Use when the user asks for a BDD review, feature file review, Cucumber review, quality check on Gherkin, or refactor suggestions for tests.
allowed-tools: Read Grep Glob
---

# Review BDD

Perform a structured BDD quality review of the project's feature files and step definitions.

## Scope

- All `.feature` files under `src/test/resources/features/`
- All step classes under `src/test/kotlin/com/petstore/automation/steps/`
- Do NOT modify files — produce a report only.

## Review Checklist

For each file, check the following dimensions and report findings grouped by category.

### 1. Scenario Title Conventions
- Title contains the business action **and** `METHOD PATH` (e.g. `with POST /pet`)?
- Title avoids implementation detail (no "JSON body", "status 200")?

### 2. Declarative vs Imperative Language
- Steps describe *what* is being tested, not *how*.
- Flag steps with implementation leaks: "open connection", "set header", "parse JSON", "convert to string".

### 3. Scenario Independence
- Are scenarios independent (can run in any order)?
- Flag scenarios that rely on a previous scenario's state (e.g. using a pet id created by an earlier scenario).
- `Background` used only for genuinely shared setup?

### 4. Step Reuse and Duplication
- Search for near-duplicate step patterns (`@Given("the pet exists")` vs `@Given("an existing pet has been created")`).
- Recommend consolidation.

### 5. Assertion Strength
- `Then` steps perform meaningful assertions beyond status code (id matches, field value, array length)?
- Flag scenarios that only assert `the response status code is 200` without validating body content.

### 6. Negative Path Coverage
- For each HTTP method (POST, GET, PUT, DELETE), is there at least one negative scenario?
- List endpoints missing negatives.

### 7. Kotlin Step Implementation Quality
- `ScenarioContext` used for state (no `companion object` or top-level `var`)?
- `@Given`/`@When`/`@Then` role separation clean?
- HTTP via `*ApiClient` wrapper, not direct `RestAssured.given()`?
- `requireNotNull` / guards on nullable context properties?

### 8. Project Convention Adherence
- Feature keywords in English?
- `@JsonInclude(NON_NULL)` on all serialized data classes?
- Files end with newline, no trailing whitespace?

## Output Format

Produce a markdown report with this structure:

```markdown
# BDD Review — <date>

## Summary
- Files reviewed: N feature files, M step classes
- Total findings: X (Y critical, Z minor)

## Findings

### Critical
- **[file.feature:line]** <finding> — <suggested fix>

### Minor
- **[file.feature:line]** <finding> — <suggested fix>

## Recommendations (prioritized)
1. ...
2. ...
```

Finish with a 3-sentence overall assessment.

## Example

- `/review-bdd` — reviews every feature + step class and produces a report.
