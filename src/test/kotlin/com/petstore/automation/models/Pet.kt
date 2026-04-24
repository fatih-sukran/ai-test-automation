package com.petstore.automation.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Pet(
    val id: Long? = null,
    val category: Category? = null,
    val name: String,
    val photoUrls: List<String> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val status: String? = null,
)
