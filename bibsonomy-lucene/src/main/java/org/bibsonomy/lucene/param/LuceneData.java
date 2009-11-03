package org.bibsonomy.lucene.param;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;
import java.util.TreeSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

public class LuceneData {


	private HashMap<String,String> bibtexContent;	
	private HashMap<String,String> bookmarkContent;	
	private RecordType contentType;
	
	
/*	
	public LuceneData() {
		this.contentType = null; 
		this.init();
	}
*/	
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

/*
 * setter for bookmarks	
 */
	
	public void setBookmarkContentId(String s) {
		this.bookmarkContent.put("content_id", s);
	}
	
	public void setBookmarkContentId(Integer i) {
		this.bookmarkContent.put("content_id", i.toString());
	}

	public void setBookmarkGroup(String s) {
		this.bookmarkContent.put("group", s);
	}

	public void setBookmarkGroup(Set<Group> g) {
		TreeSet<String> groupnameSet = new TreeSet<String>(); 
		String groupList = "";

		// sortieren (nur wichtig fuer den jUnit-Test
		for (Group grp : g) {
			groupnameSet.add(grp.getName()); 
		}
		// ausgabe der felder, getrennt durch je ein komma
		for (String s : groupnameSet) {
			groupList += s + ","; 
		}
		this.bookmarkContent.put("group", groupList.substring(0, groupList.length()-1));
	}

	public void setBookmarkDate(String s) {
		this.bookmarkContent.put("date", s);
	}

	public void setBookmarkDate(Date d) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		this.bookmarkContent.put("date", dateFormat.format(d) );
	}

	public void setBookmarkUsername(String s) {
		// use name conversion of user class (set to lowercase,...)
		User u = new User(s); 
		this.bookmarkContent.put("user_name", u.getName());
	}

	public void setBookmarkUsername(User u) {
		this.bookmarkContent.put("user_name", u.getName());
	}

	public void setBookmarkDescription(String s) {
		this.bookmarkContent.put("desc", s);
	}

	public void setBookmarkExt(String s) {
		this.bookmarkContent.put("ext", s);
	}

	public void setBookmarkUrl(String s) {
		this.bookmarkContent.put("url", s);
	}

	public void setBookmarkTas(String s) {
		this.bookmarkContent.put("tas", s);
	}

	public void setBookmarkTas(Set<Tag> t) {
		TreeSet<String> tagsSet = new TreeSet<String>();
		String tagList="";
		
		// sortieren (nur wichtig fuer den jUnit-Test
		for (Tag tag : t) {
			tagsSet.add(tag.getName()); 
		}
		
		// ausgabe der felder, getrennt durch je ein Leerzeichen
		for (String s : tagsSet) {
			tagList += s + " "; 
		}
		this.bookmarkContent.put("tas", tagList.substring(0, tagList.length()-1));
	}

	public void setBookmarkIntrahash(String s) {
		this.bookmarkContent.put("intrahash", s);
	}


/*
 * setter for bibtex
 */
	
	
	public void setBibtexContentId(String s) {
		this.bibtexContent.put("content_id", s);
	}
	
	public void setBibtexContentId(Integer i) {
		this.bibtexContent.put("content_id", i.toString());
	}

	public void setBibtexGroup(String s) {
		this.bibtexContent.put("group", s);
	}

	public void setBibtexDate(String s) {
		this.bibtexContent.put("date", s);
	}

	public void setBibtexUsername(String s) {
		this.bibtexContent.put("user_name", s);
	}

	public void setBibtexAuthor(String s) {
		this.bibtexContent.put("author", s);
	}

	public void setBibtexEditor(String s) {
		this.bibtexContent.put("editor", s);
	}

	public void setBibtexTitle(String s) {
		this.bibtexContent.put("title", s);
	}

	public void setBibtexJournal(String s) {
		this.bibtexContent.put("journal", s);
	}

	public void setBibtexBooktitle(String s) {
		this.bibtexContent.put("booktitle", s);
	}

	public void setBibtexVolume(String s) {
		this.bibtexContent.put("volume", s);
	}

	public void setBibtexNumber(String s) {
		this.bibtexContent.put("number", s);
	}

	public void setBibtexChapter(String s) {
		this.bibtexContent.put("chapter", s);
	}

	public void setBibtexEdition(String s) {
		this.bibtexContent.put("edition", s);
	}

	public void setBibtexMonth(String s) {
		this.bibtexContent.put("month", s);
	}

	public void setBibtexDay(String s) {
		this.bibtexContent.put("day", s);
	}

	public void setBibtexHowPublished(String s) {
		this.bibtexContent.put("howPublished", s);
	}

	public void setBibtexInstitution(String s) {
		this.bibtexContent.put("institution", s);
	}

	public void setBibtexOrganization(String s) {
		this.bibtexContent.put("organization", s);
	}

	public void setBibtexPublisher(String s) {
		this.bibtexContent.put("publisher", s);
	}

	public void setBibtexAddress(String s) {
		this.bibtexContent.put("address", s);
	}

	public void setBibtexSchool(String s) {
		this.bibtexContent.put("school", s);
	}

	public void setBibtexSeries(String s) {
		this.bibtexContent.put("series", s);
	}

	public void setBibtexBibtexKey(String s) {
		this.bibtexContent.put("bibtexKey", s);
	}

	public void setBibtexUrl(String s) {
		this.bibtexContent.put("url", s);
	}

	public void setBibtexType(String s) {
		this.bibtexContent.put("type", s);
	}

	public void setBibtexDescription(String s) {
		this.bibtexContent.put("desc", s);
	}

	public void setBibtex(String s) {
		this.bibtexContent.put("month", s);
	}
	
	public void setBibtexAnnote(String s) {
		this.bibtexContent.put("annote", s);
	}
	
	public void setBibtexNote(String s) {
		this.bibtexContent.put("note", s);
	}
	
	public void setBibtexPages(String s) {
		this.bibtexContent.put("pages", s);
	}
	
	public void setBibtexBKey(String s) {
		this.bibtexContent.put("bKey", s);
	}
	
	public void setBibtexCrossref(String s) {
		this.bibtexContent.put("crossref", s);
	}
	
	public void setBibtexMisc(String s) {
		this.bibtexContent.put("misc", s);
	}
	
	public void setBibtexBibtexAbstract(String s) {
		this.bibtexContent.put("bibtexAbstract", s);
	}
	
	public void setBibtexYear(String s) {
		this.bibtexContent.put("year", s);
	}

	public void setBibtexTas(String s) {
		this.bibtexContent.put("tas", s);
	}

	public void setBibtexEntrytype(String s) {
		this.bibtexContent.put("entrytype", s);
	}

	public void setBibtexIntrahash(String s) {
		this.bibtexContent.put("intrahash", s);
	}
	
	public void setBibtexInterhash(String s) {
		this.bibtexContent.put("interhash", s);
	}
	
/*
 * other getter/setter 
 */
	
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
	
	public void setPostBookmark (Post<Bookmark> bookmarkPost)
	{
		this.setBookmarkContentId(bookmarkPost.getContentId());
		this.setBookmarkDate(bookmarkPost.getDate());
		this.setBookmarkDescription(bookmarkPost.getResource().getTitle());
		this.setBookmarkGroup(bookmarkPost.getGroups());
		this.setBookmarkTas(bookmarkPost.getTags());
		this.setBookmarkUsername(bookmarkPost.getUser());
		this.setBookmarkIntrahash(bookmarkPost.getResource().getIntraHash());
		this.setBookmarkExt(bookmarkPost.getDescription());
		this.setBookmarkUrl(bookmarkPost.getResource().getUrl());
	}
		
		
	public void setPostBibTex (Post<BibTex> BibTexpost)
	{
		
		
	}
		
	@SuppressWarnings("unchecked")
	public void setPost(Post<? extends Resource> post) {
		// FIXME: remove this test by refactoring
		if( Bookmark.class.isAssignableFrom(post.getResource().getClass()) ){
			setContentType(RecordType.Bookmark);
			setPostBookmark((Post<Bookmark>)post);
		} else if( BibTex.class.isAssignableFrom(post.getResource().getClass()) ){
			setContentType(RecordType.BibTex);
			setPostBibTex((Post<BibTex>)post);
		}
	}
	
}
