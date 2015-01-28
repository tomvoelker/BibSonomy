package org.bibsonomy.es;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.es.ESClient;
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
public class ESTransportClient implements ESClient {
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
	public void setEsAddresses(String esAddresses) {
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
	public void setEsClusterName(String esClusterName) {
		this.esClusterName = esClusterName;
	}

	/**
	 *  initializing the client.
	 *  
	 */
	public void init() {
		if (client == null) {
			client = initiateTransportClient();
		}
	}

	/**
	 * @return returns the transposrt client
	 */
	private Client initiateTransportClient() {
		try {
			log.info("Getting EsClient instance");

			String esHosts = this.esAddresses;
			log.info("EsHostss value in Properties:" + esHosts);
			// Setting cluster name of ES Server
			final Builder settings = ImmutableSettings.settingsBuilder().put("cluster.name",this.esClusterName);
//			settings.put(ESConstants.PATH_CONF, ESConstants.NAMES_TXT);
			settings.put(ESConstants.SNIFF, true);
			final TransportClient transportClient = new TransportClient(
					settings);
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
		} catch (Exception e) {
			log.error("Error in TransportClient", e);
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
		return client;
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
		// TODO Auto-generated method stub

	}

}
