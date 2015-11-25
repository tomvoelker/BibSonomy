/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.update;

import java.util.Date;

import org.apache.commons.lang.ObjectUtils;

/**
 * Stores up to which ids the index has been updated
 *
 * @author lutful
 */
public class SearchIndexSyncState {
	
	// TODO: rename attribute to lastTASId
	private Integer last_tas_id;
	// TODO: rename attribute to lastLogDate
	private Date last_log_date;
	private long lastPersonChangeId;

	/**
	 * default constructor
	 */
	public SearchIndexSyncState() {
	}
	
	/**
	 * @param state
	 */
	public SearchIndexSyncState(SearchIndexSyncState state) {
		this.last_log_date = state.last_log_date;
		this.last_tas_id = state.last_tas_id;
		this.lastPersonChangeId = state.lastPersonChangeId;
	}
	
	/**
	 * @return the last_log_date
	 */
	public Date getLast_log_date() {
		return this.last_log_date;
	}

	/**
	 * @param last_log_date the last_log_date to set
	 */
	public void setLast_log_date(Date last_log_date) {
		this.last_log_date = last_log_date;
	}

	/**
	 * @return the last_tas_id
	 */
	public Integer getLast_tas_id() {
		return this.last_tas_id;
	}

	/**
	 * @param last_tas_id the last_tas_id to set
	 */
	public void setLast_tas_id(Integer last_tas_id) {
		this.last_tas_id = last_tas_id;
	}

	public long getLastPersonChangeId() {
		return this.lastPersonChangeId;
	}

	public void setLastPersonChangeId(long lastPersonChangeId) {
		this.lastPersonChangeId = lastPersonChangeId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(last_tas_id) + ObjectUtils.hashCode(last_log_date) + ((int) lastPersonChangeId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SearchIndexSyncState)) {
			return false;
		}
		
		final SearchIndexSyncState otherState = (SearchIndexSyncState)obj;
		if (!ObjectUtils.equals(last_log_date, otherState.last_log_date)) {
			return false;
		}
		if (!ObjectUtils.equals(last_tas_id, otherState.last_tas_id)) {
			return false;
		}
		if (!ObjectUtils.equals(lastPersonChangeId, otherState.lastPersonChangeId)) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + last_tas_id + ", " + last_log_date + ", " + lastPersonChangeId + "]";
	}
}
