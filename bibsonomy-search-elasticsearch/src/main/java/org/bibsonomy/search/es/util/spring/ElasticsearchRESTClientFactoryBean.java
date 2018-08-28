package org.bibsonomy.search.es.util.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * {@link FactoryBean} for {@link RestHighLevelClient}
 * @author dzo
 */
public class ElasticsearchRESTClientFactoryBean implements FactoryBean<RestHighLevelClient> {
	private static final Log log = LogFactory.getLog(ElasticsearchRESTClientFactoryBean.class);

	/** Elasticsearch client SNIFF property.*/
	private static final String SNIFF = "client.transport.sniff";

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
	public RestHighLevelClient getObject() {
		log.info("creating EsClient instance");

		final String esHosts = this.esAddresses;
		log.info("EsHostss value in Properties:" + esHosts);

		// convert the provided es address string to http hosts
		final Stream<HttpHost> hostsStream = Arrays.stream(this.esAddresses.split(",")).map(HttpHost::create);

		RestClientBuilder builder = RestClient.builder(hostsStream.toArray(HttpHost[]::new));
		final RestHighLevelClient client = new RestHighLevelClient(builder);

		return client;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return RestHighLevelClient.class;
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
