package org.bibsonomy.lucene.util;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexWriter;
import org.bibsonomy.lucene.param.LuceneConfig;
import org.bibsonomy.util.ValidationUtils;

/**
 * this class is a temporary hack for collecting all constants which should be consistent 
 * throughout the module
 * 
 *  FIXME: this should be consistent with the spring configuration
 *  
 * @author fei
 *
 */
public class LuceneBase {
	private static final Log log = LogFactory.getLog(LuceneBase.class);
	
	//------------------------------------------------------------------------
	// static configuration
	//------------------------------------------------------------------------
	public static final String PARAM_RELEVANCE = "relevance";

	public static final String CFG_LUCENENAME         = "luceneName";
	public static final String CFG_ANALYZER           = "fieldAnalyzer";
	public static final String CFG_TYPEHANDLER        = "typeHandler";
	public static final String CFG_ITEMPROPERTY       = "itemProperty";
	public static final String CFG_LIST_DELIMITER     = " ";
	public static final String CFG_FLDINDEX           = "luceneIndex";
	public static final String CFG_FLDSTORE           = "luceneStore";
	public static final String CFG_FULLTEXT_FLAG      = "fulltextSearch";
	public static final String CFG_PRIVATE_FLAG       = "privateSearch";
	public static final String CFG_INDEX_ID_DELIMITER = "-";
	
	/** delimiter to specify which field to search for */
	public static final String CFG_LUCENE_FIELD_SPECIFIER = ":";
	
	/** directory prefix for different resource indeces */
	public static final String CFG_LUCENE_INDEX_PREFIX = "lucene_";
	
	/** max. number of posts to consider for building the author tag cloud */
	public static final Integer CFG_TAG_CLOUD_LIMIT = Integer.MAX_VALUE;
	
	/** the naming context for lucene classes */
	public static final String CONTEXT_ENV_NAME    = "java:/comp/env";
	/** naming context for variables */
	public static final String CONTEXT_INDEX_PATH  = "luceneIndexPath";
	/** context variable determining whether lucene should update the index */
	public static final String CONTEXT_ENABLE_FLAG = "enableLuceneUpdater";
	/** context variable containing lucene's configuration */
	public static final String CONTEXT_CONFIG_BEAN = "luceneConfig";
		
	/** name of the property file which configures lucene */
	public static final String PROPERTYFILENAME    = "lucene.properties";
	public static final String LUCENE_CONTEXT_XML  = "LuceneIndexConfig.xml";

	// FIXME: configure these fieldnames via spring
	public static final String FLD_MERGEDFIELDS  = "mergedfields";
	public static final String FLD_PRIVATEFIELDS = "privatefields";
	public static final String FLD_INTRAHASH     = "intrahash";
	public static final String FLD_INTERHASH     = "interhash";
	public static final String FLD_GROUP         = "group";
	public static final String FLD_AUTHOR        = "author";
	public static final String FLD_USER          = "user_name";
	public static final String FLD_DATE          = "date";
	public static final String FLD_YEAR          = "year";
	public static final String FLD_TAS           = "tas";	
	public static final String FLD_ADDRESS       = "address";
	public static final String FLD_TITLE         = "title";	
	public static final String FLD_LAST_TAS_ID   = "last_tas_id";
	public static final String FLD_LAST_LOG_DATE = "last_log_date";
	public static final String FLD_USER_NAME     = "user_name";
	public static final String FLD_CONTENT_ID    = "content_id";
	
	/** keyword identifying unlimited field length in the lucene index */
	public static final String KEY_UNLIMITED     = "UNLIMITED";
	/** keyword identifying limited field length in the lucene index */
	public static final Object KEY_LIMITED       = "LIMITED";

	public static final int SQL_BLOCKSIZE = 25000;

	public static final String PROP_DB_DRIVER_NAME = "db.driver";	
	public static final String LUCENE_INDEX_PATH_PREFIX = "luceneIndexPath";
	
	//------------------------------------------------------------------------
	// runtime configuration
	//------------------------------------------------------------------------
	private static String indexBasePath           = "";
	private static String searchMode              = "database";
	private static Boolean enableUpdater          = false;
	private static Boolean loadIndexIntoRam       = false;
	private static String dbDriverName            = "com.mysql.jdbc.Driver";
	private static IndexWriter.MaxFieldLength maximumFieldLength
	                                              = new IndexWriter.MaxFieldLength(5000);
	private static Integer redundantCnt           = 2;
	private static Boolean enableTagClouds        = false;
	private static Integer tagCloudLimit          = 1000;
	
	/**
	 * get runtime configuration from context
	 */
	protected static void initRuntimeConfiguration() {
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context) initContext.lookup(CONTEXT_ENV_NAME);
			LuceneConfig config = (LuceneConfig)envContext.lookup(CONTEXT_CONFIG_BEAN);
			
			// index base path
			setIndexBasePath(config.getIndexPath());
			// search mode
			if( ValidationUtils.present(config.getSearchMode()) )
				searchMode       = config.getSearchMode();
			// db driver name
			if( ValidationUtils.present(config.getDbDriverName()) )
				dbDriverName = config.getDbDriverName();
			// maximum field length in the lucene index
			if( ValidationUtils.present(config.getMaximumFieldLength())) {
				String mflIn = config.getMaximumFieldLength();
				if( KEY_UNLIMITED.equals(mflIn) ) {
					maximumFieldLength = IndexWriter.MaxFieldLength.UNLIMITED;
				}
				else if( KEY_LIMITED.equals(mflIn) ) {
					maximumFieldLength = IndexWriter.MaxFieldLength.LIMITED;
				} else {
					Integer value;
					try {
						value = Integer.parseInt(mflIn);
					} catch (NumberFormatException e) {
						value = IndexWriter.DEFAULT_MAX_FIELD_LENGTH;
					}
					maximumFieldLength = new IndexWriter.MaxFieldLength(value);
				}				
			}
			
			// nr. of redundant indeces
			if( ValidationUtils.present(config.getRedundantCnt()) ) {
				Integer value;
				try {
					value = Integer.parseInt(config.getRedundantCnt());
				} catch (NumberFormatException e) {
					value = IndexWriter.DEFAULT_MAX_FIELD_LENGTH;
				}
				redundantCnt = value;
			}
			
			// enable/disable tag cloud on search pages
			setEnableTagClouds(Boolean.valueOf(config.getEnableTagClouds()));
			
			// limit number of posts to consider for building the tag cloud
			setTagCloudLimit(Integer.valueOf(config.getTagCloudLimit()));
			
			setEnableUpdater(Boolean.valueOf(config.getEnableUpdater()));
			loadIndexIntoRam = Boolean.valueOf(config.getLoadIndexIntoRam());
		} catch (Exception e) {
			log.error("Error requesting JNDI environment variables ' ("+e.getMessage()+")");
		}
		
		// done - print out debug information
		log.debug("\t indexBasePath    : " + getIndexBasePath());
		log.debug("\t searchMode       : " + searchMode);
		log.debug("\t enableUpdater    : " + getEnableUpdater());
		log.debug("\t loadIndexIntoRam : " + loadIndexIntoRam);
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public static void setIndexBasePath(String indexBasePath) {
		LuceneBase.indexBasePath = indexBasePath;
	}

	public static String getIndexBasePath() {
		return indexBasePath;
	}

	public static void setMaximumFieldLength(IndexWriter.MaxFieldLength maximumFieldLength) {
		LuceneBase.maximumFieldLength = maximumFieldLength;
	}

	public static IndexWriter.MaxFieldLength getMaximumFieldLength() {
		return maximumFieldLength;
	}

	public static void setDbDriverName(String dbDriverName) {
		LuceneBase.dbDriverName = dbDriverName;
	}

	public static String getDbDriverName() {
		return dbDriverName;
	}

	public static void setEnableUpdater(Boolean enableUpdater) {
		LuceneBase.enableUpdater = enableUpdater;
	}

	public static Boolean getEnableUpdater() {
		return enableUpdater;
	}

	public static void setRedundantCnt(Integer redundantCnt) {
		LuceneBase.redundantCnt = redundantCnt;
	}

	public static Integer getRedundantCnt() {
		return redundantCnt;
	}

	public static void setEnableTagClouds(Boolean enableTagClouds) {
		LuceneBase.enableTagClouds = enableTagClouds;
	}

	public static Boolean getEnableTagClouds() {
		return enableTagClouds;
	}

	public static void setTagCloudLimit(Integer tagCloudLimit) {
		LuceneBase.tagCloudLimit = tagCloudLimit;
	}

	public static Integer getTagCloudLimit() {
		return tagCloudLimit;
	}
}
