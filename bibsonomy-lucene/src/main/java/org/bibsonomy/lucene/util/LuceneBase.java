package org.bibsonomy.lucene.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bibsonomy.lucene.index.LuceneGenerateResourceIndex;
import org.bibsonomy.lucene.index.LuceneResourceIndex;

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
	protected static final String PARAM_RELEVANCE = "relevance";

	protected static final String CFG_LUCENENAME     = "luceneName";
	protected static final String CFG_TYPEHANDLER    = "typeHandler";
	protected static final String CFG_ITEMPROPERTY   = "itemProperty";
	protected static final String CFG_LIST_DELIMITER = " ";
	protected static final String CFG_FLDINDEX       = "luceneIndex";
	protected static final String CFG_FLDSTORE       = "luceneStore";

	
	/** the naming context for lucene classes */
	protected static final String CONTEXT_ENV_NAME    = "java:/comp/env";
	/** naming context for variables */
	protected static final String CONTEXT_INDEX_PATH  = "luceneIndexPath";
	/** context variable determining whether lucene should update the index */
	protected static final String CONTEXT_ENABLE_FLAG = "enableLuceneUpdater";
		
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

	protected static final int SQL_BLOCKSIZE = 1000;

	protected static final String PROP_DB_DRIVER_NAME = "db.driver";	
	protected static final String LUCENE_INDEX_PATH_PREFIX = "luceneIndexPath";
}
