package beans;

import helpers.database.DBUserTagsManager;

import java.util.SortedSet;
import java.io.Serializable;

import resources.TagConcept;


/**
 * Stores several tags which should be shown in the tag cloud of a page
 *
 */
public class TagConceptBean implements Serializable {
	
	private static final long serialVersionUID = 3257858967094524929L;
	/**
	 * if <code>true</code> tags in the set will be marked as supertags (if they're supertags)
	 */
	private boolean withMarkedSupertags;
	private SortedSet<TagConcept> tags = null;
	private String currUser            = null;
	private int sortOrder              = 0; // 0 = alph, 1 = freq
	private String requUser            = null;
	private String requAuthor		   = null;	
	private int minfreq 			   = -1;

	public TagConceptBean() {
		withMarkedSupertags = true;
	}

	public void setWithMarkedSupertags(boolean withMarkedSupertags) {
		this.withMarkedSupertags = withMarkedSupertags;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setCurrUser (String currUser) {
		this.currUser = currUser;
	}
	
	public void setRequAuthor(String requAuthor) {
		this.requAuthor = requAuthor;
	}

	public SortedSet<TagConcept> getTags() {
		if (currUser != null) {
			tags = DBUserTagsManager.getSortedTagsForUser(requUser, currUser, sortOrder, withMarkedSupertags, minfreq);
		}
		return tags;
	}

	public void setRequUser(String requUser) {
		this.requUser = requUser;
	}

	public void setMinfreq(int minfreq) {
		this.minfreq = minfreq;
	}
	
	public int getMinfreq() {
		System.err.println("hier");
		return minfreq;
	}
		
}// end class