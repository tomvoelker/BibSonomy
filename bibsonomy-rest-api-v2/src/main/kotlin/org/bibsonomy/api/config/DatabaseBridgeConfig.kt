package org.bibsonomy.api.config

import org.bibsonomy.model.logic.LogicInterface
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.core.io.FileSystemResource

/**
 * Configuration bridge to the legacy database layer.
 *
 * This configuration imports the legacy Spring 3.2 XML configuration
 * from bibsonomy-database and exposes the LogicInterface bean for use
 * in the modern Spring Boot application.
 *
 * The legacy database layer uses iBatis 2.x with Spring 3.2, while this
 * module uses Spring Boot 3.x. Spring Boot can load the old XML config
 * via @ImportResource, allowing us to reuse the database layer without
 * rewriting it.
 */
@Configuration
@ImportResource("classpath:org/bibsonomy/bibsonomy-database-context.xml")
class DatabaseBridgeConfig {

    /**
     * Property placeholder configurer for legacy bibsonomy.properties.
     *
     * Loads properties in order:
     * 1. default-bibsonomy.properties (classpath - project defaults)
     * 2. ~/bibsonomy.properties (file system - user overrides)
     *
     * This matches the legacy webapp behavior where project.properties provides
     * defaults and ~/bibsonomy.properties overrides them.
     */
    @Bean
    fun propertyConfigurer(): PropertyPlaceholderConfigurer {
        val configurer = PropertyPlaceholderConfigurer()

        // Load default properties from classpath, then user overrides from home directory
        val userHome = System.getProperty("user.home")

        configurer.setLocations(
            org.springframework.core.io.ClassPathResource("default-bibsonomy.properties"),
            FileSystemResource("$userHome/bibsonomy.properties")
        )

        configurer.setIgnoreUnresolvablePlaceholders(true)
        configurer.setIgnoreResourceNotFound(true)
        // Allow command line -D overrides to win over defaults from the legacy files.
        configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE)

        return configurer
    }
}
