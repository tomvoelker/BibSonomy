/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	 * constructs a new generation logic for one-to-many relations that uses a
	 * @param entityGenerationLogic
	 * @param toManyGenerationLogic
	 */
	public OneToManyIndexGenerationLogicAdapter(IndexGenerationLogic<E> entityGenerationLogic, IndexGenerationLogic<M> toManyGenerationLogic) {
		this.entityGenerationLogic = entityGenerationLogic;
		this.toManyGenerationLogic = toManyGenerationLogic;
	}

	@Override
	public List<M> getToManyEntities(int lastContentId, int limit) {
		return this.toManyGenerationLogic.getEntities(lastContentId, limit);
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
	public List<E> getEntities(int lastContenId, int limit) {
		return this.entityGenerationLogic.getEntities(lastContenId, limit);
	}
}
