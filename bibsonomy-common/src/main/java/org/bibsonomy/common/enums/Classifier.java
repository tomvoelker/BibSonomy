/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.common.enums;

/**
 * Defines different possibilities of classifiers of a user
 * 
 * @author Stefan Stützer
 */
public enum Classifier {
	/** An automatic classifier algorithm */
	CLASSIFIER(0),

	/** An administrator */
	ADMIN(1),
	
	/** Both */
	BOTH(2);
	
	/** the id */
	private int id;
	
	private Classifier(int id) {
		this.id = id;
	}

	/**
	 * @return the id for an enum
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * @param id of the Classifier to retrieve
	 * @return the corresponding Classifier enum
	 */
	public static Classifier getClassifier(final int id) {
		switch(id) {
		case 0: 
			return CLASSIFIER;
		case 1:
			return ADMIN;
		case 2:
			return BOTH;
		default:
			return null;
		}
	}
}