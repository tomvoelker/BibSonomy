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

import java.util.Map;

import org.bibsonomy.lucene.index.manager.LuceneGoldStandardManager;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.update.SharedIndexUpdatePlugin;
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
		for (SharedIndexUpdatePlugin<? extends Resource> srPlugin : sharedIndexUpdatePlugins.values()) {
			srPlugin.generateIndex(true);
		}
		// run updates in order to activate the indices
		for (LuceneResourceManager<?> manager : luceneResourceManagers.values()) {
			manager.updateAndReloadIndex();
		}
	}

	public ESTestClient getEsClient() {
		return this.esClient;
	}

	public void setEsClient(ESTestClient esClient) {
		this.esClient = esClient;
	}
}
