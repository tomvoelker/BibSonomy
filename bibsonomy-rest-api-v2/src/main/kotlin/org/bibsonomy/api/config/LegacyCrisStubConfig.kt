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
 *
 * Note: CRIS link methods (createCRISLink, updateCRISLink, deleteCRISLink) are invoked
 * by DBLogic but these code paths are not exercised in the MVP REST API. If CRIS
 * functionality is needed, this stub must be replaced with a proper implementation.
 */
@Configuration(proxyBeanMethods = false)
class LegacyCrisStubConfig {
    /**
     * No-op stub for CRISLinkDatabaseManager. The post-processor below ensures
     * this replaces any XML-defined bean early in the lifecycle.
     */
    private fun createStub(): CRISLinkDatabaseManager = object : CRISLinkDatabaseManager() {}

    /**
     * Post-processor to replace the XML-defined crisLinkDatabaseManager with our stub.
     * This runs before normal bean instantiation, ensuring the stub is used.
     */
    @Bean
    fun crisLinkBeanOverride(): BeanDefinitionRegistryPostProcessor =
        object : BeanDefinitionRegistryPostProcessor {
            override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
                if (registry.containsBeanDefinition("crisLinkDatabaseManager")) {
                    registry.removeBeanDefinition("crisLinkDatabaseManager")
                }
                val bd = RootBeanDefinition(CRISLinkDatabaseManager::class.java) {
                    createStub()
                }
                bd.isPrimary = true
                registry.registerBeanDefinition("crisLinkDatabaseManager", bd)
            }

            override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
                // no-op
            }
        }
}
