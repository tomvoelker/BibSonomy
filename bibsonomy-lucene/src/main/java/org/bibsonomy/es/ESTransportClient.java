package org.bibsonomy.es;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
	private static Client client;

	/**
	 * Default constructor, initializing the client.
	 */
	public ESTransportClient() {
		if (client == null) {
			client = initiateTransportClient();
		}
	}

	/**
	 * @return
	 */
	private Client initiateTransportClient() {
		try {
			log.info("Getting EsClient instance");
//			final Map<String, String> esProperties = getESHostValue();
//			String esHosts = esProperties.get(ESConstants.ES_ADDRESSS);
			String esHosts = ESConstants.ES_ADDRESSS_VALUE;
			log.info("EsHostss value in Properties:" + esHosts);
			// Setting cluster name of ES Server
			final Builder settings = ImmutableSettings.settingsBuilder().put("cluster.name",ESConstants.ES_CLUSTERNAME_VALUE);
			settings.put(ESConstants.PATH_CONF, ESConstants.NAMES_TXT);
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

	/**
	 * Gets the ES host value.
	 * 
	 * @return the ES host value
	 */
	private Map<String, String> getESHostValue() {
		Map<String, String> esProperties = new HashMap<String, String>();
		try {
			Properties prop = new Properties();
			String propFile = ESConstants.ES_PROPERTIES;
			InputStream in = ESTransportClient.class
					.getResourceAsStream(propFile);
			prop.load(in);
			esProperties.put(ESConstants.ES_ADDRESSS,
					prop.getProperty(ESConstants.ES_ADDRESSS));
			esProperties.put(ESConstants.ES_CLUSTERNAME,
					prop.getProperty(ESConstants.ES_CLUSTERNAME));
			in.close();
			return esProperties;
		} catch (Exception e) {
			log.error("Error while accessing ES propertyFile", e);
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
