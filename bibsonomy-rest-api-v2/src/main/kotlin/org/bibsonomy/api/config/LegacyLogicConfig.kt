package org.bibsonomy.api.config

import org.bibsonomy.bibtex.BibTeXToPublicationReader
import org.bibsonomy.database.common.DBSessionFactory
import org.bibsonomy.database.managers.CRISLinkDatabaseManager
import org.bibsonomy.database.managers.PersonResourceRelationDatabaseManager
import org.bibsonomy.database.managers.ProjectDatabaseManager
import org.bibsonomy.database.managers.metadata.MetaDataProvider
import org.bibsonomy.marc.MarcToBibTexReader
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.bibsonomy.model.util.CompositeBibtexReader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Wires up the legacy DBLogic as a Spring bean so the API can reuse the
 * existing database access layer without the old webapp XML stack.
 */
@Configuration
class LegacyLogicConfig {

    @Bean
    fun bibtexReaders(): Map<String, org.bibsonomy.model.util.BibTexReader> = mapOf(
        "application/marc" to MarcToBibTexReader(),
        "application/marc:application/pica" to MarcToBibTexReader(),
        "text/bibtex" to BibTeXToPublicationReader()
    )

    @Bean
    fun bibtexReader(bibtexReaders: Map<String, org.bibsonomy.model.util.BibTexReader>): CompositeBibtexReader {
        return CompositeBibtexReader(bibtexReaders)
    }

    @Bean
    @Primary
    fun logicInterfaceFactory(
        projectDatabaseManager: ProjectDatabaseManager,
        dbSessionFactory: DBSessionFactory,
        bibtexReader: CompositeBibtexReader,
        @Qualifier("crisLinkDatabaseManger") crisLinkDatabaseManager: CRISLinkDatabaseManager,
        personResourceRelationDatabaseManager: PersonResourceRelationDatabaseManager,
        @Qualifier("metaDataProviders") metaDataProviders: Map<Class<*>, MetaDataProvider<*>>
    ): LogicInterfaceFactory =
        SpringDBLogicApiInterfaceFactory(
            projectDatabaseManager = projectDatabaseManager,
            dbSessionFactory = dbSessionFactory,
            bibtexReader = bibtexReader,
            crisLinkDatabaseManager = crisLinkDatabaseManager,
            personResourceRelationDatabaseManager = personResourceRelationDatabaseManager,
            metaDataProviders = metaDataProviders
        )
}

private class SpringDBLogicApiInterfaceFactory(
    private val projectDatabaseManager: ProjectDatabaseManager,
    private val dbSessionFactory: DBSessionFactory,
    private val bibtexReader: CompositeBibtexReader,
    private val crisLinkDatabaseManager: CRISLinkDatabaseManager,
    private val personResourceRelationDatabaseManager: PersonResourceRelationDatabaseManager,
    private val metaDataProviders: Map<Class<*>, MetaDataProvider<*>>
) : org.bibsonomy.database.DBLogicApiInterfaceFactory() {

    init {
        setDbSessionFactory(dbSessionFactory)
    }

    override fun buildLogic(): org.bibsonomy.database.DBLogic {
        val logic = SpringDBLogic()
        logic.setProjectDatabaseManager(projectDatabaseManager)
        logic.setDbSessionFactory(dbSessionFactory)
        logic.setPublicationReader(bibtexReader)
        logic.setCrisLinkDatabaseManager(crisLinkDatabaseManager)
        logic.setPersonResourceRelationManager(personResourceRelationDatabaseManager)
        logic.setMetaDataProvidersMap(metaDataProviders)
        logic.initializeSpring()
        return logic
    }
}

private class SpringDBLogic : org.bibsonomy.database.DBLogic() {
    fun initializeSpring() {
        super.initializeMaps()
    }
}
