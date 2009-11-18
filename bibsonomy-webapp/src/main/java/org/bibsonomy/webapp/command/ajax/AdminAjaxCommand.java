package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.AjaxCommand;

/**
 * Command for ajax requests from admin page
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminAjaxCommand extends AjaxCommand {
	
	/** list of bookmarks of an user */
	private List<Post<Bookmark>> bookmarks;
	
	/** prediction history of a user  */
	private List<User> predictionHistory;
	
	/** user for which we want to add a group or mark as spammer */
	private String userName; 
	
	/** key for updating classifier settings */
	private String key;
	
	/** value for updating classifier settings */
	private String value;
	
	/** show spam posts; enabled by default*/
	private String showSpamPosts = "true";
	
	/** total number of bookmarks*/
	private int bookmarkCount;
	
	/** total number of bibtex*/
	private int bibtexCount;
	
	/** evaluator name */
	private String evaluator;
	
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<Post<Bookmark>> getBookmarks() {
		return this.bookmarks;
	}
	public void setBookmarks(List<Post<Bookmark>> bookmarks) {
		this.bookmarks = bookmarks;
	}
	public List<User> getPredictionHistory() {
		return this.predictionHistory;
	}
	public void setPredictionHistory(List<User> predictionHistory) {
		this.predictionHistory = predictionHistory;
	}	
	public String getShowSpamPosts() {
		return this.showSpamPosts;
	}
	public void setShowSpamPosts(String showSpamPosts) {
		this.showSpamPosts = showSpamPosts;
	}
	
	public int getBookmarkCount() {
		return bookmarkCount;
	}
	
	public void setBookmarkCount(int bookmarkCount) {
		this.bookmarkCount = bookmarkCount;
	}
	
	public int getBibtexCount() {
		return bibtexCount;
	}
	
	public void setBibtexCount(int bibtexCount) {
		this.bibtexCount = bibtexCount;
	}
	
	public String getEvaluator() {
		return this.evaluator;
	}
	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}
}