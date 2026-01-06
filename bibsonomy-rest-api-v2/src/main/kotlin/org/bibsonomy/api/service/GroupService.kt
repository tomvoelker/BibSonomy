package org.bibsonomy.api.service

import org.bibsonomy.api.dto.GroupDetailsDto
import org.bibsonomy.api.dto.GroupDto
import org.bibsonomy.api.security.BasicAuthUtils
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.Privlevel
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.model.Resource
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.bibsonomy.model.logic.query.GroupQuery
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.server.ResponseStatusException

/**
 * Service for group operations.
 *
 * Provides group listing and details retrieval.
 */
@Service
class GroupService(
    private val logic: LogicInterface,
    private val logicFactory: LogicInterfaceFactory
) {
    private val log = LoggerFactory.getLogger(GroupService::class.java)

    /**
     * Get groups for the authenticated user.
     *
     * Returns groups the current user is a member of.
     *
     * @return List of GroupDto
     * @throws ResponseStatusException 401 if not authenticated
     */
    fun getUserGroups(): List<GroupDto> {
        val logic = resolveLogicFromRequest()
        val user = logic.authenticatedUser
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")

        if (user.name.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")
        }

        val query = GroupQuery.builder()
            .userName(user.name)
            .start(0)
            .end(100)
            .build()

        try {
            val groups = logic.getGroups(query) ?: emptyList()

            return groups.mapNotNull { group ->
                try {
                    GroupDto(
                        name = group.name ?: return@mapNotNull null,
                        description = group.description,
                        memberCount = group.memberships?.size ?: 0,
                        isPrivate = group.privlevel == Privlevel.HIDDEN || group.privlevel == Privlevel.MEMBERS
                    )
                } catch (e: Exception) {
                    log.warn("Error mapping group: {}", e.message)
                    null
                }
            }
        } catch (e: Exception) {
            log.error("Error fetching groups for user {}: {}", user.name, e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch groups")
        }
    }

    /**
     * Get details for a specific group.
     *
     * @param groupName The name of the group to retrieve
     * @return GroupDetailsDto with group information
     * @throws ResponseStatusException 404 if group not found
     */
    fun getGroupDetails(groupName: String): GroupDetailsDto {
        if (groupName.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Group name is required")
        }

        val logic = resolveLogicFromRequest()

        try {
            val group = logic.getGroupDetails(groupName, false)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: $groupName")

            // Get post count for the group
            val postCount = try {
                val stats = logic.getPostStatistics(
                    Resource::class.java,
                    GroupingEntity.GROUP,
                    groupName,
                    null,
                    null,
                    null,
                    null,
                    SortKey.NONE,
                    null,
                    null,
                    0,
                    0
                )
                stats?.count ?: 0
            } catch (e: Exception) {
                log.warn("Failed to get post count for group {}: {}", groupName, e.message)
                0
            }

            return GroupDetailsDto(
                name = group.name ?: groupName,
                description = group.description,
                memberCount = group.memberships?.size ?: 0,
                isPrivate = group.privlevel == Privlevel.HIDDEN || group.privlevel == Privlevel.MEMBERS,
                postCount = postCount
            )
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            log.error("Error fetching group details for {}: {}", groupName, e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch group details")
        }
    }

    /**
     * Resolve the LogicInterface for the current request, supporting optional auth.
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
                log.debug("Invalid Authorization header format, ignoring: {}", e.message)
            }
        }
        return current
    }
}
