package de.fhdo.lemma.reconstruction.plugin.docker.operation

import kotlinx.serialization.Serializable

@Serializable
data class DockerImageInspectionConfig(val Env: List<String>? = null, val Cmd: List<String>? = null, val Entrypoint: List<String>? = null) {

}