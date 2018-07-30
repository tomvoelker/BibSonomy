/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.params;

/**
 * TODO: add documentation to this class
 *
 * @author jhi
 */
public class DenyMatchParam {
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
	public DenyMatchParam(int matchID, int newMatchId) {
		this.matchId=matchId;
		this.newMatchId=newMatchId;
	}
	/**
	 * @param matchID2
	 * @param userName2
	 */
	public DenyMatchParam(int matchID, String userName) {
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
