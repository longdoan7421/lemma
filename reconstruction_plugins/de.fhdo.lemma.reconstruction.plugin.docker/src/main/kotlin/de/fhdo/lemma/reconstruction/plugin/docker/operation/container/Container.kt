package de.fhdo.lemma.reconstruction.plugin.docker.operation.container

import de.fhdo.lemma.reconstruction.plugin.docker.operation.OperationNode

/**
 * Reconstruction element for restoring information about the architecture design from deployment
 * artifacts for containers, responsible for microservices operations.
 *
 * @author [Philip Wizenty](mailto:philip.wizenty@fh-dortmund.de)
 */
class Container(
    name: String,
//    private val technology: String, // TODO: how to detect?
//    private val deploymentTechnology: String, // TODO: how to detect? Maybe hard code to Docker Compose?
//    private val deployedServices: MutableList<String>, // TODO: how to detect?
    val endpoints: List<String>? = null,
) : OperationNode(name) {
    override fun toString(): String {
        return """
            |Container(
            |   name='$name'
            |   dependsOn='${dependencyNodes.map { it::class.qualifiedName + ":" + it.name }}'
            |   endpoints='$endpoints'
            |)""".trimMargin()
    }
}

