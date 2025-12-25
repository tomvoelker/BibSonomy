package org.bibsonomy.api.service

import org.bibsonomy.api.dto.TagDto
import org.bibsonomy.api.mapper.toDto
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.QueryScope
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.model.Resource
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Service layer for tags API.
 */
@Service
class TagService(
    private val logic: LogicInterface,
    private val logicFactory: LogicInterfaceFactory
) {

    fun listTags(
        offset: Int,
        limit: Int,
        minFreq: Int?,
        maxCount: Int?
    ): List<TagDto> {
        val logic = resolveLogicFromRequest()
        val clampedLimit = limit.coerceIn(1, 100)
        val defaultMax = 50
        val usesMinFreq = (minFreq ?: 0) > 0
        val effectiveLimit = if (usesMinFreq) {
            defaultMax
        } else {
            maxCount?.coerceIn(1, clampedLimit) ?: clampedLimit
        }
        val start = offset.coerceAtLeast(0)
        val end = start + effectiveLimit
        val sortKey = if (!usesMinFreq) SortKey.FREQUENCY else null

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
            sortKey,
            null,
            null,
            start,
            end
        )

        val filtered = if (usesMinFreq) {
            val threshold = minFreq ?: 0
            tags.filter { (it.usercount ?: it.globalcount ?: 0) >= threshold }
        } else {
            tags
        }

        return filtered.map { it.toDto() }
    }

    private fun resolveLogicFromRequest(): LogicInterface {
        val current = logic
        val user = current.authenticatedUser
        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
        val header = request?.getHeader(HttpHeaders.AUTHORIZATION)
        if (header != null && header.startsWith("Basic ")) {
            val (username, apiKey) = decodeBasic(header)
            if (user?.name.isNullOrBlank() || user?.name != username) {
                return logicFactory.getLogicAccess(username, apiKey)
            }
        }
        return current
    }

    private fun decodeBasic(header: String): Pair<String, String> {
        val base64Token = header.removePrefix("Basic ").trim()
        val decoded = String(Base64.getDecoder().decode(base64Token), StandardCharsets.UTF_8)
        val delim = decoded.indexOf(':')
        require(delim >= 0) { "Invalid basic authentication token" }
        val username = decoded.substring(0, delim)
        val apiKey = decoded.substring(delim + 1)
        return username to apiKey
    }
}
