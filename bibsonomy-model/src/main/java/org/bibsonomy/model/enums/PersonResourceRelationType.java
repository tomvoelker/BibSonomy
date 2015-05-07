package org.bibsonomy.model.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.Resource;

/**
 * relations that may hold between a {@link Person} and a {@link Resource}
 *
 * @author jil
 */
public enum PersonResourceRelationType {
	/**
	 * doctor vater
	 */
	THESIS_DOCTOR_VATER("Bdtv"),
	/**
	 * first reviewer of thesis
	 */
	THESIS_FIRST_REVIEWER("B1st"),
	/**
	 * reviewer of a thesis
	 */
	THESIS_REVIEWER("Mrev"),
	
	/**
	 * thesis advisor
	 */
	THESIS_ADVISOR("Mths"),
	/**
	 * Author
	 */
	AUTHOR("Maut"),
	/**
	 * some non-specific relation influence
	 */
	OTHER("Moth");
	
	private final String relatorCode;
	private static final Map<String, PersonResourceRelationType> byRelatorCode = new HashMap<String, PersonResourceRelationType>();
	
	static {
		for (PersonResourceRelationType value : PersonResourceRelationType.values()) {
			byRelatorCode.put(value.getRelatorCode(), value);
		}
	}

	private PersonResourceRelationType(String relatorCode) {
		this.relatorCode = relatorCode;
	}
	
	/**
	 * @return the relatorCode
	 */
	public String getRelatorCode() {
		return this.relatorCode;
	}
	
	public static PersonResourceRelationType getByRelatorCode(String relatorCode) {
		final PersonResourceRelationType rVal = byRelatorCode.get(relatorCode);
		if (rVal == null) {
			throw new NoSuchElementException(relatorCode);
		}
		return rVal;
	}

}
