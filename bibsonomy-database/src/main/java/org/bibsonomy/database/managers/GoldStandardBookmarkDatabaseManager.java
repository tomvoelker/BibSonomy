/**
 * BibSonomy-Database - Database for BibSonomy.
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
