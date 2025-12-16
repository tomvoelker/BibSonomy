package org.bibsonomy.api.config

import org.bibsonomy.common.information.JobInformation
import org.bibsonomy.database.common.DBSession
import org.bibsonomy.database.params.BibTexExtraParam
import org.bibsonomy.database.params.ClipboardParam
import org.bibsonomy.database.params.DocumentParam
import org.bibsonomy.database.params.InboxParam
import org.bibsonomy.database.params.UserParam
import org.bibsonomy.database.plugin.DatabasePlugin
import org.bibsonomy.model.BibTex
import org.bibsonomy.model.Bookmark
import org.bibsonomy.model.DiscussionItem
import org.bibsonomy.model.Group
import org.bibsonomy.model.GroupMembership
import org.bibsonomy.model.Person
import org.bibsonomy.model.PersonName
import org.bibsonomy.model.ResourcePersonRelation
import org.bibsonomy.model.Post
import org.bibsonomy.model.Resource
import org.bibsonomy.model.User
import org.bibsonomy.model.cris.CRISLink
import org.bibsonomy.model.cris.Project
import org.bibsonomy.model.enums.GoldStandardRelation
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.services.information.InformationService
import org.bibsonomy.database.managers.chain.Chain
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Stubs out gold-standard related infrastructure and plugin registry so
 * legacy beans can wire without pulling in the full mail/templating stack.
 */
@Configuration(proxyBeanMethods = false)
class LegacyGoldStandardStubConfig {

    @Bean
    fun goldStandardDefinitionStripper(): BeanDefinitionRegistryPostProcessor =
        BeanDefinitionRegistryPostProcessor { registry ->
            val toRemove = registry.beanDefinitionNames
                .filter {
                    it.startsWith("goldStandard")
                        && it !in setOf(
                            "goldStandardInformationService",
                            "goldStandardPublicationDatabaseManager",
                            "goldStandardBookmarkDatabaseMananger",
                            "goldStandardPublicationClass",
                            "goldStandardBookmarkClass",
                            "goldStandardPublicationSearch",
                            "goldStandardBookmarkSearch",
                            "goldStandardPublicationChain",
                            "goldStandardBookmarkChain",
                            "goldStandardPublicationStatisticsChain",
                            "goldStandardBookmarkStatisticsChain"
                        )
                }
            toRemove.forEach { registry.removeBeanDefinition(it) }
        }

    @Bean(name = ["goldStandardInformationService"])
    fun goldStandardInfoServiceStub(): InformationService = object : InformationService {
        override fun createdPost(username: String?, post: Post<out Resource>?) {
            // no-op
        }

        override fun updatedPost(username: String?, post: Post<out Resource>?) {
            // no-op
        }
    }

    @Bean(name = ["databasePluginManager"])
    fun databasePluginManagerStub(): DatabasePlugin = object : DatabasePlugin {
        override fun onPublicationInsert(post: Post<out BibTex>?, loggedinUser: User?, session: DBSession?): List<JobInformation> = emptyList()
        override fun onPublicationDelete(contentId: Int, session: DBSession?) {}
        override fun onPublicationUpdate(newContentId: Int, contentId: Int, session: DBSession?) {}
        override fun onPublicationMassUpdate(username: String?, groupId: Int, session: DBSession?) {}
        override fun onGoldStandardCreate(interhash: String?, session: DBSession?) {}
        override fun onGoldStandardUpdate(newContentId: Int, contentId: Int, newInterhash: String?, interhash: String?, session: DBSession?) {}
        override fun onGoldStandardUpdated(interhash: String?, loggedinUser: User?, session: DBSession?) {}
        override fun onGoldStandardPublicationReferenceCreate(userName: String?, interHash_publication: String?, interHash_reference: String?, interHash_relation: String?) {}
        override fun onGoldStandardRelationDelete(userName: String?, interHash_publication: String?, interHash_reference: String?, interHashRelation: GoldStandardRelation?, session: DBSession?) {}
        override fun onGoldStandardDelete(interhash: String?, loggedinUser: User?, session: DBSession?) {}
        override fun onBookmarkInsert(post: Post<out Resource>?, logginUser: User?, session: DBSession?): List<JobInformation> = emptyList()
        override fun onBookmarkDelete(contentId: Int, session: DBSession?) {}
        override fun onBookmarkUpdate(newContentId: Int, contentId: Int, session: DBSession?) {}
        override fun onBookmarkMassUpdate(userName: String?, groupId: Int, session: DBSession?) {}
        override fun onTagRelationDelete(upperTagName: String?, lowerTagName: String?, userName: String?, session: DBSession?) {}
        override fun onConceptDelete(conceptName: String?, userName: String?, session: DBSession?) {}
        override fun onTagDelete(contentId: Int, session: DBSession?) {}
        override fun onUserInsert(userName: String?, session: DBSession?) {}
        override fun onUserDelete(userName: String?, session: DBSession?) {}
        override fun onDeleteFellowship(param: UserParam?, session: DBSession?) {}
        override fun onDeleteFriendship(param: UserParam?, session: DBSession?) {}
        override fun onDeleteClipboardItem(param: ClipboardParam?, session: DBSession?) {}
        override fun onDeleteAllClipboardItems(userName: String?, session: DBSession?) {}
        override fun onDiscussionUpdate(interHash: String?, comment: DiscussionItem?, oldComment: DiscussionItem?, session: DBSession?) {}
        override fun onDiscussionMassUpdate(username: String?, groupId: Int, session: DBSession?) {}
        override fun onDiscussionItemDelete(interHash: String?, deletedComment: DiscussionItem?, session: DBSession?) {}
        override fun onDocumentDelete(deletedDocumentParam: DocumentParam?, session: DBSession?) {}
        override fun onDocumentUpdate(updatedDocumentParam: DocumentParam?, session: DBSession?) {}
        override fun onInboxMailDelete(deletedInboxMessageParam: InboxParam?, session: DBSession?) {}
        override fun onBibTexExtraDelete(deletedBibTexExtraParam: BibTexExtraParam?, session: DBSession?) {}
        override fun onPersonUpdate(oldPerson: Person?, newPerson: Person?, session: DBSession?) {}
        override fun onPersonUpdateByUserName(userName: String?, session: DBSession?) {}
        override fun onPersonDelete(person: Person?, user: User?, session: DBSession?) {}
        override fun onPubPersonDelete(rel: ResourcePersonRelation?, loginUser: User?, session: DBSession?) {}
    }

    @Bean(name = ["goldStandardPublicationDatabaseManager"])
    fun goldStandardPublicationDatabaseManagerStub(): Any = Any()

    @Bean(name = ["goldStandardBookmarkDatabaseMananger"])
    fun goldStandardBookmarkDatabaseManagerStub(): Any = Any()

    @Bean(name = ["goldStandardPublicationChain"])
    fun goldStandardPublicationChainStub(): Chain<Any, Any> = Chain<Any, Any>().apply { setElements(emptyList()) }

    @Bean(name = ["goldStandardBookmarkChain"])
    fun goldStandardBookmarkChainStub(): Chain<Any, Any> = Chain<Any, Any>().apply { setElements(emptyList()) }

    @Bean(name = ["goldStandardPublicationStatisticsChain"])
    fun goldStandardPublicationStatisticsChainStub(): Chain<Any, Any> = Chain<Any, Any>().apply { setElements(emptyList()) }

    @Bean(name = ["goldStandardBookmarkStatisticsChain"])
    fun goldStandardBookmarkStatisticsChainStub(): Chain<Any, Any> = Chain<Any, Any>().apply { setElements(emptyList()) }
}
