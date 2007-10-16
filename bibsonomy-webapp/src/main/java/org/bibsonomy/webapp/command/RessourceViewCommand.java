/*
 * Created on 26.08.2007
 */
package org.bibsonomy.webapp.command;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

public class RessourceViewCommand extends BaseCommand {
	private ListView<Post<Bookmark>> bookmark = new ListView<Post<Bookmark>>();
	private ListView<Post<BibTex>> bibtex = new ListView<Post<BibTex>>();
	private String requestedUser;
	
	public ListView<Post<BibTex>> getBibtex() {
		return this.bibtex;
	}
	public void setBibtex(ListView<Post<BibTex>> bibtex) {
		this.bibtex = bibtex;
	}
	public ListView<Post<Bookmark>> getBookmark() {
		return this.bookmark;
	}
	public void setBookmark(ListView<Post<Bookmark>> bookmark) {
		this.bookmark = bookmark;
	}
	public String getRequestedUser() {
		return this.requestedUser;
	}
	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}
	
	
}