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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * Used to create, read, update and delete gold standard publications from the
 * database.
 * 
 * @author dzo
 */
public final class GoldStandardPublicationDatabaseManager extends GoldStandardDatabaseManager<BibTex, GoldStandardPublication, BibTexParam> {
	private static final GoldStandardPublicationDatabaseManager INSTANCE = new GoldStandardPublicationDatabaseManager();

	/**
	 * @return the @{link:GoldStandardPublicationDatabaseManager} instance
	 */
	public static GoldStandardPublicationDatabaseManager getInstance() {
		return INSTANCE;
	}

	private GoldStandardPublicationDatabaseManager() {
	}

	@Override
	public Post<GoldStandardPublication> getPostDetails(final String loginUserName, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		final Post<GoldStandardPublication> post = super.getPostDetails(loginUserName, resourceHash, userName, visibleGroupIDs, session);

		if (present(post)) {
			/*
			 * before the resource leaves the logic parse the misc field
			 */
			try {
				post.getResource().parseMiscField();
			} catch (final InvalidModelException e) {
				// ignore invalid misc data
			}
		}

		return post;
	}

	@Override
	protected void onGoldStandardRelationDelete(final String userName, final String interHash, final String interHashRef, final GoldStandardRelation interHashRelation, final DBSession session) {
		this.plugins.onGoldStandardRelationDelete(userName, interHash, interHashRef, interHashRelation, session);
	}

	@Override
	protected BibTexParam createNewParam() {
		return new BibTexParam();
	}

}