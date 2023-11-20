/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.index.converter.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.search.es.index.converter.person.PersonFields;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * converts a person to a elasticsearch document for a cris link
 *
 * @author dzo
 */
public class CRISTargetPersonConverter implements CRISEntityConverter<Person, Map<String, Object>, Object> {
	private static final Log LOG = LogFactory.getLog(CRISTargetPersonConverter.class);

	private static List<PersonName> convertToNames(Object o) {
		final List<Map<String, Object>> values = (List<Map<String, Object>>) o;

		final List<PersonName> personNames = new LinkedList<>();

		for (final Map<String, Object> personMapping : values) {
			final String personNameStr = (String) personMapping.get(PersonFields.NAME);
			final List<PersonName> parsedPersonNames = PersonNameUtils.discoverPersonNamesIgnoreExceptions(personNameStr);
			if (!present(parsedPersonNames)) {
				LOG.error("error parsing " + personNameStr);
				throw new IllegalStateException("error while converting es document to person");
			}

			final PersonName personName = parsedPersonNames.get(0);
			personName.setMain((Boolean) personMapping.get(PersonFields.MAIN));
			personNames.add(personName);
		}

		return personNames;
	}

	@Override
	public boolean canConvert(Linkable linkable) {
		return linkable instanceof Person;
	}

	@Override
	public Map<String, Object> convert(final Person person) {
		final Map<String, Object> mapping = new HashMap<>();
		mapping.put(PersonFields.PERSON_ID, person.getPersonId());

		// map all names
		final List<PersonName> names = person.getNames();
		final List<Map<String, Object>> convertedNames = new LinkedList<>();
		for (final PersonName name : names) {
			final Map<String, Object> convertedPerson = new HashMap<>();
			convertedPerson.put(PersonFields.NAME, PersonNameUtils.serializePersonName(name));
			convertedPerson.put(PersonFields.MAIN, name.isMain());
			convertedNames.add(convertedPerson);
		}

		mapping.put(PersonFields.NAMES, convertedNames);

		return mapping;
	}

	@Override
	public Person convert(Map<String, Object> source, Object options) {
		final Person person = new Person();

		final String personID = (String) source.get(PersonFields.PERSON_ID);
		LOG.debug("converting person '" + personID + "'");
		person.setPersonId(personID);
		person.setNames(convertToNames(source.get(PersonFields.NAMES)));

		return person;
	}
}
