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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.util.Version;
import org.bibsonomy.es.IndexType;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Resource;

/**
 * reads data from database and builds lucene index for all resource entries
 * 
 * @author sst
 * @author fei
 * 
 * @param <R>
 *            the resource of the index to generate
 */
public class LuceneGenerateResourceIndex<R extends Resource> extends AbstractIndexGenerator<R> {

	private static final Log log = LogFactory.getLog(LuceneGenerateResourceIndex.class);

	/** writes the resource index */
	private IndexWriter indexWriter;

	/** converts post model objects to lucene documents */
	protected LuceneResourceConverter<R> resourceConverter;

	private LuceneResourceIndex<R> resourceIndex;

	
	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#getName()
	 */
	@Override
	protected String getName() {
		return this.resourceIndex + "-index";
	}
	
	/**
	 * frees allocated resources and closes all files
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	@Override
	public void shutdown() throws CorruptIndexException, IOException {
		if (indexWriter != null) {
			this.indexWriter.close();
		}
		super.shutdown();
	}
	
	/**
	 * deletes the old index and replaces it with the new one
	 */
	@Override
	public void activateIndex() {
		try {
			
			// close resource indexWriter
			log.info("closing index " + this.resourceIndex);
			this.indexWriter.close();

			// all done
			// log.info("(" + i + " indexed entries, " + is +
			// " not indexed spam entries)");
			
			this.resourceIndex.close();
			
			final File indexPath = new File(this.resourceIndex.getIndexPath());
			final File tmpIndexPath = new File(this.resourceIndex.getIndexPath() + TMP_INDEX_SUFFIX);
			// If there was no index directory create one
			if (!indexPath.exists()) {
				indexPath.mkdirs();
			}
			final Directory indexDirectory = FSDirectory.open(indexPath);
			final Directory tmpIndexDirectory = FSDirectory.open(tmpIndexPath);

			log.info("Deleting index " + indexPath.getAbsolutePath() + "...");
			for (final String filename : indexDirectory.listAll()) {
				indexDirectory.deleteFile(filename);
				log.debug("Deleted " + filename);
			}
			log.info("Success.");
			log.info("Coping new index files from " + tmpIndexPath.getName());
			boolean movedIndexSuccessful = true;
			for (final String filename : tmpIndexDirectory.listAll()) {
				final File file = new File(tmpIndexPath.getAbsolutePath() + "/" + filename);
				// Move file to new directory
				log.debug("Move " + filename + " to " + indexPath.getName());
				final File file2 = new File(indexPath, file.getName());
				final boolean success = file.renameTo(file2);
				if (!success) {
					movedIndexSuccessful = false;
					log.error(filename + " was not successfully moved");
				}
			}
			/*
			 * If the new regenerated index files were successfully moved to the
			 * index directory remove the temporary index directory
			 */
			if (movedIndexSuccessful) {
				final boolean success = (new File(tmpIndexPath.getAbsolutePath())).delete();
				if (success) {
					log.info("Temporary index directory successfully deleted.");
				}
			}
		} catch (final NoSuchDirectoryException e) {
			log.warn("Tried to delete the lucene-index-directory but it could not be found.", e);
		} catch (final IOException e) {
			log.error("Could not delete directory-content before index-generation or index-copy.", e);
		}
		
		// activate the index
		this.resourceIndex.reset();
	}

	/**
	 * Create empty index. Attributes must already be configured (via init()).
	 * 
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	@Override
	protected void createEmptyIndex() throws Exception {
		// create index, possibly overwriting existing index files
		log.info("Creating empty lucene index...");
		final Directory indexDirectory = FSDirectory.open(new File(this.resourceIndex.getIndexPath() + TMP_INDEX_SUFFIX));
		final IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_48, this.resourceIndex.getAnalyzer());
		iwc.setOpenMode(OpenMode.CREATE);
		this.indexWriter = new IndexWriter(indexDirectory, iwc);
	}

	@Override
	protected void addPostToIndex(final LucenePost<R> post) {
		// create index document from post model
		final Document doc = (Document) this.resourceConverter.readPost(post, IndexType.LUCENE);
		try {
			this.indexWriter.addDocument(doc);
			this.importedPost(post);
		} catch (final IOException e) {
			log.error("error while inserting post " + post.getUser().getName() + "/" + post.getResource().getIntraHash(), e);
		}
	}


	/**
	 * creates index of resource entries and adds FolkRanks to posts
	 * (experimental purpose)
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected void createIndexFromDatabaseWithFolkRanks() throws CorruptIndexException, IOException {
		log.info("Filling index with database post entries.");
		// TODO: reenable multi threaded index generation
		// TODO: don't load all posts into RAM

		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfPosts = this.dbLogic.getNumberOfPosts();
		log.info("Number of post entries: " + this.numberOfPosts);

		// initialize variables
		//final Integer lastTasId = this.dbLogic.getLastTasId();
		Date lastLogDate = this.dbLogic.getLastLogDate();

		if (lastLogDate == null) {
			lastLogDate = new Date(System.currentTimeMillis() - 1000);
		}

		log.info("Start writing data to lucene index (with duplicate detection)");

		// read block wise all posts
		List<LucenePost<R>> postList = null;
		int skip = 0;
		// int lastContenId = -1;
		int lastOffset = 0;
		int postListSize = 0;
		do {
			postList = this.dbLogic.getPostEntriesOrderedByHash(lastOffset, SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final LucenePost<R> post : postList) {
				post.setLastLogDate(lastLogDate);
				//post.setLastTasId(lastTasId);
				// executor.execute(new Runnable() {

				// FIXME had to remove Thread creation because reading FolkRank
				// values is not thread safe.
				// @Override
				// public void run() {
				if (LuceneGenerateResourceIndex.this.isNotSpammer(post)) {
					// create index document from post model
					final Document doc = (Document) LuceneGenerateResourceIndex.this.resourceConverter.readPost(post, IndexType.LUCENE);

					try {
						LuceneGenerateResourceIndex.this.indexWriter.addDocument(doc);
						LuceneGenerateResourceIndex.this.importedPost(post);
					} catch (final IOException e) {
						log.error("error while inserting post " + post.getUser().getName() + "/" + post.getResource().getIntraHash(), e);
					}
				}
				// }
				// });
			}

			if (postListSize > 0) {
				// lastContenId = postList.get(postListSize - 1).getContentId();
				lastOffset += postListSize;
			}
		} while (postListSize == SQL_BLOCKSIZE);

		// close resource indexWriter
		log.info("closing index " + this.resourceIndex);
		this.indexWriter.close();

		// all done
		// log.info("(" + i + " indexed entries, " + is +
		// " not indexed spam entries)");
	}

	/**
	 * @param resourceConverter
	 *            the resourceConverter to set
	 */
	public void setResourceConverter(final LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @param resourceIndex
	 *            the resourceIndex to set
	 */
	public void setResourceIndex(final LuceneResourceIndex<R> resourceIndex) {
		this.resourceIndex = resourceIndex;
	}

	/**
	 * @return the id of the index currently generating
	 */
	public int getGeneratingIndexId() {
		return this.resourceIndex.getIndexId();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#writeMetaInfo(java.lang.Integer, java.util.Date)
	 */
	@Override
	protected void writeMetaInfo(Integer lastTasId, Date lastLogDate) throws IOException {
		// lucene does not store meta-info in the index - it retrieves it directly from all the entries
	}

	public LuceneResourceIndex<R> getResourceIndex() {
		return this.resourceIndex;
	}

}
