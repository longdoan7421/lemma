package de.fhdo.reconstruction.framework.modules

import de.fhdo.reconstruction.framework.plugins.AbstractParseTree
import de.fhdo.reconstruction.framework.plugins.ParsingResultType

/**
 * Super class for building microservice reconstruction plugin modules.
 */
abstract class AbstractReconstructionModule {
    abstract fun getParseTree(path: String): Pair<ParsingResultType, AbstractParseTree>
    abstract fun execute(abstractParseTree: AbstractParseTree): List<AbstractReconstructionElement>
    abstract fun getSupportFileExtensions(): List<String>
}

/**
 * Annotation for identifying [AbstractReconstructionModule].
 */
@Target(AnnotationTarget.CLASS)
annotation class ReconstructionModule(val stage: ReconstructionStage)

/**
 * Enum class for specifying the reconstruction stage for reconstruction modules.
 */
enum class ReconstructionStage () {
    Microservice,
    Interface,
    Operation
}

/**
 * Abstract superclass for reconstruction elements
 */
abstract class AbstractReconstructionElement()