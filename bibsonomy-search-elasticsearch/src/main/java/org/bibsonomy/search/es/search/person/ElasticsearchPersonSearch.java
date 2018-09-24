package org.bibsonomy.search.es.search.person;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonSuggestionQuery;
import org.bibsonomy.services.searcher.PersonSearch;

import java.util.List;

public class ElasticsearchPersonSearch implements PersonSearch {

	@Override
	public List<Person> getPersonSuggestions(PersonSuggestionQuery query) {
		return null;
	}
}
