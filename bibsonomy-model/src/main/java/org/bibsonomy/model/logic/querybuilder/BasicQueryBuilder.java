package org.bibsonomy.model.logic.querybuilder;

import org.bibsonomy.model.logic.query.BasicQuery;

/**
 * abstract builder for {@link BasicQuery}
 *
 * @author dzo
 */
public abstract class BasicQueryBuilder<B extends BasicPaginatedQueryBuilder<B>> extends BasicPaginatedQueryBuilder<B> {
	protected String search;
	protected boolean usePrefixMatch = false;
	protected boolean phraseMatch = false;

	/**
	 * @param search the search to set
	 * @return the builder
	 */
	public B search(final String search) {
		this.search = search;
		return this.builder();
	}

	/**
	 * sets the prefix match for the search terms
	 * @param prefixMatch
	 * @return the group builder
	 */
	public B prefixMatch(final boolean prefixMatch) {
		this.usePrefixMatch = prefixMatch;
		return this.builder();
	}

	/**
	 * sets the phrase match for the search terms
	 * @param phraseMatch
	 * @return the group builder
	 */
	public B phraseMatch(final boolean phraseMatch) {
		this.phraseMatch = phraseMatch;
		return this.builder();
	}
}
