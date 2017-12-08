package org.bibsonomy.model;

import java.io.Serializable;
import java.util.List;

/**
 * A PersonMatch object contains the id's of two persons which might be equal and a flag if they are equal
 *
 * @author jhi
 */
public class PersonMatch implements Serializable, Comparable<PersonMatch> {
	
	private static final long serialVersionUID = -470932185819510145L;
	public static final int denieThreshold = 5;
	
	private Person person1;
	private Person person2;
	private int state; //0 open, 1 denied, 2 already merged
	private int matchID;
	private List<MatchReason> reasons;
	private List<String> userDenies;
	
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
	 * @return the person1
	 */
	public Person getPerson1() {
		return this.person1;
	}
	/**
	 * @param person1 the person1 to set
	 */
	public void setPerson1(Person person1) {
		this.person1 = person1;
	}
	/**
	 * @return the person2
	 */
	public Person getPerson2() {
		return this.person2;
	}
	/**
	 * @param person2 the person2 to set
	 */
	public void setPerson2(Person person2) {
		this.person2 = person2;
	}
	/**
	 * @return the deleted
	 */
	public int getState() {
		return this.state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the reasons
	 */
	public List<MatchReason> getReasons() {
		return this.reasons;
	}
	/**
	 * @param reasons the reasons to set
	 */
	public void setReasons(List<MatchReason> reasons) {
		this.reasons = reasons;
	}
	/**
	 * @return the userDenies
	 */
	public List<String> getUserDenies() {
		return this.userDenies;
	}
	/**
	 * @param userDenies the userDenies to set
	 */
	public void setUserDenies(List<String> userDenies) {
		this.userDenies = userDenies;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PersonMatch match) {
		if ((this.person1.getPersonId().equals(match.getPerson1().getPersonId()) && this.person2.getPersonId().equals(match.getPerson2().getPersonId()))
				|| (this.person1.getPersonId().equals(match.getPerson2().getPersonId()) && this.person2.getPersonId().equals(match.getPerson1().getPersonId()))) {
			return 0;
		}
		return -1;
	}
}
