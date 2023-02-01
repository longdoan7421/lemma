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
        val reconstructedElements = mutableListOf<AbstractReconstructionElement>()
        val dockerComposeParseTree = abstractParseTree as DockerComposeParseTree
        //todo: Change the example logic to a proper version for the extraction information
        dockerComposeParseTree.data.services.entries.forEach { entry ->
            val container = Container(entry.key)
            reconstructedElements.add(container)
        }
        return reconstructedElements
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
}