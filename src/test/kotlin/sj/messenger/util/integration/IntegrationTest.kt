package sj.messenger.util.integration

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import sj.messenger.util.config.TestStompClientConfig


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableContainers
@EnableMockAuthentication
@AutoConfigureMockMvc
@Import(TestStompClientConfig::class)
annotation class IntegrationTest()
