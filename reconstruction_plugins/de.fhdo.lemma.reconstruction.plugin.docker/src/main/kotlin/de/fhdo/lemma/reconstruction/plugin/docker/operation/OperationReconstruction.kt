package de.fhdo.lemma.reconstruction.plugin.docker.operation

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionElement
import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionModule
import de.fhdo.lemma.reconstruction.framework.modules.ReconstructionModule
import de.fhdo.lemma.reconstruction.framework.modules.ReconstructionStage
import de.fhdo.lemma.reconstruction.framework.plugins.AbstractParseTree
import de.fhdo.lemma.reconstruction.framework.plugins.ParsingResultType
import de.fhdo.lemma.reconstruction.plugin.docker.operation.container.Container
import de.fhdo.lemma.reconstruction.plugin.docker.operation.infrastructure.node.InfrastructureNode
import java.io.File

/**
 * A reconstruction module for the docker reconstruction plugin. It is used to extract information
 * from Docker-Compose files and saves it to reconstruction elements, such as a [Container].
 *
 * @author [Philip Wizenty](mailto:philip.wizenty@fh-dortmund.de)
 */
@ReconstructionModule(ReconstructionStage.Operation)
class OperationReconstruction : AbstractReconstructionModule() {
    /**
     * Main method of the reconstruction module. The method extracts information from a parse tree
     * and saves it to a reconstruction element, such as a [Container].
     */
    override fun execute(abstractParseTree: AbstractParseTree):
            List<AbstractReconstructionElement> {
        val dockerComposeParseTree = abstractParseTree as DockerComposeParseTree
        val operationNodes: HashMap<DockerComposeServiceSpec, OperationNode> = hashMapOf()

        dockerComposeParseTree.data.services.forEach { (serviceName, serviceSpec) ->
            val endpoints = determineEndpoints(serviceSpec)
            val operationNode: OperationNode = if (determineServiceType(serviceSpec) == OperationNodeType.Container) {
                Container(name = serviceName, endpoints = endpoints)
            } else {
                InfrastructureNode(name = serviceName, endpoints = endpoints)
            }
            operationNodes[serviceSpec] = operationNode
        }

        // Finding dependencies for each operation node
        operationNodes.forEach { node ->
            determineDependencies(node, operationNodes)
        }

        return operationNodes.values.toList()
    }

    /**
     * Create a parse tree based on a file path. This method provides the parse tree used by the
     * execute method.
     */
    override fun getParseTree(path: String): Pair<ParsingResultType, AbstractParseTree> {
        val mapper = YAMLMapper().registerKotlinModule()
        val parsedTree = mapper.readValue(File(path), DockerComposeSpec::class.java)
        parsedTree.services.map { it.value.__name = it.key } // set name of service just for future reference
        val dockerComposeParseTree = DockerComposeParseTree(path, parsedTree)

        return Pair(ParsingResultType.FULLY_PARSED, dockerComposeParseTree)
    }

    /**
     * Return file extension for supported file types.
     */
    override fun getSupportFileExtensions(): List<String> {
        return listOf("yaml", "yml")
    }

    private fun determineServiceType(spec: DockerComposeServiceSpec): OperationNodeType {
        // TODO: differentiate Container and InfrastructureNode
        // Option 1: based on name of docker image
        // Option 2: based on Config (`CMD` or `ENTRYPOINT`) when inspect docker image
        // See: https://docs.docker.com/engine/reference/commandline/inspect/
        return OperationNodeType.Container
    }

    private fun determineEndpoints(spec: DockerComposeServiceSpec): List<String> {
        return spec.ports.map { port -> port.split(":")[1] }
    }

    private fun determineDependencies(
        node: Map.Entry<DockerComposeServiceSpec, OperationNode>,
        allNodes: HashMap<DockerComposeServiceSpec, OperationNode>
    ): OperationNode {
        val dependencyNodeNames = node.key.depends_on ?: return node.value
        val dependencyNodes =
            allNodes.filter { dependencyNodeNames.indexOf(it.value.name) > -1 }.values.toMutableList()
        node.value.dependencyNodes = dependencyNodes
        return node.value
    }
}
