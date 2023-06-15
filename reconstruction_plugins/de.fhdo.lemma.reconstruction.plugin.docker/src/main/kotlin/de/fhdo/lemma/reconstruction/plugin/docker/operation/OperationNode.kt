package de.fhdo.lemma.reconstruction.plugin.docker.operation

import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionElement

abstract class OperationNode(
    val name: String,
    val dependencyNodes: MutableList<OperationNode> = mutableListOf(),
    val usedByNodes: MutableList<OperationNode> = mutableListOf(),
    val
) : AbstractReconstructionElement() {
    constructor(name: String) : this(name, mutableListOf())
}

enum class OperationNodeType {
    Container,
    InfrastructureNode
}