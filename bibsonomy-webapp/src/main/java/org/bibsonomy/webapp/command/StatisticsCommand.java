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
