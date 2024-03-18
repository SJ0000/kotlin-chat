package sj.messenger.util.integration

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestExecutionListeners
import sj.messenger.util.config.InjectAccessTokenExecutionListener
import sj.messenger.util.config.TestStompClientConfig
import sj.messenger.util.config.TruncateExecutionListener


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableContainers
@TestExecutionListeners(value = [InjectAccessTokenExecutionListener::class, TruncateExecutionListener::class])
@AutoConfigureMockMvc
@Import(TestStompClientConfig::class)
annotation class IntegrationTest()
