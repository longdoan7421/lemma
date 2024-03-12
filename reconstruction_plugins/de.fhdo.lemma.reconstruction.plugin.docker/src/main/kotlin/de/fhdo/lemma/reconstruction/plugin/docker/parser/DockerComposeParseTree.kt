package de.fhdo.lemma.reconstruction.plugin.docker.parser

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
