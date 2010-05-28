package org.bibsonomy.lucene.util.generator;

import static org.bibsonomy.lucene.util.LuceneBase.CFG_INDEX_ID_DELIMITER;
import static org.bibsonomy.lucene.util.LuceneBase.CFG_LUCENE_INDEX_PREFIX;
import static org.bibsonomy.lucene.util.LuceneBase.SQL_BLOCKSIZE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneResourceConverter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * reads bibsonomy data from data base and builds lucene index for bookmark
 * and publication entries
 * 
 * @author sst
 * @author fei
 * @version $Id$
 * 
 * @param <R> the resource of the index to generate
 */
public abstract class LuceneGenerateResourceIndex<R extends Resource> {
	protected static final Log log = LogFactory.getLog(LuceneGenerateResourceIndex.class);

	/** database logic */
	protected LuceneDBInterface<R> dbLogic;

	/** path to the bookmark index */
	private String luceneResourceIndexPath;
	
	/** maximum length of fields in the lucene index */
	private IndexWriter.MaxFieldLength mfl;
	
	/** writes the bookmark index */
	private IndexWriter indexWriter;

	/** default analyzer */
	private Analyzer analyzer = null;
	
	/** converts post model objects to lucene documents */
	private LuceneResourceConverter<R> resourceConverter;
	
	/**
	 * constructor 
	 */
	public LuceneGenerateResourceIndex() {
		// load configuration
		init();
	}

	/** 
	 * reads in parameters from the properties file and stores them in 
	 * the corresponding attributes 
	 */
	private void init() {
		// initialize run time configuration
		LuceneBase.initRuntimeConfiguration();
		
		// 1) index files
		this.luceneResourceIndexPath = LuceneBase.getIndexBasePath() + CFG_LUCENE_INDEX_PREFIX + getResourceName();//props.getProperty(LUCENE_INDEX_PATH_PREFIX + getResourceName());
		
		// 2) maximal field width in the index
		this.mfl = LuceneBase.getMaximumFieldLength();
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
		// create index, possibly overwriting existing index files
		log.info("Creating empty lucene index...");
		Directory indexDirectory = FSDirectory.open(new File(this.luceneResourceIndexPath+CFG_INDEX_ID_DELIMITER+"0"));
		indexWriter  = new IndexWriter(indexDirectory, getAnalyzer(), true, mfl); 
	}
	
	/**
	 * creates index of bookmark entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void createIndexFromDatabase() throws CorruptIndexException, IOException {
		log.info("Filling index with database post entries.");

		// set up resource specific data structures
		setUp();
		
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
				fillPost(postEntry);
				
				// create index document from post model
				Document post = this.resourceConverter.readPost(postEntry);

				// add (non-spam) document to index
				// FIXME: is this check necessary?
				if( isSpammer(postEntry) ) {
					indexWriter.addDocument(post);
					i++;
				} else {
					is++;
				}			
			}
			log.info("Ready.");
		}

		// optimize index
		log.info("optimizing index " + luceneResourceIndexPath);
		indexWriter.optimize();
		
		// close bookmark-indexWriter
		log.info("closing index " + luceneResourceIndexPath);
		indexWriter.close();

		// all done
		log.info("(" + i + " indexed entries, " + is + " not indexed spam entries)");
		
		// create redundant indeces
		log.info("Creating "+ LuceneBase.getRedundantCnt() + " redundant indeces.");
		this.copyRedundantIndeces();
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
	// private helper
	//------------------------------------------------------------------------
	/**
	 * copy created index to redundant indeces
	 */
	protected void copyRedundantIndeces() {
		File inputFile = new File(this.luceneResourceIndexPath+CFG_INDEX_ID_DELIMITER+"0");
		for(int i = 1; i < LuceneBase.getRedundantCnt(); i++ ) {
			try {
				File outputFile = new File(this.luceneResourceIndexPath+CFG_INDEX_ID_DELIMITER+i);
				log.info("Copying index "+i);
				copyDirectory(inputFile, outputFile);
				log.info("Done.");
			} catch( Exception e) {
				log.error("Error copying index to index file "+i);
			}
		}
	}
	
	/**
	 * Copies all files under srcDir to dstDir.
 	 * If dstDir does not exist, it will be created.
	 * @param srcDir
	 * @param dstDir
	 * @throws IOException
	 */
    public void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }
    
            String[] children = srcDir.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(srcDir, children[i]),
                                     new File(dstDir, children[i]));
            }
        } else {
            copyFile(srcDir, dstDir);
        }
    }
	
	/**
	 * Fast & simple file copy.
	 * 
	 * @param source 
	 * @param dest 
	 * @throws IOException 
	 */
	public static void copyFile(File source, File dest) throws IOException {
	     FileChannel in = null, out = null;
	     try {          
	          in = new FileInputStream(source).getChannel();
	          out = new FileOutputStream(dest).getChannel();
	 
	          long size = in.size();
	          MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
	 
	          out.write(buf);
	 
	     } finally {
	          if (in != null)          in.close();
	          if (out != null)     out.close();
	     }
	}
	
	/**
	 * @return get managed resource name
	 */
	protected abstract String getResourceName();
	
	/** fill given posts with additional data */
	protected void fillPost(@SuppressWarnings("unused") LucenePost<R> postEntry) {
		// noop
	}
	
	/** set up resource specific data structures */
	protected void setUp() {
		// noop
	}

	/**
	 * @return the dbLogic
	 */
	public LuceneDBInterface<R> getLogic() {
		return dbLogic;
	}

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setLogic(LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	/**
	 * @return the analyzer
	 */
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * @param analyzer the analyzer to set
	 */
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * @return the resourceConverter
	 */
	public LuceneResourceConverter<R> getResourceConverter() {
		return resourceConverter;
	}

	/**
	 * @param resourceConverter the resourceConverter to set
	 */
	public void setResourceConverter(LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}
}
