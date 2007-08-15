package beans;

import helpers.database.DBAdvancedSearchManager;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;

public class AdvancedSearchBean implements Serializable {

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 8838908246977059682L;

	/**
	 * the user 
	 */
	private String user = "";
	
	/**
	 * lists of retrieved data
	 */
	private LinkedList<String> tags;
	private LinkedList<String> authors;
	private LinkedList<String> titles;	
	private SortedSet[] tagTitle;
	private SortedSet[] authorTitle;
	private SortedSet[] tagAuthor;
	private SortedSet[] titleAuthor;
	private	String[]	bibtexHash;
	private String[]	bibtexUrls;
		
	/**
	 * default constructor
	 */
	public AdvancedSearchBean() {
		tags 	= new LinkedList<String>();
		authors = new LinkedList<String>();
		titles 	= new LinkedList<String>();				
	}
	
	/*
	 * getters and setters
	 */	
	public LinkedList<String> getTitles() {
		return titles;
	}

	public void setTitles(LinkedList<String> titles) {
		this.titles = titles;
	}

	public void setAuthors(LinkedList<String> authors) {
		this.authors = authors;
	}

	public LinkedList<String> getAuthors() {
		return authors;
	}

	public void setTags(LinkedList<String> tags) {
		this.tags = tags;
	}

	public LinkedList<String> getTags() {
		return tags;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		if (user != null)
			this.user = user.toLowerCase();		
		DBAdvancedSearchManager.queryDB(this);
	}

	public SortedSet[] getTagTitle() {
		return tagTitle;
	}

	public void setTagTitle(SortedSet[] tag_title) {
		this.tagTitle = tag_title;		
	}		
	
	public String getTagTitles() {
		return getArrayToString(tagTitle);
	}
	
	public String getAuthorTitles() {
		return getArrayToString(authorTitle);
	}
	
	public String getTagAuthors() {
		return getArrayToString(tagAuthor);
	}
	
	public String getTitleAuthors() {
		return getArrayToString(titleAuthor);
	}	
		
	public SortedSet[] getAuthorTitle() {
		return authorTitle;
	}

	public void setAuthorTitle(SortedSet[] authorTitle) {
		this.authorTitle = authorTitle;
	}
	
	public SortedSet[] getTagAuthor() {
		return tagAuthor;
	}

	public void setTagAuthor(SortedSet[] tagAuthor) {
		this.tagAuthor = tagAuthor;
	}

	public SortedSet[] getTitleAuthor() {
		return titleAuthor;
	}

	public void setTitleAuthor(SortedSet[] titleAuthor) {
		this.titleAuthor = titleAuthor;
	}

	public String[] getBibtexHash() {
		return bibtexHash;
	}

	public void setBibtexHash(String[] bibtexHash) {
		this.bibtexHash = bibtexHash;
	}

	public String[] getBibtexUrls() {
		return bibtexUrls;
	}

	public void setBibtexUrls(String[] bibtexUrls) {
		this.bibtexUrls = bibtexUrls;
	}

	/**
	 * generates a string from given set in javascript array syntax
	 * @param list the set
	 * @return a string of the elements
	 */
	public String getArrayToString(SortedSet[] list) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("[");		
		for (int i=0; i<list.length; i++) {
			buf.append("[");
			
			Iterator iter = list[i].iterator();			
			while(iter.hasNext()) {			
				buf.append(iter.next());				
				if(iter.hasNext())
					buf.append(",");				
			}
			
			buf.append("]");
			if (i != (list.length -1))
				buf.append(",");
		}
		buf.append("]");		
		
		return buf.toString();
	}	
}