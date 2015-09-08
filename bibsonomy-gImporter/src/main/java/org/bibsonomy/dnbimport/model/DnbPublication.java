/**
 * BibSonomy-gImporter - exports from dnb2, imports to bibsonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.dnbimport.model;

import java.util.List;

import org.bibsonomy.common.Pair;


/**
 * @author sst
 */
public class DnbPublication {
	private String titleId;
	
	private String mainTitle;
	
	private String subTitle;

	private String subYear;
	private String pubYear;
	
	private String schoolP1;
	private String schoolP2;
	
	private boolean diss;
	private boolean habil;
	
	private List<DnbPerson> persons;
	
	private List<Pair<ClassificationScheme, String>> classInfos;
	

	/**
	 * @return the diss
	 */
	public boolean isDiss() {
		return this.diss;
	}

	/**
	 * @param diss the diss to set
	 */
	public void setDiss(boolean diss) {
		this.diss = diss;
	}

	/**
	 * @return the mainTitle
	 */
	public String getMainTitle() {
		return this.mainTitle;
	}

	/**
	 * @param mainTitle the mainTitle to set
	 */
	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
	}

	/**
	 * @return the subTitle
	 */
	public String getSubTitle() {
		return this.subTitle;
	}

	/**
	 * @param subTitle the subTitle to set
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/**
	 * @return the schoolP1
	 */
	public String getSchoolP1() {
		return this.schoolP1;
	}

	/**
	 * @param schoolP1 the schoolP1 to set
	 */
	public void setSchoolP1(String schoolP1) {
		this.schoolP1 = schoolP1;
	}

	/**
	 * @return the schoolP2
	 */
	public String getSchoolP2() {
		return this.schoolP2;
	}

	/**
	 * @param schoolP2 the schoolP2 to set
	 */
	public void setSchoolP2(String schoolP2) {
		this.schoolP2 = schoolP2;
	}

	/**
	 * @return the habil
	 */
	public boolean isHabil() {
		return this.habil;
	}

	/**
	 * @param habil the habil to set
	 */
	public void setHabil(boolean habil) {
		this.habil = habil;
	}

	/**
	 * @return the subYear
	 */
	public String getSubYear() {
		return this.subYear;
	}

	/**
	 * @param subYear the subYear to set
	 */
	public void setSubYear(String subYear) {
		this.subYear = subYear;
	}

	/**
	 * @return the pubYear
	 */
	public String getPubYear() {
		return this.pubYear;
	}

	/**
	 * @param pubYear the pubYear to set
	 */
	public void setPubYear(String pubYear) {
		this.pubYear = pubYear;
	}

	public String getTitleId() {
		return this.titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public List<DnbPerson> getPersons() {
		return this.persons;
	}

	public void setPersons(List<DnbPerson> persons) {
		this.persons = persons;
	}

	public List<Pair<ClassificationScheme, String>> getClassInfos() {
		return this.classInfos;
	}

	public void setClassInfos(List<Pair<ClassificationScheme, String>> classInfos) {
		this.classInfos = classInfos;
	}


}
