package org.bibsonomy.lucene.util;

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
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bibsonomy.lucene.index.analyzer.SpringPerFieldAnalyzerWrapper;
import org.junit.Test;

public class LuceneCommandLine {
	private static final Log log = LogFactory.getLog(LuceneCommandLine.class);
	
	private Analyzer analyzer = SpringPerFieldAnalyzerWrapper.getInstance();
	
	static {
		JNDITestDatabaseBinder.bind();
	}
	
	public static void main(String[] args) throws Exception {
		
		LuceneCommandLine lcml = new LuceneCommandLine();
		lcml.doQuerying();
	}


	private void doQuerying() throws Exception {
		String luceneIndexPath = "/home/fei/tmp/lucene/lucene_BibTex";
		Directory indexDirectory = FSDirectory.open(new File(luceneIndexPath));
		IndexReader indexReader = IndexReader.open(indexDirectory, false);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		
		SortField sortField = new SortField("last_tas_id",SortField.INT,true);
		Sort sort = new Sort(sortField);

		String searchTerms = null; 
		while( !"!quit".equals(searchTerms) ) {
			System.out.print("Query: ");
			searchTerms = readStdIn();

			if( !"!quit".equals(searchTerms) ) {
				Query searchQuery = parseSearchQuery(searchTerms);

				// query the index
				Document doc     = null;
				try {
					TopDocs topDocs  = null;
					topDocs = searcher.search(searchQuery, null, 100, sort);
					for( int i=0; i<topDocs.totalHits; i++ ) {
						doc = searcher.doc(topDocs.scoreDocs[i].doc);
						System.out.println("Document["+i+"]:");
						List<Fieldable> fields = doc.getFields();
						for(Fieldable field : fields ) {
							System.out.println("  "+field.name()+":\t"+doc.getField(field.name()));
						}
					}
				} catch (Exception e) {
					log.error("Error reading index file " + luceneIndexPath);
				} finally {
					try {
						searcher.close();
					} catch (IOException e) {
						log.error("Error closing index "+luceneIndexPath+" for searching", e);
					}
				}
			}
		}
	}
	
	
	private String readStdIn() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	      String input = null;

	      //  read the username from the command-line; need to use try/catch with the
	      //  readLine() method
	      try {
	    	  input = br.readLine();
	      } catch (IOException ioe) {
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
	private Query parseSearchQuery(String searchTerms) {
		// parse search terms for handling phrase search
		QueryParser searchTermParser = new QueryParser(LUCENE_24, "mergedfields", analyzer);
		// FIXME: configure default operator via spring
		Query searchTermQuery = null;
		try {
			searchTermQuery = searchTermParser.parse(searchTerms);
		} catch (ParseException e) {
			searchTermQuery = new TermQuery(new Term("mergedfields", searchTerms) );
		}
		return searchTermQuery;
	}	
		
}
