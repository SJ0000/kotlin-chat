package sj.messenger

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import sj.messenger.util.config.InjectAccessTokenExecutionListener
import sj.messenger.util.config.TruncateExecutionListener
import sj.messenger.util.testcontainer.initializer.ContainerParallelInitializer
import sj.messenger.util.websocket.TestStompClientConfig

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = [ContainerParallelInitializer::class])
@TestExecutionListeners(value = [InjectAccessTokenExecutionListener::class, TruncateExecutionListener::class])
@AutoConfigureMockMvc
@Import(TestStompClientConfig::class)
class ParallelExecutorTests {

	@Test
	fun contextLoads() {
	}

}
