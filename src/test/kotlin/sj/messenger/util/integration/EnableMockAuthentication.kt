package sj.messenger.util.integration

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import sj.messenger.util.config.AccessTokenProvideExtension
import sj.messenger.util.config.MockAuthenticationConfig

@Import(MockAuthenticationConfig::class)
@ExtendWith(AccessTokenProvideExtension::class)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableMockAuthentication()
