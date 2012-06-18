package org.bibsonomy.entity;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.util.StringUtils;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

public class DblpTest {
	//authorname->IDs
	List<Map<String,ArrayList<String>>> authorIDNumberList = new ArrayList<Map<String,ArrayList<String>>>();
	List<Map<String,String>> authorIDToBibtex = new ArrayList<Map<String,String>>();
	//position 9 we use for 10 or more different ids
	int[] averageCountOccurences = new int[9];
	int[] averageCountAuthors = new int[9];
	boolean skipGatherFromDB = false;

	public List<Map<String,ArrayList<String>>> preperations(SqlSession sessionRkr) throws PersonListParserException {

		//List<String> authorList = session.selectList("org.mybatis.example.Entity-Identification.selectBibtexDBLP",1);
		List<Map<String,String>> authorList = sessionRkr.selectList("org.mybatis.example.Entity-Identification.DBLPTest",1);
		//List<Map<String,String>> authorList = sessionRkr.selectList("org.mybatis.example.Entity-Identification.MYOWNTest",1);
		System.out.println("Datenbankabfrage erfolgreich");

		//read all entries from bibtex and save it to author table
		ArrayList<LinkedList<PersonName>> allAuthorsWithCoAuthors = new ArrayList<LinkedList<PersonName>>();
		int z=0;
		for (Map<String,String> authorsMap: authorList) { //authorList for each publication
			System.out.println(z);
			z++;
			String authors = authorsMap.get("author");
			sessionRkr.commit();
			float timeAtStart = System.nanoTime();
			//System.out.println("List of authors: " + authors);
			ArrayList<String> authorNamesWhoHaveANumber = new ArrayList<String>();
			ArrayList<Integer> authorNumbers = new ArrayList<Integer>();

			//remove the dblp numbers from the authors e.g. JÃ¼rgen MÃ¼ller 002
			final Pattern p = Pattern.compile("\\s+([^\\s]+?)\\s+([0-9]{4})");
			final Matcher matcher = p.matcher(authors);
			while (matcher.find()) {
				authorNamesWhoHaveANumber.add(matcher.group(1));
				authorNumbers.add(Integer.parseInt(matcher.group(2)));
			}

			final Pattern p2 = Pattern.compile("[0-9]{4}");
			final String cleanedAuthors = p2.matcher(authors).replaceAll("");

			List<PersonName> allAuthorNamesOfOnePublication = PersonNameUtils.discoverPersonNames(cleanedAuthors);
			allAuthorsWithCoAuthors.add((LinkedList)allAuthorNamesOfOnePublication);

			int countAuthors = 0;
			for (PersonName author: allAuthorNamesOfOnePublication) { //each author in the list of authors
				//System.out.println("Author as string: " + author.toString());
				HashMap<String, String> authorName = new HashMap<String, String>();

				int authorNumber = 1;
				if (authorNamesWhoHaveANumber.size() > 0 && countAuthors < authorNamesWhoHaveANumber.size()) {
					if (authorNamesWhoHaveANumber.get(countAuthors).equals(author.getLastName())) {
						authorNumber = authorNumbers.get(countAuthors);
						countAuthors++;
					}
				}
				author = removeNumberFromAuthor(author);

				authorName.put("firstName", StringUtil.foldToASCII(author.getFirstName()));
				authorName.put("lastName", StringUtil.foldToASCII(author.getLastName()));
				authorName.put("normalizedName", EntityIdentification.normalizePerson(author));	


				//TODO insert
				sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthor", 1);

				List<Integer> lastInsertID = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectLastInsertID");

				authorName.put("authorID", String.valueOf(lastInsertID));
				sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthorName", authorName);

				List<PersonName> allAuthorsOfOnePublicationDBLP = null;

				int k=0;
				boolean found = false;
				//search the list if there is already a person with this name
				for (Map<String, ArrayList<String>> authorHashMap: authorIDNumberList) {
					//System.out.println("compare: " + authorHashMap.get("authorNameAndNumber").get(0) + " with " + normalizePerson(author) + authorNumber);
					if (authorHashMap.get("authorNameAndNumber").get(0).equals(EntityIdentification.normalizePerson(author) + authorNumber)) {
						//add the new authorID to the already existing data
						//System.out.println("reAdd: " + authorHashMap.get("authorNameAndNumber").get(0));
						ArrayList<String> authorIDs = authorHashMap.get("authorIDs");

						Map<String,String>saveIDAndBibtex = new HashMap<String,String>();
						saveIDAndBibtex.put("authorID",String.valueOf(lastInsertID.get(0)));
						saveIDAndBibtex.put("bibtexAuthor", authors);
						authorIDToBibtex.add(saveIDAndBibtex);


						authorIDs.add(String.valueOf(lastInsertID.get(0)));
						authorHashMap.put("authorIDs",authorIDs);

						authorIDNumberList.set(k, authorHashMap);
						found = true;
						break;
					}
					k++;
				}
				//this author is new and we add this author to the list
				if (!found) {
					Map<String,ArrayList<String>> authorHashMap = new HashMap<String,ArrayList<String>>();
					ArrayList<String> authorID = new ArrayList<String>();
					ArrayList<String> authorNameAndNumber = new ArrayList<String>();
					ArrayList<String> contentID = new ArrayList<String>();
					authorNameAndNumber.add(EntityIdentification.normalizePerson(author) + authorNumber);
					authorID.add(String.valueOf(lastInsertID.get(0)));
					contentID.add(String.valueOf(authorsMap.get("content_id")));
					String singleAuthorID = String.valueOf(lastInsertID.get(0));

					//System.out.println("add: " + authorNameAndNumber.get(0));
					authorHashMap.put("authorNameAndNumber", authorNameAndNumber);
					authorHashMap.put("authorIDs",authorID);
					authorHashMap.put("contentIDs",contentID);
					authorIDNumberList.add(authorHashMap);

					Map<String,String>saveIDAndBibtex = new HashMap<String,String>();
					saveIDAndBibtex.put("authorID",String.valueOf(lastInsertID.get(0)));
					saveIDAndBibtex.put("bibtexAuthor", authors);
					authorIDToBibtex.add(saveIDAndBibtex);
				}

				for (PersonName coauthor: allAuthorNamesOfOnePublication) {
					//add all coauthors for this author thats not the author
					coauthor = removeNumberFromAuthor(coauthor);
					if(!EntityIdentification.normalizePerson(author).equals(EntityIdentification.normalizePerson(coauthor))) {
						sessionRkr.insert("org.mybatis.example.Entity-Identification.insertCoAuthors", EntityIdentification.normalizePerson(coauthor));
					}
				}
			}

		}

		for (Map<String, ArrayList<String>> test: authorIDNumberList) {
			System.out.println(test.get("authorNameAndNumber").get(0));
			for(int k=0; k < test.get("authorIDs").size(); k++) {
				System.out.println(test.get("authorIDs").get(k));
			}
		}
		sessionRkr.commit();

		return authorIDNumberList;
	}

	public void compareResults(List<List<Integer>> authorClusters, SqlSession sessionRkr) throws PersonListParserException {
		int rightMatchesOverallLucene=0;
		int overallLuceneUnderClustering=0;
		int overallLuceneOverClustering=0;
		int rightMatchesOverallAuthorClustering=0;
		int overallAuthorUnderClustering=0;
		int overallAuthorOverClustering=0;
		int sumAuthors = 0;

		for (Map<String, ArrayList<String>> authorMap: authorIDNumberList) { //every author where we know the correct IDs
			//we use one ID as reference and search authorIDs/contentIDs that fit to this reference ID
			int referenceContentID = Integer.valueOf(authorMap.get("contentIDs").get(0)); //contentIDs
			//the last char is the number we have to remove
			String normalizedName = authorMap.get("authorNameAndNumber").get(0).substring(0,authorMap.get("authorNameAndNumber").get(0).length()-1);

			int luceneResults=0;
			int authorClusteringResults=0;

			//System.out.println("reference: " + referenceContentID);
			List<String> contentIDAuthorString= sessionRkr.selectList("org.mybatis.example.Entity-Identification.DBLPAuthor",referenceContentID);

			String authorsString = contentIDAuthorString.get(0);

			//TODO we need this already somewhere else and should maybe put this in a function
			//remove the dblp numbers from the authors e.g. Jürgen Müller 002
			final Pattern p = Pattern.compile("\\s+([^\\s]+?)\\s+([0-9]{4})");
			final Matcher matcher = p.matcher(authorsString);

			final Pattern p2 = Pattern.compile("[0-9]{4}");
			final String cleanedAuthors = p2.matcher(authorsString).replaceAll("");

			List<PersonName> allAuthorNamesOfOnePublication = PersonNameUtils.discoverPersonNames(cleanedAuthors);

			//so far we only compare the first author in the text
			//String normalizedName = EntityIdentification.normalizePerson(allAuthorNamesOfOnePublication.get(0));
			//TODO String normalizedName = authorMap.get("normalizedName");
			List<String> normalizedCoauthors = new ArrayList<String>();;

			for(int k=1; k < allAuthorNamesOfOnePublication.size(); k++) {
				normalizedCoauthors.add(EntityIdentification.normalizePerson(allAuthorNamesOfOnePublication.get(k)));
			}

			/*
			//lets see how lucene performed
			try {
				List<Integer> luceneResultIDs = Lucene.searchAuthor(normalizedName, normalizedCoauthors);

				int underClustering = 0;
				int rightMatches = 0;
				boolean found = false;
				//System.out.println("real IDs: ");
				for (String realAuthorID: authorMap.get("authorIDs")) {
					System.out.println("real ID: " + Integer.valueOf(realAuthorID));
					found = false;
					for (Integer luceneAuthorID: luceneResultIDs) {
						if (Integer.valueOf(luceneAuthorID).equals(Integer.valueOf(realAuthorID))) {
							found = true;
							rightMatches++;
							rightMatchesOverallLucene++;
						}
					}
					if (!found) {
						underClustering++;
						overallLuceneUnderClustering++;
					}
				}

				int overClustering = 0;
				//System.out.println("found IDs: ");
				for(Integer luceneAuthorID: luceneResultIDs) {
					//System.out.println(luceneAuthorID);
					found = false;
					for (String realAuthorID: authorMap.get("authorIDs")) {	
						if (luceneAuthorID.equals(Integer.valueOf(realAuthorID))) found = true;
					}
					if (!found) {
						overClustering++;
						overallLuceneOverClustering++;
						//System.out.println("not found: " + luceneAuthorID);
					}
				}


				//System.out.println("Lucene results - rightMatches: " + rightMatches + " underClustering: " + underClustering + " overClustering: " + overClustering);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 */

			//lets see how author clustering performs
			int rightMatches = 0;
			int underClustering = 0;
			int overClustering = 0;
			boolean found = false;
			//System.out.println("-------------------");
			//System.out.println("authorClustering IDs: ");

			//single ID cluster
			/* get all clusters for this name
			 * get the titles for this id
			 * get the titles for all fitting clusters
			 * test in which cluster we fit best
			 */

			//if (authorMap.get("authorIDs").size() > 10) continue;
			if (authorMap.get("authorIDs").size() > 1) sumAuthors += authorMap.get("authorIDs").size(); 
			
			//we use the first realAuthorID as example and search the cluster who contains this ID
			Integer exampleContentID = Integer.valueOf(authorMap.get("authorIDs").get(0));
			//System.out.println("exampleID: " + exampleContentID);
			for (List<Integer> clusteredIDsList: authorClusters) { //every cluster we want to compare
				for(String realAuthorID: authorMap.get("authorIDs")) {
					if (clusteredIDsList.contains(exampleContentID)) {
						//System.out.println("testCluster: " + clusteredIDsList);
						//get all realAuthorIDs that are in this cluster
						//System.out.println("real ID:" + realAuthorID);
						if (clusteredIDsList.contains(Integer.valueOf(realAuthorID))) { //we found the right cluster
							//System.out.println("found: " + realAuthorID);
							found = true;
							rightMatches++;
							rightMatchesOverallAuthorClustering++;
						}
						else {
							underClustering++;
							overallAuthorUnderClustering++;
						}
						//get overClusteringErrors
						for (Integer clusteredID: clusteredIDsList) {
							if (!authorMap.get("authorIDs").contains(String.valueOf(clusteredID))) {
								if (clusteredID < 10000) { //TODO only used for faster debugging
									//System.out.println("this is too much: " + clusteredID);
									overClustering++;
									overallAuthorOverClustering++;
								}
							}
						}
					}
				}
				if(found) break;
			}
			if (!found) {
				if (authorMap.get("authorIDs").size() > 1) {
					underClustering += authorMap.get("authorIDs").size();
					overallAuthorUnderClustering += authorMap.get("authorIDs").size();
				}
			}

			System.out.println("Author clustering results for " + exampleContentID + " - rightMatches: " + rightMatches + " underClustering: " + underClustering + " overClustering: " + overClustering);
		}


		System.out.println("\n\nOverall:");
		System.out.println(sumAuthors);
		System.out.println("-------------------------------------------------");
		System.out.println("overall lucene - right: " + rightMatchesOverallLucene + " under: " + overallLuceneUnderClustering + " over: " + overallLuceneOverClustering);
		System.out.println("overall author clustering - true: " + rightMatchesOverallAuthorClustering + " under: " + overallAuthorUnderClustering + " over: " + overallAuthorOverClustering);
	}

	public List<Map<String,ArrayList<String>>> getAuthorIDNumberList() {
		return authorIDNumberList;
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
}
