package org.bibsonomy.model.logic.querybuilder;


/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class PublicationSuggestionQueryBuilder extends AbstractSuggestionQueryBuilder<PublicationSuggestionQueryBuilder> {
	
	/**
	 * @param query any combination of title, author-name, year, school
	 */
	public PublicationSuggestionQueryBuilder(String query) {
		super(query);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.querybuilder.AbstractSuggestionQueryBuilder#getThis()
	 */
	@Override
	protected PublicationSuggestionQueryBuilder getThis() {
		return this;
	}
	
}
