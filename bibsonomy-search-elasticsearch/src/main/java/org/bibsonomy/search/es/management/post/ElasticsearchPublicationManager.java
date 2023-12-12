/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.search.es.management.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants.Fields.Publication;
import org.bibsonomy.search.es.index.converter.post.PublicationConverter;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;
import org.elasticsearch.index.engine.DocumentMissingException;

/**
 * Elasticsearch manager for publication indices.
 * This is a special implementation of {@link ElasticsearchPostManager} to update publication specific fields.
 *
 * @author dzo
 * @author jensi
 * @param <P> 
 */
public class ElasticsearchPublicationManager<P extends BibTex> extends ElasticsearchPostManager<P> {
	private static final Log log = LogFactory.getLog(ElasticsearchPublicationManager.class);

	/**
	 * Default constructor
	 *
	 * @param systemURI
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param indexEnabled
	 * @param updateEnabled
	 * @param regenerateEnabled
	 * @param inputLogic
	 */
	public ElasticsearchPublicationManager(URI systemURI,
										   ESClient client,
										   ElasticsearchIndexGenerator<Post<P>, SearchIndexState> generator,
										   Converter syncStateConverter,
										   EntityInformationProvider entityInformationProvider,
										   boolean indexEnabled,
										   boolean updateEnabled,
										   boolean regenerateEnabled,
										   SearchDBInterface<P> inputLogic) {
		super(systemURI, client, generator, syncStateConverter, entityInformationProvider, indexEnabled, updateEnabled, regenerateEnabled, inputLogic);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.management.ElasticSearchManager#updateResourceSpecificProperties(java.lang.String, org.bibsonomy.search.update.SearchIndexState, org.bibsonomy.search.update.SearchIndexState)
	 */
	@Override
	protected void updateResourceSpecificProperties(String indexName, SearchIndexState oldState, SearchIndexState targetState) {
		// TODO: limit offset TODODZO
		Date lastDocDate = oldState.getDocumentLogDate();
		if (!present(lastDocDate)) {
			lastDocDate = targetState.getDocumentLogDate();
		}
		final List<Post<P>> postsForDocUpdate = this.inputLogic.getPostsForDocumentUpdate(lastDocDate, targetState.getDocumentLogDate());
		
		// TODO: bulk update
		for (final Post<P> postDocUpdate : postsForDocUpdate) {
			final List<Map<String, String>> documents = this.getPublicationConverter().convertDocuments(postDocUpdate.getResource().getDocuments());
			final String id = this.entityInformationProvider.getEntityId(postDocUpdate);
			try {
				this.client.updateDocument(indexName, this.entityInformationProvider.getType(), id, Collections.singletonMap(Publication.DOCUMENTS, documents));
			} catch (final DocumentMissingException e) {
				log.error("could not update post with " + id, e);
			}
		}
	}

	/**
	 * FIXME: this cast is not nice
	 * @return
	 */
	private PublicationConverter getPublicationConverter() {
		final Object converter = this.entityInformationProvider.getConverter();
		return (PublicationConverter) converter;
	}

}
