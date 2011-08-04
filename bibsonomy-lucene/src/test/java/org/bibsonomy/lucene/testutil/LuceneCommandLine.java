package org.bibsonomy.lucene.testutil;

import static org.apache.lucene.util.Version.LUCENE_24;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bibsonomy.database.testutil.JNDIBinder;
import org.bibsonomy.lucene.index.analyzer.SpringPerFieldAnalyzerWrapper;

/**
 * @author fei
 * @version $Id$
 */
public class LuceneCommandLine {
	private static final Log log = LogFactory.getLog(LuceneCommandLine.class);
	
	// FIXME: check implementation
	private final Analyzer analyzer = new SpringPerFieldAnalyzerWrapper();
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		JNDIBinder.bind();
		final LuceneCommandLine lcml = new LuceneCommandLine();
		lcml.doQuerying();
	}

	private void doQuerying() throws Exception {
		final String bibTexIndexPath = "/tmp/lucene/lucene_BibTex-0";
		final Directory bibTexDirectory = FSDirectory.open(new File(bibTexIndexPath));
		final IndexReader bibTexReader = IndexReader.open(bibTexDirectory, false);
		final IndexSearcher bibTexSearcher = new IndexSearcher(bibTexReader);

		final String bookmarkIndexPath = "/tmp/lucene/lucene_Bookmark-0";
		final Directory bookmarkDirectory = FSDirectory.open(new File(bookmarkIndexPath));
		final IndexReader bookmarkReader = IndexReader.open(bookmarkDirectory, false);
		final IndexSearcher bookmarkSearcher = new IndexSearcher(bookmarkReader);

		final SortField sortField = new SortField("last_tas_id",SortField.INT,true);
		final Sort sort = new Sort(sortField);

		String searchTerms = null; 
		while( !"!quit".equals(searchTerms) ) {
			System.out.print("Query: ");
			searchTerms = readStdIn();
			
			doSearching(bookmarkSearcher, sort, searchTerms);
			doSearching(bibTexSearcher, sort, searchTerms);
		}
	}
	
	private void doSearching(final IndexSearcher searcher, final Sort sort, final String searchTerms) {
		if( !"!quit".equals(searchTerms) ) {
			long queryTimeMs = System.currentTimeMillis();
			final Query searchQuery = parseSearchQuery(searchTerms);
			queryTimeMs = System.currentTimeMillis() - queryTimeMs;
			//------------------------------------------------------------
			// query the index
			//------------------------------------------------------------
			Document doc     = null;
			try {
				TopDocs topDocs  = null;
				topDocs = searcher.search(searchQuery, null, 100, sort);
				for( int i=0; i<topDocs.totalHits; i++ ) {
					doc = searcher.doc(topDocs.scoreDocs[i].doc);
					System.out.println("Document["+i+"]:");
					final List<Fieldable> fields = doc.getFields();
					for(final Fieldable field : fields ) {
						System.out.println("  "+field.name()+":\t"+doc.getField(field.name()));
					}
				}
			} catch (final Exception e) {
				log.error("Error reading index file ("+e.getMessage()+")");
			} finally {
				try {
					searcher.close();
				} catch (final IOException e) {
					log.error("Error closing index for searching", e);
				}
			}
			System.out.println("Query time: "+queryTimeMs);
		}
	}
	
	private String readStdIn() {
		final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	      String input = null;

	      //  read the username from the command-line; need to use try/catch with the
	      //  readLine() method
	      try {
	    	  input = br.readLine();
	      } catch (final IOException ioe) {
	         log.error("Error reading input.", ioe);
	      }
	      
	      return input;
	}
	
	
	/**
	 * build full text query for given query string
	 * 
	 * @param searchTerms
	 * @return
	 */
	private Query parseSearchQuery(final String searchTerms) {
		// parse search terms for handling phrase search
		final QueryParser searchTermParser = new QueryParser(LUCENE_24, "mergedfields", analyzer);
		// FIXME: configure default operator via spring
		Query searchTermQuery = null;
		try {
			searchTermQuery = searchTermParser.parse(searchTerms);
		} catch (final ParseException e) {
			searchTermQuery = new TermQuery(new Term("mergedfields", searchTerms) );
		}
		return searchTermQuery;
	}	
		
}
