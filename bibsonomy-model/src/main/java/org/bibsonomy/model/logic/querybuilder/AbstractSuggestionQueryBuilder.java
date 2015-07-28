package org.bibsonomy.model.logic.querybuilder;

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class AbstractSuggestionQueryBuilder<T extends AbstractSuggestionQueryBuilder<?>> {
	private final String query;
	private boolean withNonEntityPersons;
	private boolean withEntityPersons;
	private Set<PersonResourceRelationType> relationTypes = new HashSet<>();
	
	/**
	 * @param query any combination of title, author-name, year, school
	 */
	public AbstractSuggestionQueryBuilder(String query) {
		this.query = query;
	}
	
	protected abstract T getThis();
	
	public T withNonEntityPersons(boolean withNonEntityPersons) {
		this.withNonEntityPersons = withNonEntityPersons;
		return getThis();
	}
	
	public T withEntityPersons(boolean withEntityPersons) {
		this.withEntityPersons = withEntityPersons;
		return getThis();
	}
	
	public T withRelationType(PersonResourceRelationType... relationTypes) {
		for (PersonResourceRelationType relationType : relationTypes) {
			this.relationTypes.add(relationType);
		}
		return getThis();
	}

	public boolean isWithNonEntityPersons() {
		return this.withNonEntityPersons;
	}

	public boolean isWithEntityPersons() {
		return this.withEntityPersons;
	}

	public Set<PersonResourceRelationType> getRelationTypes() {
		return this.relationTypes;
	}

	public String getQuery() {
		return this.query;
	}
}
