package org.bibsonomy.model.logic.querybuilder;

import java.util.List;

import org.bibsonomy.model.ResourcePersonRelation;


/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class PersonSuggestionQueryBuilder extends AbstractSuggestionQueryBuilder<PersonSuggestionQueryBuilder> {
	
	
	/**
	 * @param query any combination of title, author-name, year, school
	 */
	public PersonSuggestionQueryBuilder(String query) {
		super(query);
	}
	
	/**
	 * @return the result of the processed query
	 */
	public abstract List<ResourcePersonRelation> doIt();
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.querybuilder.AbstractSuggestionQueryBuilder#getThis()
	 */
	@Override
	protected PersonSuggestionQueryBuilder getThis() {
		return this;
	}
}
