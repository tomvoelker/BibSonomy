/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
