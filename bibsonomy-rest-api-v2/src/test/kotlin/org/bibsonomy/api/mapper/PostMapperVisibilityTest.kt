package org.bibsonomy.api.mapper

import org.bibsonomy.api.dto.Visibility
import org.bibsonomy.model.BibTex
import org.bibsonomy.model.Group
import org.bibsonomy.model.Post
import org.bibsonomy.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Tests for visibility determination in PostMapper.
 */
class PostMapperVisibilityTest {

    @Test
    fun `post with public group has PUBLIC visibility`() {
        val post = createTestPost(setOf(Group(0))) // Group ID 0 = public
        val dto = post.toDto()
        assertEquals(Visibility.PUBLIC, dto.visibility)
    }

    @Test
    fun `post with only private group has PRIVATE visibility`() {
        val post = createTestPost(setOf(Group(1))) // Group ID 1 = private
        val dto = post.toDto()
        assertEquals(Visibility.PRIVATE, dto.visibility)
    }

    @Test
    fun `post with custom group has GROUPS visibility`() {
        val post = createTestPost(setOf(Group(42))) // Group ID 42 = custom group
        val dto = post.toDto()
        assertEquals(Visibility.GROUPS, dto.visibility)
    }

    @Test
    fun `post with private and custom groups has GROUPS visibility`() {
        val post = createTestPost(setOf(Group(1), Group(42))) // Private + custom
        val dto = post.toDto()
        assertEquals(Visibility.GROUPS, dto.visibility)
    }

    @Test
    fun `post with no groups defaults to PUBLIC visibility`() {
        val post = createTestPost(emptySet())
        val dto = post.toDto()
        assertEquals(Visibility.PUBLIC, dto.visibility)
    }

    @Test
    fun `post with null groups defaults to PUBLIC visibility`() {
        val post = createTestPost(null)
        val dto = post.toDto()
        assertEquals(Visibility.PUBLIC, dto.visibility)
    }

    @Test
    fun `post with public and custom groups has PUBLIC visibility`() {
        val post = createTestPost(setOf(Group(0), Group(42))) // Public trumps all
        val dto = post.toDto()
        assertEquals(Visibility.PUBLIC, dto.visibility)
    }

    private fun createTestPost(groups: Set<Group>?): Post<BibTex> {
        val user = User().apply {
            name = "testuser"
            realname = "Test User"
        }

        val bibtex = BibTex().apply {
            title = "Test Publication"
            entrytype = "article"
        }

        return Post<BibTex>().apply {
            contentId = 1
            this.user = user
            resource = bibtex
            date = Date()
            this.groups = groups
        }
    }
}
