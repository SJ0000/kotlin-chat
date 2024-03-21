package sj.messenger.global.domain

import jakarta.persistence.AttributeConverter

class NullToEmptyStringConverter : AttributeConverter<String, String>{

    override fun convertToDatabaseColumn(attribute: String?): String {
        return attribute ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): String {
        return dbData ?: ""
    }
}