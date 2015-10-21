package org.bibsonomy.search.es.update;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.management.ElasticSearchIndex;
import org.bibsonomy.search.management.IndexLock;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchPublicationIndexUpdater;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * @param <P> 
 */
public class ElasticSearchPublicationindexUpdater<P extends BibTex> extends ElasticSearchIndexUpdater<P> implements SearchPublicationIndexUpdater<P> {
	private static final Log log = LogFactory.getLog(ElasticSearchPublicationindexUpdater.class);
	
	/**
	 * @param esClient
	 * @param indexLock
	 */
	public ElasticSearchPublicationindexUpdater(ESClient esClient, IndexLock<P, Map<String, Object>, ElasticSearchIndex<P>, String> indexLock) {
		super(esClient, indexLock);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.update.SearchPublicationIndexUpdater#updateIndexWithPersonInfo(org.bibsonomy.model.Person, org.apache.commons.collections.map.LRUMap)
	 */
	@Override
	public void updateIndexWithPersonInfo(Person person, LRUMap updatedInterhashes, SearchDBInterface<P> dbLogic) {
		updateIndexForPersonWithId(updatedInterhashes, person.getPersonId(), dbLogic);
	}
	
	private void updateIndexForPersonWithId(LRUMap updatedInterhashes, final String personId, final SearchDBInterface<P> dbLogic) {
		final ElasticSearchIndex<P> index = this.getIndex();
		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(index.getIndexName());
		searchRequestBuilder.setTypes(index.getContainer().getResourceTypeAsString());
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(QueryBuilders.termQuery(Fields.PERSON_ENTITY_IDS_FIELD_NAME, personId));

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				final Map<String, Object> doc = hit.getSource();
				final String interhash = (String) doc.get(Fields.Resource.INTERHASH);
				if (updatedInterhashes.put(interhash, interhash) == null) {
					final List<ResourcePersonRelation> newRels = dbLogic.getResourcePersonRelationsByPublication(interhash);
					this.updateIndexWithPersonRelation(interhash, newRels);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.update.SearchPublicationIndexUpdater#updateIndexWithPersonRelation(java.lang.String, java.util.List)
	 */
	@Override
	public void updateIndexWithPersonRelation(String interhash, List<ResourcePersonRelation> newRels) {
		final ElasticSearchIndex<P> index = this.getIndex();
		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(index.getIndexName());
		searchRequestBuilder.setTypes(index.getContainer().getResourceTypeAsString());
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		// FIXME: systemURL necessary? TODODZO
		searchRequestBuilder.setQuery(QueryBuilders.termQuery(Fields.Resource.INTERHASH, interhash));

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		int numUpdatedPosts = 0;
		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				final Map<String, Object> doc = hit.getSource();
				// FIXME: re add this.resourceConverter.setPersonFields(doc, newRels); TODODZO
				this.updatePostDocument(doc, hit.getId());
				numUpdatedPosts++;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("updating " + this.toString() + " with " + numUpdatedPosts + " posts having interhash = " + interhash);
		}
	}
	
	private void updatePostDocument(final Map<String, Object> jsonDocument, String indexIdStr) {
		final ElasticSearchIndex<P> index = this.getIndex();
		this.esClient.getClient().prepareUpdate(index.getIndexName(), index.getContainer().getResourceTypeAsString(), indexIdStr)
			.setDoc(jsonDocument)
			.setRefresh(true).execute().actionGet();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.update.SearchPublicationIndexUpdater#updateIndexWithPersonNameInfo(org.bibsonomy.model.PersonName, org.apache.commons.collections.map.LRUMap)
	 */
	@Override
	public void updateIndexWithPersonNameInfo(PersonName name, LRUMap updatedInterhashes, SearchDBInterface<P> dbLogic) {
		final String personId = name.getPersonId();
		updateIndexForPersonWithId(updatedInterhashes, personId, dbLogic);
	}

}
