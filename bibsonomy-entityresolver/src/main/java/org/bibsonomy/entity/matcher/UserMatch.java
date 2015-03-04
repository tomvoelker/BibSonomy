/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.entity.matcher;

import no.priv.garshol.duke.Record;


/**
 * store a single user matching
 * @author fei
 */
public class UserMatch implements Comparable<UserMatch> {
	private final String id;
	private final Record record;
	private final double confidence;
	
	/**
	 * constructor for an user match
	 * @param id
	 * @param obj
	 * @param confidence
	 */
	public UserMatch(final String id, final Record obj, final double confidence) {
		this.id = id;
		this.record = obj;
		this.confidence = confidence;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final UserMatch other = (UserMatch) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}



	@Override
	public int compareTo(final UserMatch o) {
		if (this.id == null) {
			return -1;
		}
		if ((o == null) || (o.id == null) ) {
			return 1;
		}

		return this.id.compareToIgnoreCase(o.id);
	}
	
	/**
	 * gets property from stored object
	 *  
	 * @param key name of the property
	 * @return the property value, if exists - null otherwise
	 */
	public String getProperty(final String key) {
		if (this.record != null) {
			return this.record.getValue(key);
		}
		
		return null;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the record
	 */
	public Record getRecord() {
		return this.record;
	}

	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}
}