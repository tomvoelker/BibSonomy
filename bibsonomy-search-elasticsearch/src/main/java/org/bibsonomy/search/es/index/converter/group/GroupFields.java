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
	String REALNAME_PREFIX = "realname_prefix";

	/** the real name for sorting */
	String REALNAME_SORT = "sort";

	/** the external id of the group */
	String INTERNAL_ID = "internal_id";

	/** flag if the group is a organization */
	String ORGANIZATION = "organization";

	/** homepage of the group */
	String HOMEPAGE = "homepage";

	/** the parent name */
	String PARENT_NAME = "parent_name";

	/** settings */

	/** allows join */
	String ALLOW_JOIN = "allow_join";

	/** shares documents */
	String SHARES_DOCUMENTS = "shares_docs";
}
