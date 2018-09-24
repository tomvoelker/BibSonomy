package org.bibsonomy.search.es.index.converter.person;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.util.Converter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * converts {@link Person}s to Elasticsearch representation
 *
 * @author dzo
 */
public class PersonConverter implements Converter<Person, Map<String, Object>, Object> {
	private static final Log LOG = LogFactory.getLog(PersonConverter.class);

	@Override
	public Map<String, Object> convert(final Person person) {
		final Map<String, Object> mapping = new HashMap<>();
		mapping.put(PersonFields.PERSON_ID, person.getPersonId());
		mapping.put(PersonFields.ACADEMIC_DEGREE, person.getAcademicDegree());
		mapping.put(PersonFields.COLLEGE, person.getCollege());
		final URL homepage = person.getHomepage();
		if (present(homepage)) {
			mapping.put(PersonFields.HOMEPAGE, homepage.toString());
		}
		mapping.put(PersonFields.EMAIL, person.getEmail());
		mapping.put(PersonFields.ORCID_ID, person.getOrcid());
		mapping.put(PersonFields.USER_NAME, person.getUser());
		// FIXME: add researcher id
		// mapping.put(PersonFields.RESEARCHER_ID, person.getResearcherId());

		final Gender gender = person.getGender();
		if (present(gender)) {
			mapping.put(PersonFields.GENDER, gender.toString());
		}

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
		mapping.put(PersonFields.CHANGE_DATE, ElasticsearchUtils.dateToString(person.getChangeDate()));
		return mapping;
	}

	@Override
	public Person convert(Map<String, Object> source, Object options) {
		final Person person = new Person();
		person.setPersonId((String) source.get(PersonFields.PERSON_ID));
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
		// FIXME: add researcher id
		person.setUser((String) source.get(PersonFields.USER_NAME));
		person.setGender(Gender.valueOf((String) source.get(PersonFields.GENDER)));
		person.setNames(convertToNames(source.get(PersonFields.NAME)));

		return person;
	}

	private static List<PersonName> convertToNames(Object o) {
		final List<Map<String, Object>> values = (List<Map<String, Object>>) o;

		final List<PersonName> personNames = new LinkedList<>();

		for (Map<String, Object> personMapping : values) {
			final String personNameStr = (String) personMapping.get(PersonFields.NAME);
			final PersonName personName = PersonNameUtils.discoverPersonNamesIgnoreExceptions(personNameStr).get(0);
			personName.setMain(Boolean.parseBoolean((String) personMapping.get(PersonFields.MAIN)));
			personNames.add(personName);
		}

		return personNames;
	}
}
