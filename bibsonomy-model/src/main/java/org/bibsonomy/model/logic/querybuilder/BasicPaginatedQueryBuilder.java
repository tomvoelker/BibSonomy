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
		if (start < 0) {
			throw new IllegalArgumentException(String.format("end must be >=0, was %d", start));
		}
		this.start = start;
		return this.builder();
	}

	/**
	 * @param end the end index of the list
	 * @return the builder
	 */
	public B end(final int end) {
		if (end < 0) {
			throw new IllegalArgumentException(String.format("end must be >=0, was %d", end));
		}
		this.end = end;
		return this.builder();
	}

	/**
	 * Retrieve only resources from [<code>start</code>; <code>end</code>).
	 *
	 * @param start index of the first item.
	 * @param end index of the last item.
	 *
	 * @return the builder.
	 */
	public B fromTo(int start, int end) {
		if (start > end) {
			throw new IllegalArgumentException(String.format("start must be <= end: %d > %d", start, end));
		}

		this.start(start);
		return this.end(end);
	}

	/**
	 * @param entries the number of entries to retrieve
	 * @param start the start index
	 * @return the builder
	 */
	public B entriesStartingAt(final int entries, final int start) {
		this.start(start);

		if (entries < 0) {
			throw new IllegalArgumentException(String.format("number of entries must be >= 0, was %d", entries));
		}

		return this.end(start + entries);
	}

	protected abstract B builder();
}
