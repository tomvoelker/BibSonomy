package org.bibsonomy.api.mapper

import org.bibsonomy.api.dto.*
import org.bibsonomy.model.BibTex
import org.bibsonomy.model.Bookmark
import org.bibsonomy.model.Group
import org.bibsonomy.model.Post
import org.bibsonomy.model.Resource
import org.bibsonomy.model.Tag
import org.bibsonomy.model.User
import org.bibsonomy.model.PersonName
import java.time.Instant

/**
 * Mapping functions from domain models to DTOs.
 *
 * These are explicit mapping functions that handle:
 * - Nullability conversion (Java → Kotlin)
 * - Date conversion (java.util.Date → java.time.Instant)
 * - Group visibility mapping
 *
 * NEVER expose domain models directly in the API.
 */

/**
 * Convert a Post domain model to a PostDto.
 */
fun Post<out Resource>.toDto(): PostDto {
    val contentId = this.contentId
        ?: throw IllegalStateException("Post contentId cannot be null")

    val user = this.user
        ?: throw IllegalStateException("Post user cannot be null")

    val resource = this.resource
        ?: throw IllegalStateException("Post resource cannot be null")

    val createdAt = this.date?.toInstant()
        ?: throw IllegalStateException("Post date cannot be null (contentId: $contentId, user: ${user.name})")

    val updatedAt = this.changeDate?.toInstant()

    // Determine visibility based on groups
    val visibility = determineVisibility(this.groups)

    return PostDto(
        id = contentId,
        user = user.toRefDto(),
        resource = resource.toDto(),
        description = this.description?.takeIf { it.isNotBlank() },
        tags = this.tags?.map { it.toDto() } ?: emptyList(),
        groups = this.groups?.map { it.toRefDto() } ?: emptyList(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        visibility = visibility
    )
}

/**
 * Convert a Resource to a ResourceDto.
 */
fun Resource.toDto(): ResourceDto {
    return when (this) {
        is Bookmark -> this.toDto()
        is BibTex -> this.toDto()
        else -> throw IllegalArgumentException("Unknown resource type: ${this::class.java.name}")
    }
}

/**
 * Convert a Bookmark to BookmarkDto.
 */
fun Bookmark.toDto(): BookmarkDto {
    return BookmarkDto(
        url = this.url ?: "",
        title = this.title ?: "",
        urlHash = this.interHash ?: this.intraHash
    )
}

/**
 * Convert a BibTex to BibTexDto.
 */
fun BibTex.toDto(): BibTexDto {
    return BibTexDto(
        bibtexKey = this.bibtexKey,
        entryType = this.entrytype ?: "misc",
        title = this.title ?: "",
        authors = this.author?.map { it.toDto() },
        editors = this.editor?.map { it.toDto() },
        year = this.year?.toIntOrNull(),
        month = this.month,
        journal = this.journal,
        booktitle = this.booktitle,
        publisher = this.publisher,
        volume = this.volume,
        number = this.number,
        pages = this.pages,
        doi = this.miscFields?.get("doi"),
        url = this.url,
        abstract = this.`abstract`
    )
}

/**
 * Convert a Person to PersonNameDto.
 */
fun PersonName.toDto(): PersonNameDto {
    val display = listOfNotNull(this.lastName, this.firstName)
        .joinToString(", ")
    return PersonNameDto(
        name = display,
        firstName = this.firstName?.takeIf { it.isNotBlank() },
        lastName = this.lastName?.takeIf { it.isNotBlank() }
    )
}

/**
 * Convert a User to UserRefDto.
 */
fun User.toRefDto(): UserRefDto {
    return UserRefDto(
        username = this.name ?: "",
        realName = this.realname?.takeIf { it.isNotBlank() }
    )
}

/**
 * Convert a Tag to TagDto.
 */
fun Tag.toDto(): TagDto {
    return TagDto(
        name = this.name ?: "",
        count = this.globalcount,
        countPublic = this.usercount
    )
}

/**
 * Convert a Group to GroupRefDto.
 */
fun Group.toRefDto(): GroupRefDto {
    return GroupRefDto(
        name = this.name ?: "",
        displayName = this.realname?.takeIf { it.isNotBlank() }
    )
}

/**
 * Determine post visibility based on groups.
 *
 * BibSonomy convention:
 * - Group ID 0 (public) → Visibility.PUBLIC
 * - Group ID 1 (private) → Visibility.PRIVATE
 * - Other groups → Visibility.GROUPS
 */
private fun determineVisibility(groups: Set<Group>?): Visibility {
    if (groups == null || groups.isEmpty()) {
        return Visibility.PUBLIC
    }

    val groupIds = groups.mapNotNull { it.groupId }.toSet()

    return when {
        groupIds.contains(0) -> Visibility.PUBLIC
        groupIds.contains(1) && groupIds.size == 1 -> Visibility.PRIVATE
        else -> Visibility.GROUPS
    }
}
