package org.bibsonomy.api.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant

/**
 * DTO for a post (bookmark or publication).
 *
 * Decoupled from the domain model (org.bibsonomy.model.Post).
 * Never expose domain POJOs directly in the API.
 *
 * **Identifier Design**: Uses resource hash (String) as the primary identifier,
 * matching the legacy REST API v1 behavior. The hash represents the resource content
 * itself (intraHash or interHash from the domain model), enabling natural deduplication
 * across users and RESTful resource identification.
 *
 * To retrieve a specific post: `GET /api/v2/posts/{id}?user={username}`
 * The hash alone may be ambiguous if multiple users have the same resource,
 * so the optional `user` parameter disambiguates.
 */
data class PostDto(
    val id: String,
    val user: UserRefDto,
    val resource: ResourceDto,
    val description: String?,
    val tags: List<TagDto>,
    val groups: List<GroupRefDto>,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val visibility: Visibility
)

/**
 * Base interface for resource types (bookmark or bibtex).
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "resourceType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = BookmarkDto::class, name = "bookmark"),
    JsonSubTypes.Type(value = BibTexDto::class, name = "bibtex")
)
sealed interface ResourceDto

/**
 * DTO for a bookmark resource.
 *
 * @property url The bookmark URL
 * @property title The bookmark title
 * @property urlHash MD5 hash of the URL (matches PostDto.id for bookmarks).
 *                   This is the resource identifier used in API endpoints.
 */
data class BookmarkDto(
    val url: String,
    val title: String,
    val urlHash: String
) : ResourceDto

/**
 * DTO for a BibTeX publication resource.
 *
 * @property resourceHash Hash of the publication content (matches PostDto.id for publications).
 *                        This is the resource identifier used in API endpoints. Calculated from
 *                        bibliographic fields (title, authors, year, etc.) for deduplication.
 * @property bibtexKey Optional BibTeX citation key (e.g., "Smith2020")
 * @property entryType BibTeX entry type (e.g., "article", "book", "inproceedings")
 * @property title Publication title (required)
 */
data class BibTexDto(
    val resourceHash: String,
    val bibtexKey: String?,
    val entryType: String,
    val title: String,
    val authors: List<PersonNameDto>?,
    val editors: List<PersonNameDto>?,
    val year: Int?,
    val month: String?,
    val journal: String?,
    val booktitle: String?,
    val publisher: String?,
    val volume: String?,
    val number: String?,
    val pages: String?,
    val doi: String?,
    val url: String?,
    val abstract: String?
) : ResourceDto

/**
 * DTO for a person name (author or editor).
 */
data class PersonNameDto(
    val name: String,
    val firstName: String?,
    val lastName: String?
)
