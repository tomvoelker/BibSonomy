/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unikassel.puma.webapp.command.ajax;

import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author clemens
 */
public class OpenAccessCommand extends AjaxCommand<String> {

	/**
	 * publisher to check
	 */
	private String publisher;
	private String jTitle;
	private String qType;
	private String interhash = "";	

	/**
	 * @return publisher
	 */
	public String getPublisher() {
		return this.publisher;
	}

	/**
	 * @param publisher
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return jTitle
	 */
	public String getjTitle() {
		return this.jTitle;
	}

	/**
	 * @param jTitle
	 */
	public void setjTitle(String jTitle) {
		this.jTitle = jTitle;
	}

	/**
	 * @return qType
	 */
	public String getqType() {
		return this.qType;
	}

	/**
	 * @param qType
	 */
	public void setqType(String qType) {
		this.qType = qType;
	} 	
	
	
	/**
	 * @param interhash 
	 */
	public void setInterhash(String interhash) {
		this.interhash = interhash;
	}

	/**
	 * @return the interhash
	 */
	public String getInterhash() {
		return interhash;
	}

	
	
}
