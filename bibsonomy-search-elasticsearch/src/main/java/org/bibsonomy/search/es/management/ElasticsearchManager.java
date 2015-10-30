package org.bibsonomy.search.es.management;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.exceptions.IndexAlreadyGeneratingException;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.model.SearchIndexStatistics;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.util.Sets;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * TODO: extract interface
 * manager for elastic search
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchManager<R extends Resource> {
	private static final Log log = LogFactory.getLog(ElasticsearchManager.class);
	
	/** how many posts should be retrieved from the database */
	public static final int SQL_BLOCKSIZE = 5000;
	private static final long QUERY_TIME_OFFSET_MS = 0; // TODO: remove?
	
	/**
	 * task for index generation
	 * @author dzo
	 */
	private final class ElasticSearchIndexGenerationTask implements Callable<Void> {
		private final ElasticsearchIndexGenerator<R> generator;
		private final ElasticsearchIndex<R> newIndex;

		/**
		 * @param generator
		 * @param newIndex
		 */
		private ElasticSearchIndexGenerationTask(ElasticsearchIndexGenerator<R> generator, ElasticsearchIndex<R> newIndex) {
			this.generator = generator;
			this.newIndex = newIndex;
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Void call() throws Exception {
			try {
				ElasticsearchManager.this.currentGenerator = this.generator;
				generator.generateIndex();
				ElasticsearchManager.this.activateNewIndex(this.newIndex);
			} catch (final Exception e) {
				log.error("error while generating index", e);
			} finally {
				ElasticsearchManager.this.currentGenerator = null;
				ElasticsearchManager.this.generatorLock.release();
			}
			
			return null;
		}
	}
	
	private boolean updateEnabled;
	
	protected final ESClient client;
	
	private final URI systemURI;
	
	private final Semaphore updateLock = new Semaphore(1);
	
	private final Semaphore generatorLock = new Semaphore(1);
	private ElasticsearchIndexGenerator<R> currentGenerator;
	private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	
	/** access to the main database */
	protected final SearchDBInterface<R> inputLogic;
	
	/** mappers, converters, */
	protected final ElasticsearchIndexTools<R> tools;
	
	/**
	 * @param updateEnabled 
	 * @param client
	 * @param systemURI
	 * @param inputLogic
	 * @param tools
	 */
	public ElasticsearchManager(final boolean updateEnabled, ESClient client, URI systemURI, SearchDBInterface<R> inputLogic, ElasticsearchIndexTools<R> tools) {
		super();
		this.updateEnabled = updateEnabled;
		this.client = client;
		this.systemURI = systemURI;
		this.inputLogic = inputLogic;
		this.tools = tools;
	}

	/**
	 * generates a new index for the resource
	 * @throws IndexAlreadyGeneratingException
	 */
	public void generateIndex() throws IndexAlreadyGeneratingException {
		generateIndex(true);
	}

	/**
	 * @param async 
	 * @throws IndexAlreadyGeneratingException
	 */
	protected void generateIndex(final boolean async) throws IndexAlreadyGeneratingException {
		if (!this.generatorLock.tryAcquire()) {
			throw new IndexAlreadyGeneratingException();
		}
		final String newIndexName = ElasticsearchUtils.getIndexNameWithTime(this.systemURI, this.tools.getResourceType());
		final ElasticsearchIndex<R> newIndex = new ElasticsearchIndex<>(newIndexName);
		
		final ElasticsearchIndexGenerator<R> generator = new ElasticsearchIndexGenerator<>(newIndex, this.inputLogic, this.client, this.tools);
		
		final ElasticSearchIndexGenerationTask task = new ElasticSearchIndexGenerationTask(generator, newIndex);
		if (async) {
			this.executorService.submit(task);
		} else {
			try {
				task.call();
			} catch (Exception e) {
				log.error("error while running synchronous generation task.", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @param newIndex
	 */
	protected void activateNewIndex(ElasticsearchIndex<R> newIndex) {
		try {
			this.updateLock.acquire();
			
			final String localActiveAlias = getActiveLocalAlias();
			final String localInactiveAlias = getInactiveLocalAlias();
			final String activeIndexName = this.client.getIndexNameForAlias(localActiveAlias);
			final String inactiveIndexName = this.client.getIndexNameForAlias(localInactiveAlias);
			
			final Set<Pair<String, String>> aliasesToAdd = new HashSet<>();
			aliasesToAdd.add(new Pair<>(newIndex.getIndexName(), localActiveAlias));
			if (present(activeIndexName)) {
				aliasesToAdd.add(new Pair<>(activeIndexName, localInactiveAlias));
			}
			
			final Set<Pair<String, String>> aliasesToRemove = new HashSet<>();
			if (present(activeIndexName)) {
				aliasesToRemove.add(new Pair<>(activeIndexName, localActiveAlias));
			}
			if (present(inactiveIndexName)) {
				aliasesToRemove.add(new Pair<>(inactiveIndexName, localInactiveAlias));
			}
			
			this.client.updateAliases(aliasesToAdd, aliasesToRemove);
			
			if (present(inactiveIndexName)) {
				this.client.deleteIndex(inactiveIndexName);
			}
		} catch (InterruptedException e) {
			log.error("can't acquire lock to update aliases", e);
		} finally {
			this.updateLock.release();
		}
	}

	/**
	 * @return informations about the indices managed by this manager
	 */
	public List<SearchIndexInfo> getIndexInformations() {
		final List<SearchIndexInfo> infos = new LinkedList<>();
		try {
			final String localActiveAlias = this.getActiveLocalAlias();
			final SearchIndexInfo searchIndexInfo = getIndexInfoForIndex(localActiveAlias, SearchIndexState.ACTIVE, true);
			infos.add(searchIndexInfo);
		} catch (final IndexNotFoundException e) {
			// ignore
		}
		
		try {
			final String localInactiveAlias = this.getInactiveLocalAlias();
			final SearchIndexInfo searchIndexInfoInactive = getIndexInfoForIndex(localInactiveAlias, SearchIndexState.INACTIVE, true);
			infos.add(searchIndexInfoInactive);
		} catch (final IndexNotFoundException e) {
			// ignore
		}
		
		if (this.currentGenerator != null) {
			try {
				final String indexName = this.currentGenerator.getIndex().getIndexName();
				final SearchIndexInfo searchIndexInfoGeneratingIndex = getIndexInfoForIndex(indexName, SearchIndexState.GENERATING, false);
				searchIndexInfoGeneratingIndex.setIndexGenerationProgress(this.currentGenerator.getProgress());
				searchIndexInfoGeneratingIndex.setId(indexName);
				infos.add(searchIndexInfoGeneratingIndex);
			} catch (final IndexNotFoundException e) {
				// ignore
			}
		}
		
		// TODO: include indices that could not be generated (dead bodies)
		
		return infos;
	}

	/**
	 * @param indexName
	 * @param state 
	 * @param loadSyncState 
	 * @return
	 */
	private SearchIndexInfo getIndexInfoForIndex(final String indexName, SearchIndexState state, boolean loadSyncState) {
		final SearchIndexInfo searchIndexInfo = new SearchIndexInfo();
		searchIndexInfo.setState(state);
		searchIndexInfo.setId(this.client.getIndexNameForAlias(indexName));
		
		if (loadSyncState) {
			searchIndexInfo.setSyncState(this.client.getSearchIndexStateForIndex(indexName));
		}
		
		final SearchIndexStatistics statistics = new SearchIndexStatistics();
		final long count = this.client.getDocumentCount(indexName, this.tools.getResourceTypeAsString(), null);
		statistics.setNumberOfDocuments(count);
		searchIndexInfo.setStatistics(statistics);
		return searchIndexInfo;
	}

	/**
	 * update the inactive index
	 */
	public void updateIndex() {
		if (!this.updateEnabled) {
			log.debug("skipping updating index, update disabled");
			return;
		}
		
		if (!this.updateLock.tryAcquire()) {
			log.warn("Another update in progress. Skipping update.");
		}
		
		try {
			final String localInactiveAlias = this.getInactiveLocalAlias();
			final SearchIndexSyncState oldState = this.client.getSearchIndexStateForIndex(localInactiveAlias);
			final SearchIndexSyncState targetState = this.inputLogic.getDbState();
			
			final int oldLastTasId = oldState.getLast_tas_id().intValue();
			int newLastTasId = oldLastTasId;
			
			/*
			 * 1) flag/unflag spammer if the index existed before
			 */
			if (oldState.getLast_log_date() != null) {
				this.updatePredictions(localInactiveAlias, oldState.getLast_log_date());
			}
			
			/*
			 * 2) remove old deleted or updated posts
			 */
			if (oldState.getLast_log_date() != null) {
				final List<Integer> contentIdsToDelete = this.inputLogic.getContentIdsToDelete(new Date(oldState.getLast_log_date().getTime() - QUERY_TIME_OFFSET_MS));
				
				for (final Integer contentId : contentIdsToDelete) {
					this.deletePostWithContentId(localInactiveAlias, contentId.intValue());
				}
			}
	
			/*
			 * 3) add new and updated posts to the index
			 * FIXME: use steps TODODZO
			 */
			final List<SearchPost<R>> newPosts = this.inputLogic.getNewPosts(oldLastTasId);
			final int totalCountNewPosts = newPosts.size();
			
			log.debug("inserting new/updated posts into " + localInactiveAlias);
			for (final SearchPost<R> post : newPosts) {
				// just in case there is already a post with this id
				this.deletePostWithContentId(localInactiveAlias, post.getContentId().intValue());
				this.insertPost(localInactiveAlias, post);
				newLastTasId = Math.max(post.getLastTasId().intValue(), newLastTasId);
			}
			
			log.debug("inserted " + totalCountNewPosts + " new/updated posts");
			
			this.updateResourceSpecificProperties(localInactiveAlias, oldState, targetState);
			
			// 4) update the index state
			try {
				SearchIndexSyncState newState = new SearchIndexSyncState(oldState);
				newState.setLast_log_date(targetState.getLast_log_date());
				newState.setLast_tas_id(Integer.valueOf(newLastTasId));
				newState.setLastPersonChangeId(targetState.getLastPersonChangeId());
				this.updateIndexState(localInactiveAlias, newState);
			} catch (final RuntimeException e) {
				this.updateIndexState(localInactiveAlias, oldState);
				throw e;
			} catch (final Exception e) {
				this.updateIndexState(localInactiveAlias, oldState);
				throw new RuntimeException(e);
			}
			
			if (log.isDebugEnabled()) {
				log.debug("posts updated for " + localInactiveAlias);
			}
			
			this.switchActiveAndInactiveIndex();
		} catch (final IndexNotFoundException e) {
			log.error("Can't update " + this.tools.getResourceTypeAsString() + " index. No inactive index available.");
		} finally {
			this.updateLock.release();
		}
	}
	
	/**
	 * switch active and inactive index
	 */
	private void switchActiveAndInactiveIndex() {
		final String localActiveAlias = this.getActiveLocalAlias();
		final String localInactiveAlias = this.getInactiveLocalAlias();
		final String activeIndexName = this.client.getIndexNameForAlias(localActiveAlias);
		final String inactiveIndexName = this.client.getIndexNameForAlias(localInactiveAlias);
		
		@SuppressWarnings("unchecked")
		final Set<Pair<String, String>> aliasesToAdd = Sets.asSet(
			new Pair<>(inactiveIndexName, localActiveAlias),
			new Pair<>(activeIndexName, localInactiveAlias)
		);
		@SuppressWarnings("unchecked")
		final Set<Pair<String, String>> aliasesToRemove = Sets.asSet(
			new Pair<>(activeIndexName, localActiveAlias),
			new Pair<>(inactiveIndexName, localInactiveAlias)
		);
		
		// update the aliases (atomic, see elastic search docu)
		this.client.updateAliases(aliasesToAdd, aliasesToRemove);
	}
	
	/**
	 * @return
	 */
	private String getInactiveLocalAlias() {
		return ElasticsearchUtils.getLocalAliasForResource(this.tools.getResourceType(), this.systemURI, false);
	}

	/**
	 * @return
	 */
	private String getActiveLocalAlias() {
		return ElasticsearchUtils.getLocalAliasForResource(this.tools.getResourceType(), this.systemURI, true);
	}

	/**
	 * @param indexName
	 * @param oldState
	 */
	private void updateIndexState(String indexName, SearchIndexSyncState state) {
		final Map<String, Object> values = ElasticsearchUtils.serializeSearchIndexState(state);
		
		this.client.insertNewDocument(indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, ESConstants.SYSTEM_INFO_INDEX_TYPE, values);
	}

	/**
	 * @param post
	 */
	private void insertPost(final String indexName, SearchPost<R> post) {
		final Map<String, Object> convertedPost = this.tools.getConverter().convert(post);
		final String id = ElasticsearchUtils.createElasticSearchId(post.getContentId().intValue());
		this.client.insertNewDocument(indexName, this.tools.getResourceTypeAsString(), id, convertedPost);
	}

	/**
	 * @param intValue
	 */
	private void deletePostWithContentId(final String indexName, int contentId) {
		final String indexID = ElasticsearchUtils.createElasticSearchId(contentId);
		
		this.client.removeDocumentFromIndex(indexName, this.tools.getResourceTypeAsString(), indexID);
	}

	/**
	 * @param oldState
	 * @param targetState
	 * @param indexName
	 */
	protected void updateResourceSpecificProperties(final String indexName, final SearchIndexSyncState oldState, SearchIndexSyncState targetState) {
		// noop
	}
	
	/**
	 * spam handling get spam prediction which were missed since last index
	 * update
	 * 
	 * FIXME: this code is due to the old spam-flagging-mechanism it is probably
	 * more efficient to get all un-flagged-posts directly via a join with the
	 * user table
	 * @param indexName 
	 * @param lastLogDate 
	 */
	protected void updatePredictions(final String indexName, final Date lastLogDate) {
		// keeps track of the newest log_date during last index update
		// final long lastLogDate = lastLogDate - QUERY_TIME_OFFSET_MS;
		// get date of last index update
		final Date fromDate = new Date(lastLogDate.getTime() - QUERY_TIME_OFFSET_MS);

		final List<User> predictedUsers = this.inputLogic.getPredictionForTimeRange(fromDate);

		// the prediction table holds up to two entries per user
		// - the first entry is the one to consider (ordered descending by date)
		// we keep track of users which appear twice via this set
		final Set<String> alreadyUpdated = new HashSet<String>();
		for (final User user : predictedUsers) {
			final String userName = user.getName();
			final boolean unknowUser = alreadyUpdated.add(userName);
			if (unknowUser) {
				/*
				 * flag/unflag spammer, depending on user.getPrediction()
				 */
				log.debug("updating spammer status for user " + userName);
				switch (user.getPrediction().intValue()) {
				case 0:
					log.debug("unflag non-spammer");
					// FIXME: use batch size TODODZO
					final List<SearchPost<R>> userPosts = this.inputLogic.getPostsForUser(userName, Integer.MAX_VALUE, 0);
					
					// insert new records into index
					if (present(userPosts)) {
						for (final SearchPost<R> post : userPosts) {
							
							this.deletePostWithContentId(indexName, post.getContentId().intValue());
							this.insertPost(indexName, post); // FIXME: do we need the last log date?
							// TODO: why? TODODZO: check
							// updater.unFlagUser(userName);
						}
					}
					break;
				case 1:
					log.debug("flag spammer");
					// remove all docs of the user from the index!
					this.removeAllPostsOfUser(indexName, userName);
					break;
				}
			}
		}
	}
	
	/**
	 * @param indexName
	 * @param userName
	 */
	private void removeAllPostsOfUser(String indexName, String userName) {
		this.client.deleteDocuments(indexName, this.tools.getResourceTypeAsString(), QueryBuilders.termQuery(Fields.USER_NAME, userName));
	}
	
	/**
	 * @return 
	 * 
	 */
	public CountRequestBuilder prepareCount() {
		return this.client.prepareCount(this.getActiveIndexName());
	}
	
	/**
	 * @return
	 */
	public SearchRequestBuilder prepareSearch() {
		return prepareSearch(this.getActiveIndexName());
	}

	/**
	 * @return
	 */
	private String getActiveIndexName() {
		return ElasticsearchUtils.getLocalAliasForResource(this.tools.getResourceType(), systemURI, true);
	}

	/**
	 * @param indexName
	 * @return
	 */
	protected SearchRequestBuilder prepareSearch(final String indexName) {
		return this.client.prepareSearch(indexName);
	}
	
	public void shutdown() {
		this.executorService.shutdownNow();
	}
}
