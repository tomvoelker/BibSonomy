package org.bibsonomy.es;

import org.bibsonomy.model.es.ESClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 *	starts the Node Client for connecting with the elastic search cluster
 * 
 * @author lutful
 */
public class ESNodeClient implements ESClient {

    private Node node = null;

    /**
     * Default constructor, initializing the client node.
     */
    public ESNodeClient() {
    	
    	if(node == null){
    		final Settings settings = ImmutableSettings.settingsBuilder()
    				.put("node.name", ESConstants.ES_NODE_NAME).build();

	
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
