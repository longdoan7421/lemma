package de.fhdo.lemma.reconstruction.plugin.docker.operation.models

/**
 * Reconstruction element for restoring information about the architecture design from deployment
 * artifacts for containers, responsible for microservices operations.
 *
 * @author [Philip Wizenty](mailto:philip.wizenty@fh-dortmund.de)
 */
class Container(
    name: String,
    endpoints: List<String>,
    private val deployedServices: MutableList<String>, // NOTE: assume name of deployed service is equal to name of docker compose service
    environment: Map<String, String>? = null,
//    private val technology: String, // TODO: how to detect?
//    private val deploymentTechnology: String, // TODO: how to detect? Maybe hard code to Docker Compose?
) : OperationNode(name, endpoints = endpoints, environment = environment) {
    override fun toString(): String {
        return """
            |Container(
            |   name='$name'
            |   endpoints='$endpoints'
            |   dependsOn='${dependencyNodes.map { it::class.qualifiedName + ":" + it.name }}'
            |   usedBy='${usedByNodes.map { it::class.qualifiedName + ":" + it.name }}'
            |   environment='$environment'
            |   deployedServices='${deployedServices}'
            |)""".trimMargin()
    }
}

