package org.bibsonomy.lucene;

import java.util.HashMap;
import java.util.Set;

public class LuceneData {


	private HashMap<String,String> bibtexContent;	
	private HashMap<String,String> bookmarkContent;	
	private RecordType contentType;	
	
	
	
	public LuceneData() {
		this.contentType = null; 
		this.init();
	}
	
	public LuceneData(RecordType recordType) {
		this.contentType = recordType;
		this.init();
	}
		

	private void init() {
		
		/*
		 * initialize bibtexContent
		 */
		this.bibtexContent = new HashMap<String,String>();
		this.bibtexContent.put("content_id", "");
		this.bibtexContent.put("group", "");
		this.bibtexContent.put("date", "");
		this.bibtexContent.put("user_name", "");
		this.bibtexContent.put("author", "");
		this.bibtexContent.put("editor", "");
		this.bibtexContent.put("title", "");
		this.bibtexContent.put("journal", "");
		this.bibtexContent.put("booktitle", "");
		this.bibtexContent.put("volume", "");
		this.bibtexContent.put("number", "");
		this.bibtexContent.put("chapter", "");
		this.bibtexContent.put("edition", "");
		this.bibtexContent.put("month", "");
		this.bibtexContent.put("day", "");
		this.bibtexContent.put("howPublished", "");
		this.bibtexContent.put("institution", "");
		this.bibtexContent.put("organization", "");
		this.bibtexContent.put("publisher", "");
		this.bibtexContent.put("address", "");
		this.bibtexContent.put("school", "");
		this.bibtexContent.put("series", "");
		this.bibtexContent.put("bibtexKey", "");
		this.bibtexContent.put("url", "");
		this.bibtexContent.put("type", "");
		this.bibtexContent.put("description", "");
		this.bibtexContent.put("annote", "");
		this.bibtexContent.put("note", "");
		this.bibtexContent.put("pages", "");
		this.bibtexContent.put("bKey", "");
		this.bibtexContent.put("crossref", "");
		this.bibtexContent.put("misc", "");
		this.bibtexContent.put("bibtexAbstract", "");
		this.bibtexContent.put("year", "");
		this.bibtexContent.put("tas", "");
		this.bibtexContent.put("entrytype", "");
		this.bibtexContent.put("intrahash", "");
		this.bibtexContent.put("interhash", "");
		
		/*
		 * initialize bookmarkContent
		 */
		this.bookmarkContent = new HashMap<String,String>();
		this.bookmarkContent.put("content_id", "");
		this.bookmarkContent.put("group", "");
		this.bookmarkContent.put("date", "");
		this.bookmarkContent.put("user_name", "");
		this.bookmarkContent.put("desc", "");
		this.bookmarkContent.put("ext", "");
		this.bookmarkContent.put("url", "");
		this.bookmarkContent.put("tas", "");
		this.bookmarkContent.put("intrahash", "");
	}
	
	
	public RecordType getContentType() {
		return contentType;
	}


	public void setContentType(RecordType contentType) {
		this.contentType = contentType;
	}



	public void setField(String key, String value)
	{
		if (RecordType.BibTex == this.contentType) {
			if (bibtexContent.containsKey(key)) {
				this.bibtexContent.put(key, value);
			}
		} else if (RecordType.Bookmark == this.contentType) {
			if (bookmarkContent.containsKey(key)) {
				this.bookmarkContent.put(key, value);
			}		
		}		
	}
	
	
	public Set<String> getFields() {
		Set<String> content;
		if (RecordType.BibTex == this.contentType) {
			return this.bibtexContent.keySet(); 
		} else if (RecordType.Bookmark == this.contentType) {
			return this.bookmarkContent.keySet(); 
		} else {
			content = null;
		}
		return content;
	}
	
	
	
	public HashMap<String,String> getContent(){
		HashMap<String,String> content;
		if (RecordType.BibTex == this.contentType) {
			content =  this.bibtexContent; 
		} else if (RecordType.Bookmark == this.contentType) {
			content = this.bookmarkContent; 
		} else {
			content = null;
		}
		return content;
	}
		
		
	
}
