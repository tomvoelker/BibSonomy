package org.bibsonomy.webapp.command;

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.StatisticsConstraint;
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
		USERS;
	}
	
	private boolean spammers;
	private boolean all;
	
	
	private Class<? extends Resource> resourceType = Resource.class;
	
	private GroupingEntity grouping;
	private FilterEntity filter;
	
	private Set<StatisticsConstraint> contraints = new HashSet<StatisticsConstraint>();
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
	 * @return the contraint
	 */
	public Set<StatisticsConstraint> getContraints() {
		return this.contraints;
	}

	/**
	 * @param contraints the contraints to set
	 */
	public void setContraints(Set<StatisticsConstraint> contraints) {
		this.contraints = contraints;
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
	 * @return the filter
	 */
	public FilterEntity getFilter() {
		return this.filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(FilterEntity filter) {
		this.filter = filter;
	}
}
