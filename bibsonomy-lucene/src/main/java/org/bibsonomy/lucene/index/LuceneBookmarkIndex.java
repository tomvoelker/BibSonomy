package org.bibsonomy.lucene.index;

import java.util.HashMap;

import org.bibsonomy.lucene.param.RecordType;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

/**
 * class for managing the lucene bookmark index
 * 
 * @author fei
 *
 */
public class LuceneBookmarkIndex extends LuceneResourceIndex<Bookmark> {
	/** singleton instance */
	protected static LuceneResourceIndex<Bookmark> instance;
	
	/*
	@Override
	protected HashMap<String, String> getContentFields() {
		HashMap<String, String> contentFields = new HashMap<String, String>();
		contentFields.put("content_id", "");
		contentFields.put("group", "");
		contentFields.put("date", "");
		contentFields.put("user_name", "");
		contentFields.put("desc", "");
		contentFields.put("ext", "");
		contentFields.put("url", "");
		contentFields.put("tas", "");
		contentFields.put("intrahash", "");
		
		return contentFields;
	}
*/
	
	@Override
	protected Class<? extends Resource> getResourceType() {
		return Bookmark.class;
	}

	public static LuceneResourceIndex<Bookmark> getInstance() {
		if (instance == null) instance = new LuceneBookmarkIndex();
		return instance;
	}

/*
	@Override
	protected RecordType getRecordType() {
		return RecordType.Bookmark;
	}
	*/
}
