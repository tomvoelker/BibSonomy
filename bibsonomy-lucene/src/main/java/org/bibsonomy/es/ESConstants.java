package org.bibsonomy.es;

/**
 * The Class for elastic search engine constants.
 * 
 * @author lutful
 */
public final class ESConstants {
	/**
	 * Elasticsearch Index Name
	 */
	public static final String INDEX_NAME = "posts";
	/**
	 * BATCH size to fetch results
	 */
	public static final int BATCHSIZE = 30000;

	/**
	 * Path of Elasticsearch configuration file.
	 */
	public static final String PATH_CONF = "path.conf";

	/**
	 * Path of names.txt file.
	 */
	public static final String NAMES_TXT = "/org.bibsonomy.es/";

	/**
	 * Elasticsearch client SNIFF property.
	 */
	public static final String SNIFF = "client.transport.sniff";

	/**
	 * Elasticsearch Node name
	 */
	public static final String ES_NODE_NAME = "bibsonomy_client";
	
	/**
	 * Index type for the system information
	 */
	public static final String SYSTEM_INFO_INDEX_TYPE = "SystemInformation";
}
