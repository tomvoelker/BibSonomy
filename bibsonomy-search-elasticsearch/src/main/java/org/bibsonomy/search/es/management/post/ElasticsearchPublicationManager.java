/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.management.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResourcePersonRelationLogStub;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.ESConstants.Fields.Publication;
import org.bibsonomy.search.es.index.converter.post.PublicationConverter;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.util.ValidationUtils;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

/**
 * Elasticsearch manager for publication indices.
 * This is a special implementation of {@link ElasticsearchPostManager} to update publication specific fields.
 *
 * @author dzo
 * @author jensi
 * @param <P> 
 */
public class ElasticsearchPublicationManager<P extends BibTex> extends ElasticsearchPostManager<P> {
	private static final Log log = LogFactory.getLog(ElasticsearchPublicationManager.class);
	
	private static final int UPDATED_INTERHASHES_CACHE_SIZE = 25000;

	/**
	 * Default constructor
	 *
	 * @param systemURI
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param indexEnabled
	 * @param updateEnabled
	 * @param regenerateEnabled
	 * @param inputLogic
	 */
	public ElasticsearchPublicationManager(URI systemURI,
										   ESClient client,
										   ElasticsearchIndexGenerator<Post<P>, SearchIndexState> generator,
										   Converter syncStateConverter,
										   EntityInformationProvider entityInformationProvider,
										   boolean indexEnabled,
										   boolean updateEnabled,
										   boolean regenerateEnabled,
										   SearchDBInterface<P> inputLogic) {
		super(systemURI, client, generator, syncStateConverter, entityInformationProvider, indexEnabled, updateEnabled, regenerateEnabled, inputLogic);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.management.ElasticSearchManager#updateResourceSpecificProperties(java.lang.String, org.bibsonomy.search.update.SearchIndexState, org.bibsonomy.search.update.SearchIndexState)
	 */
	@Override
	protected void updateResourceSpecificProperties(String indexName, SearchIndexState oldState, SearchIndexState targetState) {
		// TODO: limit offset TODODZO
		Date lastDocDate = oldState.getDocumentLogDate();
		if (!present(lastDocDate)) {
			lastDocDate = targetState.getDocumentLogDate();
		}
		final List<Post<P>> postsForDocUpdate = this.inputLogic.getPostsForDocumentUpdate(lastDocDate, targetState.getDocumentLogDate());
		
		// TODO: bulk update
		for (final Post<P> postDocUpdate : postsForDocUpdate) {
			final List<Map<String, String>> documents = this.getPublicationConverter().convertDocuments(postDocUpdate.getResource().getDocuments());
			final String id = this.entityInformationProvider.getEntityId(postDocUpdate);
			try {
				this.client.updateDocument(indexName, this.entityInformationProvider.getType(), id, Collections.singletonMap(Publication.DOCUMENTS, documents));
			} catch (final DocumentMissingException e) {
				log.error("could not update post with " + id, e);
			}
		}

		/*
		final LRUMap updatedInterhashes = new LRUMap(UPDATED_INTERHASHES_CACHE_SIZE);
		applyChangesInPubPersonRelationsToIndex(indexName, oldState, targetState, updatedInterhashes);
		applyPersonChangesToIndex(indexName, oldState, targetState, updatedInterhashes);
		*/
	}
	
	/**
	 * @param indexName
	 * @param oldState
	 * @param targetState
	 * @param updatedInterhashes
	 */
	private void applyPersonChangesToIndex(String indexName, SearchIndexState oldState, SearchIndexState targetState, LRUMap updatedInterhashes) {
		for (long minPersonChangeId = oldState.getPersonId() + 1; minPersonChangeId < targetState.getPersonId(); minPersonChangeId = Math.min(targetState.getPersonId(), minPersonChangeId + SQL_BLOCKSIZE)) {
			final List<PersonName> personMainNameChanges = this.inputLogic.getPersonMainNamesByChangeIdRange(minPersonChangeId, minPersonChangeId + SQL_BLOCKSIZE);
			for (PersonName name : personMainNameChanges) {
				final String personId = name.getPersonId();
				updateIndexForPersonWithId(indexName, updatedInterhashes, personId);
			}
			personMainNameChanges.clear();
			final List<Person> personChanges = this.inputLogic.getPersonByChangeIdRange(minPersonChangeId, minPersonChangeId + SQL_BLOCKSIZE);
			for (final Person person : personChanges) {
				updateIndexForPersonWithId(indexName, updatedInterhashes, person.getPersonId());
			}
			personChanges.clear();
		}
	}

	private void updateIndexForPersonWithId(String indexName, LRUMap updatedInterhashes, final String personId) {
		final TermQueryBuilder query = QueryBuilders.termQuery(Fields.PERSON_ENTITY_IDS_FIELD_NAME, personId);
		final SearchHits hits = this.search(query, null, 0, 1000, null, null);
		if (hits != null) {
			for (final SearchHit hit : hits.getHits()) {
				final Map<String, Object> doc = hit.getSourceAsMap();
				final String interhash = (String) doc.get(Fields.Resource.INTERHASH);
				if (updatedInterhashes.put(interhash, interhash) == null) {
					final List<ResourcePersonRelation> newRels = this.inputLogic.getResourcePersonRelationsByPublication(interhash);
					this.updateIndexWithPersonRelation(indexName, interhash, newRels);
				}
			}
		}
	}
	
	private void updateIndexWithPersonRelation(String indexName, String interhash, List<ResourcePersonRelation> newRels) {
		final TermQueryBuilder query = QueryBuilders.termQuery(Fields.Resource.INTERHASH, interhash);
		final SearchHits hits = this.search(query, null, 0, 1000, null, null);
		int numUpdatedPosts = 0;
		if (hits != null) {
			for (final SearchHit hit : hits.getHits()) {
				final Map<String, Object> doc = hit.getSourceAsMap();
				final PublicationConverter publicationConverter = getPublicationConverter();
				publicationConverter.updateDocumentWithPersonRelation(doc, newRels);
				this.updatePostDocument(indexName, doc, hit.getId());
				numUpdatedPosts++;
			}
		}

		log.debug("updating " + this.toString() + " with " + numUpdatedPosts + " posts having interhash = " + interhash);
	}

	/**
	 * FIXME: this cast is not nice
	 * @return
	 */
	private PublicationConverter getPublicationConverter() {
		final Object converter = this.entityInformationProvider.getConverter();
		return (PublicationConverter) converter;
	}
	
	private void updatePostDocument(final String indexName, final Map<String, Object> jsonDocument, final String indexIdStr) {
		try {
			this.client.updateDocument(indexName, this.entityInformationProvider.getType(), indexIdStr, jsonDocument);
		} catch (final DocumentMissingException e) {
			log.error("could not update documents of post " + indexIdStr);
		}
	}

	private void applyChangesInPubPersonRelationsToIndex(final String indexName, SearchIndexState oldState, SearchIndexState targetState, final LRUMap updatedInterhashes) {
		for (long minPersonChangeId = oldState.getPersonId(); minPersonChangeId < targetState.getPersonId(); minPersonChangeId += SQL_BLOCKSIZE) {
			final List<ResourcePersonRelationLogStub> relChanges = this.inputLogic.getPubPersonRelationsByChangeIdRange(minPersonChangeId, minPersonChangeId + SQL_BLOCKSIZE);
			if (log.isDebugEnabled() || ValidationUtils.present(relChanges)) {
				log.info("found " + relChanges.size() + " relation changes to update");
			}
			for (ResourcePersonRelationLogStub rel : relChanges) {
				final String interhash = rel.getPostInterhash();
				if (updatedInterhashes.put(interhash, interhash) == null) {
					List<ResourcePersonRelation> newRels = this.inputLogic.getResourcePersonRelationsByPublication(interhash);
					this.updateIndexWithPersonRelation(indexName, interhash, newRels);
				}
			}
		}
	}
}
