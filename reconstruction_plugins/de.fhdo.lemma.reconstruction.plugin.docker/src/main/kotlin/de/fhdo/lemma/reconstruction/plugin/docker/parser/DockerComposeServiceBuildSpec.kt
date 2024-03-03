package de.fhdo.lemma.reconstruction.plugin.docker.parser

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.fhdo.lemma.reconstruction.plugin.docker.parser.deserializer.ServiceBuildDeserializer

/**
 * Represents the "build" specification of a service in a Docker Compose file
 * https://github.com/compose-spec/compose-spec/blob/master/build.md
 */
@JsonDeserialize(using = ServiceBuildDeserializer::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DockerComposeServiceBuildSpec (
    val context: String,
    val dockerfile: String? = null,
    @JsonProperty("dockerfile_inline")
    val dockerfileInline: String? = null
)