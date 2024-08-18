package sj.messenger.global.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import sj.messenger.util.randomAlphabets
import sj.messenger.util.randomString

class StringLowercaseConverterTest{

    private val converter : StringLowercaseConverter = StringLowercaseConverter()

    @Test
    fun convertToDatabaseColumnTest(){
        // given
        val originString = randomAlphabets(10)

        // expected
        assertThat(converter.convertToDatabaseColumn(originString)).isEqualTo(originString.lowercase())
        assertThat(converter.convertToDatabaseColumn(null)).isEqualTo("")
    }

    @Test
    fun convertToEntityAttributeTest(){
        // given
        val originString = randomString(10)

        // expected
        assertThat(converter.convertToEntityAttribute(originString)).isEqualTo(originString.lowercase())
        assertThat(converter.convertToEntityAttribute(null)).isEqualTo("")
    }

}