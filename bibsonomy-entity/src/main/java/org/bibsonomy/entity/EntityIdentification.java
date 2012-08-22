package org.bibsonomy.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.util.StringUtils;

public class EntityIdentification {
	final boolean testsEnabled = false;
	static Integer maxContentID = 0;

	public static void main(String[] args) throws PersonListParserException, IOException {
		float timeAtStart = System.nanoTime();
		File file = new File("authorClustering.txt");
		//write results to file
		FileWriter writer;
		writer = new FileWriter(file ,true);

		//database configs
		String resource = "config.xml";
		Reader reader;

		int minContentID = 0;
		int bibtexCount = 0;
		try {
			reader = Resources.getResourceAsReader(resource);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			SqlSession session = sqlMapper.openSession();

			List<Integer> tmpConentIDList = session.selectList("org.mybatis.example.Entity-Identification.selectBibtexMinContentID",1);
			List<Integer> tmpBibtexCount = session.selectList("org.mybatis.example.Entity-Identification.selectBibtexCount",1);
			minContentID = tmpConentIDList.get(0);
			bibtexCount = tmpBibtexCount.get(0);
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int limit = 50000;
		int iterations = (bibtexCount/limit) + 1;

		for (int k=0; k <= iterations; k++) {
			System.out.println("run: " + k + " after: " + ((System.nanoTime() - timeAtStart)/1000000000) + "s");

			writer.write("run: " + k + " after: " + ((System.nanoTime() - timeAtStart)/1000000000) + "s");
			writer.flush();
			preperations(minContentID,k,limit);
		}
		writer.close();
	}


	public static void preperations(int minContentID, int k, int limit) {
		if (maxContentID > 0) minContentID = maxContentID+1;
		Runtime rt = Runtime.getRuntime();

		float timeAtStart = System.nanoTime();
		Map<Integer, Integer> authorToContent = new HashMap<Integer,Integer>();

		//database configs
		String resource = "config.xml";
		Reader reader;

		String resourceRkr = "configRkr.xml";
		Reader readerRkr;

		//get the data from the database
		try {
			reader = Resources.getResourceAsReader(resource);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			SqlSession session = sqlMapper.openSession();

			readerRkr = Resources.getResourceAsReader(resourceRkr);
			SqlSessionFactory sqlMapperRkr = new SqlSessionFactoryBuilder().build(readerRkr);
			SqlSession sessionRkr = sqlMapperRkr.openSession();

			if (k == 0) {
				sessionRkr.insert("org.mybatis.example.Entity-Identification.truncateAuthor");
				sessionRkr.insert("org.mybatis.example.Entity-Identification.truncateAuthorCoauthor");
				sessionRkr.insert("org.mybatis.example.Entity-Identification.truncateAuthorContent");
				sessionRkr.insert("org.mybatis.example.Entity-Identification.truncateAuthorName");
				sessionRkr.insert("org.mybatis.example.Entity-Identification.truncateSimilarCluster");
				sessionRkr.commit();
			}

			ArrayList<HashMap<String, String>> authorNameListMap = new ArrayList<HashMap<String,String>>();

			if (k > 0) {
				List<HashMap<String,String>> alreadyClusteredAuthorNames = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorNames",limit);
				for (HashMap<String,String> authorMap: alreadyClusteredAuthorNames) {
					authorNameListMap.add(authorMap);
				}
			}

			HashMap<String, Integer> bibtexParameter = new HashMap<String,Integer>();
			bibtexParameter.put("minContentID", minContentID);
			bibtexParameter.put("limit", limit);

			List<Map<String,String>> authorList = session.selectList("org.mybatis.example.Entity-Identification.selectBibtex",bibtexParameter);
			System.out.println("SELECT author, CAST(content_id as char) as content_id FROM bibtex WHERE content_id > " + bibtexParameter.get("minContentID") + " LIMIT " + bibtexParameter.get("limit"));
			
			Map<String,String> test = authorList.get(authorList.size() - 1);
			maxContentID =  Integer.valueOf(authorList.get(authorList.size() - 1).get("content_id"));

			List<Integer> maxInsertID = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectMaxAuthorID");
			int lastInsertID = 0;
			if (!maxInsertID.isEmpty()) lastInsertID = maxInsertID.get(0);

			
			for (Map<String,String> authorsMap: authorList) { //authorList for each publication
				//System.out.println(rt.totalMemory() - rt.freeMemory());

				List<PersonName> allAuthorNamesOfOnePublication = new ArrayList<PersonName>();
				Integer contentID = Integer.valueOf(String.valueOf(authorsMap.get("content_id")));
				try {
					allAuthorNamesOfOnePublication = PersonNameUtils.discoverPersonNames(authorsMap.get("author"));
				}
				catch (PersonListParserException e) {
					continue;
				}

				for(PersonName person: allAuthorNamesOfOnePublication) {

					//System.out.println(person.getFirstName() + " : " + person.getLastName());
					if ((person.getFirstName() == null) || (person.getLastName()== null)) continue;
					if ((person.getFirstName().length() > 255) || (person.getLastName().length() > 255)) continue;
					lastInsertID++;

					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthor", lastInsertID);

					Map<String,String> authorName = new HashMap<String,String>();
					authorName.put("author_id", String.valueOf(lastInsertID));
					authorName.put("first_name", person.getFirstName());
					authorName.put("last_name", person.getLastName());
					authorName.put("normalized_name", normalizePerson(person));

					Map<String,String> tmpMapContent = new HashMap<String,String>();
					tmpMapContent.put("contentID", String.valueOf(contentID));
					tmpMapContent.put("lastInserID", String.valueOf(lastInsertID));

					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthorContent", contentID);
					authorToContent.put(lastInsertID, contentID);
					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthorName", authorName);

					for(PersonName coauthor: allAuthorNamesOfOnePublication) {
						HashMap<String,String> tmpMap = new HashMap(authorName);
						if (!coauthor.toString().equals(person.toString())) {
							String normalizedName = normalizePerson(coauthor);
							tmpMap.put("normalized_coauthor", normalizedName);
							authorNameListMap.add(tmpMap);

							Map<String,String> tmpMapCoauthor = new HashMap<String,String>();
							tmpMapCoauthor.put("normalizedName", normalizedName);
							tmpMapCoauthor.put("lastInsertID", String.valueOf(lastInsertID));
							

							sessionRkr.insert("org.mybatis.example.Entity-Identification.insertCoAuthors", tmpMapCoauthor);		
						}
					}
				}

			}

			System.out.println("Build new database entries after: " + ((System.nanoTime() - timeAtStart)/1000000000) + "s");

			sessionRkr.commit();
			System.out.println("starting clustering");

			//start the clustering
			List<List<Integer>> authorIDsList = AuthorClustering.authorClustering(sessionRkr, authorToContent, authorNameListMap);
			System.out.println("Clustered after: " + ((System.nanoTime() - timeAtStart)/1000000000) + "s");

			//write results to file
			FileWriter writer;
			File ausgabe;
			ausgabe = new File("authorClustering.txt");
			writer = new FileWriter(ausgabe ,true);
			writer.write("run " + (minContentID/limit) + "finished after: " + ((System.nanoTime() - timeAtStart)/1000000000) + "s");
			writer.flush();
			writer.close();

			sessionRkr.close();

		} catch (IOException e) {
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
			/*		entityIdentification.normalizePerson(personName)
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
