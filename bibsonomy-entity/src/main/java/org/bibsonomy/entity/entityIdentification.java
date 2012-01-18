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
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;
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
		
		float timeAtstart = System.nanoTime();
		
		String resource = "config.xml";
		Reader reader;
				
		List<String> authorList = null;
		try {
			reader = Resources.getResourceAsReader(resource);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			
			SqlSession session = sqlMapper.openSession();
			try {
			authorList = session.selectList("org.mybatis.example.Entity-Identification.selectBibtex", 1);
			//author = (String)session.selectOne(
			//"org.mybatis.example.BlogMapper.selectBibtex");
			//System.out.println(authorList.get(3));
			} finally {
			session.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String resourceRkr = "configRkr.xml";
		Reader readerRkr;
		
		try {
			readerRkr = Resources.getResourceAsReader(resourceRkr);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(readerRkr);
			
			SqlSession sessionRkr = sqlMapper.openSession();

		List<List<PersonName>> coAuthorList = new ArrayList<List<PersonName>>(0);
		
		List<String> allPersons = new ArrayList<String>(0);
		int n=0;
		
		for (String authors: authorList) { //authorList for each publication
			final List<PersonName> allAuthorNamesOfOnePublication = PersonNameUtils.discoverPersonNames(authors); //.replaceAll("[_[^\\w\\däüöÄÜÖ\\+\\- ]]", ""));
						
			for (PersonName author: allAuthorNamesOfOnePublication) { //each author in the list of authors
			     HashMap<String, String> authorName = new HashMap<String, String>();
			     authorName.put("firstName", author.getFirstName());
			     authorName.put("lastName", author.getLastName());
			     authorName.put("normalizedName", author.getFirstName() + author.getLastName());

				 List<Integer> authorWithThisNameCount = null;
				 authorWithThisNameCount = sessionRkr.selectList("org.mybatis.example.Entity-Identification.countAuthorsByName", authorName);
				 
				 if (authorWithThisNameCount.get(0) < 2) {
					 sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthor", authorName);
				 }
				 
				 for (PersonName coAuthor: allAuthorNamesOfOnePublication) { //add the coauthors to a table
					 if (!coAuthor.equals(author)) {
						 sessionRkr.insert("org.mybatis.example.Entity-Identification.insertCoAuthors", normalizePersonName(coAuthor));
					 }
				 }
			}

		}

		System.out.println(n);
		System.out.println("Elapsed time: " + ((System.nanoTime() - timeAtstart)/1000000000) + "s");
		
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
		String newFirstName = personName.getFirstName().substring(0,1);
			
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
	
}
