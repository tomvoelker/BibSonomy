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
package org.bibsonomy.lucene.util.generator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.es.IndexUpdaterState;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * reads data from database, feeds it to an abstract index creation method and keeps track of the progress
 * 
 * @author sst
 * @author fei
 * @author jil
 * 
 * @param <R> the resource of the index to generate
 */
public abstract class AbstractIndexGenerator<R extends Resource> implements Runnable {

	/** suffix for temporary indices */
	public static final String TMP_INDEX_SUFFIX = ".tmp";

	private static final Log log = LogFactory.getLog(AbstractIndexGenerator.class);

	/** the number of posts to fetch from the database by a single generating step */
	protected static final int SQL_BLOCKSIZE = 25000;

	/** database logic */
	protected LuceneDBInterface<R> dbLogic;

	/** set to true if the generator is currently generating an index */
	protected boolean isRunning;

	protected int numberOfPosts;
	private int numberOfPostsImported;
	private boolean running = false;
	
	private GenerateIndexCallback<R> callback = null;

	/**
	 * frees allocated resources and closes all files
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public void shutdown() throws CorruptIndexException, IOException {
		if (this.callback != null) {
			this.callback.generatedIndex(this);
		}
	}

	/**
	 * Read in data from database and build index.
	 * 
	 * Database as well as index files are configured in the lucene.properties
	 * file.
	 * @throws Exception 
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void generateIndex() throws Exception {
		// Allow only one index-generation at a time.
		if (this.isRunning) {
			return;
		}

		this.isRunning = true;
		try {
			this.createEmptyIndex();
			this.createIndexFromDatabase();
			this.activateIndex();
		} finally {
			this.isRunning = false;
		}
	}

	protected abstract void activateIndex();


	/**
	 * Create empty index. Attributes must already be configured (via init()).
	 * @throws Exception 
	 * 
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	protected abstract void createEmptyIndex() throws Exception;

	/**
	 * creates index of resource entries
	 * @throws Exception 
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected void createIndexFromDatabase() throws Exception {
		log.info("Filling index with database post entries.");

		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfPosts = this.dbLogic.getNumberOfPosts();
		log.info("Number of post entries: " + this.numberOfPosts);

		// initialize variables
		final IndexUpdaterState newState = this.dbLogic.getDbState();

		if (newState.getLast_log_date() == null) {
			newState.setLast_log_date(new Date(System.currentTimeMillis() - 1000));
		}
		
		writeMetaInfo(newState);
		

		log.info("Start writing data to lucene index (with duplicate detection)");

		// read block wise all posts
		List<LucenePost<R>> postList = null;
		int skip = 0;
		int lastContenId = -1;
		int postListSize = 0;
		do {
			postList = this.dbLogic.getPostEntries(lastContenId, SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final LucenePost<R> post : postList) {
				post.setLastLogDate(newState.getLast_log_date());
				if (post.getLastTasId() == null) {
					post.setLastTasId(newState.getLast_tas_id());
				} else {
					if (post.getLastTasId() < post.getLastTasId()) {
						post.setLastTasId(post.getLastTasId());
					}
				}

				if (AbstractIndexGenerator.this.isNotSpammer(post)) {
					addPostToIndex(post);
				}

			}

			if (postListSize > 0) {
				lastContenId = postList.get(postListSize - 1).getContentId();
			}
		} while (postListSize == SQL_BLOCKSIZE);

		// all done
		// log.info("(" + i + " indexed entries, " + is +
		// " not indexed spam entries)");
	}

	/**
	 * @param lastTasId
	 * @param lastLogDate
	 * @throws IOException 
	 */
	protected abstract void writeMetaInfo(IndexUpdaterState state) throws IOException;

	protected abstract void addPostToIndex(final LucenePost<R> post);
	
	/**
	 * @param post the post which this object is to be informed about
	 */
	protected synchronized void importedPost(final LucenePost<R> post) {
		// update counter
		this.numberOfPostsImported++;
	}

	/**
	 * test if given post is a spam post
	 * 
	 * @param post
	 * @return <code>true</code> iff the post user is a spammer
	 */
	protected boolean isNotSpammer(final Post<? extends Resource> post) {
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
	 * Get the progress-percentage
	 * 
	 * @return the progressPercentage
	 */
	public int getProgressPercentage() {
		return (int) Math.round(100 * ((double) this.numberOfPostsImported / this.numberOfPosts));
	}

	/** Run the index-generation in a thread. */
	@Override
	public void run() {
		try {
			this.running = true;
			this.generateIndex();
		} catch (final Exception e) {
			log.error("Failed to generate " + getName() + "!", e);
		} finally {
			try {
				this.shutdown();
			} catch (final Exception e) {
				log.error("Failed to close index-writer!", e);
			}
		}
	}

	/**
	 * @return a name reflecting the particular index and resourcetype combination
	 */
	protected abstract String getName();
	
	

	/**
	 * @param dbLogic
	 *            the dbLogic to set
	 */
	public void setLogic(final LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}
	
	/**
	 * @param callback
	 *            the callback to set
	 */
	public void setCallback(final GenerateIndexCallback<R> callback) {
		this.callback = callback;
	}

	public boolean isRunning() {
		return this.running;
	}
}
