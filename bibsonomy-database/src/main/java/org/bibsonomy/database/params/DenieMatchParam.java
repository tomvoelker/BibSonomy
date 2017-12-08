package org.bibsonomy.database.params;

/**
 * TODO: add documentation to this class
 *
 * @author jhi
 */
public class DenieMatchParam {
	private String userName;
	private int matchId;
	private int newMatchId;
	/**
	 * @return the newMatchId
	 */
	public int getNewMatchId() {
		return this.newMatchId;
	}
	/**
	 * @param newMatchId the newMatchId to set
	 */
	public void setNewMatchId(int newMatchId) {
		this.newMatchId = newMatchId;
	}
	/**
	 * @param matchID2
	 * @param userName2
	 */
	public DenieMatchParam(int matchID, int newMatchId) {
		this.matchId=matchId;
		this.newMatchId=newMatchId;
	}
	/**
	 * @param matchID2
	 * @param userName2
	 */
	public DenieMatchParam(int matchID, String userName) {
		this.userName=userName;
		this.matchId = matchID;
	}
	/**
	 * @return the matchId
	 */
	public int getMatchId() {
		return this.matchId;
	}
	/**
	 * @param matchId the matchId to set
	 */
	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
