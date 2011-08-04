package org.bibsonomy.lucene.util;


/**
 * this class is a temporary hack for collecting all constants which should be consistent 
 * throughout the module
 * 
 *  FIXME: this should be consistent with the spring configuration
 *  
 * @author fei
 * @version $Id$
 */
public class LuceneBase {
	/** TODO: improve documentation */
	public static final String PARAM_RELEVANCE = "relevance";

	/** TODO: improve documentation */
	public static final String CFG_LUCENENAME = "luceneName";
	/** TODO: improve documentation */
	public static final String CFG_ANALYZER = "fieldAnalyzer";
	/** TODO: improve documentation */
	public static final String CFG_TYPEHANDLER = "typeHandler";
	/** TODO: improve documentation */
	public static final String CFG_LIST_DELIMITER = " ";
	/** TODO: improve documentation */
	public static final String CFG_FLDINDEX = "luceneIndex";
	/** TODO: improve documentation */
	public static final String CFG_FLDSTORE = "luceneStore";
	/** TODO: improve documentation */
	public static final String CFG_FULLTEXT_FLAG = "fulltextSearch";
	/** TODO: improve documentation */
	public static final String CFG_PRIVATE_FLAG = "privateSearch";
	/** delimiter to specify which field to search for */
	public static final String CFG_LUCENE_FIELD_SPECIFIER = ":";
}
