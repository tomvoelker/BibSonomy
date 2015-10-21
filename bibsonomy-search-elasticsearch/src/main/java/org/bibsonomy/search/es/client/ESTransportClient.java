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
package org.bibsonomy.search.es.client;


import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.util.Mapping;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * starts the Transport Client for connecting with the elastic search cluster
 * 
 * @author lutful
 */
public class ESTransportClient extends AbstractEsClient {
	private static final Log log = LogFactory.getLog(ESTransportClient.class);
	private Client client;

	/**
	 * Elasticsearch IP and port values, if we have multiple addresses, they
	 * will be separated by ","; port and ip are separated by ":"
	 * TODO: initialize this with the correct model
	 */
	private String esAddresses;

	/** Elasticsearch cluster name */
	private String esClusterName;

	/**
	 * @return the esAddresses
	 */
	public String getEsAddresses() {
		return this.esAddresses;
	}

	/**
	 * @param esAddresses the esAddresses to set
	 */
	public void setEsAddresses(final String esAddresses) {
		this.esAddresses = esAddresses;
	}

	/**
	 * @return the esClusterName
	 */
	public String getEsClusterName() {
		return this.esClusterName;
	}

	/**
	 * @param esClusterName the esClusterName to set
	 */
	public void setEsClusterName(final String esClusterName) {
		this.esClusterName = esClusterName;
	}


	/**
	 * initializing the client.
	 * 
	 */
	public void init() {
		if (this.client == null) {
			this.client = this.initiateTransportClient();
		}
	}

	/**
	 * @return returns the transport client
	 */
	private Client initiateTransportClient() {
		try {
			log.info("Getting EsClient instance");

			final String esHosts = this.esAddresses;
			log.info("EsHostss value in Properties:" + esHosts);
			// Setting cluster name of ES Server
			final Builder settings = ImmutableSettings.settingsBuilder().put("cluster.name", this.esClusterName);
			settings.put(ESConstants.SNIFF, true);
			final TransportClient transportClient = new TransportClient(settings);
			if (present(esHosts)) {
				final String[] hosts = esHosts.split(",");
				for (int i = 0; i < hosts.length; i++) {
					int port;
					String ip;
					final String aESHost = hosts[i];
					if (aESHost != null) {
						final String[] hostInfo = aESHost.split(":");
						if (hostInfo.length > 1) {
							ip = hostInfo[0];
							port = Integer.parseInt(hostInfo[1]);
							transportClient.addTransportAddress(new InetSocketTransportAddress(ip, port));
						}
					}
				}
			}

			return transportClient;
		} catch (final Exception e) {
			log.error("Error in TransportClient", e);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#insertNewDocument(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void insertNewDocument(String indexName, String type, String id, Map<String, Object> jsonDocument) {
		this.client.prepareIndex(indexName, type, id).setSource(jsonDocument).setRefresh(true).get();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#createIndex(java.lang.String, java.util.Set)
	 */
	@Override
	public boolean createIndex(String indexName, Set<Mapping<String>> mappings) {
		final CreateIndexResponse createIndex = this.client.admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
		if (!createIndex.isAcknowledged()) {
			log.error("Error in creating Index");
			return false;
		}
		
		for (final Mapping<String> mapping : mappings) {
			this.client.admin().indices().preparePutMapping(indexName).setType(mapping.getType()).setSource(mapping.getMappingInfo()).execute().actionGet();
		}
		
		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		this.waitForReadyState();
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#deleteIndex(java.lang.String)
	 */
	@Override
	public boolean deleteIndex(String oldIndexName) {
		final DeleteIndexResponse deleteResult = this.client.admin().indices().delete(new DeleteIndexRequest(oldIndexName)).actionGet();
		return deleteResult.isAcknowledged();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#existsIndexWithName(java.lang.String)
	 */
	@Override
	public boolean existsIndexWithName(String indexName) {
		final IndicesAdminClient indices = this.client.admin().indices();
		return indices.exists(new IndicesExistsRequest(indexName)).actionGet().isExists();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#createAlias(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean createAlias(String indexName, String alias) {
		final IndicesAliasesRequestBuilder prepareAliases = this.getClient().admin().indices().prepareAliases();
		
		prepareAliases.addAlias(indexName, alias);
		final IndicesAliasesResponse aliasReponse = prepareAliases.execute().actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("error creating alias '" + alias + "' for index '" + indexName + "'.");
			return false;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#getIndexNameForAlias(java.lang.String)
	 */
	@Override
	public String getIndexNameForAlias(final String alias) {
		final ImmutableOpenMap<String, List<AliasMetaData>> activeindices = this.client.admin().indices().getAliases(new GetAliasesRequest().aliases(alias)).actionGet().getAliases();
		if (!activeindices.isEmpty()) {
			if (activeindices.size() > 1) {
				throw new IllegalStateException("found more than one index for this system!");
			}
			
			return activeindices.iterator().next().key;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.ESClient#getClient()
	 */
	@Override
	public Client getClient() {
		return this.client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.ESClient#shutdown()
	 */
	@Override
	public void shutdown() {
		this.client.close();
	}
}
