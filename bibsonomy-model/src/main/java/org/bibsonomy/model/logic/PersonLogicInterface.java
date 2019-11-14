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
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Interface for person entity logic.
 * 
 * @author jil
 */
public interface PersonLogicInterface {

	/**
	 * gets the person by the person id
	 * @param idType
	 * @param id
	 * @return
	 */
	public Person getPersonById(final PersonIdType idType, final String id);

	/**
	 * sets id for new persons
	 *
	 * @param person the person to be saved or updated
	 */
	public String createPerson(Person person);

	/**
	 * Updates the given person
	 * @param person		the person to update
	 * @param operation		the desired update operation
	 */
	public void updatePerson(final Person person, final PersonUpdateOperation operation);

	public void createResourceRelation(ResourcePersonRelation resourcePersonRelation) throws ResourcePersonAlreadyAssignedException;

	/**
	 * FIXME: remove database id
	 * removes a resource relation
	 *
	 * @param personId
	 * @param interHash
	 * @param index
	 * @param type
	 */
	void removeResourceRelation(String personId, String interHash, int index, PersonResourceRelationType type);

	/**
	 * @param withPersonId
	 */
	@Deprecated // use update person
	public void createPersonName(PersonName withPersonId);

	/**
	 * FIXME: remove database id TODO_PERSONS
	 * removes a person name from a specific person
	 * @param personNameId
	 */
	@Deprecated // use update person
	public void removePersonName(Integer personNameId);


	/**
	 * retrieves persons from the database
	 * @param query
	 * @return
	 */
	List<Person> getPersons(final PersonQuery query);


	/**
	 * Retrieves a list with resources according to the query.
	 *
	 * @param builder a query builder object with the query options.
	 *
	 * @return a list of resources according to the query.
	 */
	@Deprecated
	List<ResourcePersonRelation> getResourceRelations(ResourcePersonRelationQueryBuilder builder);


	/**
	 * Retrieves a list with resource - person relations according to the query.
	 *
	 * @param query the query.
	 *
	 * @return a list of resource - person relations.
	 */
	List<ResourcePersonRelation> getResourceRelations(ResourcePersonRelationQuery query);

	/**
	 * @param username
	 */
	@Deprecated // FIXME: add to update person logic
	public void unlinkUser(String username);

	public List<PersonMatch> getPersonMatches(String personID);

	public PersonMatch getPersonMatch(int matchID);

	public void denieMerge(PersonMatch match);

	public boolean acceptMerge(PersonMatch match);

	/**
	 * @param formMatchId
	 * @param map
	 * @return
	 */
	public Boolean conflictMerge(int formMatchId, Map<String, String> map);
}
