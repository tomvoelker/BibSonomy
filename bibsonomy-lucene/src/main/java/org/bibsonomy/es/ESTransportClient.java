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
package org.bibsonomy.es;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;

/**
 * starts the Transport Client for connecting with the elastic search cluster
 * 
 * @author lutful
 */
public class ESTransportClient extends AbstractEsClient {
	private final Log log = LogFactory.getLog(ESTransportClient.class);
	private Client client;

	/**
	 * Elasticsearch IP and port values, if we have multiple addresses, they
	 * will be separated by "," and port and ip are separated by ":"
	 */
	private String esAddresses;

	/**
	 * Elasticsearch CLustername
	 */
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
			this.log.info("Getting EsClient instance");

			final String esHosts = this.esAddresses;
			this.log.info("EsHostss value in Properties:" + esHosts);
			// Setting cluster name of ES Server
			final Builder settings = ImmutableSettings.settingsBuilder().put("cluster.name", this.esClusterName);
			// settings.put(ESConstants.PATH_CONF, ESConstants.NAMES_TXT);
			settings.put(ESConstants.SNIFF, true);
			final TransportClient transportClient = new TransportClient(settings);
			if (!esHosts.trim().isEmpty()) {
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
			this.log.error("Error in TransportClient", e);
			return null;
		}
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
	 * @see org.bibsonomy.model.es.ESClient#getNode()
	 */
	@Override
	public Node getNode() {
		return null;
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
