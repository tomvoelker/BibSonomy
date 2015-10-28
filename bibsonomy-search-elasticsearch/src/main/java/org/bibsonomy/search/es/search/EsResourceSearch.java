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
package org.bibsonomy.search.es.search;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.FirstValuePairComparator;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.querybuilder.AbstractSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PublicationSuggestionQueryBuilder;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.search.SearchInfoLogic;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.index.NormalizedEntryTypes;
import org.bibsonomy.search.es.index.ResourceConverter;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.tokenizer.SimpleTokenizer;
import org.bibsonomy.services.searcher.PersonSearch;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
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
 * @author dzo
 * @param <R>
 */
public class EsResourceSearch<R extends Resource> implements PersonSearch, ResourceSearch<R> {
	private static final Log log = LogFactory.getLog(EsResourceSearch.class);
	
	/** the max offset for suggestions */
	private static final int MAX_OFFSET = 1024;
	
	private static final float GENEALOGY_USER_PREFERENCE_FACTOR = 1.1f;
	private static final Pattern YEAR_PATTERN = Pattern.compile("[12][0-9]{3}");
	
	private Class<R> resourceType;

	/** post model converter */
	private ResourceConverter<R> resourceConverter;

	/**
	 * logic interface for retrieving data from bibsonomy (friends, groups
	 * members)
	 */
	private SearchInfoLogic infoLogic;
	
	private ElasticsearchManager<R> manager;

	/** the number of person suggestions */
	private int suggestionSize = 5;

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
		final QueryBuilder query = this.buildQuery(userName, requestedUserName, requestedGroupName, null, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexkey, tagIndex, year, firstYear, lastYear, negatedTags);
		final Map<Tag, Integer> tagCounter = new HashMap<Tag, Integer>();

		try {
			final SearchRequestBuilder searchRequestBuilder = this.manager.prepareSearch();
			searchRequestBuilder.setTypes(ResourceFactory.getResourceName(resourceType));
			searchRequestBuilder.setSearchType(SearchType.DEFAULT);
			searchRequestBuilder.setQuery(query);
			searchRequestBuilder.setFetchSource(Fields.TAGS, null);
			searchRequestBuilder.addSort(Fields.DATE, SortOrder.DESC);
			searchRequestBuilder.setFrom(offset).setSize(limit).setExplain(true);
			final SearchResponse response = searchRequestBuilder.execute().actionGet();
			if (response != null) {
				final SearchHits hits = response.getHits();
				log.debug("Current Search results for '" + searchTerms + "': " + response.getHits().getTotalHits());
				// TODO: check min TODODZO
				for (int i = 0; i < Math.min(limit, hits.getTotalHits() - offset); ++i) {
					SearchHit hit = hits.getAt(i);
					final Map<String, Object> result = hit.getSource();
					final Set<Tag> tags = this.resourceConverter.onlyConvertTags(result);
					// set tag count
					if (present(tags)) {
						for (final Tag tag : tags) {
							/*
							 * we remove the requested tags because we assume
							 * that related tags are requested
							 */
							if (present(tagIndex) && tagIndex.contains(tag.getName())) {
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
		} catch (final IndexMissingException e) {
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
	 */
	@Override
	public ResultList<Post<R>> getPosts(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final org.bibsonomy.common.enums.SearchType searchType, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, Order order, final int limit, final int offset) {
		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		try {
			final QueryBuilder queryBuilder = this.buildQuery(userName,
					requestedUserName, requestedGroupName,
					requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms,
					authorSearchTerms, bibtexKey, tagIndex,
					year, firstYear, lastYear, negatedTags);
			final SearchRequestBuilder searchRequestBuilder = this.manager.prepareSearch();
			searchRequestBuilder.setTypes(ResourceFactory.getResourceName(this.resourceType));
			searchRequestBuilder.setSearchType(SearchType.DEFAULT);
			searchRequestBuilder.setQuery(queryBuilder);
		
			if (order != Order.RANK) {
				searchRequestBuilder.addSort(Fields.DATE, SortOrder.DESC);
			}
			searchRequestBuilder.setFrom(offset).setSize(limit).setExplain(true); // FIXME: remove explain

			final SearchResponse response = searchRequestBuilder.execute().actionGet();

			if (response != null) {
				final SearchHits hits = response.getHits();
				postList.setTotalCount((int) hits.getTotalHits());

				log.debug("Current Search results for '" + searchTerms + "': " + response.getHits().getTotalHits());
				for (final SearchHit hit : hits) {
					final Post<R> post = this.resourceConverter.convert(hit.getSource());
					
					final CountRequestBuilder countBuilder = this.manager.prepareCount();
					final R resource = post.getResource();
					countBuilder.setQuery(QueryBuilders.termQuery(Fields.Resource.INTERHASH, resource.getInterHash()));
					final CountResponse countResponse = countBuilder.execute().actionGet();
					
					resource.setCount((int) countResponse.getCount());
					postList.add(post);
				}
			}
		} catch (final IndexMissingException e) {
			log.error("no index found: " + e);
		}
		return postList;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.PersonSearch#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public List<Post<BibTex>> getPublicationSuggestions(PublicationSuggestionQueryBuilder options) {
		try {
			// we use inverted scores such that the best results automatically appear first according to the ascending order of a sorted map
			final SortedSet<Pair<Float, ResourcePersonRelation>> relSorter = iterativelyFetchSuggestions(null, options);
			return extractResources(relSorter);
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		}
		return new ArrayList<>();
	}

	private SortedSet<Pair<Float, ResourcePersonRelation>> iterativelyFetchSuggestions(Set<String> tokenizedQueryString, AbstractSuggestionQueryBuilder<?> options) {
		// we use inverted scores such that the best results automatically appear first according to the ascending order of a sorted map
		final SortedSet<Pair<Float, ResourcePersonRelation>> relSorter = new TreeSet<>(new FirstValuePairComparator<Float, ResourcePersonRelation>(false));
		// unfortunately our version of elasticsearch does not support topHits aggregation so we have to group by interhash ourselves: AggregationBuilder aggregation = AggregationBuilders.terms("agg").field("gender").subAggregation(AggregationBuilders.topHits("top"));
		double bestScore = Double.NaN;
		// remember alreadyAnalyzedInterhashes to skip over multiple posts of the same resource
		final Set<String> alreadyAnalyzedInterhashes = new HashSet<>();
		for (int offset = 0; relSorter.size() < suggestionSize && offset < MAX_OFFSET; offset += 1024 / 10) {
			// although we change the query by changing the minimum score of the results, we can still
			// reuse the same offset(skip) counter, because the order of the ranked results remains the
			// same and the increased minimum score only drops results beyond those we have seen so far.
			double minScore = Double.isNaN(bestScore) ? 0.05 : (bestScore / 3d);
			
			double bestScoreThisRound = fetchMoreResults(relSorter, tokenizedQueryString, offset, alreadyAnalyzedInterhashes, minScore, options);
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
	private static List<Post<BibTex>> extractResources(SortedSet<Pair<Float, ResourcePersonRelation>> relSorter) {
		final List<Post<BibTex>> rVal = new ArrayList<>();
		for (Pair<Float, ResourcePersonRelation> scoreAndRel : relSorter) {
			final ResourcePersonRelation rel = scoreAndRel.getSecond();
			rVal.add((Post<BibTex>) rel.getPost());
		}
		return rVal;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.PersonSearch#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public List<ResourcePersonRelation> getPersonSuggestion(PersonSuggestionQueryBuilder options) {
		try {
			final Set<String> tokenizedQueryString = new HashSet<>();
			for (String token : new SimpleTokenizer(options.getQuery())) {
				if (!StringUtils.isBlank(token)) {
					tokenizedQueryString.add(token.toLowerCase());
				}
			}
			final SortedSet<Pair<Float, ResourcePersonRelation>> relSorter = iterativelyFetchSuggestions(tokenizedQueryString, options);
			return extractDistinctPersons(relSorter);
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		}
		return new ArrayList<>();
	}
	
	// TODO: the outcomment calls would be nice but would slow down the query
	private QueryBuilder buildQuery(AbstractSuggestionQueryBuilder<?> options) {
		final QueryBuilder queryBuilder = filterQuery( //
				QueryBuilders.boolQuery() //
						.must(
								addPersonSearch(options, //
									QueryBuilders.multiMatchQuery(options.getQuery()) //
									.type(Type.CROSS_FIELDS)
									//.minimumShouldMatch("70%")
									.operator(Operator.AND) // "and" here means every term in the query must be in one of the following fields
									.field(Fields.Resource.TITLE, 2.5f) //
									.field(Fields.Publication.SCHOOL, 1.3f) //
								) //
								.tieBreaker(0.8f) //
								.boost(4) //
						) //
						.should(QueryBuilders.boolQuery() //
//								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.habilitation.name()).boost(11)) //
								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.phdthesis.name()).boost(10)) //
//								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.master_thesis.name()).boost(7)) //
//								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.bachelor_thesis.name()).boost(6)) //
//								.should(QueryBuilders.termQuery(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, NormalizedEntryTypes.candidate_thesis.name()).boost(5)) //
						) //
						//.should(QueryBuilders.termQuery(LuceneFieldNames.USER_NAME, genealogyUser)) //
						, addYearFilterIfYearInQuery( //
								null, // termFilter(ESConstants.SYSTEM_URL_FIELD_NAME, systemUrl), //
								options.getQuery()) //
				);
		return queryBuilder;
	}
	
	/**
	 * @param must
	 * @param addShouldYearIfYearInQuery
	 * @return
	 */
	private QueryBuilder filterQuery(QueryBuilder must, FilterBuilder filter) {
		if (filter == null) {
			return must;
		}
		return QueryBuilders.filteredQuery(must, filter);
	}

	/**
	 * @param options
	 * @param field
	 * @return
	 */
	private static MultiMatchQueryBuilder addPersonSearch(AbstractSuggestionQueryBuilder<?> options, MultiMatchQueryBuilder builder) {
		if (options.isWithEntityPersons()) {
			if (options.getRelationTypes().contains(PersonResourceRelationType.AUTHOR)) {
				builder.field(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME, 2.2f);
			}
			if (options.getRelationTypes().containsAll(Arrays.asList(PersonResourceRelationType.values()))) {
				builder.field(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME, 1.1f);
			}
		}
		if (options.isWithNonEntityPersons()) {
			builder.field(Fields.Publication.AUTHOR, 2.7f);
		}
		return builder;
	}

	private SearchRequestBuilder buildRequest(int offset, double minPlainEsScore, final QueryBuilder queryBuilder) {
		final SearchRequestBuilder searchRequestBuilder = this.manager.prepareSearch();
		searchRequestBuilder.setTypes(ResourceFactory.getResourceName(this.resourceType));
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(queryBuilder) //
		.setMinScore((float)minPlainEsScore) //
		.setFrom(offset).setSize(MAX_OFFSET / 10);
		return searchRequestBuilder;
	}
	
	private double fetchMoreResults(final SortedSet<Pair<Float, ResourcePersonRelation>> relSorter, final Set<String> queryTerms, int offset, final Set<String> alreadyAnalyzedInterhashes, double minPlainEsScore, AbstractSuggestionQueryBuilder<?> options) {
		// for finding persons, we use their names but for finding publications, we like to find publications which are not yet associated to a person entity
		final QueryBuilder queryBuilder = buildQuery(options);
		final SearchRequestBuilder searchRequestBuilder = buildRequest(offset, minPlainEsScore, queryBuilder);

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
				minScore = bestPlainEsScore / 4.0;
			} else if (hit.getScore() < minScore) {
				break;
			}
			final String interhash = (String) hit.getSource().get(Fields.Resource.INTERHASH);
			if (!alreadyAnalyzedInterhashes.add(interhash)) {
				final String userName = (String) hit.getSource().get(Fields.USER_NAME);
				// prefer posts og the genealogy user
				if (this.genealogyUser.equals(userName)) {
					// we prefer posts by the genealogy user
					final Post<R> post = this.resourceConverter.convert(hit.getSource());
					exchangePost(relSorter, post);
				}
				// we have seen this interhash before -> skip
				continue;
			}
			final Post<R> postTmp = this.resourceConverter.convert(hit.getSource());
			if (!(postTmp.getResource() instanceof BibTex)) {
				continue;
			}
			@SuppressWarnings("unchecked") // ok, we checked this before
			final Post<BibTex> post = (Post<BibTex>) postTmp;
			float postScore = hit.getScore();
			if (this.genealogyUser.equals(post.getUser().getName())) {
				postScore *= GENEALOGY_USER_PREFERENCE_FACTOR;
			}
			//
			final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr = new TreeMap<>();

			if (options instanceof PersonSuggestionQueryBuilder) {
				extractResourceRelationsForMatchingPersons(relSorter, queryTerms, post, postScore, invertedScoreToRpr, (PersonSuggestionQueryBuilder) options);
			} else {
				final ResourcePersonRelation rpr = new ResourcePersonRelation();
				rpr.setPost(post);
				relSorter.add(new Pair<>(Float.valueOf(postScore), rpr));
			}
		}
		return bestPlainEsScore;
	}

	/**
	 * @param relSorter
	 * @param post
	 */
	private void exchangePost(final SortedSet<Pair<Float, ResourcePersonRelation>> relSorter, final Post<R> post) {
		final String interHash = post.getResource().getInterHash();
		final Collection<Pair<Float, ResourcePersonRelation>> toBeAdded = new ArrayList<>();
		for (Iterator<Pair<Float,ResourcePersonRelation>> it = relSorter.iterator(); it.hasNext();) {
			final Pair<Float, ResourcePersonRelation> scoreAndRel = it.next();
			final Post<? extends BibTex> oldPost = scoreAndRel.getSecond().getPost();
			if (interHash.equals(oldPost.getResource().getInterHash())) {
				final ResourcePersonRelation oldRel = scoreAndRel.getSecond();
				final Float oldScore = scoreAndRel.getFirst();
				it.remove();
				oldRel.setPost((Post<? extends BibTex>) post);
				
				// rescore to prefer exchanged posts
				toBeAdded.add(new Pair<>(oldScore * GENEALOGY_USER_PREFERENCE_FACTOR, oldRel));
			}
		}
		relSorter.addAll(toBeAdded);
	}

	private void extractResourceRelationsForMatchingPersons(final SortedSet<Pair<Float, ResourcePersonRelation>> relSorter, final Set<String> queryTerms, final Post<BibTex> post, final float postScore, final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr, PersonSuggestionQueryBuilder options) {
		if (options.isWithEntityPersons()) {
			for (ResourcePersonRelation rpr : post.getResourcePersonRelations()) {
				PersonName mainName = rpr.getPerson().getMainName();
				int invertedScore = calculateInvertedPersonNameScore(queryTerms, mainName);
				if (rpr.getRelationType() == PersonResourceRelationType.AUTHOR) {
					invertedScore *= 2;
				}
				invertedScoreToRpr.put(invertedScore, rpr);
			}
		}
		if (options.isWithNonEntityPersons()) {
			if (options.getRelationTypes().contains(PersonResourceRelationType.AUTHOR)) {
				Map<Integer, Person> personsByIndex = null;
				if (!options.isAllowNamesWithoutEntities()) {
					personsByIndex = getIndicesOfKnownPersons(post, PersonResourceRelationType.AUTHOR);
				}
				addScoredPersonNames(post.getResource().getAuthor(), PersonResourceRelationType.AUTHOR, queryTerms, post, invertedScoreToRpr, options, personsByIndex);
			}
			if (options.getRelationTypes().contains(PersonResourceRelationType.EDITOR)) {
				Map<Integer, Person> personsByIndex = null;
				if (!options.isAllowNamesWithoutEntities()) {
					personsByIndex = getIndicesOfKnownPersons(post, PersonResourceRelationType.EDITOR);
				}
				addScoredPersonNames(post.getResource().getEditor(), PersonResourceRelationType.EDITOR, queryTerms, post, invertedScoreToRpr, options, personsByIndex);
			}
		}
		
		final int minInvertedScore = extractMinimumInvertedScore(invertedScoreToRpr);
		int lastScore = -1;
		for (Map.Entry<Integer, ResourcePersonRelation> e : invertedScoreToRpr.entrySet()) {
			final Integer score = e.getKey();
			if (score.intValue() < lastScore / 2) {
				lastScore = score;
				relSorter.add(new Pair<>(postScore * -((float) lastScore) / minInvertedScore, e.getValue()));
			}
		}
	}

	private Map<Integer, Person> getIndicesOfKnownPersons(final Post<BibTex> post, PersonResourceRelationType relType) {
		Map<Integer, Person> allowedIndices;
		allowedIndices = new HashMap<>();
		for (ResourcePersonRelation rel : post.getResourcePersonRelations()) {
			if ((rel.getPersonIndex() >= 0) && (rel.getRelationType() == relType)) {
				allowedIndices.put(rel.getPersonIndex(), rel.getPerson());
			}
		}
		return allowedIndices;
	}

	private void addScoredPersonNames(List<PersonName> names, PersonResourceRelationType relationType, final Set<String> queryTerms, final Post<BibTex> post, final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr, PersonSuggestionQueryBuilder options, Map<Integer, Person> allowedPersonsByIndex) {
		if (!present(names)) {
			return;
		}
		for (int i = 0; i < names.size(); ++i) {
			PersonName name = names.get(i);
			ResourcePersonRelation rpr = new ResourcePersonRelation();
			rpr.setPerson(new Person());
			rpr.getPerson().setMainName(name);
			rpr.setPersonIndex(i);
			rpr.setPost(post);
			rpr.setRelationType(relationType);
			
			if (allowedPersonsByIndex != null) {
				Person person = allowedPersonsByIndex.get(i);
				if (person == null) {
					continue;
				}
				rpr.getPerson().setPersonId(person.getPersonId());
			}
			int invertedScore = calculateInvertedPersonNameScore(queryTerms, name) * 2;
			if (options.isPreferUnlinked()) {
				invertedScore *= 2;
			}
			if (rpr.getRelationType() == PersonResourceRelationType.AUTHOR) {
				invertedScore *= 2;
			}
			invertedScoreToRpr.put(invertedScore, rpr);
		}
	}

	private int calculateInvertedPersonNameScore(final Set<String> queryTerms, PersonName mainName) {
		int invertedScore = -1;
		for (String token : new SimpleTokenizer(mainName.getFirstName())) {
			if (!StringUtils.isBlank(token)) {
				token = token.toLowerCase();
				if (queryTerms.contains(token)) {
					invertedScore--;
				}
			}
			
		}
		for (String token : new SimpleTokenizer(mainName.getLastName())) {
			if (!StringUtils.isBlank(token)) {
				token = token.toLowerCase();
				if (queryTerms.contains(token)) {
					invertedScore--;
				}
			}
		}
		return invertedScore;
	}

	/**
	 * @param termFilterBuilder
	 * @param queryString
	 * @return
	 */
	private static FilterBuilder addYearFilterIfYearInQuery(FilterBuilder filterBuilder, String queryString) {
		final Matcher m = YEAR_PATTERN.matcher(queryString);
		if (!m.find()) {
			return filterBuilder;
		}
		final String year = queryString.substring(m.start(), m.end());
		final FilterBuilder filter = FilterBuilders.termFilter(Fields.Publication.YEAR, year);
		if (filterBuilder == null) {
			return filter;
		}
		return FilterBuilders.andFilter(filterBuilder, filter);
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

	private List<ResourcePersonRelation> extractDistinctPersons(final SortedSet<Pair<Float, ResourcePersonRelation>> rValSorter) {
		List<ResourcePersonRelation> rVal = new ArrayList<>();
		Set<String> foundPersonIds = new HashSet<>();
		for (Pair<Float,ResourcePersonRelation> scoreAndRpr : rValSorter) {
			ResourcePersonRelation rpr = scoreAndRpr.getSecond();
			if ((rpr.getPerson().getPersonId() == null) || (foundPersonIds.add(rpr.getPerson().getPersonId()) == true)) {
				// we have not seen this personId earlier in the sorted map, so add it to the response list
				rVal.add(rpr);
				if (rVal.size() == suggestionSize) {
					return rVal;
				}
			}
		}
		return rVal;
	}

	public void setSuggestionSize(int personSuggestionSize) {
		this.suggestionSize = personSuggestionSize;
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
		return this.getTags(userName, requestedUserName, requestedGroupName, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, null, tagIndex, year, firstYear, lastYear, negatedTags, limit, offset);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getTags(java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	@Override
	public List<Tag> getTags(String userName, String requestedUserName, String requestedGroupName, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, Collection<String> tagIndex, String year, String firstYear, String lastYear, List<String> negatedTags, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
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
	protected final QueryBuilder buildQuery(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final Collection<String> negatedTags) {
		final BoolQueryBuilder mainQueryBuilder = QueryBuilders.boolQuery();
		final BoolFilterBuilder mainFilterBuilder = FilterBuilders.boolFilter();
		
		// --------------------------------------------------------------------
		// build the query
		// --------------------------------------------------------------------
		// the resulting main query
		if (present(searchTerms)) {
			final QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(searchTerms);
			mainQueryBuilder.must(queryBuilder);
		}

		if (present(titleSearchTerms)) {
			final QueryBuilder titleSearchQuery = QueryBuilders.termQuery(Fields.Resource.TITLE, titleSearchTerms);
			mainQueryBuilder.must(titleSearchQuery);
		}
		
		this.buildResourceSpecifiyQuery(mainQueryBuilder, userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, year, firstYear, lastYear);
		
		// Add the requested tags
		if (present(tagIndex)) {
			mainFilterBuilder.must(this.buildTagFilter(tagIndex));
		}
		
		if (present(negatedTags)) {
			mainFilterBuilder.must(this.buildNegatedTags(negatedTags));
		}
		
		// restrict result to given group
		if (present(requestedGroupName)) {
			// by appending a filter for all members of the group
			final BoolFilterBuilder groupMembersFilter = this.buildGroupMembersFilter(requestedGroupName);
			if (groupMembersFilter != null) {
				mainFilterBuilder.must(groupMembersFilter);
			}
		}
		
		// restricting access to posts visible to the user
		if (!present(allowedGroups)) {
			allowedGroups = Collections.singleton(GroupUtils.buildPublicGroup().getName());
		}
		
		final BoolFilterBuilder groupFilter = FilterBuilders.boolFilter();
		for (final String allowedGroup : allowedGroups){
			groupFilter.should(FilterBuilders.termFilter(Fields.GROUPS, allowedGroup));
		}
		
		if (present(userName)) {
			final TermFilterBuilder privateGroupFilter = FilterBuilders.termFilter(Fields.GROUPS, GroupUtils.buildPrivateGroup().getName());
			final TermFilterBuilder userFilter = FilterBuilders.termFilter(Fields.USER_NAME, userName);
			groupFilter.should(FilterBuilders.andFilter(userFilter, privateGroupFilter));
		}
		
		mainFilterBuilder.must(groupFilter);
		
		// post owned by user 
		// Use this restriction iff there is no user relation
		if (present(requestedUserName)) {
			final TermFilterBuilder requestedUserFilter = FilterBuilders.termFilter(Fields.USER_NAME, requestedUserName);
			mainFilterBuilder.must(requestedUserFilter);
		}
		
		this.buildResourceSpecifiyFilters(mainFilterBuilder, userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, year, firstYear, lastYear);
		
		// all done
		log.debug("Search query: '" + mainQueryBuilder.toString() + "' and filters: '" + mainFilterBuilder.toString() + "'");
		return QueryBuilders.filteredQuery(mainQueryBuilder, mainFilterBuilder);
	}
	
	/**
	 * @param mainFilterBuilder
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param requestedRelationNames
	 * @param allowedGroups
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param bibtexKey
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 */
	protected void buildResourceSpecifiyFilters(BoolFilterBuilder mainFilterBuilder, String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, String year, String firstYear, String lastYear) {
		// noop
	}

	/**
	 * @param mainQueryBuilder
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param requestedRelationNames
	 * @param allowedGroups
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param bibtexKey
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 */
	protected void buildResourceSpecifiyQuery(BoolQueryBuilder mainQueryBuilder, String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, String year, String firstYear, String lastYear) {
		// noop
	}

	/**
	 * @param negatedTags
	 * @return
	 */
	private FilterBuilder buildNegatedTags(Collection<String> negatedTags) {
		final BoolFilterBuilder tagFilter = FilterBuilders.boolFilter();
		
		for (final String negatedTag : negatedTags) {
			final FilterBuilder negatedSearchQuery = FilterBuilders.termFilter(Fields.TAGS, negatedTag);
			tagFilter.mustNot(negatedSearchQuery);
		}
		
		return tagFilter;
	}

	/**
	 * @param tagIndex
	 * @return
	 */
	private FilterBuilder buildTagFilter(Collection<String> tagIndex) {
		final BoolFilterBuilder tagsFilter = FilterBuilders.boolFilter();
		for (final String tag : tagIndex) {
			// is the tag string a concept name?
			if (tag.startsWith(Tag.CONCEPT_PREFIX)) {
				final String conceptTag = tag.substring(Tag.CONCEPT_PREFIX.length());
				// get related tags:
				final BoolFilterBuilder conceptTags = FilterBuilders.boolFilter();
				// TODO: must the tag be included? TODODZO
				final TermFilterBuilder termQuery = FilterBuilders.termFilter(Fields.TAGS, conceptTag);
				conceptTags.must(termQuery);
				for (final String subTagString : this.infoLogic.getSubTagsForConceptTag(conceptTag)) {
					conceptTags.should(FilterBuilders.termFilter(Fields.TAGS, subTagString));
				}
				tagsFilter.must(conceptTags);
			} else {
				tagsFilter.must(FilterBuilders.termFilter(Fields.TAGS, tag));
			}
		}
		return tagsFilter;
	}

	/**
	 * @param requestedGroupName
	 * @return 
	 */
	private BoolFilterBuilder buildGroupMembersFilter(String requestedGroupName) {
		final Collection<String> groupMembers = this.infoLogic.getGroupMembersByGroupName(requestedGroupName);
		if (present(requestedGroupName) && present(groupMembers)) {
			final BoolFilterBuilder groupMemberFilter = FilterBuilders.boolFilter();
			for (final String member : groupMembers) {
				final TermFilterBuilder memberFilter = FilterBuilders.termFilter(Fields.USER_NAME, member);
				groupMemberFilter.should(memberFilter);
			}
			
			return groupMemberFilter;
		}
		return null;
	}
	
	/**
	 * @param resourceConverter the resourceConverter to set
	 */
	public void setResourceConverter(final ResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(final Class<R> resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setDbLogic(SearchInfoLogic dbLogic) {
		this.infoLogic = dbLogic;
	}

	/**
	 * @param infoLogic the infoLogic to set
	 */
	public void setInfoLogic(SearchInfoLogic infoLogic) {
		this.infoLogic = infoLogic;
	}

	/**
	 * @param manager the manager to set
	 */
	public void setManager(ElasticsearchManager<R> manager) {
		this.manager = manager;
	}
	
	/**
	 * @param genealogyUser
	 */
	public void setGenealogyUser(String genealogyUser) {
		this.genealogyUser = genealogyUser;
	}
}
