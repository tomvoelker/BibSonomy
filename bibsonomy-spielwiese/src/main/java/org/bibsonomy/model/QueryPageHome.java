package org.bibsonomy.model;


import java.util.List;



/**
 * @author mgr
 *
 */
public class QueryPageHome{
	
	private List<Bookmark> homepageBookmark;
	private List<BibTex> homepageBibtex;
	int limit_bookmark=20;
	int limit_bibtex=15;
	int group =0;
	
	public List getBookmark() {
		return this.homepageBookmark;
	}
	public void setBookmark(List <Bookmark> homepageBookmark) {
		this.homepageBookmark = homepageBookmark;
	}
	
	public List getBibtex() {
		return this.homepageBibtex;
	}
	public void setBibtex(List <BibTex> homepageBibtex) {
		this.homepageBibtex = homepageBibtex;
	}
	
	
	
	
	
	
	
	
	
	
}