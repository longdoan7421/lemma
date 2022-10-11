package de.fhdo.lemma.reconstruction.framework.modules.service

import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionHandler
import de.fhdo.lemma.reconstruction.framework.modules.AbstractReconstructionModule
import de.fhdo.lemma.reconstruction.framework.modules.domain.ReconstructionDomainHandler
import de.fhdo.lemma.reconstruction.framework.modules.domain.datastructure.DataStructure
import de.fhdo.lemma.reconstruction.framework.modules.service.`interface`.Interface
import de.fhdo.lemma.reconstruction.framework.modules.service.microservice.Microservice
import de.fhdo.lemma.reconstruction.framework.modules.service.operation.Operation
import de.fhdo.lemma.reconstruction.framework.plugins.AbstractParseTree
import de.fhdo.lemma.reconstruction.framework.repository.MicroserviceRepository

/**
 * Main module of the reconstruction framework. The module deals with the handling of the plugins and reconstruction
 * phases.
 *
 * @author [Philip Wizenty](mailto:philip.wizenty@fh-dortmund.de)
 */
internal object ReconstructionServiceHandler : AbstractReconstructionHandler() {
    private val reconstructedMicroservices = mutableListOf<Microservice>()
    private val reconstructedInterfaces = mutableListOf<Interface>()
    private val reconstructedOperations = mutableListOf<Operation>()


    /**
     * Initialize the state of the main reconstruction module
     */
    override fun init() {
        println("Init: ${ReconstructionServiceHandler.javaClass.name}")
    }
    /**
     * Execute the reconstruction for a microservice based on a parsing tree, provided by the technology specific plugin.
     */
    override fun reconstructFromPassTree(tree: AbstractParseTree, module: AbstractReconstructionModule) {
        val reconstruction = module.execute(tree)

        reconstruction.forEach {
            when (it) {
                is Microservice -> reconstructedMicroservices.add(it)
                is Interface -> reconstructedInterfaces.add(it)
                is Operation -> reconstructedOperations.add(it)
                is DataStructure -> ReconstructionDomainHandler.addDataStructureDependency(it)
            }
        }
    }

    internal fun finishServiceReconstructionStage() {
        assignOperationToInterfaces()
        assignInterfacesToMicroservice()
    }

    internal fun stop() {
        println()
    }

    internal fun writeReconstructedMicroservicesToDatabase() {
        reconstructedMicroservices.forEach {
            MicroserviceRepository.saveMicroservice(it)
        }
    }

    private fun assignInterfacesToMicroservice() {
        reconstructedInterfaces.forEach {
            assignToMatchingMicroservice(it)
        }
    }

    private fun assignToMatchingMicroservice(interFace: Interface) {
        reconstructedMicroservices.find {
            it.name.endsWith(interFace.microserviceName!!)
        }?.interfaces?.add(interFace)
    }

    private fun assignOperationToInterfaces() {
        reconstructedOperations.forEach {
            assignToMatchingInterface(it)
        }
    }
    private fun assignToMatchingInterface(operation: Operation) {
        reconstructedInterfaces.find {
            it.name == operation.interfaceName
        }?.operations?.add(operation)
    }

    internal fun addServiceReconstructionModule(module: AbstractReconstructionModule) {
        reconstructionModules.add(module)
    }

    internal fun getParseTrees(): Map<String, AbstractParseTree> = reconstructionFilesAndParseTree

}

fun getServiceReconstructionFilesAndParseTrees(): Map<String, AbstractParseTree> {
    return ReconstructionServiceHandler.getParseTrees()
}