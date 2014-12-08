package org.bibsonomy.es;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.es.SearchType;

/**
 * TODO: add documentation to this class
 * 
 * @author lutful
 * @param <R>
 *            the resource of the index
 */
public class SharedResourceManager<R extends Resource> extends LuceneResourceManager<Resource>{
	private List<LuceneResourceManager<? extends Resource>> luceneResourceManagers;
	private static final Log log = LogFactory
			.getLog(SharedResourceManager.class);

	/**
	 * generates indexes for shared resource aka ElasticSearch 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void generateIndex() {
		// allow only one index-generation at a time
		if (this.generatingIndex) {
			return;
		}

		// prepare index generation
		synchronized (this) {
			this.generatingIndex = true;
			SharedResourceIndexGenerator generator = new SharedResourceIndexGenerator();
			generator.setSearchType(SearchType.ELASTICSEARCH);
			for(LuceneResourceManager<? extends Resource> manager: luceneResourceManagers){
				generator.setLogic((LuceneDBInterface<Resource>) manager.getDbLogic());
				generator.setINDEX_TYPE(manager.getResourceName());
				generator.setResourceConverter((LuceneResourceConverter<Resource>) manager.getResourceConverter());
				generator.run();
			}
		}			
	}


	/**
	 * @return the luceneResourceManagers
	 */
	public List<LuceneResourceManager<? extends Resource>> getLuceneResourceManagers() {
		return this.luceneResourceManagers;
	}


	/**
	 * @param luceneResourceManagers the luceneResourceManagers to set
	 */
	public void setLuceneResourceManagers(List<LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		this.luceneResourceManagers = luceneResourceManagers;
	}



}
