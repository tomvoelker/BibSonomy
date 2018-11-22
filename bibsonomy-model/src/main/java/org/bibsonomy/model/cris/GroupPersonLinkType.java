package org.bibsonomy.model.cris;

/**
 * all possible link types between a {@link org.bibsonomy.model.Group} and a {@link org.bibsonomy.model.Person}
 * @author dzo
 */
public enum GroupPersonLinkType implements CRISLinkType {

	/** the leader of the group or organisation */
	LEADER,

	/** all other members of the group */
	MEMBER

}
