package org.bibsonomy.entity;

import java.util.jar.Attributes.Name;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.io.Reader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;


import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.datasource.jndi.JndiDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.annotations.Param;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.util.StringUtils;

public class entityIdentification {

	public static void main(String[] args) throws PersonListParserException {
		
		float timeAtStart = System.nanoTime();
		

		float timeAtstart = System.nanoTime();
			

		String resource = "config.xml";
		Reader reader;
				
		System.exit(0);
		List<String> authorList = null;
		try {
			reader = Resources.getResourceAsReader(resource);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			
			SqlSession session = sqlMapper.openSession();
			try {
			authorList = session.selectList("org.mybatis.example.Entity-Identification.selectBibtex", 1);
			//author = (String)session.selectOne(
			//"org.mybatis.example.BlogMapper.selectBibtex");
			System.out.println(authorList.get(3));
			} finally {
			session.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String resourceRkr = "config.xml";
		Reader readerRkr;
		
		try {
			readerRkr = Resources.getResourceAsReader(resourceRkr);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(readerRkr);
			
			SqlSession sessionRkr = sqlMapper.openSession();

		//TODO List<List<PersonName>> coAuthorList = new ArrayList<List<PersonName>>(0);
		//TODO List<String> allPersons = new ArrayList<String>(0);
			

		
		//read all entries from bibtex and save it to author table

		for (String authors: authorList) { //authorList for each publication
			final List<PersonName> allAuthorNamesOfOnePublication = PersonNameUtils.discoverPersonNames(authors);
						
			for (PersonName author: allAuthorNamesOfOnePublication) { //each author in the list of authors
				sessionRkr.commit();
			     HashMap<String, String> authorName = new HashMap<String, String>();

			     authorName.put("firstName", author.getFirstName());
			     authorName.put("lastName", author.getLastName());
			     authorName.put("normalizedName", author.getFirstName() + author.getLastName());
			 
			     sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthor", authorName);			 

			     authorName.put("firstName", StringUtil.foldToASCII(author.getFirstName()));
			     authorName.put("lastName", StringUtil.foldToASCII(author.getLastName()));
			     authorName.put("normalizedName", normalizePerson(author));

			     sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthor", authorName);
			     
				 List<Integer> lastAuthorWithThisNameInsertedAuthorId = null;
				 lastAuthorWithThisNameInsertedAuthorId = sessionRkr.selectList("org.mybatis.example.Entity-Identification.lastIDInsertAuthor", authorName);	 
				 
				 boolean firstInsert = true;
				 List<Integer> listID = null;
				 if ( allAuthorNamesOfOnePublication.size() > 1) { //there are no coauthors if size == 1
					 for (PersonName coAuthor: allAuthorNamesOfOnePublication) { //add the coauthors to database
						 if (!coAuthor.equals(author)) { 
							 HashMap<String,String> coAuthorParameter = new HashMap<String,String>();
							 coAuthorParameter.put("normalizedName", normalizePerson(coAuthor));
							 coAuthorParameter.put("authorID", lastAuthorWithThisNameInsertedAuthorId.get(0) + "");
						 
							 if (firstInsert) {
								 sessionRkr.insert("org.mybatis.example.Entity-Identification.insertFirstCoAuthor", coAuthorParameter);
								 listID = sessionRkr.selectList("org.mybatis.example.Entity-Identification.lastInsertID");
								 coAuthorParameter.put("listID", listID.get(0) + "");
								 sessionRkr.insert("org.mybatis.example.Entity-Identification.insertFirstCoAuthor2", coAuthorParameter);	 
								 firstInsert = false;	 
							 }
							 else {
								 coAuthorParameter.put("listID", listID.get(0) + "");
								 sessionRkr.insert("org.mybatis.example.Entity-Identification.insertCoAuthor", coAuthorParameter);
							 }
						 }
					 }
				 }
			}
		}
		
		int threshold = 2;
		List<String> authorNames = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorNames");
		
		//check the name for every author
		for(int m=0; m < authorNames.size(); m++) {
			//do this as long there is something we can merge
			while (true) {
				//merge authors who have the same coauthors
				List<Map<String,String>> authorsWithNameX = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectCoAuthors", authorNames.get(m));
				if (authorsWithNameX.isEmpty()) {
					m++;
					continue;
				}

				
		//cluster the author table
		//HashMap<String, List<String>> authorCluster = new HashMap<String, List<String>>();
		//authorCluster = sessionRkr.selectMap("org.mybatis.example.Entity-Identification.lastIDInsertAuthor");
		//System.out.println();

				Iterator outerItr = authorsWithNameX.iterator();
		
				int innerAuthorID=0, maxAuthorID=0;
				int outerAuthorID = 0, outerMaxAuthorID=0, tmpInnerMaxAuthorID=0;
				int counter=0, max=0;
				int outerMax = 0;
				int firstAuthorID = Integer.parseInt(authorsWithNameX.get(0).get("author_id"));
			
				//tmp lists to compare within the iterations
				List<String> coAuthorNamesOuterIteration = new ArrayList<String>();		
				List<String> coAuthorNamesInnerIteration = new ArrayList<String>();
		
				//here we save our tmp results we use later to merge the both authors 
				List<String> innerCoauthors = new ArrayList<String>();		
				List<String> outerCoauthors = new ArrayList<String>();
		
				while (outerItr.hasNext()) {
					Map<String,String> outerCoAuthor = (Map)outerItr.next();
					coAuthorNamesOuterIteration.add(outerCoAuthor.get("normalized_coauthor"));
					if (outerAuthorID != Integer.parseInt(outerCoAuthor.get("author_id"))) {
						outerAuthorID = Integer.parseInt(outerCoAuthor.get("author_id"));
				
						Iterator innerItr = authorsWithNameX.iterator();
						while (innerItr.hasNext()) {
							Map<String,String> innerCoAuthor = (Map)innerItr.next();
					
							if (Integer.parseInt(outerCoAuthor.get("author_id")) == Integer.parseInt(innerCoAuthor.get("author_id"))) continue;
				
							if (innerAuthorID != Integer.parseInt(innerCoAuthor.get("author_id"))) {		
								for (int k=0; k < coAuthorNamesOuterIteration.size(); k++) {
									if (coAuthorNamesInnerIteration.contains(coAuthorNamesOuterIteration.get(k))) counter++;
								}
								coAuthorNamesInnerIteration.clear();
						
								if (counter > max) {
									max = counter;
									maxAuthorID = innerAuthorID;
								}
							
								innerAuthorID = Integer.parseInt(innerCoAuthor.get("author_id"));
								counter = 0;
							}
					
							coAuthorNamesInnerIteration.add(innerCoAuthor.get("normalized_coauthor"));	 
						}
						coAuthorNamesOuterIteration.clear();
						if (max > outerMax) {
							outerMax = max;
							outerMaxAuthorID = Integer.parseInt(outerCoAuthor.get("author_id"));
							tmpInnerMaxAuthorID = innerAuthorID;
							outerCoauthors = coAuthorNamesOuterIteration;
							innerCoauthors = coAuthorNamesInnerIteration;
						}
					}
				}
		
				System.out.println("OuterMax: " + outerMax + " - Merge " + outerAuthorID + " with " + tmpInnerMaxAuthorID);
		
				//end when there are no more authors to merge
				if (outerMax < threshold) {
					System.out.println("end this");
					break;
				}
			
				List<String> coauthorsToAdd = new ArrayList<String>();
				//calculate the authors we have to add		
				for(int k=0; k < innerCoauthors.size(); k++) { 
					if(!outerCoauthors.contains(innerCoauthors.get(k))) coauthorsToAdd.add(innerCoauthors.get(k));
				}
	
		
				for(int k=0;k < coauthorsToAdd.size(); k++) {
					HashMap<String, String> coAuthorToAdd = new HashMap<String, String>();
					coAuthorToAdd.put("authorID", outerMaxAuthorID + "");
					coAuthorToAdd.put("normalizedCoauthor", coauthorsToAdd.get(k));
					System.out.println("We have to add: " + coAuthorToAdd.get("authorID") + " " + coAuthorToAdd.get("normalizedCoauthor"));
			
					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertMergedCoAuthor", coAuthorToAdd);			 
				}
		
				System.out.println("we delete: " + tmpInnerMaxAuthorID);
				//delete the author we merged
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteAuthor", tmpInnerMaxAuthorID);
			}
		}
		
		System.out.println("Elapsed time: " + ((System.nanoTime() - timeAtStart)/1000000000) + "s");
		
		/*
		//Soundex
		Soundex soundex = new Soundex();
		System.out.println("First Name: " + personNames.get(1).getFirstName() + " Last Name: " + personNames.get(1).getLastName());
		System.out.println("Soundex Code Last Name: " + soundex.encode(personNames.get(0).getLastName()));
		System.out.println("Soundex Code First Name: " + soundex.encode(personNames.get(0).getFirstName()));
		*/

		sessionRkr.commit();
		sessionRkr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String normalizePersonName(PersonName personName) {
		//reduce the
		String newFirstName = null;
		if (personName.getFirstName() != null) {
			newFirstName = personName.getFirstName().substring(0,1);
		}
			
		/*check if firstName is shortened
		if (personName.getFirstName().length() == 2 && personName.getFirstName().substring(1,2).equals(".")) {
		
		}
		*/
		
		String normalizedName = newFirstName + personName.getLastName();
		return normalizedName.toLowerCase();		
	}

	
	/**
	 * Extracts from the last name the last part and cleans it. I.e., from 
	 * "van de Gruyter" we get "gruyter"
	 * 
	 * @param last
	 * @return
	 */
	private static String getLast(final String last) {
		/*
		 * A name enclosed in brackets {Like this One} is detected as a single 
		 * last name. We here re-parse such names to extract the "real" name.
		 */
		final String trimmedLast = last.trim();
		if (trimmedLast.startsWith("{") && trimmedLast.endsWith("}")) {
			final List<PersonName> name = PersonNameUtils.discoverPersonNamesIgnoreExceptions(trimmedLast.substring(1, trimmedLast.length() - 1));
			//return name.get(0);
			return "test";
		} 
		/*
		 * We remove all unusual characters.
		 */
		final String cleanedLast = StringUtils.removeNonNumbersOrLettersOrDotsOrCommaOrSpace(trimmedLast).toLowerCase().trim();
		/*
		 * If we find a space character, we take the last part of the name
		 */
		final int pos = cleanedLast.lastIndexOf(' ');
		return pos > 0 ? cleanedLast.substring(pos + 1) : cleanedLast;
	}

	
	public static String normalizePerson(final PersonName personName) {
		final String first = personName.getFirstName();
		final String last  = personName.getLastName();
		if (present(first) && !present(last)) {
			/*
			 * Only the first name is given. This should practically never happen,
			 * since we put such names into the last name field.
			 * 
			 */
			return StringUtils.removeNonNumbersOrLettersOrDotsOrCommaOrSpace(first).toLowerCase();
		}
		if (present(first) && present(last)) {
			/*
			 * First and last given - default.
			 * Take the first letter of the first name and append the last part
			 * of the last name.
			 */
			return getFirst(first) + "." + getLast(last);
		}
		if (present(last)) {
			/*
			 * Only last name available - could be a "regular" name enclosed
			 * in brackets.
			 */
			return getLast(last);
		}
		return "";
	}	
	
	private static final Pattern SINGLE_LETTER = Pattern.compile("(\\p{L})");
	
	private static String getFirst(final String first) {
		final Matcher matcher = SINGLE_LETTER.matcher(first);
		if (matcher.find()) {
			return matcher.group(1).toLowerCase();
		}
		return "";
	}
	
	public static boolean present(final String string) {
		return ((string != null) && (string.trim().length() > 0));
	}
	
}
