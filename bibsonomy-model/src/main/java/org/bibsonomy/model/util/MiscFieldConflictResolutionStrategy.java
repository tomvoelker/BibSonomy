/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.util;

/**
 * a conflict resolution strategy for syncing {@link org.bibsonomy.model.BibTex#misc} with
 * the {@link org.bibsonomy.model.BibTex#miscFields} map
 *
 * @author dzo
 */
public interface MiscFieldConflictResolutionStrategy {

	/** a {@link MiscFieldConflictResolutionStrategy} where the misc field value wins */
	public static final MiscFieldConflictResolutionStrategy MISC_FIELD_WINS = new MiscFieldConflictResolutionStrategy() {
		@Override
		public String resoloveConflict(String key, String miscFieldValue, String miscFieldMapValue) {
			return miscFieldValue;
		}
	};

	/** a {@link MiscFieldConflictResolutionStrategy} where the misc field map value wins */
	public static final MiscFieldConflictResolutionStrategy MISC_FIELD_MAP_WINS = new MiscFieldConflictResolutionStrategy() {
		@Override
		public String resoloveConflict(String key, String miscFieldValue, String miscFieldMapValue) {
			return miscFieldMapValue;
		}
	};

	/**
	 * resolve the conflict while syncing the misc string field and the misc field map of a {@link org.bibsonomy.model.BibTex}
	 * @param key the key of the entry
	 * @param miscFieldValue the value of the misc field
	 * @param miscFieldMapValue the value of the misc field map
	 * @return the resolved value
	 */
	public String resoloveConflict(String key, String miscFieldValue, String miscFieldMapValue);
}
