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

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

/**
 * TODO: add documentation to this class
 * 
 * @author jensi
 */
public class ESTestClient extends AbstractEsClient implements AutoCloseable {

	private Node node;
	private Client client;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() {
		shutdown();
	}

	

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.ESClient#getClient()
	 */
	@Override
	public Client getClient() {
		return client;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.ESClient#getNode()
	 */
	@Override
	public Node getNode() {
		return node;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.ESClient#shutdown()
	 */
	@Override
	public void shutdown() {
		DeleteIndexRequest indexRequest = new DeleteIndexRequest("_all");
		client.admin().indices().delete(indexRequest).actionGet();

		client.close();
		node.close();
	}



	public void setNode(Node node) {
		this.node = node;
	}



	public void setClient(Client client) {
		this.client = client;
	}
	
	
}
