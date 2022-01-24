/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.webapp.command;

import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;

/**
 * Command class which is used by the MySearchController class.
 * This class stores all information such as relations between several publication informations
 * which are needed by the mySearch.jspx side.
 * 
 * @author Christian Voigtmann
 */
@Getter
@Setter
public class MySearchCommand extends SimpleResourceViewCommand {

	/**
	 * user object
	 */
	private LinkedList<String> tags;
	private LinkedList<String> authors;
	private LinkedList<String> titles;	
	private SortedSet[] tagTitle;
	private SortedSet[] authorTitle;
	private SortedSet[] tagAuthor;
	private SortedSet[] titleAuthor;
	private	String[]	bibtexHash;
	private String[]	bibtexUrls;
	private int    simHash;
	
	private String requGroup;

	/**
	 * default constructor
	 */
	public MySearchCommand() {
		
		tags = new LinkedList<String>();
		authors = new LinkedList<String>();
		titles = new LinkedList<String>();
	}


	/**
	 * 
	 * @return relations between tag and title as a string
	 */
	public String getTagTitle() {
		return getArrayToString(this.tagTitle);
	}

	/**
	 * 
	 * @return relations between author and title as a string
	 */
	public String getAuthorTitle() {
		return getArrayToString(this.authorTitle);
	}

	/**
	 * 
	 * @return relations between tag and author as a string
	 */
	public String getTagAuthor() {
		return getArrayToString(this.tagAuthor);
	}

		/**
	 * 
	 * @return relations between title and author as a string
	 */
	public String getTitleAuthor() {
		return getArrayToString(this.titleAuthor);
	}

	/**
	 * generates a string from given set in javascript array syntax
	 * @param list the set
	 * @return a string of the elements
	 */
	private String getArrayToString(SortedSet[] list) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("[");
		if (list != null) {
			for (int i=0; i<list.length; i++) {
				buf.append("[");
				Iterator iter = list[i].iterator();
				while(iter.hasNext()) {
					buf.append(iter.next());
					if (iter.hasNext()) {
						buf.append(",");
					}
				}

				buf.append("]");
				if (i != (list.length -1))
					buf.append(",");
			}
		}
		
		buf.append("]");
		
		return buf.toString();
	}

}
