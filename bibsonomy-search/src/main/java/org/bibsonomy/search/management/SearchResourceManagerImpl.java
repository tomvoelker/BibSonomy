package org.bibsonomy.search.management;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.management.exceptions.NoIndexAvaiableException;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexUpdater;

/**
 *
 * @author dzo
 * @param <R> 
 * @param <U> 
 */
public class SearchResourceManagerImpl<R extends Resource> implements SearchResourceManagerInterface<R> {
	private static final Log log = LogFactory.getLog(SearchResourceManagerImpl.class);
	
	/** retrieve only up to this number of posts */
	public static final int SQL_BLOCKSIZE = 5000;
	private static final long QUERY_TIME_OFFSET_MS = 0; // TODO: discuss; maybe remove TODODZO
	
	/** access to the main database with all informations (users, posts, tags) */
	protected final SearchDBInterface<R> searchDBLogic;
	private final List<SearchIndexContainer<R, ?, ?, ?>> containers;
	
	/**
	 * @param searchDBLogic
	 * @param containers
	 * @param generatorExecutorService
	 */
	public SearchResourceManagerImpl(SearchDBInterface<R> searchDBLogic, List<SearchIndexContainer<R, ?, ?, ?>> containers) {
		super();
		this.searchDBLogic = searchDBLogic;
		this.containers = containers;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchResourceManagerInterface#generateIndexForResource(java.lang.Class)
	 */
	@Override
	public void generateIndexForResource(final String containerId, final String indexId) throws ExecutionException {
		final SearchIndexContainer<R, ?, ?, ?> container = this.getContainerById(containerId);
		container.generateIndex(indexId, this.searchDBLogic);
	}

	/**
	 * @param containerId
	 * @return
	 */
	private SearchIndexContainer<R, ?, ?, ?> getContainerById(String containerId) {
		for (final SearchIndexContainer<R, ?, ?, ?> searchIndexContainer : containers) {
			if (searchIndexContainer.getId().equals(containerId)) {
				return searchIndexContainer;
			}
		}
		throw new NoSuchElementException("can't find container with id " + containerId);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchResourceManagerInterface#getInfomationOfIndexForResource(java.lang.Class)
	 */
	@Override
	public List<SearchIndexInfo> getInfomationOfIndexForResource() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchResourceManagerInterface#updateIndex(java.lang.Class)
	 */
	@Override
	public void updateAllIndices() {
		// FIXME: lock this method
		final Map<SearchIndexState, List<IndexLock<R, ?, ?, ?>>> lastLogDateAndLastTasIdToUpdaters = this.getIndicesBySameState();
		try {
			final SearchIndexState targetState = this.searchDBLogic.getDbState();
			
			for (Entry<SearchIndexState, List<IndexLock<R, ?, ?, ?>>> e : lastLogDateAndLastTasIdToUpdaters.entrySet()) {
				final List<IndexLock<R, ?, ?, ?>> indices = e.getValue();
				final SearchIndexState indexState = e.getKey();
				this.updateIndex(indexState, targetState, indices);
			}
		} finally {
			for (final List<SearchIndex<R, ?, ?, ?>> ul : lastLogDateAndLastTasIdToUpdaters.values()) {
				for (SearchIndex<R, ?, ?, ?> u : ul) {
					try {
						// TODO: index unlocking TODODZO
						// u.closeUpdateProcess();
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}
	}
	
	/**
	 * @param indexState
	 * @param targetState
	 * @param indexLocks
	 */
	private void updateIndex(final SearchIndexState oldState, SearchIndexState targetState, List<IndexLock<R, ?, ?, ?>> indexLocks) {
		log.info("updating indices with same state " + oldState + " : " + indexLocks.toString());
		
		final List<SearchIndexUpdater<R>> indexUpdaters = new LinkedList<>();
		for (final IndexLock<R, ?, ?, ?> indexLock : indexLocks) {
			final SearchIndexUpdater<R> searchIndexUpdater = createUpdater(indexLock);
			indexUpdaters.add(searchIndexUpdater);
		}
		
		final int oldLastTasId = oldState.getLast_tas_id().intValue();
		int newLastTasId = oldLastTasId;
		
		/*
		 * 1) flag/unflag spammer if the index existed before
		 */
		if (oldState.getLast_log_date() != null) {
			this.updatePredictions(indexUpdaters, oldState.getLast_log_date());
		}
		
		/*
		 * 2) remove old deleted or updated posts
		 */
		if (oldState.getLast_log_date() != null) {
			final List<Integer> contentIdsToDelete = this.searchDBLogic.getContentIdsToDelete(new Date(oldState.getLast_log_date().getTime() - QUERY_TIME_OFFSET_MS));
			for (final SearchIndexUpdater<R> updater : indexUpdaters) {
				for (final Integer contentId : contentIdsToDelete) {
					updater.deletePostWithContentId(contentId.intValue());
				}
			}
		}

		/*
		 * 3) add new and updated posts to the index
		 * FIXME: use steps TODODZO
		 */
		final List<SearchPost<R>> newPosts = this.searchDBLogic.getNewPosts(oldLastTasId);
		final int totalCountNewPosts = newPosts.size();
		
		for (final SearchIndexUpdater<R> indexUpdater : indexUpdaters) {
			log.debug("inserting new/updated posts into " + indexUpdater);
			for (final SearchPost<R> post : newPosts) {
				// just in case there is already a post with this id
				indexUpdater.deletePostWithContentId(post.getContentId().intValue());
				indexUpdater.insertPost(post);
				newLastTasId = Math.max(post.getLastTasId().intValue(), newLastTasId);
			}
			log.debug("inserted new/updated posts into " + indexUpdater);
		}
		log.debug("inserted " + totalCountNewPosts + " new/updated posts");
		
		/*
		 * 4) update the 
		 */
		for (SearchIndexUpdater<R> updater : indexUpdaters) {
			try {
				SearchIndexState newState = new SearchIndexState(oldState);
				newState.setLast_log_date(targetState.getLast_log_date());
				newState.setLast_tas_id(Integer.valueOf(newLastTasId));
				newState.setLastPersonChangeId(targetState.getLastPersonChangeId());
				updater.updateIndexState(newState);
			} catch (final RuntimeException e) {
				updater.updateIndexState(oldState);
				throw e;
			} catch (final Exception e) {
				updater.updateIndexState(oldState);
				throw new RuntimeException(e);
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("publications updated for " + indexUpdaters.toString());
		}
		
		this.updateResourceSpecificProperties(oldState, targetState, indexUpdaters);
	}

	/**
	 * @param index
	 * @return
	 */
	private <T, I extends SearchIndex<R, T, I, M>, M> SearchIndexUpdater<R> createUpdater(IndexLock<R, T, I, M> indexLock) {
		final SearchIndexContainer<R, T, I, M> container = indexLock.getSearchIndex().getContainer();
		return container.createUpdaterForIndex(indexLock);
	}

	/**
	 * @param oldState
	 * @param targetState
	 * @param indexUpdaters
	 */
	protected void updateResourceSpecificProperties(final SearchIndexState oldState, SearchIndexState targetState, final List<SearchIndexUpdater<R>> indexUpdaters) {
		// noop
	}
	
	/**
	 * spam handling get spam prediction which were missed since last index
	 * update
	 * 
	 * FIXME: this code is due to the old spam-flagging-mechanism it is probably
	 * more efficient to get all un-flagged-posts directly via a join with the
	 * user table
	 * @param updaters 
	 * @param lastLogDate 
	 */
	protected void updatePredictions(List<SearchIndexUpdater<R>> updaters, final Date lastLogDate) {
		// keeps track of the newest log_date during last index update
		// final long lastLogDate = lastLogDate - QUERY_TIME_OFFSET_MS;
		// get date of last index update
		final Date fromDate = new Date(lastLogDate.getTime() - QUERY_TIME_OFFSET_MS);

		final List<User> predictedUsers = this.searchDBLogic.getPredictionForTimeRange(fromDate);

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
					final List<SearchPost<R>> userPosts = this.searchDBLogic.getPostsForUser(userName, Integer.MAX_VALUE, 0);
					
					// insert new records into index
					if (present(userPosts)) {
						for (final SearchPost<R> post : userPosts) {
							for (final SearchIndexUpdater<R> updater : updaters) {
								updater.deletePostWithContentId(post.getContentId().intValue());
								updater.insertPost(post); // FIXME: do we need the last log date?
							}
							// TODO: why? TODODZO: check
							// updater.unFlagUser(userName);
						}
					}
					break;
				case 1:
					log.debug("flag spammer");
					// remove all docs of the user from the index!
					for (SearchIndexUpdater<R> updater : updaters) {
						updater.removeAllPostsOfUser(userName);
					}
					break;
				}
			}
		}
	}

	private Map<SearchIndexState, List<IndexLock<R, ?, ?, ?>>> getIndicesBySameState() {
		final Map<SearchIndexState, List<IndexLock<R, ?, ?, ?>>> lastLogDateAndLastTasIdToUpdaters = new HashMap<>();
		
		for (final SearchIndexContainer<R, ?, ?, ?> container : this.containers) {
			try {
				// TODO: throw exception if no index is available for update
				
				@SuppressWarnings("resource") // we close the lock later and not now TODO: fix?
				final IndexLock<R, ?, ?, ?> indexToUpdateLock = container.acquireWriteLockForIndexToUpdate();
				final SearchIndexState indexUpdaterState = this.getIndexUpdaterStateForContainer(indexToUpdateLock);
				
				List<IndexLock<R, ?, ?, ?>> indicesWithSameState = lastLogDateAndLastTasIdToUpdaters.get(indexUpdaterState);
				if (indicesWithSameState == null) {
					indicesWithSameState = new ArrayList<>();
					lastLogDateAndLastTasIdToUpdaters.put(indexUpdaterState, indicesWithSameState);
				}
				indicesWithSameState.add(indexToUpdateLock);
			} catch (NoIndexAvaiableException e) {
				log.warn("no index for update for container " + container.getId());
			}
		}
		return lastLogDateAndLastTasIdToUpdaters;
	}

	/**
	 * @param container
	 * @param searchIndex
	 * @return
	 */
	private <T, I extends SearchIndex<R, T, I, M>, M> SearchIndexState getIndexUpdaterStateForContainer(IndexLock<R, T, I, M> indexLock) {
		final SearchIndex<R, T, I, M> searchIndex = indexLock.getSearchIndex();
		return searchIndex.getContainer().getUpdaterStateForIndex((I) searchIndex);
	}
	
	/**
	 * shutdown search containers
	 */
	public void shutdown() {
		for (SearchIndexContainer<R, ?, ?, ?> searchIndexContainer : containers) {
			try {
				searchIndexContainer.shutdown();
			} catch (Exception e) {
				log.error("error while shuting down container " + searchIndexContainer, e);
			}
		}
	}
}
