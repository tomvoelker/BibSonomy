package org.bibsonomy.database.common.enums;

/**
 * the reason why an entity was logged
 *
 * @author dzo
 */
public enum LogReason {

	/** the entity was updated */
	UPDATED,

	/** one linked entity was updated */
	LINKED_ENTITY_UPDATE,

	/** the entity was deleted */
	DELETED
}
