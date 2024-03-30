package sj.messenger.global.dto.serialize

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class StringLowercaseDeserializer : StdDeserializer<String>(String::class.java) {
    override fun deserialize(parser: JsonParser?, context: DeserializationContext?): String {
        parser ?: throw RuntimeException("Json Parser is null")
        context ?: throw RuntimeException("DeserializationContext is null")
        return parser.text.lowercase()
    }
}