package org.bibsonomy.api

import org.bibsonomy.api.config.DataSourceLoggingConfig
import org.bibsonomy.api.config.LegacyBeanAliasesConfig
import org.bibsonomy.api.config.LegacyCrisStubConfig
import org.bibsonomy.api.config.LegacyGoldStandardStubConfig
import org.bibsonomy.api.config.LegacyLogicConfig
import org.bibsonomy.api.config.LegacyPluginStubConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

/**
 * Main Spring Boot application for BibSonomy REST API v2.
 *
 * This modern Kotlin API reuses the legacy database layer (bibsonomy-database)
 * while providing a clean JSON REST API following modern conventions.
 */
@SpringBootApplication
@Import(
    LegacyCrisStubConfig::class,
    LegacyLogicConfig::class,
    LegacyBeanAliasesConfig::class,
    LegacyPluginStubConfig::class,
    LegacyGoldStandardStubConfig::class,
    DataSourceLoggingConfig::class
)
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
