package org.bibsonomy.es;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * TODO: add documentation to this class
 * 
 * @author jensi
 */
public class ESTestClient extends AbstractEsClient implements AutoCloseable {

	private Node node;
	private Client client;
	private String indexName;

	private Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>>   luceneResourceManagers;
	private Map<Class<? extends Resource>, SharedIndexUpdatePlugin<? extends Resource>> sharedIndexUpdatePlugins;
	
	
	public void init() {
		startNode();
		super.init();
	}
	
	public void startNode() {
		Settings settings = ImmutableSettings.settingsBuilder().put("script.disable_dynamic", false).build();
		this.node = NodeBuilder.nodeBuilder().settings(settings).node();

		this.client = node.client();
	}
	
	public void createIndex() {
		
		for (Class<? extends Resource> resourceClass : luceneResourceManagers.keySet()) {
			LuceneResourceManager<? extends Resource> luceneResMgr = luceneResourceManagers.get(resourceClass);
			SharedIndexUpdatePlugin<? extends Resource> srPlugin = sharedIndexUpdatePlugins.get(resourceClass);
			if (srPlugin != null) {
				srPlugin.generateIndex(luceneResMgr, true, false);
			}
		}
		
	}
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
		DeleteIndexRequest indexRequest = new DeleteIndexRequest(indexName);
		client.admin().indices().delete(indexRequest).actionGet();

		client.close();
		node.close();
	}

	public Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>> getLuceneResourceManagers() {
		return this.luceneResourceManagers;
	}

	public void setLuceneResourceManagers(Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		this.luceneResourceManagers = luceneResourceManagers;
	}

	public Map<Class<? extends Resource>, SharedIndexUpdatePlugin<? extends Resource>> getSharedIndexUpdatePlugins() {
		return this.sharedIndexUpdatePlugins;
	}

	public void setSharedIndexUpdatePlugins(Map<Class<? extends Resource>, SharedIndexUpdatePlugin<? extends Resource>> sharedIndexUpdatePlugins) {
		this.sharedIndexUpdatePlugins = sharedIndexUpdatePlugins;
	}

	public String getIndexName() {
		return this.indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
}
