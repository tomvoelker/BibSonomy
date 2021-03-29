package org.bibsonomy.model.logic.querybuilder;

import org.bibsonomy.model.logic.query.BasicPaginatedQuery;

/**
 * abstract builder for {@link BasicPaginatedQuery}
 *
 * @author dzo
 */
public abstract class BasicPaginatedQueryBuilder<B extends BasicPaginatedQueryBuilder<B>> {
	protected int start = 0;
	protected int end = 10;

	/**
	 * @param start the start index of the list
	 * @return the builder
	 */
	public B start(final int start) {
		this.start = start;
		return this.builder();
	}

	/**
	 * @param end the end index of the list
	 * @return the builder
	 */
	public B end(final int end) {
		this.end = end;
		return this.builder();
	}

	/**
	 * @param entries the number of entries to retrieve
	 * @param start the start index
	 * @return the builder
	 */
	public B entriesStartingAt(final int entries, final int start) {
		this.start = start;

		return this.end(start + entries);
	}

	protected abstract B builder();
}
