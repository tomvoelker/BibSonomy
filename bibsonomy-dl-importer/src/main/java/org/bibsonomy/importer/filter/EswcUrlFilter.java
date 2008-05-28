package org.bibsonomy.importer.filter;

import java.util.Properties;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/** 
 * Sets the URL of the resource to be the prefix given in the properties 
 * appended by the content_id (which is the EasyChair paper id). 
 * 
 * @author rja
 * @version $Id$
 */
public class EswcUrlFilter implements PostFilterChainElement {

	
	private static final String URL_PREFIX = ".urlPrefix";
	private String urlPrefix;
	
	/**
	 * Extracts the property 
 	 * {@link org.bibsonomy.importer.filter.EswcUrlFilter}{@code .urlPrefix} 
 	 * and uses it to create the URLs of BibTex resources.
 	 * 
	 * @param prop
	 */
	public EswcUrlFilter(final Properties prop) {
		urlPrefix = prop.getProperty(EswcUrlFilter.class.getName() + URL_PREFIX);
	}
	
	public void filterPost(final Post<BibTex> post) {
		post.getResource().setUrl(urlPrefix + post.getContentId());
	}

}
