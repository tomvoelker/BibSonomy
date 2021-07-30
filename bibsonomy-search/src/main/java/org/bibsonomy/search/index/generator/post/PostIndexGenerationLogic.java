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
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

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
public class PostIndexGenerationLogic<R extends Resource> extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<Post<R>>, DatabaseInformationLogic<DefaultSearchIndexSyncState> {
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
	public List<Post<R>> getEntities(final int lastContentId, final int max) {
		final SearchParam param = new SearchParam();
		param.setLastContentId(lastContentId);
		param.setLimit(max);

		try (final DBSession session = this.openSession()) {
			return this.queryForSearchPosts("get" + this.getResourceName() + "ForIndex", param, session);
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
	public DefaultSearchIndexSyncState getDbState() {
		final DefaultSearchIndexSyncState newState = new DefaultSearchIndexSyncState();
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
		try (final DBSession session = this.openSession()) {
			final Date date = this.queryForObject("getLastPredictionDate", Date.class, session);
			if (date == null) {
				return new Date();
			}
			return date;
		}
	}

	/**
	 * @return
	 */
	private Date getLastDocumentDate() {
		try (final DBSession session = this.openSession()) {
			return this.queryForObject("getLastDocumentDate", Date.class, session);
		}
	}

	/**
	 * @return the last tas id
	 */
	protected Integer getLastTasId() {
		try (final DBSession session = this.openSession()) {
			return this.queryForObject("getLastTasId", Integer.class, session);
		}
	}

	private Date getLastLogDate() {
		try (final DBSession session = this.openSession()) {
			final Date rVal = this.queryForObject("getLastLog" + this.getResourceName(), Date.class, session);
			if (rVal != null) {
				return rVal;
			}
			return new Date();
		}
	}

	private long getLastPersonChangeId() {
		try (final DBSession session = this.openSession()) {
			return this.generalDatabaseManager.getLastId(ConstantID.PERSON_CHANGE_ID, session).longValue();
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
