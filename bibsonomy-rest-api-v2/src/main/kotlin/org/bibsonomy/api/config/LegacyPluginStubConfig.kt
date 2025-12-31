package org.bibsonomy.api.config

import org.bibsonomy.common.information.JobInformation
import org.bibsonomy.database.common.DBSession
import org.bibsonomy.database.params.BibTexExtraParam
import org.bibsonomy.database.params.ClipboardParam
import org.bibsonomy.database.params.DocumentParam
import org.bibsonomy.database.params.InboxParam
import org.bibsonomy.database.params.UserParam
import org.bibsonomy.database.plugin.DatabasePlugin
import org.bibsonomy.database.plugin.DatabasePluginRegistry
import org.bibsonomy.model.BibTex
import org.bibsonomy.model.Bookmark
import org.bibsonomy.model.DiscussionItem
import org.bibsonomy.model.Group
import org.bibsonomy.model.GroupMembership
import org.bibsonomy.model.Person
import org.bibsonomy.model.PersonName
import org.bibsonomy.model.Resource
import org.bibsonomy.model.ResourcePersonRelation
import org.bibsonomy.model.User
import org.bibsonomy.model.cris.CRISLink
import org.bibsonomy.model.cris.Project
import org.bibsonomy.model.enums.GoldStandardRelation
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.Configuration

/**
 * Replace the legacy databasePluginManager (and its plugin graph) with a no-op stub.
 */
@Configuration(proxyBeanMethods = false)
class LegacyPluginStubConfig {

    @Configuration(proxyBeanMethods = false)
    class Registrar : BeanDefinitionRegistryPostProcessor {
        override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
            if (registry.containsBeanDefinition("databasePluginManager")) {
                registry.removeBeanDefinition("databasePluginManager")
            }
            val bd = RootBeanDefinition(DatabasePluginRegistry::class.java) {
                DatabasePluginRegistry().apply {
                    setDefaultPlugins(emptyList())
                }
            }
            registry.registerBeanDefinition("databasePluginManager", bd)
        }

        override fun postProcessBeanFactory(beanFactory: org.springframework.beans.factory.config.ConfigurableListableBeanFactory) {
            // no-op
        }
    }
}
