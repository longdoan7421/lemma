package de.fhdo.lemma.reconstruction.plugin.docker.parser

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Represents the specification of a Docker Compose file
 * https://github.com/compose-spec/compose-spec/blob/master/spec.md
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class DockerComposeSpec(
    val version: String,
    val services: Map<String, DockerComposeServiceSpec>
)