package org.bibsonomy.testutil;

import java.util.List;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.services.searcher.PersonSearch;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class DummyPersonSearch implements PersonSearch {

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.PersonSearch#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public List<ResourcePersonRelation> getPersonSuggestion(PersonSuggestionQueryBuilder options) {
		return null;
	}

}
