package de.fhdo.lemma.reconstruction.plugin.docker.operation.models

import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionElement

abstract class OperationNode(
    val name: String,
    val endpoints: List<String>,
    val defaultValues: Map<String, String>? = mutableMapOf(),
    val dependencyNodes: MutableList<OperationNode> = mutableListOf(),
    val usedByNodes: MutableList<OperationNode> = mutableListOf(),
) : AbstractReconstructionElement() {
}

enum class OperationNodeType {
    Container,
    InfrastructureNode
}