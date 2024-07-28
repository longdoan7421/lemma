package de.fhdo.lemma.reconstruction.plugin.docker.operation

import kotlinx.serialization.Serializable

@Serializable
data class SkopeoInspectionConfig(val Entrypoint: List<String>? = null, val Cmd: List<String>? = null) {
}

@Serializable
data class SkopeoInspectionResult(val config: SkopeoInspectionConfig) {
}
