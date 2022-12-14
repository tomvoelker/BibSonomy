/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;

/**
 * stores up to which ids the index has been updated
 *
 * @author lutful
 */
@Getter
@Setter
public class DefaultSearchIndexSyncState extends SearchIndexSyncState {


	/** last logged TAS ID change date in index */
	private Integer lastTasId;

	/** last logged change date of index */
	private Date lastLogDate;

	/** last logged person change date in index */
	private Date lastPersonLogDate;

	/** last logged person-resource-relation change date in index */
	private Date lastRelationLogDate;

	/** last logged document change date in index */
	private Date lastDocumentDate;

	/** last logged prediction change date in index */
	private Date lastPredictionChangeDate;

	/** the last content id of normal posts */
	private long lastPostContentId;

	/** the last content id of a person */
	private long lastPersonChangeId;

	/**
	 * default constructor
	 */
	public DefaultSearchIndexSyncState() {
		// noop
	}
	
	/**
	 * @param state
	 */
	public DefaultSearchIndexSyncState(DefaultSearchIndexSyncState state) {
		this.lastLogDate = state.lastLogDate;
		this.lastTasId = state.lastTasId;
		this.lastPersonChangeId = state.lastPersonChangeId;
		this.lastPredictionChangeDate = state.lastPredictionChangeDate;
		this.setMappingVersion(state.getMappingVersion());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.lastTasId) + ObjectUtils.hashCode(this.lastLogDate) + ObjectUtils.hashCode(this.lastDocumentDate) + ((int) lastPersonChangeId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultSearchIndexSyncState)) {
			return false;
		}
		
		final DefaultSearchIndexSyncState otherState = (DefaultSearchIndexSyncState)obj;

		if (!ObjectUtils.equals(this.lastLogDate, otherState.lastLogDate)) {
			return false;
		}
		if (!ObjectUtils.equals(this.lastTasId, otherState.lastTasId)) {
			return false;
		}

		if (!ObjectUtils.equals(this.lastDocumentDate, otherState.lastDocumentDate)) {
			return false;
		}

		/*
		if (!ObjectUtils.equals(this.lastPersonLogDate, otherState.lastPersonLogDate)) {
			return false;
		}
		if (!ObjectUtils.equals(this.lastRelationLogDate, otherState.lastRelationLogDate)) {
			return false;
		}
		 */
		
		if (!ObjectUtils.equals(this.lastPredictionChangeDate, otherState.lastPredictionChangeDate)) {
			return false;
		}

		if (!ObjectUtils.equals(this.lastPostContentId, otherState.lastPostContentId)) {
			return false;
		}

		if (!ObjectUtils.equals(this.lastPersonChangeId, otherState.lastPersonChangeId)) {
			return false;
		}

		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + this.lastTasId + ", " + this.lastLogDate + ", " + this.lastPersonChangeId + "]";
	}
}
