package org.bibsonomy.lucene.util;

import org.bibsonomy.model.BibTex;

/**
 * class for converting BibTex post objects to lucene documents and vice versa
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneBibTexConverter extends LuceneResourceConverter<BibTex> {

	@Override
	protected BibTex createNewResource() {
		return new BibTex();
	}
	
}
