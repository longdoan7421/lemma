package de.fhdo.lemma.reconstruction.plugin.docker.operation

import de.fhdo.lemma.reconstruction.framework.plugins.AbstractParseTree

/**
 * Parse tree for Docker-Compose files
 *
 * @author [Philip Wizenty](mailto:philip.wizenty@fh-dortmund.de)
 */
data class DockerComposeParseTree(
    val path: String,
    val data: DockerComposeSpec
) : AbstractParseTree("DockerComposeFile")

/**
 * Data class which represents the Docker-Compose file structure
 *
 * NOTE: Only the most important fields are represented here.
 * Full spec: https://github.com/compose-spec/compose-spec/blob/master/spec.md
 */
data class DockerComposeSpec(
    val version: String = "",
    val services: LinkedHashMap<String, DockerComposeServiceSpec> = LinkedHashMap(),
    val networks: List<Any>? = emptyList(),
    val volumes: List<Any>? = emptyList(),
)

data class DockerComposeServiceSpec(
    var __name: String = "",
    val container_name: String = "",
    val build: String = "", // TODO: support complex syntax
    val image: String = "",
    val ports: List<String>, // TODO: use union type (or something else) to support long syntax too
    val depends_on: List<String>? = null, // TODO: use union type (or something else) to support long syntax too
    val environment: Map<String, String>? = null,
    val volumes: List<String>? = null,
    val restart: String? = null,
    val networks: List<String>? = null,
)
