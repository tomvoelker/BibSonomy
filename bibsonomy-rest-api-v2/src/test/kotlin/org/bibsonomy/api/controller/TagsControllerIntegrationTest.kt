package org.bibsonomy.api.controller

import org.bibsonomy.api.config.SecurityConfig
import org.bibsonomy.api.dto.TagDetailsDto
import org.bibsonomy.api.dto.TagDto
import org.bibsonomy.api.security.LegacyAuthenticationConfiguration
import org.bibsonomy.api.security.LegacyBasicAuthenticationProvider
import org.bibsonomy.api.service.TagService
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.Role
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.model.Tag
import org.bibsonomy.model.User
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

/**
 * Integration tests for the TagsController.
 *
 * Tests the /api/v2/tags endpoint which returns globally popular tags
 * sorted by frequency (descending), suitable for tag cloud rendering.
 */
@SpringBootTest(
    classes = [TagsControllerTestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
    ]
)
class TagsControllerIntegrationTest(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @Test
    fun `GET tags returns 200 with list of popular tags`() {
        val response = restTemplate.exchange(
            "/api/v2/tags",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()
        assertTrue(tags.isNotEmpty(), "Expected at least one tag")
    }

    @Test
    fun `tags are sorted by popularity descending`() {
        val response = restTemplate.exchange(
            "/api/v2/tags",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()

        // Verify tags are sorted by count descending
        for (i in 0 until tags.size - 1) {
            val current = tags[i].count ?: 0
            val next = tags[i + 1].count ?: 0
            assertTrue(current >= next, "Tags should be sorted by count descending: ${tags[i].name}($current) >= ${tags[i+1].name}($next)")
        }
    }

    @Test
    fun `tags include global count in count field`() {
        val response = restTemplate.exchange(
            "/api/v2/tags",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()

        // All tags should have a count (global count)
        tags.forEach { tag ->
            assertTrue(tag.count != null && tag.count!! > 0, "Tag ${tag.name} should have a count")
        }
    }

    @Test
    fun `limit parameter restricts number of tags returned`() {
        val response = restTemplate.exchange(
            "/api/v2/tags?limit=5",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()
        assertEquals(5, tags.size, "Expected exactly 5 tags when limit=5")
    }

    @Test
    fun `offset parameter skips tags`() {
        // First get all tags
        val allResponse = restTemplate.exchange(
            "/api/v2/tags?limit=10",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )
        val allTags = allResponse.body ?: emptyList()

        // Then get with offset
        val offsetResponse = restTemplate.exchange(
            "/api/v2/tags?offset=2&limit=3",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )
        val offsetTags = offsetResponse.body ?: emptyList()

        assertEquals(HttpStatus.OK, offsetResponse.statusCode)
        assertEquals(3, offsetTags.size, "Expected 3 tags with offset=2, limit=3")

        // The first tag with offset=2 should be the third tag from the full list
        if (allTags.size >= 3 && offsetTags.isNotEmpty()) {
            assertEquals(allTags[2].name, offsetTags[0].name, "First tag with offset=2 should match third tag from full list")
        }
    }

    @Test
    fun `minFreq parameter filters by minimum frequency`() {
        val response = restTemplate.exchange(
            "/api/v2/tags?minFreq=500",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()

        // All returned tags should have count >= 500
        tags.forEach { tag ->
            assertTrue((tag.count ?: 0) >= 500, "Tag ${tag.name} count ${tag.count} should be >= 500")
        }
    }

    @Test
    fun `default limit returns exactly 20 tags when more are available`() {
        val response = restTemplate.exchange(
            "/api/v2/tags",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()
        // We have 25 mock tags and default limit is 20, so should return exactly 20
        assertEquals(20, tags.size, "Default limit of 20 should be enforced when more tags available")
    }

    @Test
    fun `maxCount parameter limits tags by count ceiling`() {
        val response = restTemplate.exchange(
            "/api/v2/tags?maxCount=10",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()
        // maxCount=10 should cap the number of tags returned to 10
        assertEquals(10, tags.size, "maxCount=10 should return exactly 10 tags")
    }

    @Test
    fun `maxCount combined with limit uses the smaller value`() {
        // maxCount=5 with default limit=20 should return 5
        val response = restTemplate.exchange(
            "/api/v2/tags?maxCount=5",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()
        assertEquals(5, tags.size, "maxCount=5 should limit to 5 tags")

        // limit=3 with maxCount=10 should return 3
        val response2 = restTemplate.exchange(
            "/api/v2/tags?limit=3&maxCount=10",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response2.statusCode)
        val tags2 = response2.body ?: emptyList()
        assertEquals(3, tags2.size, "limit=3 with maxCount=10 should return 3 tags")
    }

    @Test
    fun `maxCount combined with offset works correctly`() {
        val response = restTemplate.exchange(
            "/api/v2/tags?offset=5&maxCount=3",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()
        assertEquals(3, tags.size, "offset=5 with maxCount=3 should return 3 tags")

        // Verify the offset was applied by checking tag names
        // With offset=5, the first tag should be "tensorflow" (6th in the list, 0-indexed position 5)
        assertEquals("tensorflow", tags[0].name, "First tag with offset=5 should be 'tensorflow'")
    }

    @Test
    fun `countPublic field is present and valid`() {
        val response = restTemplate.exchange(
            "/api/v2/tags",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()

        // All tags should have countPublic set and it should be >= 0
        tags.forEach { tag ->
            assertTrue(tag.countPublic != null, "Tag ${tag.name} should have countPublic")
            assertTrue(tag.countPublic!! >= 0, "Tag ${tag.name} countPublic should be >= 0")

            // countPublic should be <= count when count is present
            if (tag.count != null) {
                assertTrue(
                    tag.countPublic!! <= tag.count!!,
                    "Tag ${tag.name} countPublic (${tag.countPublic}) should be <= count (${tag.count})"
                )
            }
        }
    }

    @Test
    fun `anonymous access is permitted`() {
        // Tags endpoint should be publicly accessible without authentication
        val response = restTemplate.getForEntity(
            "/api/v2/tags",
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `tag names are not blank`() {
        val response = restTemplate.exchange(
            "/api/v2/tags",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()

        tags.forEach { tag ->
            assertTrue(tag.name.isNotBlank(), "Tag name should not be blank")
        }
    }

    @Test
    fun `GET tag details returns 200 for existing tag`() {
        val response = restTemplate.getForEntity(
            "/api/v2/tags/machine-learning",
            TagDetailsDto::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("machine-learning", response.body?.name)
        assertTrue((response.body?.count ?: 0) > 0, "Tag count should be positive")
    }

    @Test
    fun `GET tag details includes related tags`() {
        val response = restTemplate.getForEntity(
            "/api/v2/tags/machine-learning?relatedLimit=5",
            TagDetailsDto::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.relatedTags)
        val relatedTags = response.body?.relatedTags ?: emptyList()
        assertTrue(relatedTags.isNotEmpty(), "Related tags should not be empty")
        assertTrue(relatedTags.size <= 5, "Related tags should respect the relatedLimit=5 parameter")
    }

    // Note: The 404 test for non-existing tags requires more sophisticated mocking
    // because the stub factory returns empty list which causes 404 after the security filter passes
}

/**
 * Test application configuration for TagsController tests.
 * Uses a mock LogicInterface to return predefined popular tags.
 */
@org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
@Configuration
@Import(
    SecurityConfig::class,
    LegacyAuthenticationConfiguration::class,
    TagsController::class,
    TagService::class,
    StubTagsBeans::class
)
class TagsControllerTestApplication

/**
 * Stub LogicInterfaceFactory that returns mock popular tags.
 */
class StubTagsLogicFactory : LogicInterfaceFactory {
    override fun getLogicAccess(loginName: String?, apiKey: String?): LogicInterface {
        val logic = Mockito.mock(LogicInterface::class.java)
        val user = User().apply {
            name = loginName ?: "guest"
            role = Role.DEFAULT
        }
        Mockito.`when`(logic.authenticatedUser).thenReturn(user)

        // Mock getTags for general listing (no specific tags filter)
        // The TagService calls with 14 parameters including QueryScope
        Mockito.`when`(
            logic.getTags(
                any(),        // resourceType (Class)
                any(),        // grouping (GroupingEntity)
                isNull(),     // groupingName (null for ALL)
                isNull(),     // tags (List<String>)
                isNull(),     // hash (String)
                isNull(),     // search (String)
                any(),        // queryScope (QueryScope)
                isNull(),     // regex (String)
                isNull(),     // relation (TagSimilarity)
                any(),        // sortKey (SortKey)
                isNull(),     // startDate (Date)
                isNull(),     // endDate (Date)
                anyInt(),     // start (int)
                anyInt()      // end (int)
            )
        ).thenAnswer { invocation ->
            val start = invocation.arguments[12] as Int
            val end = invocation.arguments[13] as Int
            val limit = end - start

            // Return mock tags sorted by globalcount (descending)
            MOCK_POPULAR_TAGS.drop(start).take(limit)
        }

        // Mock getTags with specific tag filter (for tag details)
        Mockito.`when`(
            logic.getTags(
                any(),        // resourceType (Class)
                any(),        // grouping (GroupingEntity)
                isNull(),     // groupingName (null for ALL)
                any<List<String>>(),  // tags (List<String>) - specific tag name
                isNull(),     // hash (String)
                isNull(),     // search (String)
                any(),        // queryScope (QueryScope)
                isNull(),     // regex (String)
                isNull(),     // relation (TagSimilarity)
                any(),        // sortKey (SortKey)
                isNull(),     // startDate (Date)
                isNull(),     // endDate (Date)
                anyInt(),     // start (int)
                anyInt()      // end (int)
            )
        ).thenAnswer { invocation ->
            val tagsFilter = invocation.arguments[3] as? List<*>
            val start = invocation.arguments[12] as Int
            val end = invocation.arguments[13] as Int
            val limit = end - start

            if (tagsFilter != null && tagsFilter.isNotEmpty()) {
                val tagName = tagsFilter.first().toString()
                // Return specific tag if it exists
                val tag = MOCK_POPULAR_TAGS.find { it.name == tagName }
                if (tag != null) {
                    // Return the tag and some related tags
                    listOf(tag) + MOCK_POPULAR_TAGS.filter { it.name != tagName }.take(limit - 1)
                } else {
                    emptyList()
                }
            } else {
                MOCK_POPULAR_TAGS.drop(start).take(limit)
            }
        }

        return logic
    }

    companion object {
        /**
         * Mock popular tags pre-sorted by globalcount descending.
         * This simulates what the real database would return.
         */
        val MOCK_POPULAR_TAGS: List<Tag> = listOf(
            createTag("machine-learning", 3421),
            createTag("deep-learning", 2845),
            createTag("python", 2180),
            createTag("data-science", 1950),
            createTag("neural-networks", 1720),
            createTag("tensorflow", 1510),
            createTag("nlp", 1345),
            createTag("computer-vision", 1290),
            createTag("pytorch", 1205),
            createTag("pandas", 1140),
            createTag("numpy", 1090),
            createTag("keras", 990),
            createTag("scikit-learn", 875),
            createTag("statistics", 820),
            createTag("classification", 780),
            createTag("regression", 695),
            createTag("clustering", 620),
            createTag("visualization", 590),
            createTag("jupyter", 545),
            createTag("research", 520),
            createTag("ai", 495),
            createTag("algorithms", 470),
            createTag("optimization", 445),
            createTag("transformers", 420),
            createTag("bert", 395)
        )

        private fun createTag(name: String, count: Int): Tag = Tag().apply {
            this.name = name
            this.globalcount = count
            this.usercount = (count * 0.9).toInt()  // Simulated public count
        }
    }
}

@Configuration
class StubTagsBeans {
    @Bean
    fun stubLogicInterfaceFactory(): LogicInterfaceFactory = StubTagsLogicFactory()

    @Bean
    fun legacyBasicAuthenticationProvider(factory: LogicInterfaceFactory): LegacyBasicAuthenticationProvider =
        LegacyBasicAuthenticationProvider(factory)
}
