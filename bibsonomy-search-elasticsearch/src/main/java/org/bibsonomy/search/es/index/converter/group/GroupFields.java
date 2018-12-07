package org.bibsonomy.search.es.index.converter.group;

/**
 * all fields of a group document
 *
 * @author dzo
 */
public interface GroupFields {

	/** the group name */
	String NAME = "name";

	/** the real name of the group */
	String REALNAME = "realname";

	/** the real name (lower case for prefix matching) */
	String REALNAME_LOWERCASE = "realname_lowercase";

	/** the external id of the group */
	String EXTERNAL_ID = "external_id";

	/** flag if the group is a organization */
	String ORGANIZATION = "organization";

	/** homepage of the group */
	String HOMEPAGE = "homepage";

	/** the parent name */
	String PARENT_NAME = "parent_name";
}
