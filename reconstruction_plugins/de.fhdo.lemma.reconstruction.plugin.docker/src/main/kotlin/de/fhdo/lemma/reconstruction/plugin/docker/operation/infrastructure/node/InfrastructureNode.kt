package de.fhdo.lemma.reconstruction.plugin.docker.operation.infrastructure.node

import de.fhdo.lemma.reconstruction.plugin.docker.operation.OperationNode

/**
 * Reconstruction element for restoring information about the architecture design from deployment
 * artifacts for infrastructure nodes, e.g., databases or service registries.
 *
 * @author [Philip Wizenty](mailto:philip.wizenty@fh-dortmund.de)
 */
class InfrastructureNode(
    name: String,
    endpoints: List<String>,
    environment: Map<String, String>? = null,
) : OperationNode(name, endpoints = endpoints, environment = environment) {
    override fun toString(): String {
        return """
            |InfrastructureNode(
            |   name='$name'
            |   endpoints='$endpoints'
            |   dependsOn='${dependencyNodes.map { it::class.qualifiedName + ":" + it.name }}'
            |   usedBy='${usedByNodes.map { it::class.qualifiedName + ":" + it.name }}'
            |   environment='$environment'
            |)""".trimMargin()
    }
}