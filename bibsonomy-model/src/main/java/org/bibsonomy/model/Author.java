/*
 * Created on 15.10.2007
 */
package org.bibsonomy.model;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Author {
	private static final Pattern numbersPattern = Pattern.compile("[0-9]+"); // only numbers
	private String name;
	private String firstName;
	private String lastName;
	
	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
		this.discoverFirstAnLastName();
	}
	
	/**
	 * Tries to detect the firstname and lastname of each author or editor.
	 */
	private void discoverFirstAnLastName() {
		if (this.name != null) {
			/*
			 * extract all parts of a name
			 */
			List<String> nameList = new LinkedList<String>();
			StringTokenizer token = new StringTokenizer(this.name);
			while (token.hasMoreTokens()) {
				/*
				 * ignore numbers (from DBLP author names) 
				 */
				final String part = token.nextToken();
				if (!numbersPattern.matcher(part).matches()) {
					nameList.add(part);
				}
			}

			/*
			 * detect firstname and lastname
			 */
			final StringBuilder firstNameBuilder = new StringBuilder();
			int i = 0;
			while (i < nameList.size() - 1) { // iterate up to the last but one part
				final String part = nameList.get(i++);
				firstNameBuilder.append(part + " ");
				/*
				 * stop, if this is the last abbreviated forename 
				 */
				if (part.contains(".") && !nameList.get(i).contains(".")) {
					break;
				}
			}

			final StringBuilder lastNameBuilder = new StringBuilder();
			while (i < nameList.size()) {
				lastNameBuilder.append(nameList.get(i++) + " ");
			}

			this.firstName = firstNameBuilder.toString().trim();
			this.lastName = lastNameBuilder.toString().trim();
		}	
	}
}
