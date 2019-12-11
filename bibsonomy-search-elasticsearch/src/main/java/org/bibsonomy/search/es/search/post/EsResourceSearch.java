/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.search.post;

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
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.FirstValuePairComparator;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.SortOrder;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.enums.SortDirection;
import org.bibsonomy.model.logic.querybuilder.AbstractSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PublicationSuggestionQueryBuilder;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.search.InvalidSearchRequestException;
import org.bibsonomy.search.SearchInfoLogic;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.index.converter.post.NormalizedEntryTypes;
import org.bibsonomy.search.es.index.converter.post.ResourceConverter;
import org.bibsonomy.search.es.management.post.ElasticsearchPostManager;
import org.bibsonomy.search.es.search.util.tokenizer.SimpleTokenizer;
import org.bibsonomy.search.es.util.SortingUtils;
import org.bibsonomy.services.searcher.PersonSearch;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.util.Sets;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

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

	/** post model converter */
	private ResourceConverter<R> resourceConverter;

	/**
	 * logic interface for retrieving data from the main database
	 * (friends, groups members)
	 */
	private SearchInfoLogic infoLogic;
	
	private ElasticsearchPostManager<R> manager;

	/** the number of person suggestions */
	// private int suggestionSize = 5;

	private String genealogyUser;

	@FunctionalInterface
	private interface ElasticsearchSearchCall<T> {
		T call();
	}

	private static <T> T callSearch(final ElasticsearchSearchCall<T> call, final T defaultValue) {
		try {
			return call.call();
		} catch (final ElasticsearchStatusException e) {
			if (!RestStatus.NOT_FOUND.equals(e.status())) {
				log.error("unknown error while searching", e);
			} else {
				log.error("no index found: ", e);
			}
		} catch (final SearchPhaseExecutionException e) {
			log.info("parsing query failed.", e);
			throw new InvalidSearchRequestException();
		}

		return defaultValue;
	}

	
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
	@Override
	public List<Tag> getTags(final String userName, final String requestedUserName, final String requestedGroupName, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexkey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, final int limit, final int offset) {
		final QueryBuilder query = this.buildQuery(userName, requestedUserName, requestedGroupName, null, allowedGroups, this.getUsersThatShareDocuments(userName), searchTerms, titleSearchTerms, authorSearchTerms, bibtexkey, tagIndex, year, firstYear, lastYear, negatedTags);
		if (query == null) {
			return new LinkedList<>();
		}

		final Map<Tag, Integer> tagCounter = callSearch(() -> {
			final Map<Tag, Integer> tagCounterMap = new HashMap<>();
			final SearchHits hits = this.manager.search(query, null, offset, limit, null, Collections.singleton(Fields.TAGS));

			log.debug("Current Search results for '" + searchTerms + "': " + hits.getTotalHits());
			// TODO: check min TODODZO
			for (int i = 0; i < Math.min(limit, hits.getTotalHits() - offset); ++i) {
				SearchHit hit = hits.getAt(i);
				final Map<String, Object> result = hit.getSourceAsMap();
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
						Integer oldCnt = tagCounterMap.get(tag);
						if (!present(oldCnt)) {
							oldCnt = Integer.valueOf(1);
						} else {
							oldCnt++;
						}
						tagCounterMap.put(tag, oldCnt);
					}
				}
			}
			return tagCounterMap;
		}, Collections.emptyMap());

		final List<Tag> tags = new LinkedList<>();
		// extract all tags
		for (final Map.Entry<Tag, Integer> entry : tagCounter.entrySet()) {
			final Tag tag = entry.getKey();
			final int count = entry.getValue().intValue();
			tag.setUsercount(count);
			tag.setGlobalcount(count); // FIXME: we set user==global count
			tags.add(tag);
		}
		
		log.debug("Done calculating tag statistics");
		// all done.
		return tags;
	}

	@Override
	public ResultList<Post<R>> getPosts(String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, SearchType searchType, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, Collection<String> tagIndex, String year, String firstYear, String lastYear, List<String> negatedTags, Order order, int limit, int offset) {
		SortOrder sortOrder = new SortOrder(order, SortDirection.DESC);
		return getPosts(userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchType, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, tagIndex, year, firstYear, lastYear, negatedTags, sortOrder, limit, offset);
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
	 * @param sortOrder
	 * @param limit
	 * @param offset
	 * @return returns the list of posts
	 */
	@Override
	public ResultList<Post<R>> getPosts(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final org.bibsonomy.common.enums.SearchType searchType, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, final SortOrder sortOrder, final int limit, final int offset) {

		final ResultList<Post<R>> postList = callSearch(() -> {
			final ResultList<Post<R>> posts = new ResultList<>();
			final Set<String> allowedUsers = getUsersThatShareDocuments(userName);
			final QueryBuilder queryBuilder = this.buildQuery(userName,
							requestedUserName, requestedGroupName,
							requestedRelationNames, allowedGroups, allowedUsers, searchTerms,
							titleSearchTerms, authorSearchTerms, bibtexKey,
							tagIndex, year, firstYear, lastYear, negatedTags);
			if (queryBuilder == null) {
				return posts;
			}
			final SearchHits hits = this.manager.search(queryBuilder, sortOrder, offset, limit, null, null);

			if (hits != null) {
				posts.setTotalCount((int) hits.getTotalHits());

				for (final SearchHit hit : hits) {
					final Post<R> post = this.resourceConverter.convert(hit.getSourceAsMap(), allowedUsers);
					final R resource = post.getResource();

					final long count = this.manager.getDocumentCount(QueryBuilders.termQuery(Fields.Resource.INTERHASH, resource.getInterHash()));

					resource.setCount((int) count);
					posts.add(post);
				}
			}

			return posts;
		}, new ResultList<>());
		return postList;
	}

	/**
	 * @param userName
	 * @return
	 */
	private Set<String> getUsersThatShareDocuments(final String userName) {
		if (present(userName)) {
			return this.infoLogic.getUserNamesThatShareDocumentsWithUser(userName);
		}
		return new HashSet<>();
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
		} catch (final IndexNotFoundException e) {
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
//		for (int offset = 0; relSorter.size() < suggestionSize && offset < MAX_OFFSET; offset += 1024 / 10) {
		for (int offset = 0; offset < MAX_OFFSET; offset += 1024 / 10) {
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
		} catch (final IndexNotFoundException e) {
			log.error("index not found: " + e);
		}
		return new ArrayList<>();
	}
	
	// TODO: the outcomment calls would be nice but would slow down the query
	private static QueryBuilder buildQuery(AbstractSuggestionQueryBuilder<?> options) {
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
						//.should(QueryBuilders.termQuery(ESConstants.Fields.USER_NAME, genealogyUser)) //
						, addYearFilterIfYearInQuery( //
								null, // termFilter(ESConstants.SYSTEM_URL_FIELD_NAME, systemUrl), //
								options.getQuery()) //
				);
		return queryBuilder;
	}
	
	/**
	 * @param must
	 * @param filter
	 * @return
	 */
	private static QueryBuilder filterQuery(QueryBuilder must, QueryBuilder filter) {
		if (filter == null) {
			return must;
		}
		
		return QueryBuilders.boolQuery().must(must).filter(filter);
	}

	/**
	 * @param options
	 * @param builder
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
			builder.field(Fields.Publication.AUTHORS + "." + Fields.Publication.PERSON_NAME, 2.7f);
		}
		return builder;
	}

	private SearchHits buildRequest(int offset, double minPlainEsScore, final QueryBuilder queryBuilder) {
		return this.manager.search(queryBuilder, null, offset, MAX_OFFSET / 10, (float) minPlainEsScore, null);
	}
	
	private double fetchMoreResults(final SortedSet<Pair<Float, ResourcePersonRelation>> relSorter, final Set<String> queryTerms, int offset, final Set<String> alreadyAnalyzedInterhashes, double minPlainEsScore, AbstractSuggestionQueryBuilder<?> options) {
		// for finding persons, we use their names but for finding publications, we like to find publications which are not yet associated to a person entity
		final QueryBuilder queryBuilder = buildQuery(options);
		final SearchHits hits = buildRequest(offset, minPlainEsScore, queryBuilder);

		if (hits == null || hits.getTotalHits() < 1) {
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
			final String interhash = (String) hit.getSourceAsMap().get(Fields.Resource.INTERHASH);
			if (!alreadyAnalyzedInterhashes.add(interhash)) {
				final String userName = (String) hit.getSourceAsMap().get(Fields.USER_NAME);
				// prefer posts og the genealogy user
				if (this.genealogyUser.equals(userName)) {
					// we prefer posts by the genealogy user
					final Post<R> post = this.resourceConverter.convert(hit.getSourceAsMap(), Collections.emptySet());
					exchangePost(relSorter, post);
				}
				// we have seen this interhash before -> skip
				continue;
			}
			final Post<R> postTmp = this.resourceConverter.convert(hit.getSourceAsMap(), Collections.emptySet());
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

	private static Map<Integer, Person> getIndicesOfKnownPersons(final Post<BibTex> post, PersonResourceRelationType relType) {
		Map<Integer, Person> allowedIndices;
		allowedIndices = new HashMap<>();
		for (ResourcePersonRelation rel : post.getResourcePersonRelations()) {
			if ((rel.getPersonIndex() >= 0) && (rel.getRelationType() == relType)) {
				allowedIndices.put(Integer.valueOf(rel.getPersonIndex()), rel.getPerson());
			}
		}
		return allowedIndices;
	}

	private static void addScoredPersonNames(List<PersonName> names, PersonResourceRelationType relationType, final Set<String> queryTerms, final Post<BibTex> post, final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr, PersonSuggestionQueryBuilder options, Map<Integer, Person> allowedPersonsByIndex) {
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
				final Person person = allowedPersonsByIndex.get(Integer.valueOf(i));
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
			invertedScoreToRpr.put(Integer.valueOf(invertedScore), rpr);
		}
	}

	private static int calculateInvertedPersonNameScore(final Set<String> queryTerms, PersonName mainName) {
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
	 * @param filterBuilder
	 * @param queryString
	 * @return
	 */
	private static QueryBuilder addYearFilterIfYearInQuery(QueryBuilder filterBuilder, String queryString) {
		final Matcher m = YEAR_PATTERN.matcher(queryString);
		if (!m.find()) {
			return filterBuilder;
		}
		final String year = queryString.substring(m.start(), m.end());
		final QueryBuilder filter = QueryBuilders.termQuery(Fields.Publication.YEAR, year);
		if (filterBuilder == null) {
			return filter;
		}
		return QueryBuilders.boolQuery().must(filterBuilder).must(filter);
	}

	private static int extractMinimumInvertedScore(final TreeMap<Integer, ResourcePersonRelation> invertedScoreToRpr) {
		int minInvertedScore = -1;
		for (Map.Entry<Integer, ResourcePersonRelation> e : invertedScoreToRpr.entrySet()) {
			final int key = e.getKey().intValue();
			if (minInvertedScore > key) {
				minInvertedScore = key;
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
//				if (rVal.size() == suggestionSize) {
//					return rVal;
//				}
			}
		}
		return rVal;
	}
	
	/**
	 * @param personSuggestionSize the person suggestion size
	 */
//	public void setSuggestionSize(int personSuggestionSize) {
//		this.suggestionSize = personSuggestionSize;
//	}
	
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
	 * @param usersThatShareDocs all users that the logged in user is allowed to access
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
	protected final QueryBuilder buildQuery(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, Collection<String> allowedGroups, Set<String> usersThatShareDocs, String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final Collection<String> negatedTags) {
		final BoolQueryBuilder mainQueryBuilder = QueryBuilders.boolQuery();
		final BoolQueryBuilder mainFilterBuilder = QueryBuilders.boolQuery();
		// here we exclude the logged in user the docs are already queried using the private fields
		final Set<String> usersToQueryForDocuments = new HashSet<>(usersThatShareDocs);
		if (present(userName)) {
			usersToQueryForDocuments.remove(userName);
		}
		/*
		 * build the query
		 * the resulting main query
		 */
		if (present(searchTerms)) {
			/*
			 * per default we use the and operation for multiple search query terms;
			 * to allow "and"'s for field search queries we set the dis max to false to enable
			 * boolean queries for multiple fields
			 *
			 * XXX: we also need to set these parameters to the private string queries, otherwise the configured
			 * field is ignored and ES searches again in the queried fields
			 */
			final QueryBuilder queryBuilder = buildStringQueryForSearchTerms(searchTerms, this.manager.getPublicFields());
			
			if (present(userName)) {
				// private field
				final TermQueryBuilder userFilter = QueryBuilders.termQuery(Fields.USER_NAME, userName);
				final QueryStringQueryBuilder privateFieldSearchQuery = buildStringQueryForSearchTerms(searchTerms, this.manager.getPrivateFields());
				final BoolQueryBuilder privateFieldQueryFiltered = QueryBuilders.boolQuery().must(privateFieldSearchQuery).filter(userFilter);
				
				final BoolQueryBuilder query = QueryBuilders.boolQuery().should(queryBuilder).should(privateFieldQueryFiltered);
				
				if (present(usersToQueryForDocuments)) {
					// document field
					final QueryStringQueryBuilder docFieldSearchQuery = buildStringQueryForSearchTerms(searchTerms, Sets.asSet(Fields.Publication.ALL_DOCS));
					// restrict to users that share documents and to the visible posts (group)
					final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery().must(buildUserQuery(usersThatShareDocs)).must(buildGroupFilter(allowedGroups));
					query.should(QueryBuilders.boolQuery().must(docFieldSearchQuery).filter(filterQuery));
				}
				
				mainQueryBuilder.must(query);
			} else {
				mainQueryBuilder.must(queryBuilder);
			}
		}

		if (present(titleSearchTerms)) {
			// we have search terms for title autocompletion, build a phrase prefix query for the title search terms
			final QueryBuilder titleSearchQuery = QueryBuilders.matchPhrasePrefixQuery(Fields.Resource.TITLE, titleSearchTerms);
			mainQueryBuilder.must(titleSearchQuery);
		}
		
		this.buildResourceSpecifiyQuery(mainQueryBuilder, userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, year, firstYear, lastYear);
		
		// Add the requested tags
		if (present(tagIndex)) {
			mainFilterBuilder.must(this.buildTagFilter(tagIndex));
		}
		
		if (present(negatedTags)) {
			mainFilterBuilder.must(buildNegatedTags(negatedTags));
		}
		
		// restrict result to given group
		if (present(requestedGroupName)) {
			// by appending a filter for all members of the group
			final QueryBuilder groupMembersFilter = this.buildGroupMembersFilter(requestedGroupName);
			if (groupMembersFilter != null) {
				mainFilterBuilder.must(groupMembersFilter);
			} else {
				return null;
			}
		}
		
		/*
		 * restricting access to posts visible to the user
		 */
		if (!present(allowedGroups)) {
			allowedGroups = Collections.singleton(GroupUtils.buildPublicGroup().getName());
		}
		
		final BoolQueryBuilder groupFilter = buildGroupFilter(allowedGroups);
		
		if (present(userName)) {
			/*
			 * handle private posts
			 * the group should be private or friends and the loggedin user (userName) should be the owner of the post
			 */
			final TermQueryBuilder privateGroupFilter = QueryBuilders.termQuery(Fields.GROUPS, GroupUtils.buildPrivateGroup().getName());
			final TermQueryBuilder friendsGroupFilter = QueryBuilders.termQuery(Fields.GROUPS, GroupUtils.buildFriendsGroup().getName());
			final TermQueryBuilder userFilter = QueryBuilders.termQuery(Fields.USER_NAME, userName);
			final BoolQueryBuilder privateFriendsFilter = QueryBuilders.boolQuery().should(privateGroupFilter).should(friendsGroupFilter);
			final BoolQueryBuilder privatePostFilter = QueryBuilders.boolQuery().must(userFilter).must(privateFriendsFilter);
			groupFilter.should(privatePostFilter);

			/*
			 * handle friends posts
			 * the group must be friends and the post owner must added by the loggedin user as friend
			 */
			final Collection<String> friendsOfUser = this.infoLogic.getFriendsForUser(userName);
			if (present(friendsOfUser)) {
				final BoolQueryBuilder friendsFilter = QueryBuilders.boolQuery();
				final Stream<TermQueryBuilder> friendTermQueries = friendsOfUser.stream().map(friend -> QueryBuilders.termQuery(Fields.USER_NAME, friend));
				// at least one friend must be the post owner …
				friendTermQueries.forEach(friendsFilter::should);

				// … when group is set to friends
				final BoolQueryBuilder friendsVisibilityFilter = QueryBuilders.boolQuery().must(friendsGroupFilter).must(friendsFilter);
				groupFilter.should(friendsVisibilityFilter);
			}
		}
		
		mainFilterBuilder.must(groupFilter);
		
		// post owned by user 
		// Use this restriction iff there is no user relation
		if (present(requestedUserName)) {
			final QueryBuilder requestedUserFilter = QueryBuilders.termQuery(Fields.USER_NAME, requestedUserName);
			mainFilterBuilder.must(requestedUserFilter);
		}
		
		this.buildResourceSpecifiyFilters(mainFilterBuilder, userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, year, firstYear, lastYear);
		
		// all done
		log.debug("Search query: '" + mainQueryBuilder.toString() + "' and filters: '" + mainFilterBuilder.toString() + "'");
		return QueryBuilders.boolQuery().must(mainQueryBuilder).filter(mainFilterBuilder);
	}

	private static QueryStringQueryBuilder buildStringQueryForSearchTerms(String searchTerms, final Set<String> fields) {
		final QueryStringQueryBuilder builder = QueryBuilders.queryStringQuery(searchTerms).defaultOperator(Operator.AND).tieBreaker(1f);
		// set the fields where the string query should search for the string
		fields.stream().forEach(builder::field);
		return builder;
	}

	/**
	 * @param usersThatShareDocs
	 * @return
	 */
	private static BoolQueryBuilder buildUserQuery(Set<String> usersThatShareDocs) {
		final BoolQueryBuilder groupFilter = QueryBuilders.boolQuery();
		for (final String user : usersThatShareDocs){
			groupFilter.should(QueryBuilders.termQuery(Fields.USER_NAME, user));
		}
		return groupFilter;
	}

	/**
	 * @param allowedGroups
	 * @return
	 */
	private static BoolQueryBuilder buildGroupFilter(Collection<String> allowedGroups) {
		final BoolQueryBuilder groupFilter = QueryBuilders.boolQuery();
		for (final String allowedGroup : allowedGroups){
			groupFilter.should(QueryBuilders.termQuery(Fields.GROUPS, allowedGroup));
		}
		return groupFilter;
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
	protected void buildResourceSpecifiyFilters(BoolQueryBuilder mainFilterBuilder, String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, String year, String firstYear, String lastYear) {
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
	private static QueryBuilder buildNegatedTags(Collection<String> negatedTags) {
		final BoolQueryBuilder tagFilter = QueryBuilders.boolQuery();
		
		for (final String negatedTag : negatedTags) {
			final QueryBuilder negatedSearchQuery = QueryBuilders.termQuery(Fields.TAGS, negatedTag);
			tagFilter.mustNot(negatedSearchQuery);
		}
		
		return tagFilter;
	}

	/**
	 * @param tagIndex
	 * @return
	 */
	private QueryBuilder buildTagFilter(Collection<String> tagIndex) {
		final BoolQueryBuilder tagsFilter = QueryBuilders.boolQuery();
		for (final String tag : tagIndex) {
			// is the tag string a concept name?
			if (tag.startsWith(Tag.CONCEPT_PREFIX)) {
				final String conceptTag = tag.substring(Tag.CONCEPT_PREFIX.length());
				// get related tags:
				final BoolQueryBuilder conceptTags = QueryBuilders.boolQuery();
				// TODO: must the tag be included? TODODZO
				final QueryBuilder termQuery = QueryBuilders.termQuery(Fields.TAGS, conceptTag);
				conceptTags.should(termQuery);
				for (final String subTagString : this.infoLogic.getSubTagsForConceptTag(conceptTag)) {
					conceptTags.should(QueryBuilders.termQuery(Fields.TAGS, subTagString));
				}
				tagsFilter.must(conceptTags);
			} else {
				tagsFilter.must(QueryBuilders.termQuery(Fields.TAGS, tag));
			}
		}
		return tagsFilter;
	}

	/**
	 * @param requestedGroupName
	 * @return 
	 */
	private QueryBuilder buildGroupMembersFilter(String requestedGroupName) {
		final Collection<String> groupMembers = this.infoLogic.getGroupMembersByGroupName(requestedGroupName);
		if (present(requestedGroupName) && present(groupMembers)) {
			final BoolQueryBuilder groupMemberFilter = QueryBuilders.boolQuery();
			for (final String member : groupMembers) {
				final QueryBuilder memberFilter = QueryBuilders.termQuery(Fields.USER_NAME, member);
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
	public void setManager(ElasticsearchPostManager<R> manager) {
		this.manager = manager;
	}
	
	/**
	 * @param genealogyUser
	 */
	public void setGenealogyUser(String genealogyUser) {
		this.genealogyUser = genealogyUser;
	}
}
