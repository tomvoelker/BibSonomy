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
