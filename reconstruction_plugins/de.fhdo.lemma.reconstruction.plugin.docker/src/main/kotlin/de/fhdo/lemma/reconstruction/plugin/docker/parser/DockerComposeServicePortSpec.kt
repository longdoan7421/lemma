package de.fhdo.lemma.reconstruction.plugin.docker.parser

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.fhdo.lemma.reconstruction.plugin.docker.parser.deserializer.ServicePortDeserializer

/**
 * Represents the specification of a single port mapping of a service
 * https://github.com/compose-spec/compose-spec/blob/master/05-services.md#ports
 */
@JsonDeserialize(using = ServicePortDeserializer::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DockerComposeServicePortSpec(
    /**
     * The port on the container
     */
    val target: String,
    /**
     * The port on the host
     */
    val published: String? = null,
    val hostIp: String? = null,
    val protocol: String? = null,
    val mode: String? = null,
    val name: String? = null
)