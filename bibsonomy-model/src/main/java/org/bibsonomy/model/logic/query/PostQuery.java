package org.bibsonomy.model.logic.query;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * query used to retrieve posts
 *
 * @author dzo
 * @param <R>
 */
public class PostQuery<R extends Resource> extends BasicQuery {
	/**
	 * resource type to be shown.
	 */
	private Class<R> resourceClass;

	/**
	 * whether to search locally or using an index shared by several systems or the fulltext search
	 */
	private QueryScope scope = QueryScope.LOCAL;

	/**
	 * grouping tells whom posts are to be shown: the posts of a
	 * user, of a group or of the viewables.
	 */
	private GroupingEntity grouping = GroupingEntity.ALL;

	/**
	 * name of the grouping. if grouping is user, then its the
	 * username. if grouping is set to {@link GroupingEntity#ALL},
	 * then its an empty string!
	 */
	private String groupingName;

	/**
	 * a set of tags. remember to parse special tags like
	 * ->[tagname], -->[tagname] and <->[tagname]. see documentation.
	 *  if the parameter is not used, its an empty list
	 */
	private List<String> tags;

	/**
	 * hash value of a resource, if one would like to get a list of
	 * all posts belonging to a given resource. if unused, its empty
	 * but not null.
	 */
	private String hash;

	/**
	 * filter for the retrieved posts
	 */
	private Set<Filter> filters;

	/**
	 * if given, only posts that have been created after (inclusive) startDate are returned
	 */
	private Date startDate;

	/**
	 * if given, only posts that have been created before (inclusive) endDate are returned
	 */
	private Date endDate;

	/**
	 * flag to retrieve posts where the person names are not assigned to a person
	 */
	private boolean onlyIncludeAuthorsWithoutPersonId = false;

	private List<PersonName> personNames;

	/**
	 * get all publications assigned to persons of the specified college
	 */
	private String college;

	/**
	 * list of sort criterion and ascending/descending sorting
	 */
	private List<SortCriteria> sortCriteria;

	/**
	 * default constructor
	 * @param resourceClass
	 */
	public PostQuery(final Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	/**
	 * @return the scope
	 */
	public QueryScope getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(QueryScope scope) {
		this.scope = scope;
	}

	/**
	 * @return the grouping
	 */
	public GroupingEntity getGrouping() {
		return grouping;
	}

	/**
	 * @param grouping the grouping to set
	 */
	public void setGrouping(GroupingEntity grouping) {
		this.grouping = grouping;
	}

	/**
	 * @return the groupingName
	 */
	public String getGroupingName() {
		return groupingName;
	}

	/**
	 * @param groupingName the groupingName to set
	 */
	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the filters
	 */
	public Set<Filter> getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(Set<Filter> filters) {
		this.filters = filters;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the resourceClass
	 */
	public Class<R> getResourceClass() {
		return resourceClass;
	}

	/**
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	/**
	 * @return the onlyIncludeAuthorsWithoutPersonId
	 */
	public boolean isOnlyIncludeAuthorsWithoutPersonId() {
		return onlyIncludeAuthorsWithoutPersonId;
	}

	/**
	 * @param onlyIncludeAuthorsWithoutPersonId the onlyIncludeAuthorsWithoutPersonId to set
	 */
	public void setOnlyIncludeAuthorsWithoutPersonId(boolean onlyIncludeAuthorsWithoutPersonId) {
		this.onlyIncludeAuthorsWithoutPersonId = onlyIncludeAuthorsWithoutPersonId;
	}

	/**
	 * @return the personNames
	 */
	public List<PersonName> getPersonNames() {
		return personNames;
	}

	/**
	 * @param personNames the personNames to set
	 */
	public void setPersonNames(List<PersonName> personNames) {
		this.personNames = personNames;
	}

	/**
	 * @return the college
	 */
	public String getCollege() {
		return college;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
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
