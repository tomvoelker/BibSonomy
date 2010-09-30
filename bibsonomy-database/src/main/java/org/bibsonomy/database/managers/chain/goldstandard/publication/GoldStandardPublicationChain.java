package org.bibsonomy.database.managers.chain.goldstandard.publication;

import org.bibsonomy.database.managers.chain.FirstListChainElement;
import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.managers.chain.goldstandard.publication.get.GoldStandardPublicationSearch;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;

/**
 * @author dzo
 * @version $Id$
 */
public class GoldStandardPublicationChain implements FirstListChainElement<Post<GoldStandardPublication>, BibTexParam> {

    private final ListChainElement<Post<GoldStandardPublication>, BibTexParam> goldStandardPublicationSearch;
    
    /**
     * inits all chain elements
     */
    public GoldStandardPublicationChain() {
	goldStandardPublicationSearch = new GoldStandardPublicationSearch();
    }
    
    @Override
    public ListChainElement<Post<GoldStandardPublication>, BibTexParam> getFirstElement() {
	return this.goldStandardPublicationSearch;
    }

}
