package org.bibsonomy.lucene.search;

import org.bibsonomy.model.BibTex;

/**
 * TODO: rename to LuceneSearchPublications
 * 
 * class for publication search
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneSearchBibTex extends LuceneAbstractPublicationSearch<BibTex> {
	private final static LuceneSearchBibTex singleton = new LuceneSearchBibTex();
	
	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBibTex getInstance() {
		return singleton;
	}
	
	/**
	 * constructor
	 */
	private LuceneSearchBibTex() {
		this.reloadIndex(0);
	}
	
	@Override
	protected String getResourceName() {
		return BibTex.class.getSimpleName();
	}
}