package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.LucenePostConverter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * reads bibsonomy data from data base and builds lucene index for bookmark
 * and publication entries
 * 
 * @author sst
 * @author fei
 */
public abstract class LuceneGenerateResourceIndex<R extends Resource> {
	private static final String LUCENE_INDEX_PATH_PREFIX = "luceneIndexPath";

	private static final Logger log = Logger.getLogger(LuceneGenerateResourceIndex.class);

	/** name of the property file which configures lucene */
	private static final String PROPERTYFILENAME = "lucene.properties";

	/** reference to the configuration file */
	Properties props = null;

	/** keyword identifying unlimited field length in the lucene index */
	private static final String KEY_UNLIMITED = "UNLIMITED";

	/** keyword identifying limited field length in the lucene index */
	private static final Object KEY_LIMITED = "LIMITED";

	private static final int SQL_BLOCKSIZE = 1000;

	private static final String FLD_LASTTASID = "lastTasId";

	/** MAGIC KEY identifying the context environment for this class */
	private static final String CONTEXT_ENV_NAME = "java:/comp/env";
	
	/** MAGIC KEY identifying context variables for this class */
	private static final String CONTEXT_INDEX_PATH = "luceneIndexPath";

	private static final String PROP_DB_DRIVER_NAME = "db.driver";

	/** database logic */
	private LuceneDBInterface<R> dbLogic;

	/** path to the bookmark index */
	private String luceneResourceIndexPath;
	
	/** maximum length of fields in the lucene index */
	IndexWriter.MaxFieldLength mfl;
	
	/** writes the bookmark index */
	IndexWriter indexWriter;

	private String dbDriver;

	
	/**
	 * constructor
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public LuceneGenerateResourceIndex() throws ClassNotFoundException, SQLException {
		props = new Properties();		
		try {
			// read properties
			props.load(LuceneGenerateResourceIndex.class.getClassLoader().getResourceAsStream(PROPERTYFILENAME));		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		// load configuration
		init();
	}

	/**
	 * constructor
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public LuceneGenerateResourceIndex(final Properties props) throws ClassNotFoundException, SQLException {
		//
		// configuration
		//		
		this.props = props;
		
		// load configuration
		init();
	}
	
	/** 
	 * reads in parameters from the properties file and stores them in 
	 * the corresponding attributes
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * 
	 */
	private void init() throws ClassNotFoundException, SQLException {
		//
		// configuration
		//
		// 1) database
		this.dbDriver = props.getProperty(PROP_DB_DRIVER_NAME);
		Class.forName(this.dbDriver);

		// 2) index files
		this.luceneResourceIndexPath = props.getProperty(LUCENE_INDEX_PATH_PREFIX + getResourceName());
		
		// 3) index configuration
		//    - maximum field length
		String mflIn = props.getProperty("indexWriter.maximumFieldLength");
		if( KEY_UNLIMITED.equals(mflIn) ) {
			mfl = IndexWriter.MaxFieldLength.UNLIMITED;
		}
		else if( KEY_LIMITED.equals(mflIn) ) {
			mfl = IndexWriter.MaxFieldLength.LIMITED;
		} else {
			Integer value;
			try {
				value = Integer.parseInt(mflIn);
			} catch (NumberFormatException e) {
				value = IndexWriter.DEFAULT_MAX_FIELD_LENGTH;
			}
			mfl = new IndexWriter.MaxFieldLength(value);
		}
	}

	/**
	 * frees allocated resources and closes all files
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public void shutdown() throws CorruptIndexException, IOException {
		indexWriter.close();
	}
	
	/**
	 * Read in data from database and build index. 
	 * 
	 * Database as well as index files are configured in the lucene.properties file.
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void generateIndex() throws CorruptIndexException, IOException, ClassNotFoundException, SQLException {
		// open index
		createEmptyIndex();

		// generate index
		createIndexFromDatabase();
	}
	
	/**
	 * Create empty index. Attributes must already be configured (via init()).
	 *  
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public void createEmptyIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		// Use default analyzer
		// FIXME: this has to be configured via spring!
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
		
		// create index, possibly overwriting existing index files
		indexWriter  = new IndexWriter(this.luceneResourceIndexPath, analyzer, true, mfl); 
	}

	
	/**
	 * creates index of bookmark entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void createIndexFromDatabase() throws CorruptIndexException, IOException {
		// number of post entries
		log.info("Number of post entries: "+this.dbLogic.getNumberOfPosts());
		
		// initialize variables
		Integer lastTasId = this.dbLogic.getLastTasId();
		Date lastLogDate  = this.dbLogic.getLastLogDate(); 

		//
		// get all relevant bookmarks from bookmark table
		//
		int i    = 0;		// number of evaluated entries 
		int is   = 0;		// number of spam entries 

		log.info("Start writing data to lucene index");
		
		// read block wise all posts
		List<LucenePost<R>> postList = null;
		int skip = 0;
		int max=SQL_BLOCKSIZE;
		boolean toRead = true;
		while( toRead ) {
			postList = this.dbLogic.getPostEntries(skip, max);
			toRead  = (postList.size()==SQL_BLOCKSIZE);
			skip += postList.size();
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for( LucenePost<R> postEntry : postList ) {
				// update management fields
				postEntry.setLastLogDate(lastLogDate);
				postEntry.setLastTasId(lastTasId);
				// create index document from post model
				Document post = LucenePostConverter.readPost(postEntry);

				// add (non-spam) document to index
				// FIXME: is this necessary?
				if( isSpammer(postEntry) ) {
					indexWriter.addDocument(post);
					i++;
				} else {
					is++;
				}			
					
				// FIXME: add status report
				if( (i+is)%100000==0 ) {
					log.info("Read "+(i+is)+" posts");
				}
			}
		}

		// optimize index
		log.info("optimizing index " + luceneResourceIndexPath);
		indexWriter.optimize();
		
		// close bookmark-indexWriter
		log.info("closing index " + luceneResourceIndexPath);
		indexWriter.close();

		// all done
		log.info("(" + i + " indexed entries, " + is + " not indexed spam entries)");
	}
	

	/**
	 * test if given post is a spam post
	 * 
	 * @param bibTexEntry
	 * @return
	 */
	private boolean isSpammer(Post<? extends Resource> post) {
		boolean flaggedAsSpammer = false;
		for( Group group : post.getGroups() ) {
			if( group.getGroupId()<0 )
				flaggedAsSpammer = true;
		}
		return !flaggedAsSpammer;
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setLogic(LuceneDBInterface<R> luceneDbLogic) {
		dbLogic = luceneDbLogic;
	}
	
	public LuceneDBInterface<R> getLogic() {
		return this.dbLogic;
	}


	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
	/**
	 * get managed resource type
	 */
	protected abstract Class<? extends Resource> getResourceType();
	
	/**
	 * get managed resource name
	 * @return
	 */
	private String getResourceName() {
		String name = getResourceType().getCanonicalName();
		if (name.lastIndexOf('.') > 0) {
	        name = name.substring(name.lastIndexOf('.')+1);
	    }
		
		return name;
	}
}
