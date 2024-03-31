package sj.messenger.global.domain

import jakarta.persistence.AttributeConverter

class StringLowercaseConverter : AttributeConverter<String, String> {

    override fun convertToDatabaseColumn(attribute: String?): String {
        return attribute?.lowercase() ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): String {
        return dbData?.lowercase() ?: ""
    }
}