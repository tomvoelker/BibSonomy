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
	private String item1ID;
	private String item2ID;
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
	 * @return the item1ID
	 */
	public String getItem1ID() {
		return this.item1ID;
	}
	/**
	 * @param item1id the item1ID to set
	 */
	public void setItem1ID(String item1id) {
		item1ID = item1id;
	}
	/**
	 * @return the item2ID
	 */
	public String getItem2ID() {
		return this.item2ID;
	}
	/**
	 * @param item2id the item2ID to set
	 */
	public void setItem2ID(String item2id) {
		item2ID = item2id;
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
