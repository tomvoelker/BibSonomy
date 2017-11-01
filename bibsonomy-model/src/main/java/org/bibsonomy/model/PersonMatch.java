package org.bibsonomy.model;

import java.io.Serializable;

/**
 * A PersonMatch object contains the id's of two persons which might be equal and a flag if they are equal
 *
 * @author jhi
 */
public class PersonMatch implements Serializable {
	
	private static final long serialVersionUID = -470932185819510145L;
	
	private String person1ID;
	private String person2ID;
	private int state; //0 open, 1 denied, 2 already merged
	private int matchID;
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
	 * @return the person1ID
	 */
	public String getPerson1ID() {
		return this.person1ID;
	}
	/**
	 * @param person1id the person1ID to set
	 */
	public void setPerson1ID(String person1id) {
		person1ID = person1id;
	}
	/**
	 * @return the person2ID
	 */
	public String getPerson2ID() {
		return this.person2ID;
	}
	/**
	 * @param person2id the person2ID to set
	 */
	public void setPerson2ID(String person2id) {
		person2ID = person2id;
	}
	/**
	 * @return the deleted
	 */
	public int getState() {
		return this.state;
	}
	/**
	 * @param deleted the deleted to set
	 */
	public void setState(int state) {
		this.state = state;
	}
}
