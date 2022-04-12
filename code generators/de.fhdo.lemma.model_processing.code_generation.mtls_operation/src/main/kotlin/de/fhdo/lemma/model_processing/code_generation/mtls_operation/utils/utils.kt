package de.fhdo.lemma.model_processing.code_generation.mtls_operation.utils

import de.fhdo.lemma.model_processing.asFile
import de.fhdo.lemma.model_processing.code_generation.java_base.serialization.property_files.SortableProperties
import de.fhdo.lemma.model_processing.code_generation.mtls_operation.modul_handler.MainContext
import de.fhdo.lemma.operation.InfrastructureNode
import de.fhdo.lemma.operation.intermediate.IntermediateOperationEnvironment
import de.fhdo.lemma.operation.intermediate.IntermediateOperationNode

internal fun loadPropertiesFile(filePath: String): SortableProperties {
    val file = filePath.asFile()
    val sortableProperties = SortableProperties()
    if (file.exists()) sortableProperties.load(file.inputStream())
    return sortableProperties
}

internal fun SortableProperties.asFormattedString(): String {
    var formattedString = ""
    entries.forEach {
        formattedString += "${it.key}=${it.value}\n"
    }
    return formattedString
}

internal fun InfrastructureNode.isCertificateAuthority() =
    (infrastructureTechnology.infrastructureTechnology.name == "certificateAuthority"
            && infrastructureTechnology.infrastructureTechnology.technology.name == "mTLS")

internal fun IntermediateOperationNode.hasAspect(aspectsSet: Set<String>) = aspects.any { aspectsSet.contains(it.name) }

internal fun IntermediateOperationNode.getNodeAspectsWithValues(aspectName: String): Map<String, String> {
    val resultMap = mutableMapOf<String, String>()

    aspects.filter { it.name == aspectName }.forEach { aspect ->
        aspect.properties.filter {
            !it.defaultValue.isNullOrEmpty()
        }.forEach { property ->
            resultMap[springPropertyMapping(property.name)] = property.defaultValue
        }
        aspect.propertyValues.forEach { propertyValue ->
            resultMap[springPropertyMapping(propertyValue.property.name)] = propertyValue.value
        }
    }
    return resultMap
}

internal fun SortableProperties.addProperty(property: Pair<String, String>) {
    this[springPropertyMapping(property.first)] = property.second
}

fun springPropertyMapping(property: String) = when (property) {
    "keyStoreRelativePath" -> "server.ssl.key-store"
    "keyStorePassword" -> "server.ssl.key-store-password"
    "trustStoreRelativePath" -> "server.ssl.trust-store"
    "trustStorePassword" -> "server.ssl.trust-store-password"
    "hostnameVerifierBypass" -> "server.ssl.bypass.hostname-verifier"
    "validityInDays" -> "server.ssl.key-store.validityInDays"
    "bitLength" -> "server.ssl.bitLength"
    "caName" -> "server.ssl.ca-name"
    "caCertificatePassword" -> "server.ssl.server.ca-password"
    "caDomain" -> "server.ssl.ca-domain.name"
    "certificateStandard" -> "server.ssl.certificateStandard"
    "cipher" -> "server.ssl.cipher"
    "caKeyFile" -> "server.ssl.ca-key.file"
    "caCertFile" -> "server.ssl.ca-Cert.file"
    "subject" -> "server.ssl.subject"
    "applicationName" -> "server.ssl.applicationName"
    else -> property
}

fun isConformApplicationNames(applicationNames: String): Boolean {
//    ([a-z0-9_.]+[ ]?[=][ ]?[a-z0-9_.]+)((,)([a-z0-9_.]+[ ]?[=][ ]?[a-z0-9_.]+))*
//    matches follow strings:
//    "com.myexample.name1=ms1"
//    "com.myexample.name1=ms1,com.myexample.name2=name2"
//    "com.myexample.name1 =ms1,com.myexample.name2= name2,com.myexample.name3 = ms3"

    val nameChars = "[a-z0-9_.]+"
    val equal = "[ ]?[=][ ]?"
    return applicationNames.matches(
        """(${nameChars}${equal}${nameChars})((,)(${nameChars}${equal}${nameChars}))*""".toRegex()
    )
}
fun isValidSystemEnvironmentVariable(environmentVariable: String) =
    environmentVariable.matches("[\$][{]([\\w_])+[}]".toRegex())

private fun getAllSystemEnvironmentVariable(environmentVariable: String) =
    "[\$][{][\\w-#~,+*?^(){\$\\[\\]|.]+[}]".toRegex().findAll(environmentVariable).toList()

fun hasAnyInvalidSystemEnvironmentVariable(environmentVariable: String) =
    getAllSystemEnvironmentVariable(environmentVariable).any { matchResult ->
        !matchResult.groupValues.any { isValidSystemEnvironmentVariable(it) }
    }



fun parseApplicationNames(applicationNames: String): Map<String, String> {
    val retval = mutableMapOf<String, String>()
    if (!isConformApplicationNames(applicationNames))
        return retval
    applicationNames.split(",").forEach {
        retval[it.split("=")[0].trim()] = it.split("=")[1].trim()
    }
    return retval
}

