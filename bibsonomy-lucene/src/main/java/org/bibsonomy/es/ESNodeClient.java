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

    private static Node node = null;

    /**
     * Default constructor, initializing the client node.
     */
    public ESNodeClient() {
    	
    	if(node == null){
    		final Settings settings = ImmutableSettings.settingsBuilder()
    				.put("node.name", "bibsonomy_client").build();

	
    		node = new NodeBuilder().settings(settings)
    				//	.clusterName("elasticsearch-bibsonomy")
    				// .local(true)
    				.client(true).build().start();
    	}	
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
	 * @see org.bibsonomy.model.es.ESClient#shutdown()
	 */
	@Override
	public void shutdown() {
		if(node!=null){
			getClient().close();
			node.close();
			node = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.es.ESClient#getNode()
	 */
	@Override
	public Node getNode() {
		return this.node;
	}

	
}
