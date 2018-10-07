package org.bibsonomy.model.logic.query;

import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;

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

}
