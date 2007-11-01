/*
 * Created on 15.10.2007
 */
package org.bibsonomy.model;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Name of a person
 * 
 * @author Jens Illig
 */
public class PersonName {
	/** pattern that matches only on positive numbers */
	private static final Pattern numbersPattern = Pattern.compile("[0-9]+");
	
	private String name;
	private String firstName;
	private String lastName;
	
	/**
	 * @return the firstname(s) of the person
	 */
	public String getFirstName() {
		return this.firstName;
	}
	
	/**
	 * @return the lastname(s) of the person
	 */
	public String getLastName() {
		return this.lastName;
	}
	
	/**
	 * @return the full name of the person
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * sets the full name and tries to set first- and lastname from extracted values also. 
	 * @param name the full name of the person
	 */
	public void setName(String name) {
		this.name = name;
		this.discoverFirstAnLastName();
	}
	
	/**
	 * Tries to detect the firstname and lastname of each author or editor.
	 * Firstnames must be abbreviated with a '.' to be identified as firstnames.
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
