package de.fhdo.lemma.reconstruction.plugin.docker.operation

import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionElement

abstract class OperationNode(
    val name: String,
    var dependencyNodes: MutableList<OperationNode> = mutableListOf(),
) : AbstractReconstructionElement() {
    constructor(name: String) : this(name, mutableListOf())
}

enum class OperationNodeType {
    Container,
    InfrastructureNode
}