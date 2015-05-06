/**
 * BibSonomy-Logging - Logs clicks from users of the BibSonomy webapp.
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
package org.bibsonomy.gImporter;

/**
 * @author sst
 */
public class gImporterData {


	/** date of insertion of logdata */
	private String title;

	private String year;
	
	private String address;
	
	private boolean diss;
	
	@Override
	public String toString() {
		return "\ntitle: "+ this.title;		
	}	

	/**
	 * @return the logdate
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param logdate the logdate to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	
	/**
	 * @return the address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

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
	 * @return the year
	 */
	public String getYear() {
		return this.year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the diss
	 */
/*	public boolean getDiss() {
		return this.diss;
	}
*/
	/**
	 * @param diss the diss to set
	 */
	/*public void setDiss(boolean diss) {
		this.diss = diss;
	}*/

}
