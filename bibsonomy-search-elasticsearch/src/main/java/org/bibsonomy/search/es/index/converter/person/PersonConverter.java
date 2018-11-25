package org.bibsonomy.search.es.index.converter.person;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.search.es.index.converter.cris.CRISTargetPersonConverter;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;

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
