package org.bibsonomy.api.dto

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Post visibility levels.
 *
 * Visibility is computed from the post's group memberships:
 * - PUBLIC: Post belongs to the public group (group ID 0)
 * - PRIVATE: Post belongs only to the owner's private group (group ID 1)
 * - GROUPS: Post is shared with specific custom groups
 *
 * This is a read-only field derived from the groups array.
 */
enum class Visibility(@JsonValue val value: String) {
    /** Visible to everyone (group ID 0) */
    PUBLIC("public"),

    /** Visible only to owner (group ID 1) */
    PRIVATE("private"),

    /** Visible to specific groups listed in 'groups' array */
    GROUPS("groups");

    override fun toString(): String = value
}
