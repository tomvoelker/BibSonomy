package org.bibsonomy.model.logic.querybuilder;

import java.util.List;

import org.bibsonomy.model.ResourcePersonRelation;


/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class PersonSuggestionQueryBuilder extends AbstractSuggestionQueryBuilder<PersonSuggestionQueryBuilder> {
	
	
	private boolean preferUnlinked;
	private boolean allowNamesWithoutEntities = true;

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

	/**
	 * @param b
	 * @return
	 */
	public PersonSuggestionQueryBuilder preferUnlinked(boolean preferUnlinked) {
		this.preferUnlinked = preferUnlinked;
		return this;
	}

	public boolean isPreferUnlinked() {
		return this.preferUnlinked;
	}

	/**
	 * @param allowNamesWithoutEntities - whether the query response may contain names of bibtex-authors/editors that are not associated to a person entity.
	 * @return this
	 */
	public AbstractSuggestionQueryBuilder<PersonSuggestionQueryBuilder> allowNamesWithoutEntities(boolean allowNamesWithoutEntities) {
		this.allowNamesWithoutEntities = allowNamesWithoutEntities;
		return this;
	}

	/**
	 * @return see {@link #allowNamesWithoutEntities(boolean)}
	 */
	public boolean isAllowNamesWithoutEntities() {
		return this.allowNamesWithoutEntities;
	}
}
