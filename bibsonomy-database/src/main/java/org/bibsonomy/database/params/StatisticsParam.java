/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsUnit;

/**
 * @author Christian Kramer
 */
public class StatisticsParam extends GenericParam {
	private StatisticsUnit unit;
	
	private Classifier classifier;
	
	private Integer interval;
	
	private SpamStatus spamStatus;

	/**
	 * @return the unit
	 */
	public StatisticsUnit getUnit() {
		return this.unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(StatisticsUnit unit) {
		this.unit = unit;
	}

	/**
	 * @return the classifier
	 */
	public Classifier getClassifier() {
		return this.classifier;
	}

	/**
	 * @param classifier the classifier to set
	 */
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	/**
	 * @return the spamStatus
	 */
	public SpamStatus getSpamStatus() {
		return this.spamStatus;
	}

	/**
	 * @param spamStatus the spamStatus to set
	 */
	public void setSpamStatus(SpamStatus spamStatus) {
		this.spamStatus = spamStatus;
	}
	
	/**
	 * @return the interval
	 */
	public Integer getInterval() {
		return this.interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}
}