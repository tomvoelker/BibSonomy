package org.bibsonomy.model.logic.query;

import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;

/**
 * adapter for {@link PersonSuggestionQueryBuilder}
 *
 * FIXME add real person query builder
 * @author dzo
 */
public class PersonQuery extends PersonSuggestionQueryBuilder {

	private final String college;

	/**
	 * @param query any combination of title, author-name
	 */
	public PersonQuery(String query) {
		this(query, null);
	}

	/**
	 * @param query
	 * @param college
	 */
	public PersonQuery(String query, String college) {
		super(query);
		this.college = college;
	}

	/**
	 * @return the college
	 */
	public String getCollege() {
		return college;
	}
}
