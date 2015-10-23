package org.bibsonomy.search.es.management;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResourcePersonRelationLogStub;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.index.PublicationConverter;
import org.bibsonomy.search.es.index.ResourceConverter;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.util.ValidationUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * manager that also updates person informations
 *
 * @author dzo
 * @author jensi
 * @param <P> 
 */
public class ElasticSearchPublicationManager<P extends BibTex> extends ElasticSearchManager<P> {
	private static final Log log = LogFactory.getLog(ElasticSearchPublicationManager.class);
	
	private static final int UPDATED_INTERHASHES_CACHE_SIZE = 25000;
	
	/**
	 * @param client
	 * @param systemURI
	 * @param inputLogic
	 * @param tools
	 */
	public ElasticSearchPublicationManager(ESClient client, URI systemURI, SearchDBInterface<P> inputLogic, ElasticSearchIndexTools<P> tools) {
		super(client, systemURI, inputLogic, tools);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.management.ElasticSearchManager#updateResourceSpecificProperties(java.lang.String, org.bibsonomy.search.update.SearchIndexState, org.bibsonomy.search.update.SearchIndexState)
	 */
	@Override
	protected void updateResourceSpecificProperties(String indexName, SearchIndexSyncState oldState, SearchIndexSyncState targetState) {
		final LRUMap updatedInterhashes = new LRUMap(UPDATED_INTERHASHES_CACHE_SIZE);
		applyChangesInPubPersonRelationsToIndex(indexName, oldState, targetState, updatedInterhashes);
		applyPersonChangesToIndex(indexName, oldState, targetState, updatedInterhashes);
	}
	
	/**
	 * @param indexName 
	 * @param targetState
	 * @param updatedInterhashes
	 * @param indexUpdaters
	 */
	private void applyPersonChangesToIndex(String indexName, SearchIndexSyncState oldState, SearchIndexSyncState targetState, LRUMap updatedInterhashes) {
		for (long minPersonChangeId = oldState.getLastPersonChangeId() + 1; minPersonChangeId < targetState.getLastPersonChangeId(); minPersonChangeId = Math.min(targetState.getLastPersonChangeId(), minPersonChangeId + SQL_BLOCKSIZE)) {
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
		final SearchRequestBuilder searchRequestBuilder = this.prepareSearch(indexName);
		searchRequestBuilder.setTypes(this.tools.getResourceTypeAsString());
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(QueryBuilders.termQuery(Fields.PERSON_ENTITY_IDS_FIELD_NAME, personId));

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				final Map<String, Object> doc = hit.getSource();
				final String interhash = (String) doc.get(Fields.Resource.INTERHASH);
				if (updatedInterhashes.put(interhash, interhash) == null) {
					final List<ResourcePersonRelation> newRels = this.inputLogic.getResourcePersonRelationsByPublication(interhash);
					this.updateIndexWithPersonRelation(indexName, interhash, newRels);
				}
			}
		}
	}
	
	private void updateIndexWithPersonRelation(String indexName, String interhash, List<ResourcePersonRelation> newRels) {
		final SearchRequestBuilder searchRequestBuilder = this.prepareSearch(indexName);
		searchRequestBuilder.setTypes(this.tools.getResourceTypeAsString());
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(QueryBuilders.termQuery(Fields.Resource.INTERHASH, interhash));

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		int numUpdatedPosts = 0;
		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				final Map<String, Object> doc = hit.getSource();
				final ResourceConverter<P> converter = this.tools.getConverter();
				if (converter instanceof PublicationConverter) {
					final PublicationConverter publicationConverter = (PublicationConverter) converter;
					publicationConverter.updateDocumentWithPersonRelation(doc, newRels);
					this.updatePostDocument(indexName, doc, hit.getId());
					numUpdatedPosts++;
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("updating " + this.toString() + " with " + numUpdatedPosts + " posts having interhash = " + interhash);
		}
	}
	
	private void updatePostDocument(final String indexName, final Map<String, Object> jsonDocument, String indexIdStr) {
		this.client.getClient().prepareUpdate(indexName, this.tools.getResourceTypeAsString(), indexIdStr)
			.setDoc(jsonDocument)
			.setRefresh(true).execute().actionGet();
	}

	private void applyChangesInPubPersonRelationsToIndex(final String indexName, SearchIndexSyncState oldState, SearchIndexSyncState targetState, final LRUMap updatedInterhashes) {
		for (long minPersonChangeId = oldState.getLastPersonChangeId() + 1; minPersonChangeId < targetState.getLastPersonChangeId(); minPersonChangeId += SQL_BLOCKSIZE) {
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
