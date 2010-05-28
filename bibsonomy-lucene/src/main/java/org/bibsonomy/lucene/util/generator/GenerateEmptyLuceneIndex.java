package org.bibsonomy.lucene.util.generator;




/**
 * Generates empty index files for lucene. The path of the index files is configured 
 * in 'lucene.properties'
 *  
 * @author sst
 * @author fei
 * @version $Id$
 */
public class GenerateEmptyLuceneIndex {
	
	/**
	 * generates a empty index foreach known index
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception	{
		LuceneGenerateBibTexIndex.main(null);
		LuceneGenerateBookmarkIndex.main(null);
	}
	
}
