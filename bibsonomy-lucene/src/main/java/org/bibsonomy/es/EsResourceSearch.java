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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.database.LuceneInfoLogic;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.converter.NormalizedEntryTypes;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.services.searcher.PersonSearch;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
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
public class EsResourceSearch<R extends Resource> extends ESQueryBuilder implements PersonSearch {

	private static final Pattern YEAR_PATTERN = Pattern.compile("[12][0-9]{3}");
	
	private String resourceType;

	/** post model converter */
	private LuceneResourceConverter<R> resourceConverter;

	/**
	 * logic interface for retrieving data from bibsonomy (friends, groups
	 * members)
	 */
	private LuceneInfoLogic dbLogic;
	/**
	 * 
	 */
	protected static final Log log = LogFactory.getLog(EsResourceSearch.class);

	private final int maxOffset = 2048;

	private ESIndexManager esIndexManager;

	/** url of this system */
	private String systemUrl;

	/** the number of person suggestions */
	private int personSuggestionSize = 8;

	
	/**
	 * get tag cloud for given search query for the Shared Resource System
	 * 
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param allowedGroups
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param bibtexkey 
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param negatedTags
	 * @param limit
	 * @param offset
	 * @return returns the list of tags for the tag cloud
	 */
	public List<Tag> getTags(final String userName, final String requestedUserName, final String requestedGroupName, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexkey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, final int limit, final int offset) {
		final BoolQueryBuilder query= this.buildQuery(userName, requestedUserName, requestedGroupName, null, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexkey, tagIndex, year, firstYear, lastYear, negatedTags);
		final Map<Tag, Integer> tagCounter = new HashMap<Tag, Integer>();

		try (final IndexLock indexLock = getEsIndexManager().aquireReadLockForTheActiveIndex(this.resourceType)) {
			

				SearchRequestBuilder searchRequestBuilder = getEsIndexManager().getClient().prepareSearch(indexLock.getIndexName());
				searchRequestBuilder.setTypes(resourceType);
				searchRequestBuilder.setSearchType(SearchType.DEFAULT);
				searchRequestBuilder.setQuery(query);
				searchRequestBuilder.addSort(LuceneFieldNames.DATE,
						SortOrder.DESC);
				searchRequestBuilder.setFrom(offset).setSize(limit)
						.setExplain(true);
				final SearchResponse response = searchRequestBuilder.execute()
						.actionGet();
				if (response != null) {
					SearchHits hits = response.getHits();
					log.info("Current Search results for '" + searchTerms
							+ "': " + response.getHits().getTotalHits());
					for (int i = 0; i < Math.min(limit, hits.getTotalHits()
							- offset); ++i) {
						SearchHit hit = hits.getAt(i);
						Map<String, Object> result = hit.getSource();
						final Post<R> post = this.resourceConverter
								.writePost(result);
						// set tag count
						if (present(post.getTags())) {
							for (final Tag tag : post.getTags()) {
								/*
								 * we remove the requested tags because we assume
								 * that related tags are requested
								 */
								if (present(tagIndex)
										&& tagIndex.contains(tag.getName())) {
									continue;
								}
								Integer oldCnt = tagCounter.get(tag);
								if (!present(oldCnt)) {
									oldCnt = 1;
								} else {
									oldCnt += 1;
								}
								tagCounter.put(tag, oldCnt);
							}
						}
					}
				}
			} catch (IndexMissingException e) {
				log.error("IndexMissingException: " + e);
			}
		
		
		
		final List<Tag> tags = new LinkedList<Tag>();
		// extract all tags
		for (final Map.Entry<Tag, Integer> entry : tagCounter.entrySet()) {
			final Tag tag = entry.getKey();
			tag.setUsercount(entry.getValue());
			tag.setGlobalcount(entry.getValue()); // FIXME: we set user==global count
			tags.add(tag);
		}
		log.debug("Done calculating tag statistics");
		
		// all done.
		return tags;
	}
	

	/**
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param requestedRelationNames
	 * @param allowedGroups
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param bibtexKey 
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param negatedTags
	 * @param order
	 * @param limit
	 * @param offset
	 * @return returns the list of posts
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public ResultList<Post<R>> getPosts(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, Order order, final int limit, final int offset) throws CorruptIndexException, IOException {
		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		try (final IndexLock indexLock = getEsIndexManager().aquireReadLockForTheActiveIndex(this.resourceType)) {
			
				final BoolQueryBuilder queryBuilder = this.buildQuery(userName,
						requestedUserName, requestedGroupName,
						requestedRelationNames, allowedGroups, searchTerms,
						titleSearchTerms, authorSearchTerms, bibtexKey,
						tagIndex, year, firstYear, lastYear, negatedTags);
				final SearchRequestBuilder searchRequestBuilder = this.esIndexManager.getClient().prepareSearch(indexLock.getIndexName());

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
	 * @return the resourceType
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(final String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @return the dbLogic
	 */
	public LuceneInfoLogic getDbLogic() {
		return this.dbLogic;
	}

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setDbLogic(LuceneInfoLogic dbLogic) {
		this.dbLogic = dbLogic;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.PersonSearch#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public List<ResourcePersonRelation> getPersonSuggestion(String queryString) {
		try (final IndexLock indexLock = getEsIndexManager().aquireReadLockForTheActiveIndex(this.resourceType)) {
			
			// we use inverted scores such that the best results automatically appear first according to the ascending order of a sorted map
			final TreeMap<Float, ResourcePersonRelation> relSorter = new TreeMap<>();
			
			final Set<String> tokenizedQueryString = new HashSet<>();
			for (String token : new SimpleTokenizer(queryString)) {
				if (!StringUtils.isBlank(token)) {
					tokenizedQueryString.add(token.toLowerCase());
				}
			} 
			
			// unfortunately our version of elasticsearch does not support topHits aggregation so we have to group by interhash ourselves: AggregationBuilder aggregation = AggregationBuilders.terms("agg").field("gender").subAggregation(AggregationBuilders.topHits("top"));
			
			double bestScore = Double.NaN;
			// remember alreadyAnalyzedInterhashes to skip over multiple posts of the same resource
			final Set<String> alreadyAnalyzedInterhashes = new HashSet<>();
			for (int offset = 0; relSorter.size() < personSuggestionSize && offset < maxOffset; offset += personSuggestionSize) {
				double minScore = Double.isNaN(bestScore) ? 0.05 : (bestScore / 3d);
				double bestScoreThisRound = fetchMoreResults(relSorter, queryString, tokenizedQueryString, offset, alreadyAnalyzedInterhashes, indexLock.getIndexName(), minScore);
				if (Double.isNaN(bestScore)) {
					bestScore = bestScoreThisRound;
				}
				if (Double.isNaN(bestScoreThisRound) || (bestScoreThisRound < minScore)) {
					break;
				}
			}
				
			return extractDistinctPersons(relSorter);
				
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		}
		return new ArrayList<>();
	}

	private double fetchMoreResults(final TreeMap<Float, ResourcePersonRelation> relSorter, String queryString, final Set<String> queryTerms, int offset, final Set<String> alreadyAnalyzedInterhashes, String indexName, double minPlainEsScore) {
		final QueryBuilder queryBuilder = QueryBuilders.filteredQuery( //
				QueryBuilders.boolQuery() //
						.should(QueryBuilders.multiMatchQuery(queryString) //
								.field(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME, 2) //
								.field(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME, 1) //
								.field(LuceneFieldNames.TITLE, 3) //
								.field(LuceneFieldNames.SCHOOL, 2) //
								.tieBreaker(0.8f) //
								.boost(2)) //
						.should(QueryBuilders.boolQuery() //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.habilitation.name()).boost(11)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.phdthesis.name()).boost(10)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.master_thesis.name()).boost(7)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.bachelor_thesis.name()).boost(6)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.candidate_thesis.name()).boost(5)) //
						), //
						addShouldYearIfYearInQuery(
								FilterBuilders.termFilter(ESConstants.SYSTEM_URL_FIELD_NAME, systemUrl),
								queryString) //
				);
		final SearchRequestBuilder searchRequestBuilder = this.esIndexManager.getClient().prepareSearch(indexName);
		searchRequestBuilder.setTypes(this.resourceType);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(queryBuilder) //
		.setMinScore((float)minPlainEsScore) //
		.setFrom(offset).setSize(personSuggestionSize * 5);

		final SearchResponse response = searchRequestBuilder.execute().actionGet();
		if (response == null) {
			return 0;
		}
		final SearchHits hits = response.getHits();
		if (hits.getTotalHits() < 1) {
			return 0;
		}
		
		double bestPlainEsScore = 0;
		double minScore = 0;

		for (final SearchHit hit : hits) {
			if (bestPlainEsScore < hit.getScore()) {
				bestPlainEsScore = hit.getScore();
				minScore = bestPlainEsScore / 3.0;
			} else if (hit.getScore() < minScore) {
				break;
			}
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
				int invertedScore = -1;
				for (String token : new SimpleTokenizer(mainName.getFirstName())) {
					if (!StringUtils.isBlank(token)) {
						token = token.toLowerCase();
						if (queryTerms.contains(token) == true) {
							invertedScore--;
						}
					}
					
				}
				for (String token : new SimpleTokenizer(mainName.getLastName())) {
					if (!StringUtils.isBlank(token)) {
						token = token.toLowerCase();
						if (queryTerms.contains(token) == true) {
							invertedScore--;
						}
					}
				}
				if (rpr.getRelationType() == PersonResourceRelationType.AUTHOR) {
					invertedScore *= 2;
				}
				invertedScoreToRpr.put(invertedScore, rpr);
			}
			
			final int minInvertedScore = extractMinimumInvertedScore(invertedScoreToRpr);
			int lastScore = -1;
			for (Map.Entry<Integer, ResourcePersonRelation> e : invertedScoreToRpr.entrySet()) {
				if (e.getKey() < lastScore / 2) {
					lastScore = e.getKey();
					relSorter.put(postScore * -((float) lastScore) / ((float) minInvertedScore), e.getValue());
				}
			}
		}

		return bestPlainEsScore;
	}

	/**
	 * @param termFilterBuilder
	 * @param queryString
	 * @return
	 */
	private FilterBuilder addShouldYearIfYearInQuery(FilterBuilder filterBuilder, String queryString) {
		Matcher m = YEAR_PATTERN.matcher(queryString);
		if (!m.find()) {
			return filterBuilder;
		}
		String year = queryString.substring(m.start(), m.end());
		return FilterBuilders.andFilter(filterBuilder, FilterBuilders.termFilter(LuceneFieldNames.YEAR, year));
	}


	private int extractMinimumInvertedScore(final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr) {
		int minInvertedScore = -1;
		for (Map.Entry<Integer, ResourcePersonRelation> e : invertedScoreToRpr.entrySet()) {
			if (minInvertedScore > e.getKey()) {
				minInvertedScore = e.getKey();
			}
		}
		return minInvertedScore;
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


	public ESIndexManager getEsIndexManager() {
		return this.esIndexManager;
	}


	public void setEsIndexManager(ESIndexManager esIndexManager) {
		this.esIndexManager = esIndexManager;
	}

}
