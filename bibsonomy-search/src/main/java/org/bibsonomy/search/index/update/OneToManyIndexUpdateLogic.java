package org.bibsonomy.search.index.update;

/**
 * an index update logic for a index with a one to many relation
 *
 * @author dzo
 */
public final class OneToManyIndexUpdateLogic<E, M> {
	private final IndexUpdateLogic<E> indexUpdateLogic;
	private final IndexUpdateLogic<M> toManyIndexUpdateLogic;

	/**
	 * default constructor
	 *
	 * @param indexUpdateLogic
	 * @param toManyIndexUpdateLogic
	 */
	public OneToManyIndexUpdateLogic(IndexUpdateLogic<E> indexUpdateLogic, IndexUpdateLogic<M> toManyIndexUpdateLogic) {
		this.indexUpdateLogic = indexUpdateLogic;
		this.toManyIndexUpdateLogic = toManyIndexUpdateLogic;
	}

	/**
	 * @return the toManyIndexUpdateLogic
	 */
	public IndexUpdateLogic<M> getToManyIndexUpdateLogic() {
		return toManyIndexUpdateLogic;
	}

	/**
	 * @return the indexUpdateLogic
	 */
	public IndexUpdateLogic<E> getIndexUpdateLogic() {
		return indexUpdateLogic;
	}
}
