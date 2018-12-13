package org.bibsonomy.model.logic.query;

import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;

/**
 * adapter for {@link PersonSuggestionQueryBuilder}
 *
 * FIXME add real person query builder
 * @author dzo
 */
public class PersonQuery extends PersonSuggestionQueryBuilder {

	private String college;

	/**
	 * @param query any combination of title, author-name
	 */
	public PersonQuery(String query) {
		super(query);
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
}
