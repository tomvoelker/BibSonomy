/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.StatisticsUnit;
import org.bibsonomy.model.Resource;


/**
 * command for statistic informations
 *
 * @author dzo
 */
public class StatisticsCommand extends BaseCommand {
	public enum StatisticType {
		POSTS,
		TAGS,
		DOCUMENTS,
		USERS;
	}
	
	private boolean spammers;
	private boolean all;
	
	
	private Class<? extends Resource> resourceType = Resource.class;
	
	private GroupingEntity grouping;
	private Set<Filter> filters = new HashSet<Filter>();
	
	private ConceptStatus conceptStatus;
	
	private Integer interval;
	private StatisticsUnit unit;
	private StatisticType type = StatisticType.POSTS;
	
	private String responseString;
	
	/**
	 * @return the spammers
	 */
	public boolean isSpammers() {
		return this.spammers;
	}

	/**
	 * @param spammers the spammers to set
	 */
	public void setSpammers(boolean spammers) {
		this.spammers = spammers;
	}

	/**
	 * @return the all
	 */
	public boolean isAll() {
		return this.all;
	}

	/**
	 * @param all the all to set
	 */
	public void setAll(boolean all) {
		this.all = all;
	}

	/**
	 * @return the type
	 */
	public StatisticType getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(StatisticType type) {
		this.type = type;
	}

	/**
	 * @return the responseString
	 */
	public String getResponseString() {
		return this.responseString;
	}

	/**
	 * @param responseString the responseString to set
	 */
	public void setResponseString(String responseString) {
		this.responseString = responseString;
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
	 * @return the grouping
	 */
	public GroupingEntity getGrouping() {
		return this.grouping;
	}

	/**
	 * @param grouping the grouping to set
	 */
	public void setGrouping(GroupingEntity grouping) {
		this.grouping = grouping;
	}

	/**
	 * @return the resourceType
	 */
	public Class<? extends Resource> getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}
	
	/**
	 * @return the filters
	 */
	public Set<Filter> getFilters() {
		return this.filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(Set<Filter> filters) {
		this.filters = filters;
	}

	/**
	 * @return the conceptStatus
	 */
	public ConceptStatus getConceptStatus() {
		return this.conceptStatus;
	}

	/**
	 * @param conceptStatus the conceptStatus to set
	 */
	public void setConceptStatus(ConceptStatus conceptStatus) {
		this.conceptStatus = conceptStatus;
	}
}
