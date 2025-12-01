package com.example.demo

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jacksonMapperBuilder

@SpringBootApplication
class DemoValueClassesApplication

fun main(args: Array<String>) {
    runApplication<DemoValueClassesApplication>(*args)
}

val VEHICLE_ERROR =
    Vehicle(
        id = VehicleId("vehicle-1"),
        name = "Vehicle #1",
        transmissions = mapOf(VehicleTransmission("automatic") to "Automatic"),
    )

val VEHICLE_OK =
    Vehicle(
        id = null,
        name = "Vehicle #2",
        transmissions = mapOf(VehicleTransmission("automatic") to "Automatic"),
    )

@Configuration
class JacksonConfiguration {
    @Bean
    @Primary
    fun jsonMapper(): JsonMapper =
        jacksonMapperBuilder()
            .apply {
                // If in the line below it is used "NON_NULL" instead of "NON_EMPTY", there is no error
                changeDefaultPropertyInclusion { it.withValueInclusion(JsonInclude.Include.NON_EMPTY) }
                changeDefaultPropertyInclusion { it.withContentInclusion(JsonInclude.Include.NON_EMPTY) }
            }.build()
}

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class VehiclesController {
    @GetMapping("/vehicles/error")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getVehicle1() = VEHICLE_ERROR

    @GetMapping("/vehicles/ok")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getVehicle2() = VEHICLE_OK
}

data class Vehicle(
    val id: VehicleId?,
    val name: String,
    val transmissions: Map<VehicleTransmission, String>,
)

@JvmInline
value class VehicleId(
    val value: String,
)

@JvmInline
value class VehicleTransmission(
    val value: String,
)
