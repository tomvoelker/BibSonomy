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
package org.bibsonomy.search.index.generator.post;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * generation logic for posts
 *
 * @param <R>
 */
public class PostIndexGenerationLogic<R extends Resource> extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<Post<R>> {
	protected Class<R> resourceClass;
	private GeneralDatabaseManager generalDatabaseManager;
	protected PersonDatabaseManager personDatabaseManager;

	@Override
	public int getNumberOfEntities() {
		try (final DBSession session = this.openSession()) {
			return saveConvertToint(this.queryForObject("get" + this.getResourceName() + "Count", Integer.class, session));
		}
	}

	@Override
	public List<Post<R>> getEntites(final int lastContentId, final int max) {
		final SearchParam param = new SearchParam();
		param.setLastContentId(lastContentId);
		param.setLimit(max);

		final DBSession session = this.openSession();
		try {
			return this.queryForSearchPosts("get" + this.getResourceName() + "ForIndex", param, session);
		} finally {
			session.close();
		}
	}

	// FIXME: duplicate code see SearchDBInterface
	@SuppressWarnings("unchecked")
	protected List<Post<R>> queryForSearchPosts(final String query, final Object param, final DBSession session) {
		final List<Post<R>> posts = (List<Post<R>>) this.queryForList(query, param, session);
		// FIXME: remove ugly instance of check!
		if (BibTex.class.isAssignableFrom(this.resourceClass)) {
			setPersonRelations(posts, session);
		}
		return posts;
	}

	// FIXME: document why we have to query the database each time, and do not
	// query this information with a join
	protected void setPersonRelations(final List<Post<R>> posts, final DBSession session) {
		final Map<String, List<ResourcePersonRelation>> relationCache = new HashMap<>();
		for (Post<R> post : posts) {
			final String interHash = post.getResource().getInterHash();
			List<ResourcePersonRelation> rels = relationCache.get(interHash);
			if (rels == null) {
				rels = this.personDatabaseManager.getResourcePersonRelationsByPublication(interHash, session);
				if (rels == null) {
					rels = Collections.emptyList();
				}
				relationCache.put(interHash, rels);
			}
			post.setResourcePersonRelations(rels);
		}
	}

	@Override
	public SearchIndexSyncState getDbState() {
		final SearchIndexSyncState newState = new SearchIndexSyncState();
		newState.setLast_tas_id(this.getLastTasId());
		newState.setLast_log_date(this.getLastLogDate());
		newState.setLastPersonChangeId(this.getLastPersonChangeId());
		newState.setLastDocumentDate(this.getLastDocumentDate());
		newState.setLastPredictionChangeDate(this.getLastPreditionDate());
		return newState;
	}

	/**
	 * @return
	 */
	private Date getLastPreditionDate() {
		final DBSession session = this.openSession();
		try {
			final Date date = this.queryForObject("getLastPredictionDate", Date.class, session);
			if (date == null) {
				return new Date();
			}
			return date;
		} finally {
			session.close();
		}
	}

	/**
	 * @return
	 */
	private Date getLastDocumentDate() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLastDocumentDate", Date.class, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @return the last tas id
	 */
	protected Integer getLastTasId() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLastTasId", Integer.class, session);
		} finally {
			session.close();
		}
	}

	private Date getLastLogDate() {
		final DBSession session = this.openSession();
		try {
			final Date rVal = this.queryForObject("getLastLog" + this.getResourceName(), Date.class, session);
			if (rVal != null) {
				return rVal;
			}
			return new Date();
		} finally {
			session.close();
		}
	}

	private long getLastPersonChangeId() {
		final DBSession session = this.openSession();
		try {
			return this.generalDatabaseManager.getLastId(ConstantID.PERSON_CHANGE_ID, session).longValue();
		} finally {
			session.close();
		}
	}

	/**
	 * @return the resourceName
	 */
	protected String getResourceName() {
		return this.resourceClass.getSimpleName();
	}

	/**
	 * @param generalDatabaseManager the generalDatabaseManager to set
	 */
	public void setGeneralDatabaseManager(GeneralDatabaseManager generalDatabaseManager) {
		this.generalDatabaseManager = generalDatabaseManager;
	}

	/**
	 * @param personDatabaseManager the personDatabaseManager to set
	 */
	public void setPersonDatabaseManager(PersonDatabaseManager personDatabaseManager) {
		this.personDatabaseManager = personDatabaseManager;
	}

	/**
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}
}
