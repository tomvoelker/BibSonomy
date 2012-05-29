package org.bibsonomy.entity;

import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.util.StringUtils;

public class EntityIdentification {

	public static void main(String[] args) throws PersonListParserException {

		float timeAtStart = System.nanoTime();

		//database configs
		String resource = "config.xml";
		Reader reader;

		String resourceRkr = "configRkr.xml";
		Reader readerRkr;

		//get the data from the database
		try {
			/*
			reader = Resources.getResourceAsReader(resource);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			SqlSession session = sqlMapper.openSession();
			*/

			readerRkr = Resources.getResourceAsReader(resourceRkr);
			SqlSessionFactory sqlMapperRkr = new SqlSessionFactoryBuilder().build(readerRkr);
			SqlSession sessionRkr = sqlMapperRkr.openSession();

			sessionRkr.insert("org.mybatis.example.Entity-Identification.truncateAuthor");
			sessionRkr.insert("org.mybatis.example.Entity-Identification.truncateAuthorCoauthor");
			//sessionRkr.insert("org.mybatis.example.Entity-Identification.backupAuthor");
			//sessionRkr.insert("org.mybatis.example.Entity-Identification.backupAuthorCoauthor");
			sessionRkr.insert("org.mybatis.example.Entity-Identification.myownBackupAuthor");
			sessionRkr.insert("org.mybatis.example.Entity-Identification.myownBackupAuthorCoauthor");
			sessionRkr.commit();

			Lucene lucene =  new Lucene();
			try {
				lucene.createLuceneIndexForAllAuthors(sessionRkr);
			} catch (IOException e) {}
			catch (ParseException p) {}	

			//run "myown" test
			//MyOwnTest.findSamePersonDifferentNames(sessionRkr);

			//run dblp test
			DblpTest dblpTest = new DblpTest();
			List<Map<String,ArrayList<String>>> authorIDNumberList = dblpTest.preperations(sessionRkr);

			/*
			//Lucene compare
			List<Map<String,ArrayList<String>>> authorIDNumberList = dblpTest.getAuthorIDNumberList();
			//create the authorCluster we can compare then
			for (Map<String, ArrayList<String>> authorMap: authorIDNumberList) { //every author where we know the correct IDs
				lucene.searchAuthor(normalizedName, coauthors)
			}
			 */

			//author clustering compare
			List<List<Integer>> authorIDsList = AuthorClustering.authorClustering(sessionRkr);
			AuthorClustering.useTitleToMergeClusters(sessionRkr, authorIDNumberList);
			dblpTest.compareResults(authorIDsList, sessionRkr);

			System.out.println("Elapsed time: " + ((System.nanoTime() - timeAtStart)/1000000000) + "s");

			//session.close();
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
