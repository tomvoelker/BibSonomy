/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * TODO: implement chain
 * 
 * @author dzo
 */
public class GoldStandardBookmarkDatabaseManager extends GoldStandardDatabaseManager<Bookmark, GoldStandardBookmark, BookmarkParam> {

	private static final GoldStandardBookmarkDatabaseManager INSTANCE = new GoldStandardBookmarkDatabaseManager();

	/**
	 * @return the @{link:CommunityPostBookmarkDatabaseManager} instance
	 */
	@Deprecated // config via spring
	public static GoldStandardBookmarkDatabaseManager getInstance() {
		return INSTANCE;
	}

	private GoldStandardBookmarkDatabaseManager() {
		// noop
	}

	@Override
	protected void onGoldStandardRelationDelete(final String userName, final String interHash, final String interHashRef, final GoldStandardRelation interHashRelation, final DBSession session) {
		// TODO: implement reference model for bookmarks
	}

	@Override
	protected BookmarkParam createNewParam() {
		return new BookmarkParam();
	}
}
