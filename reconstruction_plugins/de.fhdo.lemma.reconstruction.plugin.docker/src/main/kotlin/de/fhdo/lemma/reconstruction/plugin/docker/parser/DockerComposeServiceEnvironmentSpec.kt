package de.fhdo.lemma.reconstruction.plugin.docker.parser

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.fhdo.lemma.reconstruction.plugin.docker.parser.deserializer.ServiceEnvironmentDeserializer

@JsonDeserialize(using = ServiceEnvironmentDeserializer::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DockerComposeServiceEnvironmentSpec(
    val key: String,
    val value: String
)