package com.petstore.automation.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.ObjectMapperConfig
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.mapper.ObjectMapperType
import io.restassured.specification.RequestSpecification

object RequestSpecFactory {

    init {
        RestAssured.config = RestAssured.config()
            .objectMapperConfig(
                ObjectMapperConfig()
                    .defaultObjectMapperType(ObjectMapperType.JACKSON_2)
                    .jackson2ObjectMapperFactory { _, _ ->
                        ObjectMapper()
                            .registerModule(KotlinModule.Builder().build())
                            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    }
            )
    }

    fun default(): RequestSpecification {
        val builder = RequestSpecBuilder()
            .setBaseUri(petstoreConfig.baseUrl())
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(AllureRestAssured())

        if (petstoreConfig.logRequestResponse()) {
            builder.addFilter(RequestLoggingFilter())
            builder.addFilter(ResponseLoggingFilter())
        }

        return builder.build()
    }
}
