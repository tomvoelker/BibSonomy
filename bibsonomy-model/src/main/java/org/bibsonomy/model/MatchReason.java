package org.bibsonomy.model;

import java.io.Serializable;

/**
 * A Match object contains the id's of two persons which might be equal and information why this merge is considered.
 * The reasons could be the same co_authors, same title of one of their publications as well as similar related tags.
 * Only persons with the same name will be considered
 *
 * @author jhi
 */
public class MatchReason implements Serializable{
	
	private static final long serialVersionUID = 1117046550975490684L;

	private int matchID;
	private Post item1;
	private Post item2;
	private String mode; // "auth" for matched via common co_authors on some pubs, "titl" for same title of a pub, or "sim" for similar via tags

	/**
	 * @return the matchID
	 */
	public int getMatchID() {
		return this.matchID;
	}
	/**
	 * @param matchID the matchID to set
	 */
	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}
	/**
	 * @return the item1
	 */
	public Post getItem1() {
		return this.item1;
	}
	/**
	 * @param item the item1 to set
	 */
	public void setItem1(Post item) {
		this.item1 = item;
	}
	/**
	 * @return the item2
	 */
	public Post getItem2() {
		return this.item2;
	}
	/**
	 * @param item the item2 to set
	 */
	public void setItem2(Post item) {
		this.item2 = item;
	}
	/**
	 * @return the mode
	 */
	public String getMode() {
		return this.mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
}
