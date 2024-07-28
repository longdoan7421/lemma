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
import de.fhdo.lemma.reconstruction.plugin.docker.operation.models.Container
import de.fhdo.lemma.reconstruction.plugin.docker.operation.models.InfrastructureNode
import de.fhdo.lemma.reconstruction.plugin.docker.operation.models.OperationNode
import de.fhdo.lemma.reconstruction.plugin.docker.operation.models.OperationNodeType
import de.fhdo.lemma.reconstruction.plugin.docker.parser.DockerComposeParseTree
import de.fhdo.lemma.reconstruction.plugin.docker.parser.DockerComposeServiceSpec
import de.fhdo.lemma.reconstruction.plugin.docker.parser.DockerComposeSpec
import evalBash
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * A reconstruction module for the docker reconstruction plugin. It is used to extract information
 * from Docker-Compose files and saves it to reconstruction elements, such as a [Container].
 *
 * @author [Philip Wizenty](mailto:philip.wizenty@fh-dortmund.de)
 */
@ReconstructionModule(ReconstructionStage.Operation)
class OperationReconstruction : AbstractReconstructionModule() {
    private val infrastructureTechnologyDictionary = mutableListOf(
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
    )

    private val infrastructureKeywords = mutableListOf(
        "db",
        "database",
        "registry",
        "discovery",
        "configuration",
        "gateway",
        "broker",
        "identity"
    ) + infrastructureTechnologyDictionary

    private val json: Json = Json { ignoreUnknownKeys = true }

    /**
     * Main method of the reconstruction module. The method extracts information from a parse tree
     * and saves it to a reconstruction element, such as a [Container].
     */
    override fun execute(abstractParseTree: AbstractParseTree): List<AbstractReconstructionElement> {
        val dockerComposeParseTree = abstractParseTree as DockerComposeParseTree
        val composeServiceSpecOperationNodeHashMap: HashMap<DockerComposeServiceSpec, OperationNode> = hashMapOf()

        dockerComposeParseTree.data.services.forEach { (serviceName, serviceSpec) ->
            val endpoints = determineEndpoints(serviceSpec)
            val environment = serviceSpec.environment?.associate { it.key to it.value }
            val operationNode: OperationNode =
                if (determineNodeType(
                        serviceSpec,
                        serviceName,
                        dockerComposeParseTree.path
                    ) == OperationNodeType.Container
                ) {
                    Container(
                        name = serviceName,
                        endpoints = endpoints,
                        defaultValues = environment,
                        deployedServices = mutableListOf(serviceName)
                    )
                } else {
                    InfrastructureNode(name = serviceName, endpoints = endpoints, defaultValues = environment)
                }
            composeServiceSpecOperationNodeHashMap[serviceSpec] = operationNode
        }

        // Finding dependencies for each operation node
        composeServiceSpecOperationNodeHashMap.forEach { hashMapEntry ->
            determineDependencyNodes(hashMapEntry, composeServiceSpecOperationNodeHashMap)
        }

        // Note:
        composeServiceSpecOperationNodeHashMap.forEach { hashMapEntry ->
            determineUsedByNodes(hashMapEntry, composeServiceSpecOperationNodeHashMap)
        }

        return composeServiceSpecOperationNodeHashMap.values.toList()
    }

    /**
     * Create a parse tree based on a file path. This method provides the parse tree used by the
     * execute method.
     */
    override fun getParseTree(path: String): Pair<ParsingResultType, AbstractParseTree> {
        val mapper = YAMLMapper().registerKotlinModule()
        val dockerComposeSpec = mapper.readValue(File(path), DockerComposeSpec::class.java)
        val dockerComposeParseTree = DockerComposeParseTree(path, dockerComposeSpec)

        return Pair(ParsingResultType.PARTIALLY_PARSED, dockerComposeParseTree)
    }

    /**
     * Return file extension for supported file types.
     */
    override fun getSupportFileExtensions(): List<String> {
        return listOf("yaml", "yml")
    }

    private fun determineNodeType(
        spec: DockerComposeServiceSpec,
        serviceName: String,
        dockerComposeFilePath: String
    ): OperationNodeType {
        // Option 1: based on name of service
        if (infrastructureKeywords.contains(serviceName)) {
            return OperationNodeType.InfrastructureNode
        }

        // Option 2: based on image name
        if (!spec.image.isNullOrBlank()) {
            val doesImageNameContainCommonInfrastructureName =
                infrastructureKeywords.any { spec.image.contains(it, true) }
            if (doesImageNameContainCommonInfrastructureName) {
                return OperationNodeType.InfrastructureNode
            }
        }


        // Option 3: based on Config (`CMD` or `ENTRYPOINT`) by inspecting docker image
        if (spec.build == null) {
            return determineNodeTypeByInspectPrebuildImage(spec)
        }

        return determineNodeTypeByInspectBuildImage(spec, serviceName, dockerComposeFilePath)
    }

    private fun determineNodeTypeByInspectBuildImage(
        spec: DockerComposeServiceSpec,
        serviceName: String,
        dockerComposeFilePath: String
    ): OperationNodeType {
        // build service's image
        val dockerComposeFile = File(dockerComposeFilePath)
        "cd ${dockerComposeFile.parent} && docker compose -f ${dockerComposeFile.name} build $serviceName".evalBash(env = emptyMap())
            .getOrNull();

        // inspect service's image
        return inspectImage(spec.image)
    }

    private fun determineNodeTypeByInspectPrebuildImage(spec: DockerComposeServiceSpec): OperationNodeType {
        if (isSkopeoAvailable()) {
            return determineNodeTypeByInspectPrebuildImageWithSkopeo(spec)
        }

        return determineNodeTypeByInspectPrebuildImageWithDocker(spec)
    }

    private fun isSkopeoAvailable(): Boolean {
        val skopeoVersion = "skopeo --version".evalBash(env = emptyMap()).getOrNull()
        return skopeoVersion?.isNotBlank() ?: false
    }

    private fun determineNodeTypeByInspectPrebuildImageWithSkopeo(spec: DockerComposeServiceSpec): OperationNodeType {
        try {
            println("Start inspecting image ${spec.image} with skopeo...")
            val inspectionResultString =
                "skopeo inspect --config docker://${spec.image}".evalBash(env = emptyMap()).getOrNull();

            if (inspectionResultString.isNullOrBlank()) {
                println("Cannot inspect image ${spec.image} with skopeo, fallback to docker command.")
                return determineNodeTypeByInspectPrebuildImageWithDocker(spec)
            }

            val inspectionResult = json.decodeFromString<SkopeoInspectionResult>(inspectionResultString)

            if ((!inspectionResult.config.Cmd.isNullOrEmpty() && inspectionResult.config.Cmd.isMyElementExistsInList(
                    infrastructureTechnologyDictionary
                )) ||
                (!inspectionResult.config.Entrypoint.isNullOrEmpty() && inspectionResult.config.Entrypoint.isMyElementExistsInList(
                    infrastructureTechnologyDictionary
                ))
            ) {
                return OperationNodeType.InfrastructureNode
            }

            return OperationNodeType.Container
        } catch (e: Exception) {
            println("Unexpected error occurred while inspecting ${spec.image} with skopeo: ${e.message}")
            return OperationNodeType.Container
        }
    }

    private fun determineNodeTypeByInspectPrebuildImageWithDocker(spec: DockerComposeServiceSpec): OperationNodeType {
        try {
            println("Start inspecting image ${spec.image} with docker command...")
            println("Pulling image ${spec.image}...")
            val pullImageExitStates = "docker pull ${spec.image}".evalBash(env = emptyMap()).getOrNull();
            if (pullImageExitStates.isNullOrBlank()) {
                println("Could not pull image ${spec.image}.")
                return OperationNodeType.Container
            }

            return inspectImage(spec.image)
        } catch (e: Exception) {
            println("Unexpected error occurred while inspecting ${spec.image}: ${e.message}")
            return OperationNodeType.Container
        }
    }

    private fun inspectImage(image: String?): OperationNodeType {
        try {
            println("Inspecting image ${image}...")
            val inspectingResults =
                "docker inspect --format='{{json .Config}}' $image".evalBash(env = emptyMap()).getOrNull();

            if (inspectingResults.isNullOrBlank()) {
                println("There is no Config object while inspecting ${image}.")
                return OperationNodeType.Container
            }

            val inspectionConfig = json.decodeFromString<DockerImageInspectionConfig>(inspectingResults)

            if ((!inspectionConfig.Cmd.isNullOrEmpty() && inspectionConfig.Cmd.isMyElementExistsInList(
                    infrastructureTechnologyDictionary
                )) ||
                (!inspectionConfig.Entrypoint.isNullOrEmpty() && inspectionConfig.Entrypoint.isMyElementExistsInList(
                    infrastructureTechnologyDictionary
                ))
            ) {
                return OperationNodeType.InfrastructureNode
            }

            return OperationNodeType.Container
        } catch (e: Exception) {
            println("Unexpected error occurred while inspecting ${image}: ${e.message}")
            return OperationNodeType.Container
        }
    }

    private fun determineEndpoints(spec: DockerComposeServiceSpec): List<String> {
        return spec.ports?.map { it.toString() } ?: emptyList()
    }

    private fun determineDependencyNodes(
        node: Map.Entry<DockerComposeServiceSpec, OperationNode>,
        allNodes: HashMap<DockerComposeServiceSpec, OperationNode>
    ) {
        val dependencyNodeNames = node.key.dependsOn ?: return
        val dependencyNodes =
            allNodes.filter { dependencyNodeNames.indexOf(it.value.name) > -1 }.values.toMutableList()
        node.value.dependencyNodes.addAll(dependencyNodes)
    }

    private fun determineUsedByNodes(
        node: Map.Entry<DockerComposeServiceSpec, OperationNode>,
        allNodes: HashMap<DockerComposeServiceSpec, OperationNode>
    ) {
        val usedByNodes = mutableListOf<OperationNode>()
        allNodes.forEach { (_, value) ->
            if (value.dependencyNodes.contains(node.value)) {
                usedByNodes.add(value)
            }
        }
        node.value.usedByNodes.addAll(usedByNodes)
    }
}

fun runCommand(command: String): String {
    // Split the command by space and create a ProcessBuilder
    val processBuilder = ProcessBuilder(*command.split(" ").toTypedArray())
    // Redirect error stream to the standard output stream
    processBuilder.redirectErrorStream(true)

    // Start the process
    val process = processBuilder.start()
    val output = StringBuilder()

    // Read the output of the process
    BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }
    }

    // Wait for the process to complete
    val exitCode = process.waitFor()
    println("Process exited with code $exitCode")

    return output.toString()
}

