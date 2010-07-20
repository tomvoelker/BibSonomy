package helpers;

@Deprecated
public final class constants {
	
	/* ***********************************************************************
	 * SQL constants
	 * ***********************************************************************/

	public static final int BOOKMARK_CONTENT_TYPE=1;
	public static final int BIBTEX_CONTENT_TYPE=2;
	
	// similarity hashes
	private static final int SIM_HASH_0 = 0; // OLD intra-user hash
	private static final int SIM_HASH_1 = 1; // inter-user hash 1 (actually used!)
	private static final int SIM_HASH_2 = 2; // NEW intra-user hash
	private static final int SIM_HASH_3 = 3; // inter-user hash 3 (unused)
	public static final int INTER_HASH = SIM_HASH_1; // default similarity hash (inter-user hash)
	public static final int INTRA_HASH = SIM_HASH_2;
	
	/* constant group ids */
	public static final int SQL_CONST_GROUP_PUBLIC  = 0;
	public static final int SQL_CONST_GROUP_PRIVATE = 1;
	public static final int SQL_CONST_GROUP_FRIENDS = 2;
	
	/* privacy levels for groups */
	public static final int SQL_CONST_PRIVLEVEL_PUBLIC  = 0; /* member list public */
	public static final int SQL_CONST_PRIVLEVEL_HIDDEN  = 1; /* member list hidden */
	public static final int SQL_CONST_PRIVLEVEL_MEMBERS = 2; /* members can list members */
	
	/* names for ids table */
	public static final int SQL_IDS_CONTENT_ID = 0;
	public static final int SQL_IDS_TAS_ID = 1;
	
	public static final int SQL_IDS_TAGREL_ID = 2;
	public static final int SQL_IDS_QUESTION_ID = 3;
	public static final int SQL_IDS_CYCLE_ID = 4;
	public static final int SQL_IDS_EXTENDED_FIELDS = 5;
	public static final int SQL_IDS_SCRAPER_METADATA = 7;
	
	/*spammer ids*/
	public static final int SQL_CONST_SPAMMER_TRUE   = 1;
	public static final int SQL_CONST_SPAMMER_FALSE  = 0;
	public static final int SQL_CONST_TO_CLASSIFY_FALSE = 0;
	
	/* ***********************************************************************
	 * HTTP constants
	 * ***********************************************************************/
	
	public static final String HTTP_COOKIE_SPAMMER_KEY = "_lPost";
	public static final char HTTP_COOKIE_SPAMMER_CONTAINS = '3';

	/* ***********************************************************************
	 * DBLP constants
	 * ***********************************************************************/

	public static final String dblpUser = "dblp";
	
	
}