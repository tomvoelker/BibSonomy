package org.bibsonomy.lucene;

import java.util.Map;

import org.bibsonomy.database.testutil.JNDIBinder;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;

/**
 * 
 * @author dzo
 * @version $Id$
 */
public class LuceneIndexGenerator {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		if (args.length == 0) {
			System.out.println("usage: resourcetype *");
			System.exit(0);
		}
		
		JNDIBinder.bind("bibsonomy_lucene_database.properties");
		@SuppressWarnings("unchecked")
		final Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>> managers = (Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>>) LuceneSpringContextWrapper.getBeanFactory().getBean("allLuceneResourceManagers");
		
		for (final String resource : args) {
			final Class<? extends Resource> resourceClass = ResourceFactory.getResourceClass(resource);
			System.out.println("creating new index for " + resource);
			final LuceneResourceManager<? extends Resource> luceneResourceManager = managers.get(resourceClass);
			luceneResourceManager.generateIndex(false);
			System.out.println("created new index for " + resource);
		}
		
	}
}
