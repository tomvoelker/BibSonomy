package org.bibsonomy.api.config

import org.bibsonomy.database.managers.CRISLinkDatabaseManager
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Overrides the legacy cris link manager to avoid circular dependencies that
 * block startup. This stub is sufficient for the REST API where CRIS links are
 * currently unused.
 */
@Configuration(proxyBeanMethods = false)
class LegacyCrisStubConfig {
    @Bean(name = ["crisLinkDatabaseManager"])
    @Primary
    fun crisLinkDatabaseManagerStub(): CRISLinkDatabaseManager = object : CRISLinkDatabaseManager() {}

    /**
     * Ensure the stub replaces the XML definition early in the lifecycle.
     */
    @Bean
    fun crisLinkBeanOverride(): BeanDefinitionRegistryPostProcessor =
        object : BeanDefinitionRegistryPostProcessor {
            override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
                if (registry.containsBeanDefinition("crisLinkDatabaseManager")) {
                    registry.removeBeanDefinition("crisLinkDatabaseManager")
                }
                val bd = RootBeanDefinition(CRISLinkDatabaseManager::class.java) {
                    crisLinkDatabaseManagerStub()
                }
                bd.isPrimary = true
                registry.registerBeanDefinition("crisLinkDatabaseManager", bd)
            }

            override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
                // no-op
            }
        }
}
