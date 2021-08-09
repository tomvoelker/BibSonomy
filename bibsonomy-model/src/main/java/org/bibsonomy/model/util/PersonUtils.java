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
package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * util methods for {@link Person}
 *
 * @author dzo
 */
public final class PersonUtils {
	private PersonUtils() {}
	
	/**
	 * generates the base of person identifier
	 * @param person
	 * @return the base of the person identifier
	 */
	public static String generatePersonIdBase(final Person person) {
		final String firstName = person.getMainName().getFirstName();
		final String lastName  = person.getMainName().getLastName();
		
		if (!present(lastName)) {
			throw new IllegalArgumentException("lastName may not be empty");
		}
		
		final StringBuilder sb = new StringBuilder();
		if (present(firstName)) {
			sb.append(StringUtils.normalizeString(firstName).charAt(0));
			sb.append('.');
		}
		sb.append(StringUtils.normalizeString(lastName));
	
		return sb.toString();
	}

	public static List<PersonName> getPersonsByRoleWithFallback(BibTex publication, PersonResourceRelationType role) {
		final List<PersonName> personsByRole = getPersonsByRole(publication, role);

		if (personsByRole != null) {
			return personsByRole;
		}

		// MacGyver-fix, in case there are multiple similar simhash1 caused by author == editor
		switch (role) {
			case AUTHOR: return publication.getEditor();
			case EDITOR: return publication.getAuthor();
		}

		return null;
	}

	public static List<PersonName> getPersonsByRole(final BibTex publication, PersonResourceRelationType role) {
		switch(role) {
			case AUTHOR: return publication.getAuthor();
			case EDITOR: return publication.getEditor();
		}

		return null;
	}

	/**
	 * finds the top resource relations that should be displayed for the person
	 *
	 * @param relations
	 * @return
	 */
	public static ResourcePersonRelation findTopRelation(final List<ResourcePersonRelation> relations) {
		if (!present(relations)) {
			return null;
		}

		// prefer a kind of thesis
		for (final String type : Arrays.asList(BibTexUtils.PHD_THESIS, BibTexUtils.MASTERS_THESIS, BibTexUtils.THESIS)) {
			final ResourcePersonRelation relationByType = findRelationByType(type, relations);
			if (present(relationByType)) {
				return relationByType;
			}
		}

		// prefer authors
		final ResourcePersonRelation relationByRelationType = findRelationByRelationType(PersonResourceRelationType.AUTHOR, relations);
		if (present(relationByRelationType)) {
			return relationByRelationType;
		}

		// fall back
		return relations.get(0);
	}

	private static ResourcePersonRelation findRelationByRelationType(PersonResourceRelationType relationType, List<ResourcePersonRelation> relations) {
		for (ResourcePersonRelation relation : relations) {
			if (relationType.equals(relation.getRelationType())) {
				return relation;
			}
		}

		return null;
	}

	private static ResourcePersonRelation findRelationByType(String type, List<ResourcePersonRelation> relations) {
		for (ResourcePersonRelation relation : relations) {
			if (type.equals(relation.getPost().getResource().getType())) {
				return relation;
			}
		}

		return null;
	}

	/**
	 * finds the index of the person in the author or editor list of the publication
	 * @param person
	 * @param resource
	 * @return
	 */
	public static int findIndexOfPerson(final Person person, final BibTex resource) {
		final int indexOfPerson = findIndexOfPerson(person, resource.getAuthor());
		if (indexOfPerson >= 0) {
			return indexOfPerson;
		}

		return findIndexOfPerson(person, resource.getEditor());
	}

	/**
	 * finds the index of the person in the person list
	 * see PersonNameUtils.getPositionsInPersonList (this list does not norm the person name)
	 *
	 * @param person
	 * @param personNames
	 * @return
	 */
	public static int findIndexOfPerson(final Person person, final List<PersonName> personNames) {
		if (!present(personNames)) {
			return -1;
		}

		// first try the main name to prefer it (mainname is also in the person name list of a person)
		final int mainNameIndex = personNames.indexOf(person.getMainName());

		if (mainNameIndex >= 0) {
			return mainNameIndex;
		}

		// now try the other names
		for (final PersonName personName : person.getNames()) {
			final int personNameIndex = personNames.indexOf(personName);

			if (personNameIndex >= 0) {
				return personNameIndex;
			}
		}

		return -1;
	}

	/**
	 * Returns the relation of the person of a post. Is either AUTHOR or EDITOR
	 * @param person
	 * @param resource
	 * @return
	 */
	public static PersonResourceRelationType getRelationType(final Person person, final BibTex resource){
		if (!present(resource)) {
			return null;
		}
		final PersonName name = person.getMainName();
		final List<PersonName> author = resource.getAuthor();
		if (present(author) && author.contains(name)) {
			return PersonResourceRelationType.AUTHOR;
		}
		final List<PersonName> editor = resource.getEditor();
		if (present(editor) && editor.contains(name)) {
			return PersonResourceRelationType.EDITOR;
		}
		return null;
	}

	/**
	 * Get the person's additional key specified by the key name
	 *
	 * @param person	the person to get additional key of
	 * @param keyName	the key name
	 * @return the additional key as an object, null if not found
	 */
	public static AdditionalKey getAdditionalKey(final Person person, final String keyName) {
		for (AdditionalKey additionalKey : person.getAdditionalKeys()) {
			if (additionalKey.getKeyName().equalsIgnoreCase(keyName)) {
				return additionalKey;
			}
		}
		return null;
	}

	/**
	 * Add a new additional key to the person with key name and value
	 *
	 * @param person	the person add a key to
	 * @param keyName	the key name
	 * @param keyValue	the key value
	 * @return true, if added or exact same key was already present, false if key couldn't be added
	 */
	public static boolean addAdditionalKey(final Person person, final String keyName, final String keyValue) {
		AdditionalKey additionalKey = new AdditionalKey(keyName, keyValue);
		return addAdditionalKey(person, additionalKey);
	}

	/**
	 * Add a new additional key to the person with an additional key object
	 * @param person			the person to add a key to
	 * @param additionalKey		the additional key object
	 * @return true, if added or exact same key was already present, false if key couldn't be added
	 */
	public static boolean addAdditionalKey(final Person person, final AdditionalKey additionalKey) {
		AdditionalKey foundKey = getAdditionalKey(person, additionalKey.getKeyName());

		if (present(foundKey)) {
			// Return true, if exact same key was already present
			// Return false, if key name with different value was found
			return foundKey.getKeyValue().equals(additionalKey.getKeyName());
		} else {
			// Adding key
			person.getAdditionalKeys().add(additionalKey);
			return true;
		}
	}

	/**
	 * Remove an additional key from the person specified by the key name
	 *
	 * @param person	the person to remove a key from
	 * @param keyName	the key name
	 * @return true, if removing it was successful or key wasn't present. false otherwise
	 */
	public static boolean removeAdditionalKey(Person person, final String keyName) {
		AdditionalKey foundKey = getAdditionalKey(person, keyName);
		if (present(foundKey)) {
			person.getAdditionalKeys().remove(foundKey);
			return true;
		}
		return false;
	}

	/**
	 * Update an additional key of the person specified by the key name
	 *
	 * @param person	the person to update
	 * @param keyName	the key name
	 * @param keyValue	the new key value
	 * @return			true, if additional key was updated. False, if key wasn't found to update
	 */
	public static boolean updateAdditionalKey(final Person person, final String keyName, final String keyValue) {
		AdditionalKey foundKey = getAdditionalKey(person, keyName);
		if (present(foundKey)) {
			foundKey.setKeyValue(keyValue);
			return true;
		}
		return false;
	}
}
