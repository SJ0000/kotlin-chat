package sj.messenger.util.annotation

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import sj.messenger.util.testcontainer.annotation.EnableMongoContainer

@DataMongoTest
@EnableMongoContainer
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MongoRepositoryTest ()