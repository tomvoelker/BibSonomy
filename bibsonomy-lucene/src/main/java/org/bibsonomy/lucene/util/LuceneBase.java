package org.bibsonomy.lucene.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
	protected static final String PARAM_RELEVANCE = "relevance";

	protected static final String CFG_LUCENENAME     = "luceneName";
	protected static final String CFG_TYPEHANDLER    = "typeHandler";
	protected static final String CFG_ITEMPROPERTY   = "itemProperty";
	protected static final String CFG_LIST_DELIMITER = " ";
	protected static final String CFG_FLDINDEX       = "luceneIndex";
	protected static final String CFG_FLDSTORE       = "luceneStore";

	/** directory prefix for different resource indeces */
	protected static final String CFG_LUCENE_INDEX_PREFIX = "lucene_";
	
	/** the naming context for lucene classes */
	protected static final String CONTEXT_ENV_NAME    = "java:/comp/env";
	/** naming context for variables */
	protected static final String CONTEXT_INDEX_PATH  = "luceneIndexPath";
	/** context variable determining whether lucene should update the index */
	protected static final String CONTEXT_ENABLE_FLAG = "enableLuceneUpdater";
	/** context variable containing lucene's configuration */
	protected static final String CONTEXT_CONFIG_BEAN = "luceneConfig";
		
	/** name of the property file which configures lucene */
	protected static final String PROPERTYFILENAME    = "lucene.properties";
	protected static final String LUCENE_CONTEXT_XML  = "LuceneIndexConfig.xml";

	protected static final String FLD_MERGEDFIELDS  = "mergedfields";
	protected static final String FLD_INTRAHASH     = "intrahash";
	protected static final String FLD_GROUP         = "group";
	protected static final String FLD_AUTHOR        = "author";
	protected static final String FLD_USER          = "user_name";
	protected static final String FLD_DATE          = "date";
	protected static final String FLD_YEAR          = "year";
	protected static final String FLD_TAS           = "tas";	
	protected static final String FLD_ADDRESS       = "address";
	protected static final String FLD_TAGS          = "tas";
	protected static final String FLD_TITLE         = "title";	
	protected static final String FLD_LAST_TAS_ID   = "last_tas_id";
	protected static final String FLD_LAST_LOG_DATE = "last_log_date";
	protected static final String FLD_USER_NAME     = "user_name";
	protected static final String FLD_CONTENT_ID    = "content_id";
	
	/** keyword identifying unlimited field length in the lucene index */
	protected static final String KEY_UNLIMITED     = "UNLIMITED";
	/** keyword identifying limited field length in the lucene index */
	protected static final Object KEY_LIMITED       = "LIMITED";

	protected static final int SQL_BLOCKSIZE = 25000;

	protected static final String PROP_DB_DRIVER_NAME = "db.driver";	
	protected static final String LUCENE_INDEX_PATH_PREFIX = "luceneIndexPath";
	
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
			
			setEnableUpdater(new Boolean(config.getEnableUpdater()));
			loadIndexIntoRam = new Boolean(config.getLoadIndexIntoRam());
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
}
