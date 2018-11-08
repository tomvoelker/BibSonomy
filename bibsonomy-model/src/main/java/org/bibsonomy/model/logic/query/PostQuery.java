package org.bibsonomy.model.logic.query;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;

import java.util.List;

/**
 * query used to retrieve posts
 *
 * @author dzo
 * @param <R>
 */
public class PostQuery<R extends Resource> extends BasicQuery {
	private Class<R> resourceClass;

	/** flag to retrieve posts where the person names are not assigned to a person */
	private boolean personIdSet = true;

	private List<PersonName> personNames;

	/**
	 * default constructor
	 * @param resourceClass
	 */
	public PostQuery(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
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
	 * @return the personIdSet
	 */
	public boolean isPersonIdSet() {
		return personIdSet;
	}

	/**
	 * @param personIdSet the personIdSet to set
	 */
	public void setPersonIdSet(boolean personIdSet) {
		this.personIdSet = personIdSet;
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
}
