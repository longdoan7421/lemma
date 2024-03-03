package de.fhdo.lemma.reconstruction.plugin.docker.parser.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import de.fhdo.lemma.reconstruction.plugin.docker.parser.DockerComposeServiceBuildSpec

class ServiceBuildDeserializer : JsonDeserializer<DockerComposeServiceBuildSpec>() {
    override fun deserialize(jsonParser: JsonParser?, context: DeserializationContext?): DockerComposeServiceBuildSpec {
        if (jsonParser == null) {
            throw IllegalArgumentException("JsonParser is null")
        }

        val node = jsonParser.codec.readTree<JsonNode>(jsonParser)
        if (node.isTextual) {
            return DockerComposeServiceBuildSpec(context = node.asText())
        }

        val buildContext = node.get("context").asText()
        val dockerfile = node.get("dockerfile")?.asText()
        val dockerfileInline = node.get("dockerfile_inline")?.asText()

        return DockerComposeServiceBuildSpec(context = buildContext, dockerfile = dockerfile, dockerfileInline = dockerfileInline)
    }
}