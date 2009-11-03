package org.bibsonomy.lucene.index;

import java.util.HashMap;

import org.bibsonomy.lucene.param.RecordType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;

/**
 * class for managing the lucene bibtex index
 * 
 * @author fei
 *
 */
public class LuceneBibTexIndex extends LuceneResourceIndex<BibTex> {
	
	/** singleton instance */
	protected static LuceneResourceIndex<BibTex> instance;
	
	
	@Override
	protected HashMap<String, String> getContentFields() {
		HashMap<String, String> contentFields = new HashMap<String, String>();
		
		contentFields.put("content_id", "");
		contentFields.put("group", "");
		contentFields.put("date", "");
		contentFields.put("user_name", "");
		contentFields.put("author", "");
		contentFields.put("editor", "");
		contentFields.put("title", "");
		contentFields.put("journal", "");
		contentFields.put("booktitle", "");
		contentFields.put("volume", "");
		contentFields.put("number", "");
		contentFields.put("chapter", "");
		contentFields.put("edition", "");
		contentFields.put("month", "");
		contentFields.put("day", "");
		contentFields.put("howPublished", "");
		contentFields.put("institution", "");
		contentFields.put("organization", "");
		contentFields.put("publisher", "");
		contentFields.put("address", "");
		contentFields.put("school", "");
		contentFields.put("series", "");
		contentFields.put("bibtexKey", "");
		contentFields.put("url", "");
		contentFields.put("type", "");
		contentFields.put("description", "");
		contentFields.put("annote", "");
		contentFields.put("note", "");
		contentFields.put("pages", "");
		contentFields.put("bKey", "");
		contentFields.put("crossref", "");
		contentFields.put("misc", "");
		contentFields.put("bibtexAbstract", "");
		contentFields.put("year", "");
		contentFields.put("tas", "");
		contentFields.put("entrytype", "");
		contentFields.put("intrahash", "");
		contentFields.put("interhash", "");
		
		return contentFields;
	}

	@Override
	protected Class<? extends Resource> getResourceType() {
		return BibTex.class;
	}

	public static LuceneResourceIndex<BibTex> getInstance() {
		if (instance == null) instance = new LuceneBibTexIndex();
		return instance;
	}

	@Override
	protected RecordType getRecordType() {
		return RecordType.BibTex;
	}
}
