/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import org.bibsonomy.common.enums.PersonOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PhDRecommendation;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

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
	Person getPersonById(final PersonIdType idType, final String id);

	/**
	 * sets id for new persons
	 *
	 * @param person the person to be saved or updated
	 */
	String createPerson(final Person person);

	/**
	 * Updates the given person
	 * @param person		the person to update
	 * @param operation		the desired update operation
	 */
	void updatePerson(final Person person, final PersonOperation operation);

	/**
	 * stores the specified person relation
	 * @param resourcePersonRelation
	 * @throws ResourcePersonAlreadyAssignedException
	 */
	void createResourceRelation(final ResourcePersonRelation resourcePersonRelation) throws ResourcePersonAlreadyAssignedException;

	/**
	 * removes a resource relation
	 *
	 * @param personId
	 * @param interHash
	 * @param index
	 * @param type
	 */
	void removeResourceRelation(final String personId, final String interHash, final int index, final PersonResourceRelationType type);

	/**
	 * retrieves persons
	 * @param query the query specifying what person should be returned
	 * @return the persons
	 */
	List<Person> getPersons(final PersonQuery query);

	/**
	 * Retrieves a list with resource - person relations according to the query.
	 *
	 * @param query the query.
	 *
	 * @return a list of resource - person relations.
	 */
	List<ResourcePersonRelation> getResourceRelations(ResourcePersonRelationQuery query);

	/**
	 * TODO: add documentation
	 *
	 * @param personID the id of the person
	 * @return
	 */
	List<PersonMatch> getPersonMatches(final String personID);

	/**
	 * TODO: add documentation
	 * FIXME: do not use database ids in the logic!!!!!!!!
	 *
	 * @param matchID
	 * @return
	 */
	PersonMatch getPersonMergeRequest(int matchID);

	/**
	 * TODO: add documentation
	 *
	 * @param match
	 */
	void denyPersonMerge(final PersonMatch match);

	/**
	 * TODO: add documentation
	 *
	 * @param match
	 * @return
	 */
	boolean acceptMerge(final PersonMatch match);

	/**
	 * TODO: add
	 *
	 * @param formMatchId
	 * @param map
	 * @return
	 */
	Boolean mergePersonsWithConflicts(final int formMatchId, final Map<String, String> map);

	/**
	 * @param personID
	 * @return
	 */
	List<PhDRecommendation> getPhdAdvisorRecForPerson(String personID);

}
