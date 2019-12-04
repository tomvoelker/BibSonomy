/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.management.database;

import java.util.Date;
import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResourcePersonRelationLogStub;
import org.bibsonomy.model.User;
import org.bibsonomy.search.index.generator.post.PostIndexGenerationLogic;
import org.bibsonomy.search.management.database.manager.PersonSearchDatabaseManager;
import org.bibsonomy.search.management.database.params.SearchParam;

/**
 * class for accessing the main database 
 * 
 * @author fei
 * @param <R> the resource the logic handles
 */
public class SearchDBLogic<R extends Resource> extends PostIndexGenerationLogic<R> implements SearchDBInterface<R> {
	private final PersonSearchDatabaseManager personSearchDatabaseManager;

	/**
	 * default constructor
	 * @param personSearchDatabaseManager
	 */
	public SearchDBLogic(PersonSearchDatabaseManager personSearchDatabaseManager) {
		this.personSearchDatabaseManager = personSearchDatabaseManager;
	}

	@Override
	public List<User> getPredictionForTimeRange(final Date fromDate, Date toDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getPredictionForTimeRange", new Pair<>(fromDate, toDate), User.class, session);
		}
	}
	
	@Override
	public List<ResourcePersonRelation> getResourcePersonRelationsByPublication(String interHash) {
		try (final DBSession session = this.openSession()) {
			return this.personDatabaseManager.getResourcePersonRelationsByPublication(interHash, session);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getPostsForUser(java.lang.String, java.lang.String, org.bibsonomy.common.enums.HashID, int, java.util.List, int, int)
	 */
	@Override
	public List<Post<R>> getPostsForUser(final String userName, final int limit, final int offset) {
		final SearchParam param = new SearchParam();
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);

		try (final DBSession session = this.openSession()) {
			return this.queryForSearchPosts("get" + this.getResourceName() + "ForUser", param, session);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getContentIdsToDelete(java.util.Date)
	 */
	@Override
	public List<Integer> getContentIdsToDelete(final Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("get" + this.getResourceName() + "ContentIdsToDelete", lastLogDate, Integer.class, session);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getNewPosts(java.lang.Integer)
	 */
	@Override
	public List<Post<R>> getNewPosts(final int lastTasId, final int limit, final int offset) {
		final SearchParam param = new SearchParam();
		param.setLastTasId(Integer.valueOf(lastTasId));
		param.setLimit(limit);
		param.setOffset(offset);

		try (final DBSession session = this.openSession()) {
			return this.queryForSearchPosts("get" + this.getResourceName() + "PostsForTimeRange", param, session);
		}
	}

	@Override
	public List<ResourcePersonRelationLogStub> getPubPersonRelationsByChangeIdRange(long fromPersonChangeId, long toPersonChangeIdExclusive) {
		try (final DBSession session = this.openSession()) {
			return personSearchDatabaseManager.getPubPersonChangesByChangeIdRange(fromPersonChangeId, toPersonChangeIdExclusive, session);
		}
	}

	@Override
	public List<PersonName> getPersonMainNamesByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive) {
		try (final DBSession session = this.openSession()) {
			return personSearchDatabaseManager.getPersonMainNamesByChangeIdRange(firstChangeId, toPersonChangeIdExclusive, session);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getPostsForDocumentUpdate(java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Post<R>> getPostsForDocumentUpdate(Date lastDocumentDate, Date targetDocumentDate) {
		try (final DBSession session = this.openSession()) {
			return (List<Post<R>>) this.queryForList("getPostsForDocumentUpdate", new Pair<>(lastDocumentDate, targetDocumentDate), session);
		}
	}

	@Override
	public List<Person> getPersonByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive) {
		try (final DBSession session = this.openSession()) {
			return personSearchDatabaseManager.getPersonByChangeIdRange(firstChangeId, toPersonChangeIdExclusive, session);
		}
	}
}
