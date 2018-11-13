package org.bibsonomy.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.Gender;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * util methods for person matching
 *
 * @author dzo
 * @author jhi
 */
public final class PersonMatchUtils {
	private static final Log LOG = LogFactory.getLog(PersonMatchUtils.class);

	private PersonMatchUtils() {
		// noop
	}

	/**
	 * FIXME: why is the value an array of conflicts and not a list
	 *
	 * returns a map that contains for each match in matches a list
	 * @param matches
	 * @return
	 */
	public static Map<Integer, PersonMergeFieldConflict[]> getMergeConflicts(List<PersonMatch> matches){
		//A map with a list of conflicts for every match of a person
		//If a match does not have any conflict it has an entry with an empty list
		Map<Integer, PersonMergeFieldConflict[]> map = new HashMap<>();
		for (PersonMatch match : matches) {
			//the list of all fields that are holding a conflict
			List<PersonMergeFieldConflict> conflictFields = getPersonMergeConflicts(match);
			final PersonMergeFieldConflict[] p = new PersonMergeFieldConflict[conflictFields.size()];
			conflictFields.toArray(p);
			map.put(new Integer(match.getMatchID()), p);
		}
		return map;
	}

	/**
	 * returns a list of conflicts
	 * @param match
	 * @return
	 */
	public static List<PersonMergeFieldConflict> getPersonMergeConflicts(PersonMatch match) {
		List<PersonMergeFieldConflict> conflictFields = new LinkedList<>();
		try {
			for (String fieldName : Person.fieldsWithResolvableMergeConflicts) {
				PropertyDescriptor desc = new PropertyDescriptor(fieldName, Person.class);
				Object person1Value = desc.getReadMethod().invoke(match.getPerson1());
				Object person2Value = desc.getReadMethod().invoke(match.getPerson2());
				if (person1Value != null && person2Value != null) {
					//test if the values are different and add them to the list
					if (person1Value.getClass().equals(String.class)) {
						if (!person1Value.equals(person2Value)) {
							conflictFields.add(new PersonMergeFieldConflict(fieldName, (String) person1Value, (String) person2Value));
						}
					} else if (person1Value.getClass().equals(PersonName.class)) {

						final PersonName personName1 = (PersonName) person1Value;
						final String person1Name = PersonNameUtils.serializePersonName(personName1);
						final PersonName personName2 = (PersonName) person2Value;
						final String person2Name = PersonNameUtils.serializePersonName(personName2);
						if (!person1Name.equals(person2Name)) {
							conflictFields.add(new PersonMergeFieldConflict(fieldName, person1Name, person2Name));
						}
					} else if (person1Value.getClass().equals(Gender.class)) {
						if (!person1Value.equals(person2Value)) {
							conflictFields.add(new PersonMergeFieldConflict(fieldName, ((Gender) person1Value).name(), ((Gender) person2Value).name()));
						}
					} else {
						LOG.warn("Missing " + person1Value.getClass() + " class case for merge conflict detection");
					}
				}
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
				| IntrospectionException e) {
			LOG.error(e);
		}
		return conflictFields;
	}
}
