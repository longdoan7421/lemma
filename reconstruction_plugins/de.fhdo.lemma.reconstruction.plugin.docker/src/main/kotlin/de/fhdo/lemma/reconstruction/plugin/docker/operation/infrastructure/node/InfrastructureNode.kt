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
    val endpoints: List<String>? = null,
) : OperationNode(name) {
    override fun toString(): String {
        return """
            |InfrastructureNode(
            |   name='$name'
            |   dependsOn='${dependencyNodes.map { it::class.qualifiedName + ":" + it.name }}'
            |   endpoints='$endpoints'
            |)""".trimMargin()
    }
}