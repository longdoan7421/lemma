package de.fhdo.lemma.reconstruction.plugin.docker.operation

import kotlinx.serialization.Serializable

@Serializable
data class DockerImageInspectionConfig(val Env: List<String>, val Cmd: List<String>, val Entrypoint: List<String>) {

}