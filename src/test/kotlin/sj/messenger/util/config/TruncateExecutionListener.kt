package sj.messenger.util.config

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class TruncateExecutionListener : TestExecutionListener {

    private val tables =
        listOf("users", "direct_chat", "friend", "friend_request", "group_chat", "participant", "notification_token")

    override fun afterTestExecution(testContext: TestContext) {
        val jdbcTemplate = getJdbcTemplate(testContext)
        truncateOracle(jdbcTemplate)

        val mongoTemplate = getMongoTemplate(testContext)
        truncateMongo(mongoTemplate);
    }

    private fun truncateMySql(jdbcTemplate: JdbcTemplate) {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;")
        tables.forEach {
            jdbcTemplate.execute("TRUNCATE TABLE ${it};")
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1;")
    }

    private fun truncateOracle(jdbcTemplate: JdbcTemplate) {
        tables.forEach {
            jdbcTemplate.execute("TRUNCATE TABLE ${it}")
        }
    }

    private fun truncateMongo(mongoTemplate: MongoTemplate) {
        mongoTemplate.collectionNames.forEach {
            mongoTemplate.remove(Query(), it)
        }
    }

    private fun getMongoTemplate(context: TestContext): MongoTemplate {
        return context.applicationContext.getBean(MongoTemplate::class.java)
    }

    private fun getJdbcTemplate(context: TestContext): JdbcTemplate {
        return context.applicationContext.getBean(JdbcTemplate::class.java)
    }
}