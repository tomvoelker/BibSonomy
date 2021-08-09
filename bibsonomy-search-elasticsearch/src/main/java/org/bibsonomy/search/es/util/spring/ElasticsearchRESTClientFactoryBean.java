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
