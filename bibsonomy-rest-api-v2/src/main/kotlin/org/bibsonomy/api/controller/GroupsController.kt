package org.bibsonomy.api.controller

import org.bibsonomy.api.dto.GroupDetailsDto
import org.bibsonomy.api.dto.GroupDto
import org.bibsonomy.api.service.GroupService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for groups endpoints.
 *
 * Provides group listing and details retrieval.
 */
@RestController
@RequestMapping("/api/v2/groups")
class GroupsController(
    private val groupService: GroupService
) {

    /**
     * GET /api/v2/groups - List groups
     *
     * Returns groups the authenticated user is a member of.
     * Requires authentication.
     *
     * @return List of GroupDto
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listGroups(): ResponseEntity<List<GroupDto>> {
        val groups = groupService.getUserGroups()
        return ResponseEntity.ok(groups)
    }

    /**
     * GET /api/v2/groups/{groupName} - Get group details
     *
     * Returns details for a specific group.
     *
     * @param groupName The name of the group to retrieve
     * @return GroupDetailsDto with group information
     */
    @GetMapping("/{groupName}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGroupDetails(@PathVariable groupName: String): ResponseEntity<GroupDetailsDto> {
        val details = groupService.getGroupDetails(groupName)
        return ResponseEntity.ok(details)
    }
}
