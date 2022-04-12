package de.fhdo.lemma.model_processing.code_generation.mtls_operation

import de.fhdo.lemma.model_processing.AbstractModelProcessor

public const val BASE_PACKAGE = "de.fhdo.lemma.model_processing.code_generation.mtls_operation"

class MtlsOperationsGenerator : AbstractModelProcessor(BASE_PACKAGE)

fun isConformSystemEnvironmentVariable(environmentVariable: String) {
    val incorrectRegex = "[\$][{][\\w-#~,+*?^(){\$\\[\\]|.]+[}]".toRegex()
    val correctRegex = "[\$][{]([\\w_])+[}]".toRegex()

    val match = incorrectRegex.findAll(environmentVariable)
    match.toSet().forEach { matchResult ->
        matchResult.groupValues.forEach {
            println("it: ${it}")
            if (it.matches(correctRegex))
                println("Super: ${it}")
            else
                println("Uiuiui: ${it}")
        }
    }
}


fun main(args: Array<String>) {
//    println("${hasAnyInvalidSystemEnvironmentVariable("\${my_password}")}")
//    println("${hasAnyInvalidSystemEnvironmentVariable("\${my.password}")}")
//    println("${hasAnyInvalidSystemEnvironmentVariable("my.password")}")

//    parseApplicationNames("de.pelki.ms1=ms1")
//    parseApplicationNames("de.pelki.ms1=ms1=")
//    parseApplicationNames("de.pelki.ms1=ms1,")
//    parseApplicationNames("de.pelki.ms1= ms1")
//    parseApplicationNames("de.pelki.ms1=ms1,de.pelki.ms1=ms1")
//    parseApplicationNames("de.pelki.ms1 = ms1,de.pelki.ms1=ms1")
//    parseApplicationNames("de.pelki.ms1 =ms1,de.pelki.ms1=ms1")
//    parseApplicationNames("de.pelki.ms1= ms1,de.pelki.ms1=ms1")
//    parseApplicationNames("de.pelki.ms1=ms1,de.pelki.ms1=ms1,")
//    parseApplicationNames("de.pelki.ms1=ms1,de.pelki.ms1=ms1,de.pelki.ms1=ms1")
//    parseApplicationNames("de.pelki.ms1=ms1,de.pelki.ms1=ms1,de.pelki.ms1=ms1,")
//    parseApplicationNames("de.pelki.ms1=ms1,de.pelki.ms1=ms1,de.pelki.ms1=ms1,de.pelki.ms1=ms1")
//    parseApplicationNames("de.pelki.ms1=ms1,de.pelki.ms1=ms1,de.pelki.ms1=ms1,de.pelki.ms1=ms1,")
    MtlsOperationsGenerator().run(args)
}