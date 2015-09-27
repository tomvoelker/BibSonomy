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
package org.bibsonomy.search.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

/**
 * Wrapper around an ElasticSearch Client. Different ways of obtaining a Client
 * can be implemented in classes implementing this interface.
 * 
 * @author lka
 */
public interface ESClient {
/**
	 * Get a reference to an ElasticSearch Client.
	 * 
	 * @return the Client.
	 */

	Client getClient();

	/**
	 * @return the node
	 */
	Node getNode();

	/**
	 * Shutdown the ElasticSearch Client. The client will be no more available
	 * for querying and indexing.
	 */
	void shutdown();

	/**
	 * if necessary wait for the index to be ready to work
	 */
	public void waitForReadyState();
}
