plugins {
    kotlin("jvm") version "1.9.23"
    id("io.qameta.allure") version "2.11.2"
}

group = "com.petstore.automation"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:json-schema-validator:5.4.0")

    testImplementation("io.cucumber:cucumber-java:7.15.0")
    testImplementation("io.cucumber:cucumber-picocontainer:7.15.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.15.0")

    testImplementation("org.junit.platform:junit-platform-suite:1.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")

    testImplementation("org.aeonbits.owner:owner:1.0.12")

    testImplementation("io.qameta.allure:allure-cucumber7-jvm:2.25.0")
    testImplementation("io.qameta.allure:allure-rest-assured:2.25.0")

    testImplementation("org.slf4j:slf4j-api:2.0.12")
    testImplementation("ch.qos.logback:logback-classic:1.4.14")
}

allure {
    version.set("2.25.0")
    adapter {
        autoconfigure.set(false)
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("cucumber.junit-platform.naming-strategy", "long")
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = true
    }
    outputs.upToDateWhen { false }
}
