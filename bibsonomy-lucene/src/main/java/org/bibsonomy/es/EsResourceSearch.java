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
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.querybuilder.AbstractSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PublicationSuggestionQueryBuilder;
import org.bibsonomy.services.searcher.PersonSearch;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
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
public class EsResourceSearch<R extends Resource> implements PersonSearch, ResourceSearch<R> {

	/**
	 * 
	 */
	private static final float GENEALOGY_USER_PREFERENCE_FACTOR = 1.1f;

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
	private int suggestionSize = 8;

	private String genealogyUser;

	
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
		final QueryBuilder query= this.buildQuery(userName, requestedUserName, requestedGroupName, null, allowedGroups, org.bibsonomy.common.enums.SearchType.LOCAL, null, titleSearchTerms, authorSearchTerms, bibtexkey, tagIndex, year, firstYear, lastYear, negatedTags);
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
	@Override
	public ResultList<Post<R>> getPosts(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final org.bibsonomy.common.enums.SearchType searchType, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, Order order, final int limit, final int offset) {
		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		try (final IndexLock indexLock = getEsIndexManager().aquireReadLockForTheActiveIndex(this.resourceType)) {
			
				final QueryBuilder queryBuilder = this.buildQuery(userName,
						requestedUserName, requestedGroupName,
						requestedRelationNames, allowedGroups, searchType, searchTerms,
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
	public List<Post<BibTex>> getPublicationSuggestions(PublicationSuggestionQueryBuilder options) {
		try (final IndexLock indexLock = getEsIndexManager().aquireReadLockForTheActiveIndex(this.resourceType)) {
			// we use inverted scores such that the best results automatically appear first according to the ascending order of a sorted map
			final TreeMap<Float, ResourcePersonRelation> relSorter = iterativelyFetchSuggestions(indexLock, null, options);
				
			return extractResources(relSorter);
				
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		}
		return new ArrayList<>();
	}


	private TreeMap<Float, ResourcePersonRelation> iterativelyFetchSuggestions(final IndexLock indexLock, Set<String> tokenizedQueryString, AbstractSuggestionQueryBuilder<?> options) {
		// we use inverted scores such that the best results automatically appear first according to the ascending order of a sorted map
		final TreeMap<Float, ResourcePersonRelation> relSorter = new TreeMap<>();
		
		// unfortunately our version of elasticsearch does not support topHits aggregation so we have to group by interhash ourselves: AggregationBuilder aggregation = AggregationBuilders.terms("agg").field("gender").subAggregation(AggregationBuilders.topHits("top"));
		double bestScore = Double.NaN;
		// remember alreadyAnalyzedInterhashes to skip over multiple posts of the same resource
		final Set<String> alreadyAnalyzedInterhashes = new HashSet<>();
		for (int offset = 0; relSorter.size() < suggestionSize && offset < maxOffset; offset += suggestionSize) {
			double minScore = Double.isNaN(bestScore) ? 0.05 : (bestScore / 3d);

			double bestScoreThisRound = fetchMoreResults(relSorter, tokenizedQueryString, offset, alreadyAnalyzedInterhashes, indexLock.getIndexName(), minScore, options);
			if (Double.isNaN(bestScore)) {
				bestScore = bestScoreThisRound;
			}
			if (Double.isNaN(bestScoreThisRound) || (bestScoreThisRound < minScore)) {
				break;
			}
		}
		return relSorter;
	}

	/**
	 * @param relSorter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<Post<BibTex>> extractResources(TreeMap<Float, ResourcePersonRelation> relSorter) {
		final List<Post<BibTex>> rVal = new ArrayList<>();
		for (ResourcePersonRelation rel : relSorter.values()) {
			rVal.add((Post<BibTex>) rel.getPost());
		}
		return rVal;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.PersonSearch#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public List<ResourcePersonRelation> getPersonSuggestion(PersonSuggestionQueryBuilder options) {
				try (final IndexLock indexLock = getEsIndexManager().aquireReadLockForTheActiveIndex(resourceType)) {
					final Set<String> tokenizedQueryString = new HashSet<>();
					for (String token : new SimpleTokenizer(options.getQuery())) {
						if (!StringUtils.isBlank(token)) {
							tokenizedQueryString.add(token.toLowerCase());
						}
					} 
					final TreeMap<Float, ResourcePersonRelation> relSorter = iterativelyFetchSuggestions(indexLock, tokenizedQueryString, options);
					return extractDistinctPersons(relSorter);
						
				} catch (final IndexMissingException e) {
					log.error("IndexMissingException: " + e);
				}
				return new ArrayList<>();
		
	}

	private QueryBuilder buildQuery(AbstractSuggestionQueryBuilder<?> options) {
		final QueryBuilder queryBuilder = QueryBuilders.filteredQuery( //
				QueryBuilders.boolQuery() //
						.should(
								addPersonSearch(options, //
									QueryBuilders.multiMatchQuery(options.getQuery()) //
									.field(LuceneFieldNames.TITLE, 3) //
									.field(LuceneFieldNames.SCHOOL, 2) //
								) //
								.tieBreaker(0.8f) //
								.boost(4) //
						) //
						.should(QueryBuilders.boolQuery() //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.habilitation.name()).boost(11)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.phdthesis.name()).boost(10)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.master_thesis.name()).boost(7)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.bachelor_thesis.name()).boost(6)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.candidate_thesis.name()).boost(5)) //
						) //
						//.should(QueryBuilders.termQuery(LuceneFieldNames.USER_NAME, genealogyUser)) //
						, addShouldYearIfYearInQuery( //
								FilterBuilders.termFilter(ESConstants.SYSTEM_URL_FIELD_NAME, systemUrl), //
								options.getQuery()) //
				);
		return queryBuilder;
	}

	/**
	 * @param options
	 * @param field
	 * @return
	 */
	private static MultiMatchQueryBuilder addPersonSearch(AbstractSuggestionQueryBuilder<?> options, MultiMatchQueryBuilder builder) {
		if (options.isWithEntityPersons()) {
			if (options.getRelationTypes().contains(PersonResourceRelationType.AUTHOR)) {
				builder.field(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME, 2);
			}
			if (options.getRelationTypes().containsAll(Arrays.asList(PersonResourceRelationType.values()))) {
				builder.field(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME, 1);
			}
		}
		if (options.isWithNonEntityPersons()) {
			builder.field(LuceneFieldNames.AUTHOR, 1);
		}
		return builder;
	}


	private SearchRequestBuilder buildRequest(int offset, String indexName, double minPlainEsScore, final QueryBuilder queryBuilder) {
		final SearchRequestBuilder searchRequestBuilder = this.esIndexManager.getClient().prepareSearch(indexName);
		searchRequestBuilder.setTypes(this.resourceType);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(queryBuilder) //
		.setMinScore((float)minPlainEsScore) //
		.setFrom(offset).setSize(suggestionSize * 5);
		return searchRequestBuilder;
	}
	
	private double fetchMoreResults(final TreeMap<Float, ResourcePersonRelation> relSorter, final Set<String> queryTerms, int offset, final Set<String> alreadyAnalyzedInterhashes, String indexName, double minPlainEsScore, AbstractSuggestionQueryBuilder<?> options) {
		// for finding persons, we use their names but for finding publications, we like to find publications which are not yet associated to a person entity
		final QueryBuilder queryBuilder = buildQuery(options);
		final SearchRequestBuilder searchRequestBuilder = buildRequest(offset, indexName, minPlainEsScore, queryBuilder);

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
				String userName = (String) hit.getSource().get(LuceneFieldNames.USER_NAME);
				if (this.genealogyUser.equals(userName)) {
					// we prefer posts by the genealogy user
					final Post<R> post = this.resourceConverter.writePost(hit.getSource());
					exchangePost(relSorter, post);
				}
				// we have seen this interhash before -> skip
				continue;
			}
			final Post<R> post = this.resourceConverter.writePost(hit.getSource());
			float postScore = hit.getScore();
			if (this.genealogyUser.equals(post.getUser().getName())) {
				postScore *= GENEALOGY_USER_PREFERENCE_FACTOR;
			}
			//
			final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr = new TreeMap<>();

			if (options instanceof PersonSuggestionQueryBuilder) {
				extractResourceRelationsForMatchingPersons(relSorter, queryTerms, post, postScore, invertedScoreToRpr);
			} else {
				ResourcePersonRelation rpr = new ResourcePersonRelation();
				rpr.setPost((Post<? extends BibTex>) post);
				relSorter.put(postScore, rpr);
			}
		}

		return bestPlainEsScore;
	}


	/**
	 * @param relSorter
	 * @param post
	 */
	private void exchangePost(TreeMap<Float, ResourcePersonRelation> relSorter, Post<R> post) {
		final String interHash = post.getResource().getInterHash();
		final Map<Float, ResourcePersonRelation> toBeAdded = new TreeMap<>();
		for (Iterator<Map.Entry<Float,ResourcePersonRelation>> it = relSorter.entrySet().iterator(); it.hasNext();) {
			final Entry<Float, ResourcePersonRelation> scoredRel = it.next();
			Post<? extends BibTex> oldPost = scoredRel.getValue().getPost();
			if (interHash.equals(oldPost.getResource().getInterHash())) {
				it.remove();
				scoredRel.getValue().setPost((Post<? extends BibTex>) post);
				// rescore to prefer exchanged posts
				toBeAdded.put(scoredRel.getKey() * GENEALOGY_USER_PREFERENCE_FACTOR, scoredRel.getValue());
			}
		}
		relSorter.putAll(toBeAdded);
	}


	private void extractResourceRelationsForMatchingPersons(final TreeMap<Float, ResourcePersonRelation> relSorter, final Set<String> queryTerms, final Post<R> post, final float postScore, final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr) {
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
				relSorter.put(postScore * -((float) lastScore) / minInvertedScore, e.getValue());
			}
		}
	}

	/**
	 * @param termFilterBuilder
	 * @param queryString
	 * @return
	 */
	private static FilterBuilder addShouldYearIfYearInQuery(FilterBuilder filterBuilder, String queryString) {
		Matcher m = YEAR_PATTERN.matcher(queryString);
		if (!m.find()) {
			return filterBuilder;
		}
		String year = queryString.substring(m.start(), m.end());
		return FilterBuilders.andFilter(filterBuilder, FilterBuilders.termFilter(LuceneFieldNames.YEAR, year));
	}


	private static int extractMinimumInvertedScore(final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr) {
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
				if (rVal.size() == suggestionSize) {
					return rVal;
				}
			}
		}
		return rVal;
	}

	public void setSystemUrl(String systemUrl) {
		this.systemUrl = systemUrl;
	}

	public void setSuggestionSize(int personSuggestionSize) {
		this.suggestionSize = personSuggestionSize;
	}


	public ESIndexManager getEsIndexManager() {
		return this.esIndexManager;
	}


	public void setEsIndexManager(ESIndexManager esIndexManager) {
		this.esIndexManager = esIndexManager;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPostsByBibtexKey(java.lang.String, java.util.Collection, org.bibsonomy.common.enums.SearchType, java.lang.String, java.util.Collection, java.util.List, org.bibsonomy.model.enums.Order, int, int)
	 */
	@Override
	public List<Post<R>> getPostsByBibtexKey(String userName, Collection<String> allowedGroups, org.bibsonomy.common.enums.SearchType searchType, String bibtexKey, Collection<String> tagIndex, List<String> negatedTags, Order order, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getTags(java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, org.bibsonomy.common.enums.SearchType, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	@Override
	public List<Tag> getTags(String userName, String requestedUserName, String requestedGroupName, Collection<String> allowedGroups, String searchTerms, org.bibsonomy.common.enums.SearchType searchType, String titleSearchTerms, String authorSearchTerms, Collection<String> tagIndex, String year, String firstYear, String lastYear, List<String> negatedTags, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getTags(java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	@Override
	public List<Tag> getTags(String userName, String requestedUserName, String requestedGroupName, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, Collection<String> tagIndex, String year, String firstYear, String lastYear, List<String> negatedTags, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * @param genealogyUser
	 */
	public void setGenealogyUser(String genealogyUser) {
		this.genealogyUser = genealogyUser;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPosts(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, org.bibsonomy.common.enums.SearchType, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.model.enums.Order, int, int)
	 */
	@Override
	public List<Post<R>> getPosts(String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, Collection<String> tagIndex, String year, String firstYear, String lastYear, List<String> negatedTags, Order order, int limit, int offset) {
		return getPosts(userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, org.bibsonomy.common.enums.SearchType.LOCAL, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, tagIndex, year, firstYear, lastYear, negatedTags, order, limit, offset);
	}

	
	/**
	 * build the overall elasticsearch query term
	 * 
	 * @param userName
	 * @param requestedUserName
	 *            restrict the resulting posts to those which are owned by this
	 *            user name
	 * @param requestedGroupName
	 *            restrict the resulting posts to those which are owned this
	 *            group
	 * @param requestedRelationNames
	 * 				expand the search in the post of users which are defined by the given 
	 * 				relation names
	 * @param allowedGroups 
	 * @param searchType 
	 * @param searchTerms
	 * @param titleSearchTerms 
	 * @param authorSearchTerms 
	 * @param bibtexKey 
	 * @param tagIndex 
	 * @param year 
	 * @param firstYear 
	 * @param lastYear 
	 * @param negatedTags
	 * @return overall elasticsearch query
	 */
	protected QueryBuilder buildQuery(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, org.bibsonomy.common.enums.SearchType searchType, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final Collection<String> negatedTags) {

		BoolQueryBuilder mainQueryBuilder = QueryBuilders.boolQuery();

		// --------------------------------------------------------------------
		// build the query
		// --------------------------------------------------------------------
		// the resulting main query
		if (present(searchTerms)) {
			QueryBuilder queryBuilder = QueryBuilders.queryString(searchTerms);
			mainQueryBuilder.must(queryBuilder);
		}

		if (present(titleSearchTerms)) {
			QueryBuilder titleSearchQuery = termQuery(LuceneFieldNames.TITLE, titleSearchTerms);
			mainQueryBuilder.must(titleSearchQuery);		}
		
		if (present(authorSearchTerms)) {
			QueryBuilder authorSearchQuery = termQuery(LuceneFieldNames.AUTHOR, authorSearchTerms);
			mainQueryBuilder.must(authorSearchQuery);			
		}
		
		if(present(bibtexKey)){
			QueryBuilder bibtexKeyQuery = termQuery(LuceneFieldNames.BIBTEXKEY, bibtexKey);
			mainQueryBuilder.must(bibtexKeyQuery);			
		}
		
		// Add the requested tags
		if (present(tagIndex) || present(negatedTags)) {
			this.addTagQuerries(tagIndex, negatedTags, mainQueryBuilder);
		}

		// restrict result to given group
		if (present(requestedGroupName)) {
			//TODO	
		}
		
		if(allowedGroups!=null){
			final BoolQueryBuilder groupSearchQuery = new BoolQueryBuilder();
			for(String allowedGroup:allowedGroups){
				groupSearchQuery.should(termQuery(LuceneFieldNames.GROUP, allowedGroup));
			}
			mainQueryBuilder.must(groupSearchQuery);
		}else{
			QueryBuilder groupSearchQuery = termQuery(LuceneFieldNames.GROUP, "public");
			mainQueryBuilder.must(groupSearchQuery);			
		}
		// restricting access to posts visible to the user

		// --------------------------------------------------------------------
		// post owned by user 
		// Use this restriction iff there is no user relation
		// --------------------------------------------------------------------
		if (present(requestedUserName) && !present(requestedRelationNames)) {
			QueryBuilder requesterUserSearchQuery = termQuery(LuceneFieldNames.USER, requestedUserName);
			mainQueryBuilder.must(requesterUserSearchQuery);			
		}
		// If there is at once one relation then restrict the results only 
		// to the users in the given relations (inclduing posts of the logged in users)
		else if (present(requestedRelationNames)) {
			// for all relations: TODO

		}
		
		
		QueryBuilder rVal = mainQueryBuilder;
		if (searchType == org.bibsonomy.common.enums.SearchType.LOCAL) {
			rVal = QueryBuilders.filteredQuery(mainQueryBuilder, FilterBuilders.termFilter(ESConstants.SYSTEM_URL_FIELD_NAME, this.systemUrl));
		}
		// all done
		log.debug("Search query: " + mainQueryBuilder.toString());
		
		return rVal;
	}
	
	private void addTagQuerries(final Collection<String> tagIndex, final Collection<String> negatedTags, final BoolQueryBuilder mainQuery) {
		/*
		 * Process normal tags
		 */
		if (present(tagIndex)) {
			for (final String tag : tagIndex) {
				// Is the tag string a concept name?
				if (tag.startsWith(Tag.CONCEPT_PREFIX)) {
					final String conceptTag = tag.substring(2);
					// get related tags:
					final BoolQueryBuilder conceptTags = new BoolQueryBuilder();
					QueryBuilder termQuery = termQuery(LuceneFieldNames.TAS, conceptTag);
					conceptTags.must(termQuery);
					for (final String t : this.dbLogic.getSubTagsForConceptTag(conceptTag)) {
						conceptTags.should(termQuery(LuceneFieldNames.TAS, t));
					}
					mainQuery.must(conceptTags);
				} else {
					mainQuery.must(termQuery(LuceneFieldNames.TAS, tag));
				}
			}
		}
		/*
		 * Process negated Tags
		 */
		
		if (present(negatedTags)) {
			for (final String negatedTag : negatedTags) {
				final QueryBuilder negatedSearchQuery = termQuery(LuceneFieldNames.TAS, negatedTag);
				mainQuery.mustNot(negatedSearchQuery);
			}
		}

	}	
}
