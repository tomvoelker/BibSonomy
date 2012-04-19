package org.bibsonomy.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.lucene.queryParser.ParseException;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;

public class MyOwnTest {
	public static void findSamePersonDifferentNames(SqlSession session) {
		 List<Map<String,String>> myOwnAuthorList = session.selectList("org.mybatis.example.Entity-Identification.selectMyOwn", 1);

		//count the first and last names and define the name of the user as the most counted first and last name
		String userName = null;
		int userNameCount = 0;
		HashMap<String,Integer> saveFirstNameCount = new HashMap<String,Integer>();
		HashMap<String,Integer> saveLastNameCount = new HashMap<String,Integer>();
		List<String> firstNamesAlreadyCounted = new ArrayList<String>();
		List<String> lastNamesAlreadyCounted = new ArrayList<String>();
		List<String> publicationsForThisUsername = new ArrayList<String>();
		for(int k=0; k < myOwnAuthorList.size(); k++) {
			publicationsForThisUsername.add(myOwnAuthorList.get(k).get("author"));
			List<PersonName> authors;
			try {
				authors = PersonNameUtils.discoverPersonNames(myOwnAuthorList.get(k).get("author"));
				//count the name only one time for each publication e.g. Alexander X. and Alexander Y. shouldnt be the same person
				firstNamesAlreadyCounted.clear();
				lastNamesAlreadyCounted.clear();
				for(PersonName author: authors) {
					int countFirstName = 0, countLastName = 0;
					if (saveFirstNameCount.containsKey(author.getFirstName())) {
						if (!firstNamesAlreadyCounted.contains(author.getFirstName())) {
							countFirstName = saveFirstNameCount.get(author.getFirstName());
							saveFirstNameCount.put(author.getFirstName(),++countFirstName);
							firstNamesAlreadyCounted.add(author.getFirstName());
						}
					}
					else {
						saveFirstNameCount.put(author.getFirstName(),1);
						firstNamesAlreadyCounted.add(author.getFirstName());
					}

					if (saveLastNameCount.containsKey(author.getLastName())) {
						if (!lastNamesAlreadyCounted.contains(author.getLastName())) {
							countLastName = saveLastNameCount.get(author.getLastName());
							saveLastNameCount.put(author.getLastName(),++countLastName);
							lastNamesAlreadyCounted.add(author.getLastName());
						}
					}
					else {
						saveLastNameCount.put(author.getLastName(),1);
						lastNamesAlreadyCounted.add(author.getLastName());
					}
				}

				if (userName == null) {
					//System.out.println("user_name: " + myOwnAuthorList.get(k).get("user_name"));
					userName = myOwnAuthorList.get(k).get("user_name");
				}
				else if(!myOwnAuthorList.get(k).get("user_name").equals(userName)) { //here starts a new userName
					if (userNameCount > 1) {
						//System.out.println("---firstName:---");
						String firstNameWithMostCounts = null, lastNameWithMostCounts = null;
						int firstNameCount = 0, lastNameCount = 0;
						for( Map.Entry<String, Integer> e : saveFirstNameCount.entrySet()) {
							if (e.getValue() > firstNameCount) {
								firstNameWithMostCounts = e.getKey();
								firstNameCount = e.getValue();
							}
							//System.out.println(e.getKey() + ": " + e.getValue());
						}
						//System.out.println("winner is: " + firstNameWithMostCounts + " with " + firstNameCount);
						//System.out.println("---lastName:---");
						lastNameWithMostCounts = null;
						lastNameCount = 0;
						for( Map.Entry<String, Integer> e : saveLastNameCount.entrySet()) {
							if (e.getValue() > lastNameCount) {
								lastNameWithMostCounts = e.getKey();
								lastNameCount = e.getValue();
							}
							//System.out.println(e.getKey() + ": " + e.getValue());
						}
						//System.out.println("winner is: " + lastNameWithMostCounts + " with " + lastNameCount);

						//now we think we know the real name and we can check all the names the person is listed in the database with
						String realName = null;
						boolean lastNameTest = false;
						//TODO bernauer: 2 names with the same count -> first and last names may mix up
						//if first names consisting only of a single letter, different persons with this same single letter can dominate the right lastName e.g. like with A. {Vaivads}
						if (firstNameWithMostCounts == null) firstNameWithMostCounts = "null"; //there is no first name
						if (lastNameCount >= firstNameCount || ((firstNameWithMostCounts.length() <= 2) && lastNameCount >= 0.6 * firstNameCount)) {
							realName = lastNameWithMostCounts;
							lastNameTest = true;
						}
						else realName = firstNameWithMostCounts;
						//System.out.println("realName: " + realName);

						HashMap<String,Integer> otherNamesOfThisAuthor = new HashMap<String,Integer>();
						List<PersonName> tempAuthorNames = null;
						publicationsForThisUsername.remove(publicationsForThisUsername.size()-1); //remove the last element
						for (String tempAuthorsList: publicationsForThisUsername) { //go through the publications for this userName
							tempAuthorNames = PersonNameUtils.discoverPersonNames(tempAuthorsList);
							for (PersonName authorName: tempAuthorNames) { //check if there is an author with the same first or last name in the publication
								if(lastNameTest) { //save other lastNames
									if (authorName.getLastName() != null && authorName.getFirstName() != null) {
										if(authorName.getLastName().equals(realName)) {
											int count = 1;
											if (otherNamesOfThisAuthor.containsKey(authorName.getFirstName())) count = otherNamesOfThisAuthor.get(authorName.getFirstName()) + 1;
											otherNamesOfThisAuthor.put(authorName.getFirstName(), count);
										}
									}
								}
								else { //save other firstNames
									if(authorName.getLastName() != null && authorName.getFirstName() != null) {
										if(authorName.getFirstName().equals(realName)) {
											int count = 1;
											if(otherNamesOfThisAuthor.containsKey(authorName.getLastName())) count = otherNamesOfThisAuthor.get(authorName.getLastName()) + 1;
											otherNamesOfThisAuthor.put(authorName.getLastName(), count);
										}
									}
								}
							}
						}
						//System.out.println("this person has the following names:");
						for(Map.Entry<String, Integer> e : otherNamesOfThisAuthor.entrySet()) {
							//System.out.println(e.getKey() + " count: " + e.getValue());
						}
					}

					//delete the name counts
					userNameCount = 0;
					userName = null;
					saveFirstNameCount.clear();
					saveLastNameCount.clear();
					publicationsForThisUsername.clear();
				}
				else userNameCount++;
			} catch (PersonListParserException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
