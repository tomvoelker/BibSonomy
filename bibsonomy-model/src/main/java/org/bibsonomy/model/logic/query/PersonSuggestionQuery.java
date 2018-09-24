package org.bibsonomy.model.logic.query;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;

import java.util.List;

/**
 * adapter for {@link PersonSuggestionQueryBuilder}
 * @author dzo
 */
public class PersonSuggestionQuery extends PersonSuggestionQueryBuilder {

	/**
	 * @param query any combination of title, author-name, year, school
	 */
	public PersonSuggestionQuery(String query) {
		super(query);
	}

	@Override
	public List<ResourcePersonRelation> doIt() {
		throw new UnsupportedOperationException();
	}
}
