package com.petstore.automation.config

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.LoadPolicy
import org.aeonbits.owner.Config.LoadType
import org.aeonbits.owner.Config.Sources
import org.aeonbits.owner.ConfigFactory

@Sources("classpath:config.properties")
@LoadPolicy(LoadType.MERGE)
interface PetstoreConfig : Config {

    @Config.Key("baseUrl")
    fun baseUrl(): String

    @Config.Key("requestTimeoutMs")
    @Config.DefaultValue("15000")
    fun requestTimeoutMs(): Int

    @Config.Key("logRequestResponse")
    @Config.DefaultValue("true")
    fun logRequestResponse(): Boolean
}

val petstoreConfig: PetstoreConfig = ConfigFactory.create(PetstoreConfig::class.java)
