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

	/**
	 * Elasticsearch IP and port values, if we have multiple addresses, they
	 * will be separated by ","; port and ip are separated by ":"
	 * TODO: initialize this with the correct model
	 */
	private String esAddresses;

	@Deprecated // FIXME: copied from httpHost of httpclient 4 lib; used to also use httpclient3 lib dependencies
	public static HttpHost create(String s) {
		String text = s;
		String scheme = null;
		int schemeIdx = s.indexOf("://");
		if (schemeIdx > 0) {
			scheme = s.substring(0, schemeIdx);
			text = s.substring(schemeIdx + 3);
		}

		int port = -1;
		int portIdx = text.lastIndexOf(":");
		if (portIdx > 0) {
			try {
				port = Integer.parseInt(text.substring(portIdx + 1));
			} catch (NumberFormatException var7) {
				throw new IllegalArgumentException("Invalid HTTP host: " + text);
			}

			text = text.substring(0, portIdx);
		}

		return new HttpHost(text, port, scheme);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public RestHighLevelClient getObject() {
		log.info("creating rest high level client instance");
		log.info("EsHostss value in Properties:" + this.esAddresses);

		// convert the provided es address string to http hosts
		final Stream<HttpHost> hostsStream = Arrays.stream(this.esAddresses.split(",")).map(ElasticsearchRESTClientFactoryBean::create);

		final RestClientBuilder builder = RestClient.builder(hostsStream.toArray(HttpHost[]::new));
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
}
