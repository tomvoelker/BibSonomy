package org.bibsonomy.lucene.index;

import java.util.HashMap;

import org.bibsonomy.lucene.param.RecordType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;

/**
 * class for managing the lucene bibtex index
 * 
 * @author fei
 *
 */
public class LuceneBibTexIndex extends LuceneResourceIndex<BibTex> {
	
	/** singleton instance */
	protected static LuceneResourceIndex<BibTex> instance;
	
	@Override
	protected Class<? extends Resource> getResourceType() {
		return BibTex.class;
	}

	public static LuceneResourceIndex<BibTex> getInstance() {
		if (instance == null) instance = new LuceneBibTexIndex();
		return instance;
	}

}
