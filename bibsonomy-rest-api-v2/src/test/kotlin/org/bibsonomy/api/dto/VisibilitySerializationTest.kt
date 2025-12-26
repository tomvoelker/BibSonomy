package org.bibsonomy.api.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests for Visibility enum JSON serialization/deserialization.
 */
class VisibilitySerializationTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `PUBLIC serializes to lowercase string`() {
        val json = objectMapper.writeValueAsString(Visibility.PUBLIC)
        assertEquals("\"public\"", json)
    }

    @Test
    fun `PRIVATE serializes to lowercase string`() {
        val json = objectMapper.writeValueAsString(Visibility.PRIVATE)
        assertEquals("\"private\"", json)
    }

    @Test
    fun `GROUPS serializes to lowercase string`() {
        val json = objectMapper.writeValueAsString(Visibility.GROUPS)
        assertEquals("\"groups\"", json)
    }

    @Test
    fun `lowercase string deserializes to PUBLIC`() {
        val visibility = objectMapper.readValue("\"public\"", Visibility::class.java)
        assertEquals(Visibility.PUBLIC, visibility)
    }

    @Test
    fun `lowercase string deserializes to PRIVATE`() {
        val visibility = objectMapper.readValue("\"private\"", Visibility::class.java)
        assertEquals(Visibility.PRIVATE, visibility)
    }

    @Test
    fun `lowercase string deserializes to GROUPS`() {
        val visibility = objectMapper.readValue("\"groups\"", Visibility::class.java)
        assertEquals(Visibility.GROUPS, visibility)
    }

    @Test
    fun `PostDto with visibility serializes correctly`() {
        val postDto = PostDto(
            id = 1,
            user = UserRefDto("testuser", "Test User"),
            resource = BookmarkDto("https://example.com", "Example", "hash123"),
            description = "Test post",
            tags = emptyList(),
            groups = emptyList(),
            createdAt = java.time.Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = null,
            visibility = Visibility.PUBLIC
        )

        val json = objectMapper.writeValueAsString(postDto)
        assert(json.contains("\"visibility\":\"public\""))
    }
}
