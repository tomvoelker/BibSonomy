/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.logic;

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;

/**
 * Interface for person entity logic.
 * 
 * @author jil
 */
public interface PersonLogicInterface {

	public void addResourceRelation(ResourcePersonRelation resourcePersonRelation) throws ResourcePersonAlreadyAssignedException;

	/**
	 * FIXME: remove database id TODO_PERSONS
	 * removes a resource relation
	 * @param resourceRelationId
	 */
	public void removeResourceRelation(int resourceRelationId);

	/**
	 * sets id for new persons
	 * 
	 * @param person the person to be saved or updated
	 */
	public void createOrUpdatePerson(Person person);
	
	public Person getPersonById(PersonIdType idType, String id);
	
	/**
	 * FIXME: remove database id TODO_PERSONS
	 * removes a person name from a specific person
	 * @param personNameId
	 */
	public void removePersonName(Integer personNameId);

	/**
	 * @param withPersonId
	 */
	public void createPersonName(PersonName withPersonId);
	
	/**
	 * @param queryString a search string coming from an autocomplete field. Planned but not yet implemented: May contain an incomplete word, which will be internally autocompleted before searching persons
	 * @return a builder object fo optional parameters
	 */
	public PersonSuggestionQueryBuilder getPersonSuggestion(String queryString);

	/**
	 * @return a querybuilder object by which options for the query can be specified
	 */
	public ResourcePersonRelationQueryBuilder getResourceRelations();

	
	/**
	 * Updates the given person
	 * @param person		the person to update
	 * @param operation		the desired update operation
	 */
	public void updatePerson(final Person person, final PersonUpdateOperation operation);
	

}
