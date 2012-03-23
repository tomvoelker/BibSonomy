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
import java.util.LinkedList;
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

		String resource = "config.xml";
		Reader reader;
				
		List<String> authorList = null;
		List<Map<String,Integer>> authorIDNumberList = new ArrayList<Map<String,Integer>>();
		
		try {
			reader = Resources.getResourceAsReader(resource);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			SqlSession session = sqlMapper.openSession();
			try {
			authorList = session.selectList("org.mybatis.example.Entity-Identification.selectBibtexDBLP", 1);
			System.out.println(authorList.get(0));			
		} finally {
			session.close();
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		String resourceRkr = "configRkr.xml";
		Reader readerRkr;
		
		try {
			readerRkr = Resources.getResourceAsReader(resourceRkr);
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(readerRkr);
			
			SqlSession sessionRkr = sqlMapper.openSession();

		//read all entries from bibtex and save it to author table
		ArrayList<LinkedList<PersonName>> allAuthorsWithCoAuthors = new ArrayList<LinkedList<PersonName>>();
		for (String authors: authorList) { //authorList for each publication
			List<PersonName> allAuthorNamesOfOnePublication = PersonNameUtils.discoverPersonNames(authors);
			allAuthorsWithCoAuthors.add((LinkedList)allAuthorNamesOfOnePublication);
			
			for (PersonName author: allAuthorNamesOfOnePublication) { //each author in the list of authors
				
				sessionRkr.commit();
				HashMap<String, String> authorName = new HashMap<String, String>();
				
				author = removeNumberFromAuthor(author);
				
				authorName.put("firstName", StringUtil.foldToASCII(author.getFirstName()));
			    authorName.put("lastName", StringUtil.foldToASCII(author.getLastName()));
				authorName.put("normalizedName", normalizePerson(author));			    

				int authorNumber = 0;
				try {
					Integer.parseInt(author.getLastName());
					authorNumber = Integer.parseInt(author.getLastName());
				}
				catch(NumberFormatException nfe) {
					authorNumber = 1;
				}
				
			    //TODO insert
			    sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthor", authorName);
			    
				List<Integer> lastInsertID = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectLastInsertID");
				List<PersonName> allAuthorsOfOnePublicationDBLP = null;

				//add the authorID + authorNumber combination to the list
				HashMap<String,Integer> IDAndNumber = new HashMap<String,Integer>();
				IDAndNumber.put("authorID",lastInsertID.get(0));
				IDAndNumber.put("authorNumber",authorNumber);
				authorIDNumberList.add(IDAndNumber);
			
			    for (PersonName coauthor: allAuthorNamesOfOnePublication) {
			    	//add all coauthors for this author thats not the author
			    	coauthor = removeNumberFromAuthor(coauthor);
			    	if (coauthor != author) sessionRkr.insert("org.mybatis.example.Entity-Identification.insertCoAuthors", normalizePerson(coauthor));
			    }
			}
		}
		
		System.out.println(authorIDNumberList.get(0).get("authorID"));
		System.out.println(authorIDNumberList.get(0).get("authorNumber"));
		
		//run the algorithms
		
		//compare
		
		System.exit(1);
		
		/*
		LuceneTest lucene =  new LuceneTest();
		try {
			lucene.luceneSearch(allAuthorsWithCoAuthors);
		} catch (IOException e) {}
		catch (ParseException p) {}
		*/	
				
		int threshold = 2;
		List<String> authorNames = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorNames");
		
		//check the name for every author
		for(int m=0; m < authorNames.size(); m++) {
			//do this as long there is something we can merge
			while (true) {
				System.out.println(authorNames.get(m));
				//merge authors who have the same coauthors
				//TODO List<Map<String,String>> testX = getAuthorsWithNameLikeX("r.wille", sessionRkr);
				List<Map<Integer,String>> authorsWithNameX = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectCoAuthors", authorNames.get(m));
				if (authorsWithNameX.isEmpty()) {
					System.out.println("its empty");
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
				
				System.out.println(authorsWithNameX.get(0).get("normalized_coauthor"));
				System.out.println(String.valueOf(authorsWithNameX.get(0).get("author_id")));
				int firstAuthorID = Integer.parseInt(String.valueOf((authorsWithNameX.get(0).get("author_id"))));
			
				//tmp lists to compare within the iterations
				List<String> coAuthorNamesOuterIteration = new ArrayList<String>();		
				List<String> coAuthorNamesInnerIteration = new ArrayList<String>();
		
				//here we save our tmp results we use later to merge the both authors 
				List<String> innerCoauthors = new ArrayList<String>();		
				List<String> outerCoauthors = new ArrayList<String>();
		
				while (outerItr.hasNext()) {
					Map<String,String> outerCoAuthor = (Map)outerItr.next();
					coAuthorNamesOuterIteration.add(outerCoAuthor.get("normalized_coauthor"));
					if (outerAuthorID != Integer.parseInt(String.valueOf(outerCoAuthor.get("author_id")))) {
						outerAuthorID = Integer.parseInt(String.valueOf(outerCoAuthor.get("author_id")));
				
						Iterator innerItr = authorsWithNameX.iterator();
						while (innerItr.hasNext()) {
							Map<String,String> innerCoAuthor = (Map)innerItr.next();
					
							if (Integer.parseInt(String.valueOf(outerCoAuthor.get("author_id"))) == Integer.parseInt(String.valueOf(innerCoAuthor.get("author_id")))) continue;
				
							if (innerAuthorID != Integer.parseInt(String.valueOf(innerCoAuthor.get("author_id")))) {		
								for (int k=0; k < coAuthorNamesOuterIteration.size(); k++) {
									if (coAuthorNamesInnerIteration.contains(coAuthorNamesOuterIteration.get(k))) counter++;
								}
								coAuthorNamesInnerIteration.clear();
						
								if (counter > max) {
									max = counter;
									maxAuthorID = innerAuthorID;
								}
							
								innerAuthorID = Integer.parseInt(String.valueOf(innerCoAuthor.get("author_id")));
								counter = 0;
							}
					
							coAuthorNamesInnerIteration.add(innerCoAuthor.get("normalized_coauthor"));	 
						}
						coAuthorNamesOuterIteration.clear();
						if (max > outerMax) {
							outerMax = max;
							outerMaxAuthorID = Integer.parseInt(String.valueOf(outerCoAuthor.get("author_id")));
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
			e.printStackTrace();
		}
	}
	
	public static List<Map<String,String>> getAuthorsWithNameLikeX (String nameX, SqlSession sessionRkr) {
		List<Map<Integer,String>> allAuthors = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectRkrAuthor");
		Directory index = Lucene.createLuceneIndex(allAuthors);
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		
		String query = "author:" + nameX + "~0.8";
		List<Map<String,String>> listOfAuthorsWithNameLikeX = new ArrayList<Map<String,String>>();
		
		try {
			Query q = new QueryParser(Version.LUCENE_35, "author", analyzer).parse(query);

			int hitsPerPage = 50;
			IndexReader luceneReader = IndexReader.open(index);
			IndexSearcher searcher = new IndexSearcher(luceneReader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
	
			System.out.println("Found " + hits.length + " hits.");
			for(int i=0;i<hits.length;++i) {
				Map<String,String> authorMap = new HashMap<String, String>();
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				System.out.println((i + 1) + ". " + d.get("author") + " author_id: " + d.get("author_id"));
				authorMap.put("author_id", d.get("author_id"));
				authorMap.put("normalized_name", d.get("author"));
				listOfAuthorsWithNameLikeX.add(authorMap);
			}

			searcher.close();
	
		}
		catch (IOException e) {}
		catch (ParseException p) {}
	
		return listOfAuthorsWithNameLikeX;
	}
	
	private static PersonName removeNumberFromAuthor(PersonName author) {
		boolean isInt;
		int authorNumber = 0;
		try {
			Integer.parseInt(author.getLastName());
			isInt = true;
			authorNumber = Integer.parseInt(author.getLastName());
		}
		catch(NumberFormatException nfe) {
			isInt = false;
		}
		
		if (isInt) {
			//authorNumber = Integer.parseInt(author.getLastName());
			//fix the authorName (the lastName is the authorNumber)
			try {
				List<PersonName> authorWithoutNumber = PersonNameUtils.discoverPersonNames(author.getFirstName());

				author.setFirstName(authorWithoutNumber.get(0).getFirstName());
				author.setLastName(authorWithoutNumber.get(0).getLastName());
			} catch (PersonListParserException plpe) {}
		    
		}
		else { 
			//authorNumber = 1;
		}
		
		return author;
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
