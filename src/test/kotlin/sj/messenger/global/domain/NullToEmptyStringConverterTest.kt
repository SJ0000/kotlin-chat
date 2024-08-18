package sj.messenger.global.domain

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import sj.messenger.util.randomString

class NullToEmptyStringConverterTest{

    private val converter : NullToEmptyStringConverter = NullToEmptyStringConverter()

    @Test
    fun convertToDatabaseColumnTest(){
        // given
        val originString = randomString(10)

        // expected
        assertThat(converter.convertToDatabaseColumn(originString)).isEqualTo(originString)
        assertThat(converter.convertToDatabaseColumn(null)).isEqualTo("")
    }

    @Test
    fun convertToEntityAttributeTest(){
        // given
        val originString = randomString(10)

        // expected
        assertThat(converter.convertToEntityAttribute(originString)).isEqualTo(originString)
        assertThat(converter.convertToEntityAttribute(null)).isEqualTo("")
    }
}