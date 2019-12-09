package org.bibsonomy.model;

import java.io.Serializable;

/**
 * PhD Advisor Recommendations for a person with a thesis (doctor).
 *
 * @author jhi
 */
public class PhDRecommendation implements Serializable{

	private static final long serialVersionUID = -6572434911635271629L;
	
	private Person doctor;
	private Person advisor;
	private Float confidence;
	private int rank;
	/**
	 * @return the doctor
	 */
	public Person getDoctor() {
		return this.doctor;
	}
	/**
	 * @param doctor the doctor to set
	 */
	public void setDoctor(Person doctor) {
		this.doctor = doctor;
	}
	/**
	 * @return the advisor
	 */
	public Person getAdvisor() {
		return this.advisor;
	}
	/**
	 * @param advisor the advisor to set
	 */
	public void setAdvisor(Person advisor) {
		this.advisor = advisor;
	}
	/**
	 * @return the confidence
	 */
	public Float getConfidence() {
		return this.confidence;
	}
	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(Float confidence) {
		this.confidence = confidence;
	}
	/**
	 * @return the rank
	 */
	public int getRank() {
		return this.rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	
}
