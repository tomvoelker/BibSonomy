package org.bibsonomy.api.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant

/**
 * DTO for a post (bookmark or publication).
 *
 * Decoupled from the domain model (org.bibsonomy.model.Post).
 * Never expose domain POJOs directly in the API.
 */
data class PostDto(
    val id: Int,
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
 */
data class BookmarkDto(
    val url: String,
    val title: String,
    val urlHash: String?
) : ResourceDto

/**
 * DTO for a BibTeX publication resource.
 */
data class BibTexDto(
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
