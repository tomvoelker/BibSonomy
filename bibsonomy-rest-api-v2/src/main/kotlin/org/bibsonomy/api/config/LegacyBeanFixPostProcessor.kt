package org.bibsonomy.api.config

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.stereotype.Component

/**
 * Post-processor that fixes legacy bean definitions that are incompatible with Spring Boot 3.x.
 *
 * Specifically, this removes properties that were set as String values in legacy XML but need
 * to be Maps. The actual values are set via InitializingBean in [LegacyBeanAliasesConfig].
 */
@Component
class LegacyBeanFixPostProcessor : BeanFactoryPostProcessor {

    @Throws(BeansException::class)
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        if (beanFactory is BeanDefinitionRegistry) {
            fixPermissionDatabaseManager(beanFactory)
        }
    }

    private fun fixPermissionDatabaseManager(registry: BeanDefinitionRegistry) {
        val beanName = "permissionDatabaseManager"
        if (!registry.containsBeanDefinition(beanName)) {
            return
        }

        val beanDef: BeanDefinition = registry.getBeanDefinition(beanName)
        val propertyValues = beanDef.propertyValues

        // Remove the specialUserTagMap property that can't be converted from String to Map.
        // The actual value will be set by permissionDatabaseManagerDefaults in LegacyBeanAliasesConfig.
        if (propertyValues.contains("specialUserTagMap")) {
            propertyValues.removePropertyValue("specialUserTagMap")
        }
    }
}
