package org.bibsonomy.search.index.generator;

import java.util.List;

/**
 * a general to many generation logic that uses two index generation logics
 *
 * @author dzo
 */
public class OneToManyIndexGenerationLogicAdapter<E, M> implements OneToManyIndexGenerationLogic<E, M> {

	private final IndexGenerationLogic<E> entityGenerationLogic;
	private final IndexGenerationLogic<M> toManyGenerationLogic;

	/**
	 * constructs a new generation logic for one to many relations that uses a
	 * @param entityGenerationLogic
	 * @param toManyGenerationLogic
	 */
	public OneToManyIndexGenerationLogicAdapter(IndexGenerationLogic<E> entityGenerationLogic, IndexGenerationLogic<M> toManyGenerationLogic) {
		this.entityGenerationLogic = entityGenerationLogic;
		this.toManyGenerationLogic = toManyGenerationLogic;
	}

	@Override
	public List<M> getToManyEntities(int lastContentId, int limit) {
		return this.toManyGenerationLogic.getEntites(lastContentId, limit);
	}

	@Override
	public int getNumberOfToManyEntities() {
		return this.toManyGenerationLogic.getNumberOfEntities();
	}

	@Override
	public int getNumberOfEntities() {
		return this.entityGenerationLogic.getNumberOfEntities();
	}

	@Override
	public List<E> getEntites(int lastContenId, int limit) {
		return this.entityGenerationLogic.getEntites(lastContenId, limit);
	}
}
