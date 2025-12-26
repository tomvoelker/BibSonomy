package org.bibsonomy.api.config

import org.bibsonomy.common.errors.ErrorMessage
import org.bibsonomy.database.common.DBSession
import org.bibsonomy.database.validation.DatabaseModelValidator
import org.bibsonomy.database.managers.PermissionDatabaseManager
import org.bibsonomy.database.managers.metadata.MetaDataProvider
import org.bibsonomy.database.managers.metadata.ProjectMetaDataAdapter
import org.bibsonomy.database.managers.metadata.ResourceMetaDataProvider
import org.bibsonomy.database.managers.metadata.DistinctFieldProvider
import org.bibsonomy.model.GoldStandardBookmark
import org.bibsonomy.model.GoldStandardPublication
import org.bibsonomy.model.validation.ModelValidator
import org.bibsonomy.api.config.MetaDataProvidersFactory
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery
import org.bibsonomy.model.BibTex
import org.bibsonomy.model.Bookmark
import org.bibsonomy.services.filesystem.FileLogic
import org.bibsonomy.services.searcher.ProjectSearch
import org.bibsonomy.services.searcher.ResourceSearch
import org.bibsonomy.testutil.DummyInformationService
import org.bibsonomy.testutil.DummyFileLogic
import org.bibsonomy.search.management.database.SearchDBInterface
import org.bibsonomy.api.search.DummySearchDBInterface
import org.bibsonomy.database.systemstags.SystemTagFactory
import org.bibsonomy.database.systemstags.markup.ExternalSystemTag
import org.bibsonomy.database.systemstags.markup.HiddenTagSystemTag
import org.bibsonomy.database.systemstags.markup.JabrefSystemTag
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag
import org.bibsonomy.database.systemstags.markup.RelevantForSystemTag
import org.bibsonomy.database.systemstags.markup.ReportedSystemTag
import org.bibsonomy.database.systemstags.markup.SentSystemTag
import org.bibsonomy.database.systemstags.markup.UnfiledSystemTag
import org.bibsonomy.database.systemstags.search.AuthorSystemTag
import org.bibsonomy.database.systemstags.search.BibTexKeySystemTag
import org.bibsonomy.database.systemstags.search.DaysSystemTag
import org.bibsonomy.database.systemstags.search.EntryTypeSystemTag
import org.bibsonomy.database.systemstags.search.GroupSystemTag
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag
import org.bibsonomy.database.systemstags.search.NotTagSystemTag
import org.bibsonomy.database.systemstags.search.TitleSystemTag
import org.bibsonomy.database.systemstags.search.UserRelationSystemTag
import org.bibsonomy.database.systemstags.search.UserSystemTag
import org.bibsonomy.database.systemstags.search.YearSystemTag
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Qualifier
import org.slf4j.LoggerFactory

/**
 * Provides bean aliases expected by the legacy XML configuration but not
 * explicitly named in the modern Spring Boot setup.
 */
@Configuration
class LegacyBeanAliasesConfig {

    companion object {
        private val logger = LoggerFactory.getLogger(LegacyBeanAliasesConfig::class.java)
    }

    /**
     * Remove/override selected legacy beans from the imported XML when they
     * cause conflicts in this Spring Boot context.
     */
    @Bean
    fun databaseValidatorOverride(): BeanDefinitionRegistryPostProcessor =
        BeanDefinitionRegistryPostProcessor { registry: BeanDefinitionRegistry ->
            listOf(
                "metaDataProviders",
                "systemTagFactory",
                "executableSystemTagSet",
                "searchSystemTagSet",
                "markUpSystemTagSet",
                "forGroupTag",
                "forFriendTag",
                "reportTag",
                "addRelationTag",
                "authorTag",
                "bibtexkeyTag",
                "daysTag",
                "entrytypeTag",
                "groupTag",
                "titleTag",
                "userTag",
                "yearTag",
                "notTag",
                "userRelationSystemTag",
                "networkRelationSystemTag",
                "relevantForSystemTag",
                "sentSystemTag",
                "myOwnSystemTag",
                "unfiledSystemTag",
                "jabrefSystemTag",
                "hiddenSystemTag",
                "externalSystemTag",
                "reportedSystemTag"
            ).forEach { name ->
                if (registry.containsBeanDefinition(name)) {
                    registry.removeBeanDefinition(name)
                }
            }

            registry.registerBeanDefinition(
                "reportInformationService",
                RootBeanDefinition(DummyInformationService::class.java)
            )
            if (registry.containsBeanDefinition("mainSqlMapClient") && !registry.isAlias("sqlMapClient")) {
                registry.registerAlias("mainSqlMapClient", "sqlMapClient")
            }
            // Reintroduce system tag sets (non-empty) to restore system tag processing.
            registry.registerBeanDefinition("executableSystemTagSet", RootBeanDefinition(Set::class.java) { emptySet<Any>() })
            val searchTags = setOf(
                AuthorSystemTag(),
                BibTexKeySystemTag(),
                DaysSystemTag(),
                EntryTypeSystemTag(),
                GroupSystemTag(),
                TitleSystemTag(),
                UserSystemTag(),
                YearSystemTag(),
                UserRelationSystemTag(),
                NetworkRelationSystemTag(),
                NotTagSystemTag()
            )
            val markupTags = setOf(
                RelevantForSystemTag(),
                SentSystemTag(),
                MyOwnSystemTag(),
                UnfiledSystemTag(),
                JabrefSystemTag(),
                HiddenTagSystemTag(),
                ExternalSystemTag(),
                ReportedSystemTag()
            )
            registry.registerBeanDefinition("searchSystemTagSet", RootBeanDefinition(Set::class.java) { searchTags })
            registry.registerBeanDefinition("markUpSystemTagSet", RootBeanDefinition(Set::class.java) { markupTags })

            val systemTagFactoryDef = RootBeanDefinition(SystemTagFactory::class.java) {
                SystemTagFactory.getInstance().apply {
                    // Executable tags remain disabled until their legacy dependencies (noAuthInterfaceFactory, mail utils, etc.) are reintroduced.
                    setExecutableSystemTags(emptySet())
                    setSearchSystemTags(searchTags)
                    setMarkUpSystemTags(markupTags)
                }
            }
            registry.registerBeanDefinition("systemTagFactory", systemTagFactoryDef)

            // Make sure permission manager gets a real map instead of an unparsed string placeholder
            if (registry.containsBeanDefinition("permissionDatabaseManager")) {
                val def = registry.getBeanDefinition("permissionDatabaseManager")
                def.propertyValues.add("specialUserTagMap", emptyMap<String, String>())
            }
        }

    /**
     * Real model validator from legacy model module (provides resource validation).
     */
    @Bean(name = ["dbModelValidator", "modelValidator"])
    fun modelValidator(): ModelValidator = ModelValidator()

    /**
     * Minimal file logic to satisfy legacy beans that expect the webapp's ServerFileLogic.
     * Uses DummyFileLogic from the legacy test utilities to avoid pulling in the servlet stack.
     */
    @Bean
    fun fileLogic(): FileLogic = DummyFileLogic()

    // Stub search DB logics to allow search beans to instantiate without the full legacy DB wiring.
    @Bean
    @Suppress("UNCHECKED_CAST")
    fun bookmarkSearchDBLogic(): SearchDBInterface<*> = DummySearchDBInterface()

    @Bean
    @Suppress("UNCHECKED_CAST")
    fun publicationSearchDBLogic(): SearchDBInterface<*> = DummySearchDBInterface()

    @Bean
    @Suppress("UNCHECKED_CAST")
    fun goldStandardBookmarkSearchDBLogic(): SearchDBInterface<*> = DummySearchDBInterface()

    @Bean
    @Suppress("UNCHECKED_CAST")
    fun goldStandardPublicationSearchDBLogic(): SearchDBInterface<*> = DummySearchDBInterface()

    @Bean(name = ["goldStandardPublicationClass"])
    fun goldStandardPublicationClass(): Class<GoldStandardPublication> = GoldStandardPublication::class.java

    @Bean(name = ["goldStandardBookmarkClass"])
    fun goldStandardBookmarkClass(): Class<GoldStandardBookmark> = GoldStandardBookmark::class.java

    @Bean(name = ["metaDataProviders"])
    fun metaDataProviders(
        projectSearch: ProjectSearch,
        @Qualifier("projectClass") projectClass: Class<*>,
        @Qualifier("publicationSearch") publicationSearch: ResourceSearch<BibTex>,
        @Qualifier("publicationClass") publicationClass: Class<*>,
        @Qualifier("goldStandardPublicationSearch") goldStandardPublicationSearch: ResourceSearch<GoldStandardPublication>,
        @Qualifier("goldStandardPublicationClass") goldStandardPublicationClass: Class<*>
    ): Map<Class<*>, MetaDataProvider<*>> {
        return MetaDataProvidersFactory.build(
            projectSearch,
            goldStandardPublicationSearch,
            publicationSearch,
            projectClass,
            goldStandardPublicationClass,
            publicationClass
        )
    }

    /**
     * Ensure permission manager has sensible defaults even if properties were not applied before first access.
     */
    @Bean
    fun permissionDatabaseManagerDefaults(
        permissionDatabaseManager: PermissionDatabaseManager,
        @Value("\${database.maxQuerySize:1000}") maxQuerySize: Int,
        @Value("\${system.specialUsersTagMap:}") specialUsersTagMapRaw: String?
    ): InitializingBean = InitializingBean {
        if (permissionDatabaseManager.maxQuerySize <= 0) {
            permissionDatabaseManager.setMaxQuerySize(maxQuerySize)
        }
        val parsed = parseSpecialUsersTagMap(specialUsersTagMapRaw)
        permissionDatabaseManager.setSpecialUserTagMap(parsed)
    }

    private fun parseSpecialUsersTagMap(raw: String?): Map<String, String> {
        if (raw.isNullOrBlank()) return emptyMap()
        return try {
            ObjectMapper().readValue(raw, object : TypeReference<Map<String, String>>() {})
        } catch (e: Exception) {
            logger.warn("Failed to parse specialUsersTagMap as JSON, falling back to manual parser. Raw input: '$raw'", e)
            // Fallback: remove braces and split on commas; tolerate unresolved placeholders.
            raw.trim('{', '}')
                .split(',')
                .mapNotNull { entry ->
                    val parts = entry.split(':', limit = 2).map { it.trim().trim('"') }
                    if (parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) {
                        parts[0] to parts[1]
                    } else null
                }.toMap()
        }
    }
}
