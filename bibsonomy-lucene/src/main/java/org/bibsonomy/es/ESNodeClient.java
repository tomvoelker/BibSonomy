package org.bibsonomy.es;

import org.bibsonomy.model.es.ESClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * Wrapper around an ElasticSearch Node, to obtain a Client
 * 
 * @author lka
 */
public class ESNodeClient implements ESClient {

    private Node node;

    /**
     * Default constructor, initializing the client node.
     */
    public ESNodeClient() {
	final Settings settings = ImmutableSettings.settingsBuilder()
		.put("node.name", "bibsonomy").build();

	node = new NodeBuilder().settings(settings)
//		.clusterName("elasticsearch-bibsonomy")
		// .local(true)
		.client(true).build().start();
    }	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.ESClient#getClient()
	 */
	@Override
	public Client getClient() {
		return node.client();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.ESClient#shutdown()
	 */
	@Override
	public void shutdown() {
		getClient().close();
		node.close();
		node = null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.es.ESClient#getNode()
	 */
	@Override
	public Node getNode() {
		return this.node;
	}
}
