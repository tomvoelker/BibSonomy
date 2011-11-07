package org.bibsonomy.rest;

/**
 * DO NOT CHANGE any constant values after a release
 * 
 * @author dzo
 * @version $Id$
 */
public final class RESTConfig {
	private RESTConfig() {}
	
	public static final String RESOURCE_TYPE_PARAM = "resourcetype";
	
	public static final String RESOURCE_PARAM = "resource";
	
	public static final String TAGS_PARAM = "tags";
	
	public static final String FILTER_PARAM = "filter";
	
	public static final String ORDER_PARAM = "order";
	
	public static final String CONCEPT_STATUS_PARAM = "status";
	
	public static final String SEARCH_PARAM = "search";
	
	public static final String SUB_TAG_PARAM = "subtag";
	
	public static final String REGEX_PARAM = FILTER_PARAM;
	
	public static final String START_PARAM = "start";
	
	public static final String END_PARAM = "end";
	
	public static final String SYNC_STRATEGY_PARAM = "strategy";

	public static final String SYNC_DIRECTION_PARAM = "direction";
	
	public static final String SYNC_DATE_PARAM = "date";
	
	public static final String SYNC_STATUS = "status";

	/**
	 * Request Attribute ?relation="incoming/outgoing"
	 */
	public static final String ATTRIBUTE_KEY_RELATION = "relation";

	/** value for "incoming" */
	public static final String INCOMING_ATTRIBUTE_VALUE_RELATION = "incoming";

	/** value for "outgoing" */
	public static final String OUTGOING_ATTRIBUTE_VALUE_RELATION = "outgoing";

	/** default value */
	public static final String DEFAULT_ATTRIBUTE_VALUE_RELATION = INCOMING_ATTRIBUTE_VALUE_RELATION;
}
