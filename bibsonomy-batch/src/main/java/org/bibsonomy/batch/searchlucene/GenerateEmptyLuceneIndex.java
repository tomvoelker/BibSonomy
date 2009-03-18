/**
 * Generate an empty lucene index
 */
package org.bibsonomy.batch.searchlucene;

import java.io.IOException;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;


/**
 * @author sst
 *
 */


public class GenerateEmptyLuceneIndex {

	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException
	  
	{

		// ADJUST THIS!
		// Lucene index path
		// any existing index in this path would be deleted!
		// if path does not exist, it would be created
		String luceneBasePath = "/home/abc/def/bibsonomy/";
		final String luceneBookmarksPath = luceneBasePath+"lucene_bookmarks/"; 
		final String lucenePublicationsPath = luceneBasePath+"lucene_publications/"; 

		

		// Use default analyzer
		SimpleAnalyzer analyzer_bm = new SimpleAnalyzer();
		SimpleAnalyzer analyzer_pub = new SimpleAnalyzer();


//		Bookmark
		System.out.println("generate empty lucene index in "+luceneBookmarksPath );
		IndexWriter writer_bm = new IndexWriter(luceneBookmarksPath, analyzer_bm,true); // true überschreibt aktuellen index
		writer_bm.close();

		
//		BibTex
		IndexWriter writer_pub = new IndexWriter(lucenePublicationsPath, analyzer_pub,true); // true überschreibt aktuellen index
		writer_pub.close();
		System.out.println("generate empty lucene index in "+lucenePublicationsPath );

		
		System.out.println("done.");

	}



}
