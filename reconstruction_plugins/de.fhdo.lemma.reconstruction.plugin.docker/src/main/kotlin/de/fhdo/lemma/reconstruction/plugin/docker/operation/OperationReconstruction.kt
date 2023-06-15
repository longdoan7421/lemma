package de.fhdo.lemma.reconstruction.plugin.docker.operation

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionElement
import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionModule
import de.fhdo.lemma.reconstruction.framework.modules.ReconstructionModule
import de.fhdo.lemma.reconstruction.framework.modules.ReconstructionStage
import de.fhdo.lemma.reconstruction.framework.plugins.AbstractParseTree
import de.fhdo.lemma.reconstruction.framework.plugins.ParsingResultType
import de.fhdo.lemma.reconstruction.plugin.docker.extensions.*
import de.fhdo.lemma.reconstruction.plugin.docker.operation.container.Container
import de.fhdo.lemma.reconstruction.plugin.docker.operation.infrastructure.node.InfrastructureNode
import evalBash
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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
        val operationNodeHashMap: HashMap<DockerComposeServiceSpec, OperationNode> = hashMapOf()

        dockerComposeParseTree.data.services.forEach { (serviceName, serviceSpec) ->
            val endpoints = determineEndpoints(serviceSpec)
            val environment = serviceSpec.environment
            val operationNode: OperationNode = if (determineServiceType(serviceSpec) == OperationNodeType.Container) {
                Container(name = serviceName, endpoints = endpoints, environment = environment)
            } else {
                InfrastructureNode(name = serviceName, endpoints = endpoints, environment = environment)
            }
            operationNodeHashMap[serviceSpec] = operationNode
        }

        // Finding dependencies for each operation node
        operationNodeHashMap.forEach { hashMapEntry ->
            determineDependencyNodes(hashMapEntry, operationNodeHashMap)
        }

        // Note:
        operationNodeHashMap.forEach{ hashMapEntry ->
            determineUsedByNodes(hashMapEntry, operationNodeHashMap)
        }

        return operationNodeHashMap.values.toList()
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

    private val json: Json = Json { ignoreUnknownKeys = true }

    private fun determineServiceType(spec: DockerComposeServiceSpec): OperationNodeType {
        // Option 1: based on name of service
        val commonInfrastructureTechnology = mutableListOf(
            "mongo",
            "mongodb",
            "mysql",
            "postgres",
            "postgresql",
            "sqlserver",
            "mariadb",
            "redis",
            "rabbitmq",
            "kafka",
            "zookeeper",
            "eureka",
            "keycloak",
        );

        val commonInfrastructureTypes = mutableListOf(
            "db",
            "database",
            "registry",
            "discovery",
            "configuration",
            "gateway",
            "broker",
            "identity"
        ) + commonInfrastructureTechnology

        if (commonInfrastructureTypes.contains(spec.__name)) {
            return OperationNodeType.InfrastructureNode
        }

        // Option 2: based on image name

        // If the image is not set, we assume that it is a container
        if (spec.image.isEmpty()) {
            return OperationNodeType.Container
        }

        // If the image is set, we check if it contains a common infrastructure name
        val doesImageNameContainCommonInfrastructureName =
            commonInfrastructureTypes.any { spec.image.contains(it, true) }
        if (doesImageNameContainCommonInfrastructureName) {
            return OperationNodeType.InfrastructureNode
        }

        // Option 3: based on Config (`CMD` or `ENTRYPOINT`) when inspect docker image
        // See: https://docs.docker.com/engine/reference/commandline/inspect/

        try {
            println("Pulling image ${spec.image}...")
            val pullImageExitStates = "docker pull ${spec.image}".evalBash(env = emptyMap()).getOrNull();
            if (pullImageExitStates.isNullOrBlank()) {
                println("Could not pull image ${spec.image}.")
                return OperationNodeType.Container
            }

            println("Inspecting image ${spec.image}...")
            val inspectingResults =
                "docker inspect --format='{{json .Config}}' ${spec.image}".evalBash(env = emptyMap()).getOrNull();

            if (inspectingResults.isNullOrBlank()) {
                println("There is no Config object while inspecting ${spec.image}.")
                return OperationNodeType.Container
            }

            val inspectionConfig = json.decodeFromString<DockerImageInspectionConfig>(inspectingResults)

            if (inspectionConfig.Cmd.isMyElementExistsInList(commonInfrastructureTechnology) ||
                inspectionConfig.Entrypoint.isMyElementExistsInList(commonInfrastructureTechnology)
            ) {
                return OperationNodeType.InfrastructureNode
            }
        } catch (e: Exception) {
            println("Unexpected error occurred while inspecting ${spec.image}: ${e.message}")
            return OperationNodeType.Container
        }

        return OperationNodeType.Container
    }

    private fun determineEndpoints(spec: DockerComposeServiceSpec): List<String> {
        return spec.ports.map { port -> port.split(":")[1] }
    }

    private fun determineDependencyNodes(
        node: Map.Entry<DockerComposeServiceSpec, OperationNode>,
        allNodes: HashMap<DockerComposeServiceSpec, OperationNode>
    ) {
        val dependencyNodeNames = node.key.depends_on ?: return
        val dependencyNodes =
            allNodes.filter { dependencyNodeNames.indexOf(it.value.name) > -1 }.values.toMutableList()
        node.value.dependencyNodes.addAll(dependencyNodes)
    }

    private fun determineUsedByNodes(
        node: Map.Entry<DockerComposeServiceSpec, OperationNode>,
        allNodes: HashMap<DockerComposeServiceSpec, OperationNode>
    ) {
        val usedByNodes = mutableListOf<OperationNode>()
        allNodes.forEach { (key, value) ->
            if (value.dependencyNodes.contains(node.value)) {
                usedByNodes.add(value)
            }
        }
        node.value.usedByNodes.addAll(usedByNodes)
    }
}
