package org.bibsonomy.search.es.util.spring;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.search.es.ESConstants;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} for a transport client
 *
 * @author dzo
 */
public class ElasticsearchTransportClientFactoryBean implements FactoryBean<Client> {
	private static final Log log = LogFactory.getLog(ElasticsearchTransportClientFactoryBean.class);
	
	/**
	 * Elasticsearch IP and port values, if we have multiple addresses, they
	 * will be separated by ","; port and ip are separated by ":"
	 * TODO: initialize this with the correct model
	 */
	private String esAddresses;

	/** Elasticsearch cluster name */
	private String esClusterName;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public Client getObject() throws Exception {
		try {
			log.info("creating EsClient instance");

			final String esHosts = this.esAddresses;
			log.info("EsHostss value in Properties:" + esHosts);
			// Setting cluster name of ES Server
			final Builder settings = Settings.settingsBuilder().put("cluster.name", this.esClusterName);
			settings.put(ESConstants.SNIFF, true);
			
			final TransportClient transportClient = TransportClient.builder().settings(settings).build();
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
							transportClient.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(ip, port)));
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
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return Client.class;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	/**
	 * @param esAddresses the esAddresses to set
	 */
	public void setEsAddresses(final String esAddresses) {
		this.esAddresses = esAddresses;
	}
	
	/**
	 * @param esClusterName the esClusterName to set
	 */
	public void setEsClusterName(final String esClusterName) {
		this.esClusterName = esClusterName;
	}
}
