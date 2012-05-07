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
	List<Map<String,ArrayList<String>>> authorIDNumberList = new ArrayList<Map<String,ArrayList<String>>>();
	List<Map<String,String>> authorIDToBibtex = new ArrayList<Map<String,String>>();
	//position 9 we use for 10 or more different ids
	int[] averageCountOccurences = new int[9];
	int[] averageCountAuthors = new int[9];
	boolean skipGatherFromDB = true;

	public void preperations(SqlSession sessionRkr) throws PersonListParserException {

		List<Map<String,String>> dbAuthorIDNumberList= sessionRkr.selectList("org.mybatis.example.Entity-Identification.DBLPPreperation");
		boolean foundNoGather = false;
		//search the list if there is already a person with this name
		for (Map<String, String> dbAuthorHashMap: dbAuthorIDNumberList) {
			int k=0;
			foundNoGather = false;
			for (Map<String, ArrayList<String>>  arrayAuthorHashMap: authorIDNumberList) {
				//System.out.println("inner: " + arrayAuthorHashMap.get("authorNameAndNumber").get(0));
				//the author already exists
				if (dbAuthorHashMap.get("author_name_and_number").equals(arrayAuthorHashMap.get("authorNameAndNumber").get(0))) {
					//add the new authorID to the already existing data
					//System.out.println("reAdd: " + arrayAuthorHashMap.get("authorNameAndNumber").get(0));
					ArrayList<String> authorIDs = arrayAuthorHashMap.get("authorIDs");
					ArrayList<String> contentIDs = arrayAuthorHashMap.get("contentIDs");
					ArrayList<String> normalizedName = arrayAuthorHashMap.get("normalizedName");

					authorIDs.add(String.valueOf(dbAuthorHashMap.get("author_id")));
					contentIDs.add(String.valueOf(dbAuthorHashMap.get("content_id")));
					normalizedName.add(dbAuthorHashMap.get("author_name_and_number"));
					
					arrayAuthorHashMap.put("authorIDs",authorIDs);
					arrayAuthorHashMap.put("conentIDs",contentIDs);
					arrayAuthorHashMap.put("normalizedName",normalizedName);

					authorIDNumberList.set(k, arrayAuthorHashMap);
					foundNoGather = true;
					break;
				}
				k++;
			}
			//this author is new and we add this author to the list
			if (!foundNoGather) {
				Map<String,ArrayList<String>> arrayAuthorHashMap = new HashMap<String,ArrayList<String>>();
				ArrayList<String> authorID = new ArrayList<String>();
				ArrayList<String> contentID = new ArrayList<String>();
				ArrayList<String> normalizedName = new ArrayList<String>();
				ArrayList<String> authorNameAndNumber = new ArrayList<String>();
				
				authorID.add(String.valueOf(dbAuthorHashMap.get("author_id")));
				contentID.add(String.valueOf(dbAuthorHashMap.get("content_id")));
				normalizedName.add(dbAuthorHashMap.get("author_name_and_number"));
				authorNameAndNumber.add(dbAuthorHashMap.get("author_name_and_number"));

				//System.out.println("add: " + authorNameAndNumber.get(0));
				
				arrayAuthorHashMap.put("authorIDs",authorID);
				arrayAuthorHashMap.put("contentIDs",contentID);
				arrayAuthorHashMap.put("normalizedName",normalizedName);
				arrayAuthorHashMap.put("authorNameAndNumber", authorNameAndNumber);
				authorIDNumberList.add(arrayAuthorHashMap);
			}
		}

		if (skipGatherFromDB) return;

		//List<String> authorList = session.selectList("org.mybatis.example.Entity-Identification.selectBibtexDBLP",1);
		List<Map<String,String>> authorList = sessionRkr.selectList("org.mybatis.example.Entity-Identification.DBLPTest",1);
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

				//TODO String test = org.bibsonomy.model.util.PersonNameUtils.discoverPersonNames(persons);

				//TODO insert
				sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthor", authorName);

				List<Integer> lastInsertID = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectLastInsertID");
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

						String singleAuthorID = String.valueOf(lastInsertID.get(0));
						HashMap<String,String> dbEntry = new HashMap<String,String>();
						dbEntry.put("authorID", singleAuthorID);
						dbEntry.put("authorNameAndNumber", EntityIdentification.normalizePerson(author) + authorNumber);
						dbEntry.put("authorsString", authors);
						dbEntry.put("contentID", String.valueOf(authorsMap.get("content_id")));

						sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthorIDAndNumber", dbEntry);

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
					authorNameAndNumber.add(EntityIdentification.normalizePerson(author) + authorNumber);
					authorID.add(String.valueOf(lastInsertID.get(0)));
					String singleAuthorID = String.valueOf(lastInsertID.get(0));

					HashMap<String,String> dbEntry = new HashMap<String,String>();
					dbEntry.put("authorID", singleAuthorID);
					dbEntry.put("authorNameAndNumber", EntityIdentification.normalizePerson(author) + authorNumber);
					dbEntry.put("authorsString", authors);
					dbEntry.put("contentID", String.valueOf(authorsMap.get("content_id")));

					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertAuthorIDAndNumber", dbEntry);

					//System.out.println("add: " + authorNameAndNumber.get(0));
					authorHashMap.put("authorNameAndNumber", authorNameAndNumber);
					authorHashMap.put("authorIDs",authorID);
					authorIDNumberList.add(authorHashMap);

					Map<String,String>saveIDAndBibtex = new HashMap<String,String>();
					saveIDAndBibtex.put("authorID",String.valueOf(lastInsertID.get(0)));
					saveIDAndBibtex.put("bibtexAuthor", authors);
					authorIDToBibtex.add(saveIDAndBibtex);
				}

				for (PersonName coauthor: allAuthorNamesOfOnePublication) {
					//add all coauthors for this author thats not the author
					coauthor = removeNumberFromAuthor(coauthor);
					if (coauthor != author) sessionRkr.insert("org.mybatis.example.Entity-Identification.insertCoAuthors", EntityIdentification.normalizePerson(coauthor));
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
	}

	public void compareResults(List<List<Integer>> authorClusters, SqlSession sessionRkr) throws PersonListParserException {
		int rightMatchesOverallLucene=0;
		int overallLuceneUnderClustering=0;
		int overallLuceneOverClustering=0;
		int rightMatchesOverallAuthorCLustering=0;
		int overallAuthorUnderClustering=0;
		int overallAuthorOverClustering=0;

		for (Map<String, ArrayList<String>> authorMap: authorIDNumberList) { //every author where we know the correct IDs
			//we use one ID as reference and search authorIDs(contentIDs) that fit to this reference ID
			int referenceAuthorID = Integer.valueOf(authorMap.get("authorIDs").get(0));
			int referenceContentID = Integer.valueOf(authorMap.get("contentIDs").get(0));
			//the last char is the number we have to remove
			String normalizedName = authorMap.get("normalizedName").get(0).substring(0,authorMap.get("normalizedName").get(0).length()-1);

			int luceneResults=0;
			int authorClusteringResults=0;

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

			//lets see how lucene performed
			try {
				List<Integer> luceneResultIDs = Lucene.searchAuthor(normalizedName, normalizedCoauthors);

				int underClustering = 0;
				int rightMatches = 0;
				boolean found = false;
				System.out.println("real IDs: ");
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
				System.out.println("found IDs: ");
				for (Integer luceneAuthorID: luceneResultIDs) {
					System.out.println(luceneAuthorID);
					found = false;
					for (String realAuthorID: authorMap.get("authorIDs")) {	
						if (luceneAuthorID.equals(Integer.valueOf(realAuthorID))) found = true;
					}
					if (!found) {
						overClustering++;
						overallLuceneOverClustering++;
						System.out.println("not found: " + luceneAuthorID);
					}
				}


				System.out.println("Lucene results - rightMatches: " + rightMatches + " underClustering: " + underClustering + " overClustering: " + overClustering);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//lets see how author clustering performs
			int rightMatches = 0;
			int underClustering = 0;
			int overClustering = 0;
			boolean found = false;
			System.out.println("cluster IDs: ");
			for (String realAuthorID: authorMap.get("authorIDs")) {
				found = false;
				for (List<Integer> clusteredIDsList: authorClusters) { //every cluster we want to compare
					for (Integer clusteredID: clusteredIDsList) {
						if (clusteredID.equals(Integer.valueOf(realAuthorID))) {
							if (clusteredIDsList.size() > 1) {
								System.out.println("found: " + clusteredID);
								found = true;
								rightMatches++;
								rightMatchesOverallAuthorCLustering++;
							}
						}
					}
					//find the overClustering errors
					if (found) {
						boolean tmpFound = false;
						//IDs saved in this cluster but not in the real IDs list
						for (int clusteredID: clusteredIDsList) {
							tmpFound = false;
							for (String tmpRealAuthorID: authorMap.get("authorIDs")) {
								if (Integer.valueOf(tmpRealAuthorID).equals(clusteredID)) tmpFound = true;
							}
							if (!found) {
								overClustering++;
								overallAuthorOverClustering++;
							}
						}
					}
				}
				if (!found) {
					underClustering++;
					overallAuthorUnderClustering++;
				}

			}

			System.out.println("Author clustering results - rightMatches: " + rightMatches + " underClustering: " + underClustering + " overClustering: " + overClustering);
		}

		System.out.println("\n\nOverall:");
		System.out.println("-------------------------------------------------");
		System.out.println("overall lucene - right: " + rightMatchesOverallLucene + " under: " + overallLuceneUnderClustering + " over: " + overallLuceneOverClustering);
		System.out.println("overall author clustering - right: " + rightMatchesOverallAuthorCLustering + " under: " + overallAuthorUnderClustering + " over: " + overallAuthorOverClustering);
	}

	public void oldUnusedCompareResults(List<List<Integer>> authorClusters) {
		//compare the results
		float avgClusters = 0;
		int countAuthors = 0, countIDs = 0;
		int totalAuthorIDs = 0;
		for (Map<String, ArrayList<String>> authorMap: authorIDNumberList) { //every author where we know the correct IDs
			System.out.println("test1");
			int totalIDsInClusters = 0;
			totalAuthorIDs += authorMap.get("authorIDs").size();
			if (authorMap.get("authorIDs").size() < 2) continue; //ignore authors with only 1 ID

			int countClusters = 0;
			countAuthors++;
			ArrayList<Integer> savePositions = new ArrayList<Integer>(); 
			countIDs += authorMap.get("authorIDs").size();
			System.out.println("this author has the following IDs:");

			if (authorMap.get("authorIDs").size() < 10) {
				System.out.println("add author to avg");
				averageCountAuthors[authorMap.get("authorIDs").size()-2]++;
			}
			else  averageCountAuthors[8]++;

			String bibtexAuthor="";
			int countAuthorsInUsedClusters = 0;

			for (int k=0; k<authorMap.get("authorIDs").size(); k++) { //every ID of this author
				for (Map<String,String> singleAuthorIDToBibtex: authorIDToBibtex) {
					if (singleAuthorIDToBibtex.get("authorID") == authorMap.get("authorIDs").get(k)) bibtexAuthor = singleAuthorIDToBibtex.get("bibtexAuthor");
				}
				System.out.println(authorMap.get("authorIDs").get(k) + " size: " + authorIDToBibtex.size());
				System.out.println(bibtexAuthor);
				//count the clusters this IDs are split into
				boolean found = false;
				int m=0; //each cluster has its own number
				for (List<Integer> clusteredIDsList: authorClusters) { //every cluster we want to compare
					for(Integer clusteredID: clusteredIDsList) { //every ID of this cluster
						//the calculated cluster id is within the author id
						if (clusteredID == Integer.parseInt(authorMap.get("authorIDs").get(k))) {
							//TODO we can speed this up with deleting the already found IDs from calculatedIDsList
							found = true;
							//we found this in a new cluster
							if (!savePositions.contains(m)) {
								System.out.println("--split--");
								//save the position
								savePositions.add(m);
								countClusters++;
								if (authorMap.get("authorIDs").size() < 10) averageCountOccurences [authorMap.get("authorIDs").size()-2]++;
								else averageCountOccurences[8]++;
								//count the number of IDs that are in this cluster and not in the author IDs
								countAuthorsInUsedClusters += clusteredID;
								break;
							}
						}
					}
					m++;
				}
				//we havent merged this authorID with another one and this authorID has its own cluster 
				if (!found) {
					countClusters++;
					System.out.println("--single split--");
					if (authorMap.get("authorIDs").size() < 10) averageCountOccurences [authorMap.get("authorIDs").size()-2]++;
					else  averageCountOccurences[8]++;
				}
			}

			System.out.println("ratio clustered IDs/real IDs: " + countAuthorsInUsedClusters/(float)authorMap.get("authorIDs").size());
			//author IDs - IDs in all used clusters
			System.out.println("this author is split into: " + countClusters + " clusters");
			avgClusters += countClusters;
			totalIDsInClusters += countAuthorsInUsedClusters;
		}

		for (int k=0; k < averageCountOccurences.length; k++) {
			System.out.println("authors with " + (k+2) + " IDs are split into an average of: " + (float)averageCountOccurences[k]/(float)averageCountAuthors[k] +  " clusters (" + (1-((float)averageCountOccurences[k]/(float)averageCountAuthors[k])/(k+2)) + " correct)");
			System.out.println("aO:" + averageCountOccurences[k] + " aA: " + averageCountAuthors[k]);
		}

		//avgClusters
		System.out.println("total authorIDs:total clusteredIDs " + totalAuthorIDs + ":" + totalAuthorIDs);
		System.out.println("Each author with more then one ID has an average of " + (float)countIDs/(float)countAuthors + " IDs");
		System.out.println("Each author is split into an average of " + (float)avgClusters/(float)countAuthors + " clusters");
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
