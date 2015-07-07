/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.converter.NormalizedEntryTypes;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.services.searcher.PersonSearch;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

/**
 * This class performs a search in the Shared Resource Indices based on the
 * search term
 * 
 * @author lutful
 * @param <R>
 */
public class EsResourceSearch<R extends Resource> implements PersonSearch {

	private String resourceType;

	/** post model converter */
	private LuceneResourceConverter<R> resourceConverter;

	/**
	 * 
	 */
	protected static final Log log = LogFactory.getLog(EsResourceSearch.class);

	private final int maxOffset = 2048;

	private ESClient esClient;

	/** url of this system */
	private String systemUrl;

	/** the number of person suggestions */
	private int personSuggestionSize = 8;

	private String indexName = ESConstants.INDEX_NAME;

	/**
	 * @param esClient the esClient to set
	 */
	public void setEsClient(final ESClient esClient) {
		this.esClient = esClient;
	}

	/**
	 * @return the ElasticSearch Client
	 */
	public ESClient getEsClient() {
		return this.esClient;
	}

	/**
	 * @param searchTerms
	 * @param order
	 * @param offset
	 * @param limit
	 * @return postList
	 * @throws IOException
	 * @throws CorruptIndexException
	 * 
	 */
	public ResultList<Post<R>> fullTextSearch(final String searchTerms, final Order order, final int limit, final int offset) throws CorruptIndexException, IOException {

		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		try {
			final QueryBuilder queryBuilder = QueryBuilders.queryString(searchTerms);
			final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(indexName);
			searchRequestBuilder.setTypes(this.resourceType);
			searchRequestBuilder.setSearchType(SearchType.DEFAULT);
			searchRequestBuilder.setQuery(queryBuilder);
			if (order != Order.RANK) {
				searchRequestBuilder.addSort(LuceneFieldNames.DATE, SortOrder.DESC);
			}
			searchRequestBuilder.setFrom(offset).setSize(limit).setExplain(true);

			final SearchResponse response = searchRequestBuilder.execute().actionGet();

			if (response != null) {
				final SearchHits hits = response.getHits();
				postList.setTotalCount((int) hits.getTotalHits());

				log.info("Current Search results for '" + searchTerms + "': " + response.getHits().getTotalHits());
				for (final SearchHit hit : hits) {
					postList.add(this.resourceConverter.writePost(hit.getSource()));
				}
			}
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		}

		return postList;
	}

	/**
	 * @return the resourceConverter
	 */
	public LuceneResourceConverter<R> getResourceConverter() {
		return this.resourceConverter;
	}

	/**
	 * @param resourceConverter the resourceConverter to set
	 */
	public void setResourceConverter(final LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @return the INDEX_TYPE
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param resourceType
	 */
	public void setResourceType(final String resourceType) {
		this.resourceType = resourceType;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.PersonSearch#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public List<ResourcePersonRelation> getPersonSuggestion(String queryString) {
		try {
			
			// we use inverted scores such that the best results automatically appear first according to the ascending order of a sorted map
			final TreeMap<Float, ResourcePersonRelation> relSorter = new TreeMap<>();
			
			final Set<String> tokenizedQueryString = new HashSet<>();
			for (String token : new SimpleTokenizer(queryString)) {
				tokenizedQueryString.add(token);
			} 
			
			// unfortunately our version of elasticsearch does not support topHits aggregation so we have to group by interhash ourselves: AggregationBuilder aggregation = AggregationBuilders.terms("agg").field("gender").subAggregation(AggregationBuilders.topHits("top"));
			
			// remember alreadyAnalyzedInterhashes to skip over multiple posts of the same resource
			final Set<String> alreadyAnalyzedInterhashes = new HashSet<>();
			for (int offset = 0; relSorter.size() < personSuggestionSize && offset < maxOffset; offset += personSuggestionSize) {
				boolean moreEntriesMightBeFound = fetchMoreResults(relSorter, queryString, tokenizedQueryString, offset, alreadyAnalyzedInterhashes);
				if (moreEntriesMightBeFound == false) {
					break;
				}
			}
				
			return extractDistinctPersons(relSorter);
				
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		}
		return new ArrayList<>();
	}

	private boolean fetchMoreResults(final TreeMap<Float, ResourcePersonRelation> relSorter, String queryString, final Set<String> queryTerms, int offset, final Set<String> alreadyAnalyzedInterhashes) {
		final QueryBuilder queryBuilder = QueryBuilders.filteredQuery( //
				QueryBuilders.boolQuery() //
						.should(QueryBuilders.multiMatchQuery(queryString) //
								.field(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME, 2) //
								.field(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME, 2) //
								.field(LuceneFieldNames.TITLE, 2) //
								.field(LuceneFieldNames.YEAR, 2) //
								.field(LuceneFieldNames.SCHOOL, 2) //
								.tieBreaker(0.2f) //
								.boost(2)) //
						.should(QueryBuilders.boolQuery() //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.habilitation.name()).boost(11)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.phdthesis.name()).boost(10)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.master_thesis.name()).boost(7)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.bachelor_thesis.name()).boost(6)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.candidate_thesis.name()).boost(5)) //
						), //
				FilterBuilders.termFilter(ESConstants.SYSTEM_URL_FIELD_NAME, systemUrl) //
				);
		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(indexName);
		searchRequestBuilder.setTypes(this.resourceType);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(queryBuilder).setFrom(offset).setSize(personSuggestionSize);

		final SearchResponse response = searchRequestBuilder.execute().actionGet();
		if (response == null) {
			return false;
		}
		final SearchHits hits = response.getHits();
		if (hits.getTotalHits() < 1) {
			return false;
		}

		for (final SearchHit hit : hits) {
			String interhash = (String) hit.getSource().get(LuceneFieldNames.INTERHASH);
			if (!alreadyAnalyzedInterhashes.add(interhash)) {
				// we have seen this interhash before -> skip
				continue;
			}
			final Post<R> post = this.resourceConverter.writePost(hit.getSource());
			final float postScore = hit.getScore();
			//
			final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr = new TreeMap<>();

			for (ResourcePersonRelation rpr : post.getResourcePersonRelations()) {
				PersonName mainName = rpr.getPerson().getMainName();
				int invertedScore = 0;
				for (String token : new SimpleTokenizer(mainName.getFirstName())) {
					if (queryTerms.contains(token) == true) {
						invertedScore--;
					}
				}
				for (String token : new SimpleTokenizer(mainName.getLastName())) {
					if (queryTerms.contains(token) == true) {
						invertedScore--;
					}
				}
				if (rpr.getRelationType() == PersonResourceRelationType.AUTHOR) {
					invertedScore *= 2;
				}
				invertedScoreToRpr.put(invertedScore, rpr);
			}
			int lastScore = 1;
			int minInvertedScore = 1;
			for (Map.Entry<Integer, ResourcePersonRelation> e : invertedScoreToRpr.entrySet()) {
				if (minInvertedScore == 1) {
					minInvertedScore = e.getKey();
				}
				if (e.getKey() < lastScore / 2) {
					lastScore = e.getKey();
					relSorter.put(-1f * postScore * ((float) lastScore) / ((float) minInvertedScore), e.getValue());
				}
			}
		}

		return true;
	}

	private List<ResourcePersonRelation> extractDistinctPersons(final TreeMap<Float, ResourcePersonRelation> rValSorter) {
		List<ResourcePersonRelation> rVal = new ArrayList<>();
		Set<String> foundPersonIds = new HashSet<>();
		for (ResourcePersonRelation rpr : rValSorter.values()) {
			if (foundPersonIds.add(rpr.getPerson().getPersonId()) == true) {
				// we have not seen this personId earlier in the sorted map, so add it to the response list
				rVal.add(rpr);
				if (rVal.size() == personSuggestionSize) {
					return rVal;
				}
			}
		}
		return rVal;
	}

	public void setSystemUrl(String systemUrl) {
		this.systemUrl = systemUrl;
	}

	public void setPersonSuggestionSize(int personSuggestionSize) {
		this.personSuggestionSize = personSuggestionSize;
	}

	public String getIndexName() {
		return this.indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

}
