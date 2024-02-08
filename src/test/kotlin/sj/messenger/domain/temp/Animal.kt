package sj.messenger.domain.temp

import org.junit.jupiter.api.Test

data class Person(val name: String, val age: Int)

class Book(val title: String, val authors: List<String>)

class Animal{
    @Test
    fun test(){
        val books = listOf(
            Book("alpha", listOf("AAA","BBB")),
            Book("alpha", listOf("CCC")),
            Book("alpha", listOf("CCC","DDD")),
            Book("alpha", listOf("AAA","BBB","EEE")),
        )
        // [AAA, BBB, CCC, CCC, DDD, AAA, BBB, EEE]
        println(books.flatMap { it.authors })
        // [AAA, BBB, CCC, DDD, EEE]
        println(books.flatMap { it.authors }.toSet())
    }

    @Test
    fun flattenTest(){
        val nestedList = listOf(listOf(1,2,3,4), listOf(5,6,7,8), listOf(9,10,11,12))
        println(nestedList) // [[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12]]
        println(nestedList.flatten()) // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]



    }



}

