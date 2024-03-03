package de.fhdo.lemma.reconstruction.plugin.docker.parser.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import de.fhdo.lemma.reconstruction.plugin.docker.parser.DockerComposeServicePortSpec

class ServicePortDeserializer : JsonDeserializer<DockerComposeServicePortSpec>() {
    override fun deserialize(jsonParser: JsonParser?, context: DeserializationContext?): DockerComposeServicePortSpec {
        if (jsonParser == null) {
            throw IllegalArgumentException("JsonParser is null")
        }

        val node = jsonParser.codec.readTree<JsonNode>(jsonParser)
        if (node.isTextual) {
            return parseShortSyntax(node.asText())
        }

        return parseLongSyntax(node as ObjectNode)
    }

    private fun parseShortSyntax(portMappingString: String): DockerComposeServicePortSpec {
        val portMapping = portMappingString.split("/")[0]
        val protocol = portMappingString.split("/").getOrNull(1)

        val parts = portMapping.split(":")

        // if no colon is present, the string value is the container port,
        // while the host port is assigned automatically by Docker (normally same port as container port)
        if (parts.size == 1) {
            return DockerComposeServicePortSpec(target = parts[0], protocol = protocol)
        }

        // if there is 1 colon, the first part is the host port, the second part is the container port
        if (parts.size == 2) {
            return DockerComposeServicePortSpec(published = parts[0], target = parts[1], protocol = protocol)
        }

        // if there is 2 colon, the first part is host ip, the second part is the host port, the third part is the container port
        if (parts.size == 3) {
            return DockerComposeServicePortSpec(published = parts[1], target = parts[2], hostIp = parts[0], protocol = protocol)
        }

        throw IllegalArgumentException("Invalid port string: $portMappingString")
    }

    private fun parseLongSyntax(portMapping: ObjectNode): DockerComposeServicePortSpec {
        val name = portMapping.get("name")?.asText()
        val target = portMapping.get("target").asText()
        val hostIp = portMapping.get("host_ip")?.asText()
        val published = portMapping.get("published")?.asText()
        val protocol = portMapping.get("protocol")?.asText()
        val mode = portMapping.get("mode")?.asText()

        return DockerComposeServicePortSpec(
            target = target,
            published = published,
            hostIp = hostIp,
            protocol = protocol,
            mode = mode,
            name = name
        )
    }
}