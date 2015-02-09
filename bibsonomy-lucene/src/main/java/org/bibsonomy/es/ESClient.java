package org.bibsonomy.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

/**
 * Wrapper around an ElasticSearch Client. Different ways of obtaining a
 * Client can be implemented in classes implementing this interface. 
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
}
