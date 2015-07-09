package org.bibsonomy.es;

import java.util.Map;

import org.bibsonomy.lucene.index.manager.LuceneGoldStandardManager;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class ESTestClientInitializer {
	private Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>>   luceneResourceManagers;
	private Map<Class<? extends Resource>, SharedIndexUpdatePlugin<? extends Resource>> sharedIndexUpdatePlugins;
	private ESTestClient esClient;
	
	

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
	
	
	
	public void init() {
		startNode();
		createIndex();
	}
	
	public void startNode() {
		Settings settings = ImmutableSettings.settingsBuilder().put("script.disable_dynamic", false).build();
		Node node = NodeBuilder.nodeBuilder().settings(settings).node();
		esClient.setNode(node);
		esClient.setClient(node.client());
	}
	
	private void createIndex() {
		
		for (Class<? extends Resource> resourceClass : luceneResourceManagers.keySet()) {
			LuceneResourceManager<? extends Resource> luceneResMgr = luceneResourceManagers.get(resourceClass);
			SharedIndexUpdatePlugin<? extends Resource> srPlugin = sharedIndexUpdatePlugins.get(resourceClass);
			if (srPlugin != null) {
				srPlugin.generateIndex(luceneResMgr, true);
			}
		}
		
		LuceneGoldStandardManager<GoldStandardPublication> manager = (LuceneGoldStandardManager<GoldStandardPublication>) LuceneSpringContextWrapper.getBeanFactory().getBean("luceneGoldStandardPublicationManager");
		manager.updateAndReloadIndex();
		LuceneResourceManager<BibTex> lucenePublicationUpdater = (LuceneResourceManager<BibTex>) EsSpringContextWrapper.getBeanFactory().getBean("lucenePublicationManager");
		lucenePublicationUpdater.updateAndReloadIndex();
		LuceneResourceManager<Bookmark> luceneBookmarkUpdater = (LuceneResourceManager<Bookmark>) EsSpringContextWrapper.getBeanFactory().getBean("luceneBookmarkManager");
		luceneBookmarkUpdater.updateAndReloadIndex();
		
	}

	public ESTestClient getEsClient() {
		return this.esClient;
	}

	public void setEsClient(ESTestClient esClient) {
		this.esClient = esClient;
	}
}
