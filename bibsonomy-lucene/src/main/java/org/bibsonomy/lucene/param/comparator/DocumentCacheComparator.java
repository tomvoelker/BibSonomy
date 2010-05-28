package org.bibsonomy.lucene.param.comparator;

import java.util.Comparator;

import org.apache.lucene.document.Document;
import org.bibsonomy.lucene.util.LuceneBase;

/**
 * comparator for avoiding duplicates in index-update-cache
 * 
 * FIXME: we have to consider the case, that a post is updated between
 *        two update sessions, after the user was unflagged as spammer
 *        THIS UPDATE WILL BE LOST!!!
 * 
 * @author fei
 * @version $Id$
 */
public class DocumentCacheComparator extends LuceneBase implements Comparator<Document> {

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
			if( (date1!=null) && (date2!=null) ) {
				int cmp = date1.compareTo(date2);
				if( cmp==0 )
					return -1;
				
				return cmp;
			}
		} catch(NumberFormatException e) {
			return -1;
		}
		
		// fallback
		return -1;
	}
}
