package org.bibsonomy.webapp.command.admin;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.lucene.param.LuceneIndexInfo;


/**
 * @author mba
 * @version $Id$
 */
public class LuceneResourceIndicesInfoContainer {
	
	private String resourceName;
	private List<LuceneIndexInfo> luceneResoruceIndicesInfos = new LinkedList<LuceneIndexInfo>();

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return this.resourceName;
	}
	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	/**
	 * @return the luceneResoruceIndicesInfos
	 */
	public List<LuceneIndexInfo> getLuceneResoruceIndicesInfos() {
		return this.luceneResoruceIndicesInfos;
	}
	/**
	 * @param luceneResoruceIndicesInfos the luceneResoruceIndicesInfos to set
	 */
	public void setLuceneResoruceIndicesInfos(List<LuceneIndexInfo> luceneResoruceIndicesInfos) {
		this.luceneResoruceIndicesInfos = luceneResoruceIndicesInfos;
	}
	
	/**
	 * @return
	 */
	public LuceneIndexInfo getGeneratingIndex() {
		for (LuceneIndexInfo lii: luceneResoruceIndicesInfos) {
			if (lii.isGeneratingIndex())
				return lii;
		}
		return null;
	}
		
}
