package org.bibsonomy.entity;

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
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;

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
			authorList = session.selectList("org.mybatis.example.Entity-Identification.selectBibtex");
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

		    List<String> test = null;
		    test = sessionRkr.selectList("org.mybatis.example.Entity-Identification.countAuthorsByName");
		    //System.out.println(authorCount.get(0));

		List<List<PersonName>> coAuthorList = new ArrayList<List<PersonName>>(0);
		
		List<String> allPersons = new ArrayList<String>(0);
		int n=0;
		
		for (String authors: authorList) { //authorList for each publication
			final List<PersonName> personNames = PersonNameUtils.discoverPersonNames(authors); //.replaceAll("[_[^\\w\\däüöÄÜÖ\\+\\- ]]", ""));
			coAuthorList.add(personNames);
						
			for (PersonName person: personNames) { //each author in the list of authors
			     HashMap<String, String> authorName = new HashMap<String, String>();
			     authorName.put("firstName", person.getFirstName());
			     authorName.put("lastName", person.getLastName());

			     sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthor", authorName);
				//check if person already exists in database
				
				if (true) {} //select >= 1
				for (String personA: allPersons) { //compare each author in the list with each author already computed
					if (personA.equals(person.toString())) { //this person name already exists
						n++;
					}
				}
				//System.out.println(person);
				//System.out.println(StringUtil.foldToASCII(person.toString())); //only ASCII
				//System.out.println(StringUtil.foldToASCII(person.getLastName()));
				allPersons.add(person.toString());
			}
			//System.out.println("----------------------------------------");
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
			
		//check if firstName is shortened
		if (personName.getFirstName().length() == 2 && personName.getFirstName().substring(1,2).equals(".")) {
		
		}
		
		return newFirstName + personName.getLastName();
		
	}
	
}
