package de.fhdo.lemma.reconstruction.plugin.docker.parser.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import de.fhdo.lemma.reconstruction.plugin.docker.parser.DockerComposeServiceEnvironmentSpec

class ServiceEnvironmentDeserializer : JsonDeserializer<DockerComposeServiceEnvironmentSpec>() {
    override fun deserialize(
        jsonParser: JsonParser?,
        context: DeserializationContext?
    ): DockerComposeServiceEnvironmentSpec {
        if (jsonParser == null) {
            throw IllegalArgumentException("JsonParser is null")
        }

        val node = jsonParser.codec.readTree<JsonNode>(jsonParser)
        if (node.isTextual) {
            val nodeValue = node.textValue().trim('"')
            val parts = nodeValue.split("=")
            if (parts.size != 2) {
                throw IllegalArgumentException("Invalid environment variable: $nodeValue")
            }

            return DockerComposeServiceEnvironmentSpec(key = parts[0], value = parts[1])
        }

        val key = node.get("key").asText()
        val value = node.get("value").asText()
        return DockerComposeServiceEnvironmentSpec(key = key, value = value)
    }
}