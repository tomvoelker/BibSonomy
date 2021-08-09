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
package org.bibsonomy.search.es.index.converter.person;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.search.es.index.converter.cris.CRISTargetPersonConverter;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * converts {@link Person}s to Elasticsearch representation
 *
 * @author dzo
 */
public class PersonConverter extends CRISTargetPersonConverter {
	private static final Log LOG = LogFactory.getLog(PersonConverter.class);

	@Override
	public Map<String, Object> convert(final Person person) {
		final Map<String, Object> mapping = super.convert(person);

		final String mainName = PersonNameUtils.serializePersonName(person.getMainName());
		mapping.put(PersonFields.MAIN_NAME, mainName);
		mapping.put(PersonFields.MAIN_NAME_PREFIX, ElasticsearchIndexSearchUtils.getPrefixForString(mainName));
		mapping.put(PersonFields.PERSON_DATABASE_ID, person.getPersonChangeId());

		mapping.put(PersonFields.ACADEMIC_DEGREE, person.getAcademicDegree());
		mapping.put(PersonFields.COLLEGE, person.getCollege());
		final URL homepage = person.getHomepage();
		if (present(homepage)) {
			mapping.put(PersonFields.HOMEPAGE, homepage.toString());
		}
		mapping.put(PersonFields.EMAIL, person.getEmail());
		mapping.put(PersonFields.ORCID_ID, person.getOrcid());
		mapping.put(PersonFields.USER_NAME, person.getUser());
		mapping.put(PersonFields.RESEARCHER_ID, person.getResearcherid());

		final Gender gender = person.getGender();
		if (present(gender)) {
			mapping.put(PersonFields.GENDER, gender.toString());
		}

		mapping.put(PersonFields.CHANGE_DATE, ElasticsearchUtils.dateToString(person.getChangeDate()));
		mapping.put(PersonFields.JOIN_FIELD, Collections.singletonMap("name", PersonFields.TYPE_PERSON));
		return mapping;
	}

	@Override
	public Person convert(Map<String, Object> source, Object options) {
		final Person person = super.convert(source, options);

		person.setAcademicDegree((String) source.get(PersonFields.ACADEMIC_DEGREE));
		person.setCollege((String) source.get(PersonFields.COLLEGE));

		final String url = (String) source.get(PersonFields.HOMEPAGE);
		try {
			person.setHomepage(new URL(url));
		} catch (final MalformedURLException e) {
			LOG.info("error converting url " + url);
		}

		person.setEmail((String) source.get(PersonFields.EMAIL));
		person.setOrcid((String) source.get(PersonFields.ORCID_ID));
		person.setResearcherid((String) source.get(PersonFields.RESEARCHER_ID));
		person.setUser((String) source.get(PersonFields.USER_NAME));
		final String genderString = (String) source.get(PersonFields.GENDER);
		if (present(genderString)) {
			person.setGender(Gender.valueOf(genderString));
		}

		person.setChangeDate(ElasticsearchUtils.parseDate(source, PersonFields.CHANGE_DATE));

		return person;
	}
}
