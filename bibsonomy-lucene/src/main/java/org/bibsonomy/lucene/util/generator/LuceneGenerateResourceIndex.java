/**
 * BibSonomy - A blue social bookmark and publication sharing system.
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
package org.bibsonomy.lucene.util.generator;

import java.io.File;
import java.io.IOException;

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
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.generator.AbstractIndexGenerator;
import org.bibsonomy.search.update.SearchIndexSyncState;

/**
 * reads data from database and builds lucene index for all resource entries
 * 
 * @author sst
 * @author fei
 * 
 * @param <R>
 *            the resource of the index to generate
 */
@Deprecated // TODO: remove lucene
public class LuceneGenerateResourceIndex<R extends Resource> extends AbstractIndexGenerator<R> {

	private static final Log log = LogFactory.getLog(LuceneGenerateResourceIndex.class);

	/** writes the resource index */
	private IndexWriter indexWriter;

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
	protected void addPostToIndex(final SearchPost<R> post) {
		// create index document from post model
		final Document doc = (Document) this.resourceIndex.getResourceConverter().readPost(post);
		try {
			this.indexWriter.addDocument(doc);
			this.importedPost(post);
		} catch (final IOException e) {
			log.error("error while inserting post " + post.getUser().getName() + "/" + post.getResource().getIntraHash(), e);
		}
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
	protected void writeMetaInfo(SearchIndexSyncState state) throws IOException {
		// lucene does not store meta-info in the index - it retrieves it directly from all the entries
	}

	/**
	 * @return java representation of the index that is generated by this object
	 */
	public LuceneResourceIndex<R> getResourceIndex() {
		return this.resourceIndex;
	}

}
