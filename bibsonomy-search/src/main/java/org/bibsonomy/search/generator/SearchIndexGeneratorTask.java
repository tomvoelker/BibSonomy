package org.bibsonomy.search.generator;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.management.IndexLock;
import org.bibsonomy.search.management.SearchIndex;
import org.bibsonomy.search.management.SearchResourceManagerImpl;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexState;

/**
 * abstract task to generate a {@link SearchIndex}
 * 
 * @author sst
 * @author fei
 * @author jil
 * @author dzo
 * 
 * @param <R> 
 * @param <I> 
 */
public abstract class SearchIndexGeneratorTask<R extends Resource, I extends SearchIndex<R, ?, I, ?>> implements Callable<Void> {
	private static final Log log = LogFactory.getLog(SearchIndexGeneratorTask.class);
	
	private final SearchDBInterface<R> inputLogic;
	private final IndexLock<R, ?, I, ?> indexLock;
	
	private boolean running = false;
	private boolean finishedSuccessfully = false;
	private int numberOfPosts = 0;
	private int writtenPosts = 0;

	/**
	 * @param inputLogic
	 * @param indexLock
	 */
	public SearchIndexGeneratorTask(SearchDBInterface<R> inputLogic, final IndexLock<R, ?, I, ?> indexLock) {
		this.inputLogic = inputLogic;
		this.indexLock = indexLock;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {
		this.running = true;
		this.generateIndex();
		this.finishedSuccessfully = true;
		this.running = false;
		return null;
	}
	
	private void generateIndex() throws Exception {
		this.createEmptyIndex();
		this.createIndexFromDatabase();
	}

	/**
	 * generates the index from the database
	 */
	private void createIndexFromDatabase() {
		log.info("Filling index with database post entries.");

		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfPosts = this.inputLogic.getNumberOfPosts();
		log.info("Number of post entries: " + this.numberOfPosts);

		// initialize variables
		final SearchIndexState newState = this.inputLogic.getDbState();

		if (newState.getLast_log_date() == null) {
			newState.setLast_log_date(new Date(System.currentTimeMillis() - 1000));
		}
		
		log.info("Start writing posts to index");
		
		// read block wise all posts
		List<SearchPost<R>> postList = null;
		int skip = 0;
		int lastContenId = -1;
		int postListSize = 0;
		do {
			postList = this.inputLogic.getPostEntries(lastContenId, SearchResourceManagerImpl.SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final SearchPost<R> post : postList) {
				post.setLastLogDate(newState.getLast_log_date());
				if (post.getLastTasId() == null) {
					post.setLastTasId(newState.getLast_tas_id());
				} else {
					if (post.getLastTasId().intValue() < post.getLastTasId().intValue()) {
						post.setLastTasId(post.getLastTasId());
					}
				}

				if (isNotSpammer(post)) {
					addPostToIndex(post);
					this.writtenPosts++;
				}
			}

			if (postListSize > 0) {
				lastContenId = postList.get(postListSize - 1).getContentId().intValue();
			}
		} while (postListSize == SearchResourceManagerImpl.SQL_BLOCKSIZE);
		
		writeMetaInfo(newState);
	}
	
	public double getProgress() {
		return this.writtenPosts / (double) this.numberOfPosts;
	}
	
	/**
	 * @param newState
	 */
	protected abstract void writeMetaInfo(SearchIndexState newState);

	/**
	 * @param post
	 */
	protected abstract void addPostToIndex(SearchPost<R> post);

	private static boolean isNotSpammer(final Post<? extends Resource> post) {
		for (final Group group : post.getGroups()) {
			if (group.getGroupId() < 0) {
				/*
				 * spammer group found => user is spammer
				 */
				return false;
			}
		}
		return true;
	}

	/**
	 * create an empty index before we fill the index
	 * @throws IOException TODO
	 */
	protected abstract void createEmptyIndex() throws IOException;

	/**
	 * @return the finishedSuccessfully
	 */
	public boolean isFinishedSuccessfully() {
		return this.finishedSuccessfully;
	}

	/**
	 * @return the searchIndex
	 */
	public IndexLock<R, ?, I, ?> getSearchIndexLock() {
		return this.indexLock;
	}
}
