package org.bibsonomy.api.config

import org.bibsonomy.database.managers.PermissionDatabaseManager
import org.bibsonomy.database.systemstags.SystemTagFactory
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import jakarta.annotation.Resource

@Disabled("System tags depend on legacy search wiring that is currently stubbed")
@SpringBootTest(classes = [SystemTagTestConfig::class])
class SystemTagConfigTest {

    @Resource
    private lateinit var systemTagFactory: SystemTagFactory

    @Test
    fun `system tag factory has search and markup tags wired`() {
        assertFalse(systemTagFactory.searchSystemTags.isEmpty(), "search system tags should not be empty")
        assertFalse(systemTagFactory.markUpSystemTags.isEmpty(), "markup system tags should not be empty")
    }
}

@Configuration
@Import(LegacyBeanAliasesConfig::class)
class SystemTagTestConfig {
    @Bean
    fun permissionDatabaseManager(): PermissionDatabaseManager = PermissionDatabaseManager.getInstance()
}
