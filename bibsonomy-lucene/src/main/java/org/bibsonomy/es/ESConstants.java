package org.bibsonomy.es;

/**
 * The Class for elastic search engine constants.
 * 
 * @author lutful
 */
public final class ESConstants {
	/**
	 * Es Index Name
	 */
	public static final String INDEX_NAME = "posts";
	/**
	 * BATCH size to fetch results
	 */
	public static final int BATCHSIZE = 30000;

	/**
	 * Path of ES configuration file.
	 */
	public static final String PATH_CONF = "path.conf";

	/**
	 * Path of names.txt file.
	 */
	public static final String NAMES_TXT = "/org.bibsonomy.es/";

	/**
	 * Es client SNIFF property.
	 */
	public static final String SNIFF = "client.transport.sniff";
	/**
	 * IP constants.
	 */
	public static final String IP = "ip";
	/**
	 * ES.properties filename
	 */
	public static final String ES_PROPERTIES = "project.properties";
	/**
	 * Elasticsearch Node name
	 */
	public static final String ES_NODE_NAME = "bibsonomy_client";
	/**
	 * Elasticsearch IP and port.
	 */
	public static final String ES_ADDRESSS = "es.address";
	/**
	 * Elasticsearch IP and port values, if we have multiple addresses, they
	 * will be separated by "," and port and ip are separated by ":"
	 */
	public static final String ES_ADDRESSS_VALUE = "localhost:9300";

	/**
	 * Elasticsearch CLustername
	 */
	public static final String ES_CLUSTERNAME = "es.cluster.name";
	/**
	 * Elasticsearch clustername value
	 */
	public static final String ES_CLUSTERNAME_VALUE = "elasticsearch";
	
	/**
	 * Index type for the system information
	 */
	public static final String SYSTEM_INFO_INDEX_TYPE = "SystemInformation";
}
