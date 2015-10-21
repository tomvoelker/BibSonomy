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
package org.bibsonomy.search.es.generator;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.index.ResourceConverter;
import org.bibsonomy.search.es.update.SharedIndexUpdatePlugin;
import org.bibsonomy.search.es.update.SharedResourceIndexUpdater;
import org.bibsonomy.search.generator.AbstractIndexGenerator;
import org.bibsonomy.search.update.SearchIndexState;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.client.IndicesAdminClient;

/**
 * This class is responsible for generating the index for Shared Resources
 *
 * @param <R> the resource of the index to generate
 *
 * @author lutful
 * @author jil
 */
@Deprecated // TODO: remove TODODZO
public class SharedResourceIndexGenerator<R extends Resource> extends AbstractIndexGenerator<R> {
	private static final Log log = LogFactory.getLog(SharedResourceIndexGenerator.class);
	
	private final String indexName;

	/** converts post model objects to elasticsearch documents */
	protected ResourceConverter<R> resourceConverter;
	
	private ESClient esClient;
	private final URI systemHome;
	private SharedResourceIndexUpdater<R> updater;
	private final SharedIndexUpdatePlugin<R> updatePlugin;
	
	/**
	 * @param systemHome
	 * @param sharedIndexUpdatePlugin 
	 * @param indexName 
	 */
	public SharedResourceIndexGenerator(final URI systemHome, SharedIndexUpdatePlugin<R> sharedIndexUpdatePlugin, final String indexName) {
		this.systemHome = systemHome;
		this.updatePlugin = sharedIndexUpdatePlugin;
		this.indexName = indexName;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#run()
	 */
	@Override
	public void run() {
		this.updater = updatePlugin.createUpdaterForGenerator(this.indexName);
		try {
			super.run();
		} finally {
			this.updater.close();
		}
	}

	/**
	 * creates index of resource entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Override
	public void createEmptyIndex() throws IOException {
		this.esClient.waitForReadyState();
		
		// check if the index already exists if not, it creates empty index
		final IndicesAdminClient indices = this.esClient.getClient().admin().indices();
		final boolean indexExist = indices.exists(new IndicesExistsRequest(indexName)).actionGet().isExists();
		if (!indexExist) {
			log.info("index not existing - generating a new one");
			final CreateIndexResponse createIndex = indices.create(new CreateIndexRequest(indexName)).actionGet();
			if (!createIndex.isAcknowledged()) {
				log.error("Error in creating Index");
				return;
			}
		}
		
		log.info("Start writing data to shared index");
		
		// add mapping here depending on the resource type which is here indexType
		// ESResourceMapping resourceMapping = new ESResourceMapping(resourceType, esClient, indexName);
		// resourceMapping.doMapping();
	}
	
	@Override
	protected void writeMetaInfo(SearchIndexState state) throws IOException {
		updater.setSystemInformation(state);
		updater.flushSystemInformation();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#addPostToIndex(org.bibsonomy.lucene.param.LucenePost)
	 */
	@Override
	protected void addPostToIndex(SearchPost<R> post) {
		if (!post.getGroups().contains(GroupUtils.buildPublicGroup())) {
			return;
		}
		final Map<String, Object> jsonDocument = this.resourceConverter.convert(post);
		// TODO: 
		jsonDocument.put(Fields.SYSTEM_URL, systemHome);
		final long indexId = SharedResourceIndexUpdater.calculateIndexId(post.getContentId(), this.systemHome);
		esClient.getClient()
				.prepareIndex(indexName, ResourceFactory.getResourceName(this.resourceType), String.valueOf(indexId))
				.setSource(jsonDocument).execute().actionGet();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#getName()
	 */
	@Override
	protected String getName() {
		return getIndexName() + "-" + resourceType;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return this.indexName;
	}

	/**
	 * @param esClient the esClient to set
	 */
	public void setEsClient(ESClient esClient) {
		this.esClient = esClient;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#activateIndex()
	 */
	@Override
	protected void activateIndex() {
		esClient.getClient().admin().indices().flush(new FlushRequest(indexName)).actionGet();
		// do not truly activate the index by now. Later, an updater will find it, update it and activate it  
	}

	/**
	 * @param resourceConverter
	 */
	public void setResourceConverter(ResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + indexName + "]";
	}
}
