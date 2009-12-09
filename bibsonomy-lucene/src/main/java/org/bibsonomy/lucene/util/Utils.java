package org.bibsonomy.lucene.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;

/**
 * Utility class to provide some often used methods 
 * 
 * @version $Id$
 *
 */

public class Utils {
	/**
	 * FIXME: this is OLD
	 * @param lucenePath
	 * @return
	 */
	@Deprecated
	public static LuceneIndexStatistics getStatistics(String lucenePath) {
		final Log LOGGER = LogFactory.getLog(Utils.class);
		
		// open Lucene index for reading
		IndexReader reader=null;
		LuceneIndexStatistics indexStatistics = new LuceneIndexStatistics();

		/*
		try {
			reader = IndexReader.open(lucenePath);
			indexStatistics.setNewestRecordDate(Utils.getNewestRecordDateFromIndex(reader));
			indexStatistics.setCurrent(reader.isCurrent());u
			//indexStatistics.setLastModified(IndexReader.lastModified(lucenePath));
			//indexStatistics.setCurrentVersion(IndexReader.getCurrentVersion(lucenePath));
		} catch (CorruptIndexException e) {
			LOGGER.error("CorruptIndexException in getStatistics ("+e.getMessage()+")");
		} catch (IOException e) {
			LOGGER.error("IOException in getStatistics("+e.getMessage()+")");
		}

		indexStatistics.setNumDocs(reader.numDocs());
		indexStatistics.setNumDeletedDocs(reader.numDeletedDocs());
		*/
		return indexStatistics;
	}
	
}