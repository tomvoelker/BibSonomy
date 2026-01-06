package org.bibsonomy.api.service

import org.bibsonomy.api.dto.RelatedTagDto
import org.bibsonomy.api.dto.TagDetailsDto
import org.bibsonomy.api.dto.TagDto
import org.bibsonomy.api.mapper.toDto
import org.bibsonomy.api.security.BasicAuthUtils
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.QueryScope
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.model.Resource
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.server.ResponseStatusException

/**
 * Service layer for tags API.
 *
 * Returns globally popular tags from the pre-computed popular_tags table,
 * matching the legacy webapp's homepage tag cloud behavior.
 *
 * Note: SortKey.FREQUENCY (not POPULAR) is used intentionally because:
 * - FREQUENCY → GetAllTags handler → queries popular_tags table (actual user-created popular tags)
 * - POPULAR → GetPopularTags handler → queries tags table with show_tag=TRUE (curated category tags)
 */
@Service
class TagService(
    private val logic: LogicInterface,
    private val logicFactory: LogicInterfaceFactory
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Fetch multiplier when minFreq filter is active.
     * Since minFreq filtering happens in-memory after fetching, we need to
     * over-fetch to compensate for filtered items.
     */
    private companion object {
        const val MIN_FREQ_FETCH_MULTIPLIER = 3
    }

    fun listTags(
        offset: Int,
        limit: Int,
        minFreq: Int?,
        maxCount: Int?
    ): List<TagDto> {
        val logic = resolveLogicFromRequest()
        val clampedLimit = limit.coerceIn(1, 100)
        val effectiveLimit = maxCount?.coerceIn(1, clampedLimit) ?: clampedLimit
        val requestedOffset = offset.coerceAtLeast(0)

        return if (minFreq != null) {
            // When filtering by minFreq, fetch a larger batch since the filter
            // is applied in-memory. Fetch from start, filter, then paginate.
            val fetchEnd = (requestedOffset + effectiveLimit) * MIN_FREQ_FETCH_MULTIPLIER
            val tags = logic.getTags(
                Resource::class.java,
                GroupingEntity.ALL,
                null,
                null,
                null,
                null,
                QueryScope.LOCAL,
                null,
                null,
                SortKey.FREQUENCY,  // Use FREQUENCY to query popular_tags table (legacy behavior)
                null,
                null,
                0,
                fetchEnd
            )
            tags.filter { it.globalcount >= minFreq }
                .drop(requestedOffset)
                .take(effectiveLimit)
                .map { it.toDto() }
        } else {
            // No frequency filter - use direct database pagination
            val end = requestedOffset + effectiveLimit
            logic.getTags(
                Resource::class.java,
                GroupingEntity.ALL,
                null,
                null,
                null,
                null,
                QueryScope.LOCAL,
                null,
                null,
                SortKey.FREQUENCY,  // Use FREQUENCY to query popular_tags table (legacy behavior)
                null,
                null,
                requestedOffset,
                end
            ).map { it.toDto() }
        }
    }

    /**
     * Get details for a specific tag, including related tags.
     *
     * @param tagName The tag name to retrieve
     * @param relatedLimit Maximum related tags to return (default: 20)
     * @return TagDetailsDto with tag information and related tags
     * @throws ResponseStatusException 404 if tag not found
     */
    fun getTagDetails(tagName: String, relatedLimit: Int = 20): TagDetailsDto {
        val logic = resolveLogicFromRequest()

        if (tagName.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag name is required")
        }

        // Query for the specific tag
        val tags = logic.getTags(
            Resource::class.java,
            GroupingEntity.ALL,
            null,
            listOf(tagName),  // Filter by specific tag
            null,
            null,
            QueryScope.LOCAL,
            null,
            null,
            SortKey.FREQUENCY,
            null,
            null,
            0,
            1
        )

        val tag = tags?.firstOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found: $tagName")

        // Get related tags by finding tags that co-occur with this tag
        // Related tags are tags that appear on posts that also have the target tag
        val relatedTags = try {
            val cooccurringTags = logic.getTags(
                Resource::class.java,
                GroupingEntity.ALL,
                null,
                listOf(tagName),  // Posts with this tag
                null,
                null,
                QueryScope.LOCAL,
                null,
                null,
                SortKey.FREQUENCY,
                null,
                null,
                0,
                relatedLimit + 1  // +1 to potentially filter out the target tag
            )
            cooccurringTags
                ?.filter { it.name != tagName }  // Exclude the target tag itself
                ?.take(relatedLimit)
                ?.map { RelatedTagDto(name = it.name, count = it.globalcount ?: 0) }
                ?: emptyList()
        } catch (e: Exception) {
            logger.warn("Failed to get related tags for {}: {}", tagName, e.message)
            emptyList()
        }

        return TagDetailsDto(
            name = tag.name ?: tagName,
            count = tag.globalcount ?: 0,
            relatedTags = relatedTags
        )
    }

    /**
     * Resolve the LogicInterface for the current request, supporting optional auth.
     *
     * Uses BasicAuthUtils for credential decoding (shared with PostService).
     */
    private fun resolveLogicFromRequest(): LogicInterface {
        val current = logic
        val user = current.authenticatedUser
        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
        val header = request?.getHeader(HttpHeaders.AUTHORIZATION)
        if (header != null && header.startsWith(BasicAuthUtils.BASIC_PREFIX)) {
            try {
                val (username, apiKey) = BasicAuthUtils.decode(header)
                if (user?.name.isNullOrBlank() || user?.name != username) {
                    return logicFactory.getLogicAccess(username, apiKey)
                }
            } catch (e: BadCredentialsException) {
                logger.debug("Invalid Authorization header format, ignoring: {}", e.message)
                // Fall through to return current logic with default/anonymous access
            }
        }
        return current
    }
}
