package org.bibsonomy.search.update;

import java.util.List;

import org.apache.commons.collections.map.LRUMap;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.management.database.SearchDBInterface;

/**
 * 
 * @author dzo
 * @param <P> 
 */
public interface SearchPublicationIndexUpdater<P extends BibTex> extends SearchIndexUpdater<P> {
	/**
	 * @param person
	 * @param updatedInterhashes
	 * @param dbLogic
	 */
	public void updateIndexWithPersonInfo(Person person, LRUMap updatedInterhashes, SearchDBInterface<P> dbLogic);
	
	/**
	 * @param interhash
	 * @param newRels
	 */
	public void updateIndexWithPersonRelation(String interhash, List<ResourcePersonRelation> newRels);

	/**
	 * @param name
	 * @param updatedInterhashes
	 * @param dbLogic
	 */
	public void updateIndexWithPersonNameInfo(PersonName name, LRUMap updatedInterhashes, SearchDBInterface<P> dbLogic);
}
