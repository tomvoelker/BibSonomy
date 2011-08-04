package org.bibsonomy.lucene.index;

/**
 * 
 * @author fmi
 * @version $Id$
 */
public abstract class LuceneFieldNames {

	// FIXME: configure these fieldnames via spring
	public static final String MERGED_FIELDS  = "mergedfields";
	public static final String PRIVATE_FIELDS = "privatefields";
	public static final String INTRAHASH     = "intrahash";
	public static final String INTERHASH     = "interhash";
	public static final String GROUP         = "group";
	public static final String AUTHOR        = "author";
	public static final String USER          = "user_name";
	public static final String DATE          = "date";
	public static final String YEAR          = "year";
	public static final String TAS           = "tas";	
	public static final String ADDRESS       = "address";
	public static final String TITLE         = "title";	
	public static final String LAST_TAS_ID   = "last_tas_id";
	public static final String LAST_LOG_DATE = "last_log_date";
	public static final String USER_NAME     = "user_name";
	public static final String CONTENT_ID    = "content_id";
}
