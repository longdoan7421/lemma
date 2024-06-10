package de.fhdo.lemma.reconstruction.plugin.docker.parser

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents the specification of a service
 * https://github.com/compose-spec/compose-spec/blob/master/05-services.md
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class DockerComposeServiceSpec(
    @JsonProperty("container_name")
    val containerName: String? = null,
    val build: DockerComposeServiceBuildSpec? = null,
    val image: String? = null,
    val ports: List<DockerComposeServicePortSpec>? = null,
    @JsonProperty("depends_on")
    val dependsOn: List<String>? = null,
    val environment: List<DockerComposeServiceEnvironmentSpec>? = null,
    @JsonProperty("pull_policy")
    val pullPolicy: PullPolicy? = null,
) {
    val isValid: Boolean
        get() = image != null || build != null
}

/**
 * https://github.com/compose-spec/compose-spec/blob/master/05-services.md#pull_policy
 */
enum class PullPolicy(val value: String) {
    @JsonProperty("always")
    ALWAYS("always"),

    @JsonProperty("never")
    NEVER("never"),

    @JsonProperty("missing")
    MISSING("missing"),

    /**
     * Alias for "missing"
     */
    @JsonProperty("if_not_present")
    IF_NOT_PRESENT("if_not_present"),

    @JsonProperty("build")
    BUILD("build")
}