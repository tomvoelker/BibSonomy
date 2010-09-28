package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByResourceSearch;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author fei
 * @version $Id$
 */
public class GetBibtexByResourceSearch extends GetResourcesByResourceSearch<BibTex, BibTexParam> {

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (!present(param.getBibtexKey()) &&
				(present(param.getSearch()) || present(param.getAuthor()) || present(param.getTitle()))
				); 
	}	
}