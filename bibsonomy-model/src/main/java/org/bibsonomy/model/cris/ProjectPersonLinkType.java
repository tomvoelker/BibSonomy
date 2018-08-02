package org.bibsonomy.model.cris;

/**
 * all possible link types between a {@link Project} and a {@link org.bibsonomy.model.Person}
 * @author dzo
 */
public enum ProjectPersonLinkType {
	/** manages the project */
	MANAGER,

	/** works on the project */
	MEMBER;
}
