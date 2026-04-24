package com.petstore.automation.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Category(
    val id: Long? = null,
    val name: String? = null,
)
