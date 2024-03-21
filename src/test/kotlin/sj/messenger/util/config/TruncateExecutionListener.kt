package sj.messenger.util.config

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class TruncateExecutionListener : TestExecutionListener {

    private val tables = listOf("users", "direct_chat", "friend", "friend_request", "group_chat", "participant")

    override fun afterTestExecution(testContext: TestContext) {
        val jdbcTemplate = getJdbcTemplate(testContext)
        // truncateMySql(jdbcTemplate)
        truncateOracle(jdbcTemplate)
    }

    private fun truncateMySql(jdbcTemplate: JdbcTemplate) {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;")
        tables.forEach {
            jdbcTemplate.execute("TRUNCATE TABLE ${it};")
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1;")
    }

    private fun truncateOracle(jdbcTemplate: JdbcTemplate){
        tables.forEach {
            jdbcTemplate.execute("TRUNCATE TABLE ${it}")
        }
    }

    private fun getJdbcTemplate(context: TestContext): JdbcTemplate {
        return context.applicationContext.getBean(JdbcTemplate::class.java)
    }
}