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
public enum PersonResourceRelation {
	/**
	 * Author
	 */
	AUTHOR("Maut"),
	/**
	 * thesis advisor
	 */
	THESIS_ADVISOR("Mths"),
	/**
	 * reviewer of a thesis
	 */
	THESIS_REVIEWER("Mrev"),
	/**
	 * first reviewer of thesis
	 */
	THESIS_FIRST_REVIEWER("B1st"),
	/**
	 * doctor vater
	 */
	THESIS_DOCTOR_VATER("Bdtv"),
	/**
	 * some non-specific relation influence
	 */
	OTHER("Moth");
	
	private final String relatorCode;
	private static final Map<String, PersonResourceRelation> byRelatorCode = new HashMap<String, PersonResourceRelation>();
	
	static {
		for (PersonResourceRelation value : PersonResourceRelation.values()) {
			byRelatorCode.put(value.getRelatorCode(), value);
		}
	}

	private PersonResourceRelation(String relatorCode) {
		this.relatorCode = relatorCode;
	}
	
	/**
	 * @return the relatorCode
	 */
	public String getRelatorCode() {
		return this.relatorCode;
	}
	
	public static PersonResourceRelation getByRelatorCode(String relatorCode) {
		final PersonResourceRelation rVal = byRelatorCode.get(relatorCode);
		if (rVal == null) {
			throw new NoSuchElementException(relatorCode);
		}
		return rVal;
	}

}
