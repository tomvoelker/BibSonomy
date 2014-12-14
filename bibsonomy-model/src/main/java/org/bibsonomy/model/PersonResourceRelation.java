package org.bibsonomy.model;

/**
 * TODO: add documentation to this class
 *
 * @author Chris
 */
public class PersonResourceRelation {
	
	private int id;
	private String simhash1;
	private String simhash2;
	private String relatorCode;
	private int qualifying;
	private int personNameId;
	private int personId;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the simhash1
	 */
	public String getSimhash1() {
		return this.simhash1;
	}
	/**
	 * @param simhash1 the simhash1 to set
	 */
	public void setSimhash1(String simhash1) {
		this.simhash1 = simhash1;
	}
	/**
	 * @return the simhash2
	 */
	public String getSimhash2() {
		return this.simhash2;
	}
	/**
	 * @param simhash2 the simhash2 to set
	 */
	public void setSimhash2(String simhash2) {
		this.simhash2 = simhash2;
	}
	/**
	 * @return the relatorCode
	 */
	public String getRelatorCode() {
		return this.relatorCode;
	}
	/**
	 * @param relatorCode the relatorCode to set
	 */
	public void setRelatorCode(String relatorCode) {
		this.relatorCode = relatorCode;
	}
	/**
	 * @return the qualifying
	 */
	public int getQualifying() {
		return this.qualifying;
	}
	/**
	 * @param qualifying the qualifying to set
	 */
	public void setQualifying(int qualifying) {
		this.qualifying = qualifying;
	}

	/**
	 * @return the personId
	 */
	public int getPersonId() {
		return this.personId;
	}
	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	
	/**
	 * @return the personNameId
	 */
	public int getPersonNameId() {
		return this.personNameId;
	}
	/**
	 * @param personNameId the personNameId to set
	 */
	public void setPersonNameId(int personNameId) {
		this.personNameId = personNameId;
	}
}
