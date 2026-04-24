---
paths:
  - "src/test/kotlin/com/petstore/automation/models/**/*.kt"
  - "src/test/kotlin/com/petstore/automation/api/**/*.kt"
---

# Model and API Client Rules

This file activates when Claude is working on files under `models/` or `api/`.

## Data Classes (`models/`)

- All properties nullable with defaults:
  ```kotlin
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class Order(
      val id: Long? = null,
      val petId: Long? = null,
      val quantity: Int? = null,
      val shipDate: String? = null,
      val status: String? = null,
      val complete: Boolean? = null,
  )
  ```
- `@JsonInclude(JsonInclude.Include.NON_NULL)` is mandatory — null fields break Petstore POST bodies.
- Nested types (Category, Tag, etc.) belong in their own data class files.
- `id` is `Long?` (Petstore schemas use int64).
- Nullable booleans are `Boolean?`, never the primitive form.

## API Clients (`api/`)

- One `object *ApiClient` per resource (PetApiClient, StoreApiClient, UserApiClient).
- Each method expresses a business operation; path and HTTP method stay inside the client.
- Uniform request shape:
  ```kotlin
  fun createOrder(order: Order): Response =
      RestAssured.given(RequestSpecFactory.default())
          .body(order)
          .post("/store/order")

  fun getOrderById(orderId: Long): Response =
      RestAssured.given(RequestSpecFactory.default())
          .pathParam("orderId", orderId)
          .get("/store/order/{orderId}")
  ```
- Client methods return `Response` — parsing is the step's job (step calls `response.as(Order::class.java)`).
- Do not use `Map<String, Any>` for request bodies. If a payload shape is needed, create a new data class.

## Forbidden Patterns

- No assertions inside clients (`assertEquals`) — assertions belong to steps.
- No `println` or logger calls in clients — request/response filters already log.
- No hardcoded config in clients — everything flows from `RequestSpecFactory.default()`.
- No `runBlocking`, coroutines, or `async` — the project is synchronous RestAssured.

## Fast Path for Adding an Endpoint

`/add-endpoint-test METHOD PATH` scaffolds model + client + feature + steps in accordance with these rules. When doing it manually, follow the same order.
