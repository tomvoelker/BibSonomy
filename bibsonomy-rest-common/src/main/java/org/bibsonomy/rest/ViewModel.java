/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
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
package org.bibsonomy.rest;

import java.util.List;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class ViewModel {

	/**
	 * a url to the next part of the resource list
	 */
	private String urlToNextResources;

	/**
	 * start value for the list of resources
	 */
	private int startValue;

	/**
	 * end value for the list of resources
	 */
	private int endValue;

	/**
	 * a list of sort criteria
	 */
	private List<SortCriteria> sortCriteria;

	/**
	 * @return Returns the urlToNextResources.
	 */
	public String getUrlToNextResources() {
		return urlToNextResources;
	}

	/**
	 * @param urlToNextResources
	 *            The urlToNextResources to set.
	 */
	public void setUrlToNextResources(String urlToNextResources) {
		this.urlToNextResources = urlToNextResources;
	}

	/**
	 * @return Returns the endValue.
	 */
	public int getEndValue() {
		return endValue;
	}

	/**
	 * @param endValue
	 *            The endValue to set.
	 */
	public void setEndValue(int endValue) {
		this.endValue = endValue;
	}

	/**
	 * @return Returns the startValue.
	 */
	public int getStartValue() {
		return startValue;
	}

	/**
	 * @param startValue
	 *            The startValue to set.
	 */
	public void setStartValue(int startValue) {
		this.startValue = startValue;
	}

	/**
	 * @return the sortCriteria
	 */
	public List<SortCriteria> getSortCriteria() {
		return sortCriteria;
	}

	/**
	 * @param sortCriteria the sortCriteria to set
	 */
	public void setSortCriteria(List<SortCriteria> sortCriteria) {
		this.sortCriteria = sortCriteria;
	}
}