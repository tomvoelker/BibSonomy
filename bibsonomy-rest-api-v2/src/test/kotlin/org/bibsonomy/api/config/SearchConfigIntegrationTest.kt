package org.bibsonomy.api.config

import org.assertj.core.api.Assertions.assertThat
import org.bibsonomy.api.ApiApplication
import org.bibsonomy.database.managers.metadata.MetaDataProvider
import org.bibsonomy.model.BibTex
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery
import org.bibsonomy.services.searcher.ProjectSearch
import org.bibsonomy.services.searcher.ResourceSearch
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

@Disabled("Search stack temporarily stubbed while legacy wiring is rebuilt")
@SpringBootTest(
    classes = [ApiApplication::class],
    properties = ["spring.main.allow-bean-definition-overriding=true"]
)
class SearchConfigIntegrationTest(
    @Autowired @Qualifier("publicationSearch") private val publicationSearch: ResourceSearch<BibTex>,
    @Autowired @Qualifier("projectSearch") private val projectSearch: ProjectSearch,
    @Autowired @Qualifier("metaDataProviders") private val metaDataProviders: Map<Class<*>, MetaDataProvider<*>>
) {

    @Test
    fun `publication search bean is wired`() {
        assertThat(publicationSearch).isNotNull
        assertThat(publicationSearch::class.java.simpleName.lowercase()).contains("search")
    }

    @Test
    fun `project search bean is wired`() {
        assertThat(projectSearch).isNotNull
    }

    @Test
    fun `metadata providers contain distinct field provider`() {
        assertThat(metaDataProviders).isNotEmpty
        assertThat(metaDataProviders).containsKey(DistinctFieldQuery::class.java)
    }
}
