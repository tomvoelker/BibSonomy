/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.lucene.index.manager;

import java.util.Date;
import java.util.List;

import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.management.database.SearchDBLogic;
import org.bibsonomy.search.management.database.SearchGoldStandardLogic;
import org.bibsonomy.search.update.IndexUpdater;

/**
 * Updates the gold standard publication posts
 * uses the {@link LuceneFieldNames#LAST_TAS_ID} for the latest content id
 * (gold standard posts have no tags)
 * 
 * {@link SearchGoldStandardLogic} overrides {@link SearchDBLogic#getLastTasId()}
 * to query for the latest content id
 * 
 * @author dzo
 * @param <R> 
 */
public class LuceneGoldStandardManager<R extends Resource & GoldStandard<?>> extends LuceneResourceManager<GoldStandardPublication> {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.index.manager.LuceneResourceManager#updatePredictions(java.util.List, java.util.Date)
	 */
	@Override
	protected void updatePredictions(List<IndexUpdater<GoldStandardPublication>> updaters, Date lastLogDate) {
		// FIXME: maybe we must update a goldstandard that was updated by a spammer
		// nothing to do here, because goldstandard posts do not have an owner
	}
}
