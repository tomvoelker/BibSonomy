package org.bibsonomy.database.managers.chain.concept;

import org.bibsonomy.database.managers.chain.FirstListChainElement;
import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.managers.chain.concept.get.GetAllConcepts;
import org.bibsonomy.database.managers.chain.concept.get.GetAllConceptsForUser;
import org.bibsonomy.database.managers.chain.concept.get.GetPickedConceptsForUser;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.model.Tag;

/**
 * Chain for concept queries
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class ConceptChain implements FirstListChainElement<Tag, TagRelationParam> {

	private final ListChainElement<Tag, TagRelationParam> getAllConcepts;
	private final ListChainElement<Tag, TagRelationParam> getAllConceptsForUser;
	private final ListChainElement<Tag, TagRelationParam> getPickedConceptsForUser;

	/**
	 * Constructs the chain
	 */
	public ConceptChain() {
		// intialize chain elements
		this.getAllConcepts = new GetAllConcepts();
		this.getAllConceptsForUser = new GetAllConceptsForUser();
		this.getPickedConceptsForUser = new GetPickedConceptsForUser();

		// set order of chain elements
		this.getAllConcepts.setNext(this.getAllConceptsForUser);
		this.getAllConceptsForUser.setNext(this.getPickedConceptsForUser);
	}

	@Override
	public ListChainElement<Tag, TagRelationParam> getFirstElement() {
		return this.getAllConcepts;
	}
}