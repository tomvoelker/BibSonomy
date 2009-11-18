package org.bibsonomy.lucene.param.comparator;

import java.util.Comparator;

import org.apache.lucene.document.Document;

/**
 * comparator for avoiding duplicates in index-update-cache
 * 
 * @author fei
 */
public class DocumentCacheComparator implements Comparator<Document> {

	private static final String FLD_LAST_LOG_DATE = "last_log_date";
	private static final String FLD_CONTENT_ID    = "content_id";

	@Override
	public int compare(Document o1, Document o2) {
		
		// most important: treat documents as equal, if their content_ids conincide
		String id1 = o1.get(FLD_CONTENT_ID);
		String id2 = o2.get(FLD_CONTENT_ID);
		if( (id1!=null) && (id2!=null) && (id1.equals(id2)) )
			return 0;
		
		// otherwise order according to the date field
		try {
			Long date1  = Long.parseLong(o1.get(FLD_LAST_LOG_DATE));
			Long date2  = Long.parseLong(o2.get(FLD_LAST_LOG_DATE));
			if( (date1!=null) && (date2!=null) )
				return date1.compareTo(date2);
		} catch(NumberFormatException e) {
			return -1;
		}
		
		// fallback
		return -1;
	}
}
