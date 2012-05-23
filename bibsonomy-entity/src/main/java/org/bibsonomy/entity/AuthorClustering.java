package org.bibsonomy.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class AuthorClustering {
	List<Map<String,Document>> authorTitleMaps = new ArrayList<Map<String,Document>>();
	static List<Map<String,List<String>>> clusterIDsList = new ArrayList<Map<String,List<String>>>();

	//Lucene globals
	static IndexWriter w = null;
	static StandardAnalyzer analyzer = null;
	static Directory index = null;

	public static List<Map<String,List<String>>> authorClustering(SqlSession sessionRkr) {

		//clustering the authors with the coauthor relationship
		int threshold = 1;
		//TODO we need a left join
		final List<Map<String,String>> authorNames = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorNames");

		//authorName->List of coauthors
		Map<String,List<Map<String,String>>> authorMap = new HashMap<String,List<Map<String,String>>>();

		//build the datastructure
		for (Map<String,String> tmpAuthor: authorNames) {
			//add this the first time 
			if (authorMap.get(tmpAuthor.get("normalized_name")) == null) {
				List tmpList = new ArrayList<Map<String,String>>();
				Map tmpMap = new HashMap<String,String>();
				tmpMap.put("author_id", String.valueOf(tmpAuthor.get("author_id")));
				tmpMap.put("normalized_coauthor", tmpAuthor.get("normalized_coauthor"));
				tmpList.add(tmpMap);
				authorMap.put(tmpAuthor.get("normalized_name"), tmpList);
			}
			else {
				List<Map<String,String>> tmpList = authorMap.get(tmpAuthor.get("normalized_name"));
				Map<String,String> tmpMap = new HashMap<String,String>();
				tmpMap.put("author_id", String.valueOf(tmpAuthor.get("author_id")));
				tmpMap.put("normalized_coauthor", tmpAuthor.get("normalized_coauthor"));
				tmpList.add(tmpMap);
				authorMap.put(tmpAuthor.get("normalized_name"), tmpList);

			}
		}

		int z=1;
		for(Map.Entry authorSet : authorMap.entrySet()) {
			//for (final Map<String,String> authorName : authorNames) {

			System.out.println("Author Clustering: " + z + " " + authorSet.getKey());
			z++;

			while (true) { //do this as long there is something we can merge
				//merge authors who have the same coauthors

				List<Map<String,String>> coauthors = authorMap.get(authorSet.getKey());
				//List<Map<Integer,String>> coauthors = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectCoAuthors", authorName);				
				if (coauthors.isEmpty()) {
					break;
				}

				//cluster the author table
				Iterator outerItr = coauthors.iterator();

				Integer innerAuthorID=0, maxAuthorID=0;
				Integer outerAuthorID = 0, outerMaxAuthorID=0, tmpInnerMaxAuthorID=0;
				int counter=0, max=0;
				int outerMax=0; //number of same coauthors

				//tmp lists to compare within the iterations
				final List<String> coAuthorNamesOuterIteration = new ArrayList<String>();		
				final List<String> coAuthorNamesInnerIteration = new ArrayList<String>();

				//here we save our tmp results we use later to merge the both authors 
				List<String> innerCoauthors = new ArrayList<String>();		
				List<String> outerCoauthors = new ArrayList<String>();

				while (outerItr.hasNext()) { //compare every coauthor for this authorName
					Map<String,String> outerCoAuthor = (Map)outerItr.next();
					coAuthorNamesOuterIteration.add(outerCoAuthor.get("normalized_coauthor"));

					//System.out.println("outerCoAuthor: " + outerCoAuthor.get("normalized_coauthor") + " ID: " + String.valueOf(outerCoAuthor.get("author_id")));
					//System.out.println("coAuthorNamesOuterIteration: ");

					if (!outerAuthorID.equals(Integer.valueOf(String.valueOf(outerCoAuthor.get("author_id"))))) { //dont compare a cluster with itself
						outerAuthorID = Integer.valueOf((String.valueOf(outerCoAuthor.get("author_id"))));

						Iterator innerItr = coauthors.iterator();
						while (innerItr.hasNext()) {
							Map<String,String> innerCoAuthor = (Map)innerItr.next();

							//continue if outer and inner are coauthors of the same author
							Integer outerCoauthorID = Integer.valueOf((String.valueOf(outerCoAuthor.get("author_id"))));
							if (outerCoauthorID.equals(Integer.valueOf((String.valueOf(innerCoAuthor.get("author_id")))))) continue;

							//gather the coauthorNames till we got a new author_id
							if (!innerAuthorID.equals(Integer.valueOf(String.valueOf(innerCoAuthor.get("author_id"))))) {		
								for (int k=0; k < coAuthorNamesOuterIteration.size(); k++) {
									if (coAuthorNamesInnerIteration.contains(coAuthorNamesOuterIteration.get(k))) counter++;
								}
								coAuthorNamesInnerIteration.clear();

								//check if the 2 clusters have the most similar coauthors
								if (counter > max) {
									max = counter;
									//System.out.println("new inner max: " + max);
									maxAuthorID = innerAuthorID;
								}

								innerAuthorID = Integer.valueOf((String.valueOf(innerCoAuthor.get("author_id"))));
								counter = 0;
							}

							coAuthorNamesInnerIteration.add(innerCoAuthor.get("normalized_coauthor"));	 
						}
						coAuthorNamesOuterIteration.clear();
						//check if overall the 2 clusters have the most similar coauthors
						if (max > outerMax) {
							outerMax = max;
							//System.out.println("new outer max: " + outerMax);
							outerMaxAuthorID = Integer.valueOf(String.valueOf(outerCoAuthor.get("author_id")));
							tmpInnerMaxAuthorID = innerAuthorID;
							/*
							 * FIXME: outerCoauthors will always be empty because
							 * of coAuthorNamesOuterIteration.clear() in line 105 
							 * above. 
							 */

							outerCoauthors = coAuthorNamesOuterIteration;  
							innerCoauthors = coAuthorNamesInnerIteration;
						}
					}
				}

				//System.out.println("OuterMax: " + outerMax + " - Merge " + outerAuthorID + " with " + tmpInnerMaxAuthorID + " name: " + authorNames.get(m));

				//end when there are no more authors to merge
				if (outerMax < threshold) {
					//System.out.println("end this");
					break;
				}

				List<String> coauthorsToAdd = new ArrayList<String>();
				//calculate the authors we have to add		
				for(int k=0; k<innerCoauthors.size(); k++) { 
					if (!outerCoauthors.contains(innerCoauthors.get(k))) coauthorsToAdd.add(innerCoauthors.get(k));
				}

				for(int k=0;k < coauthorsToAdd.size(); k++) {
					HashMap<String, String> coAuthorToAdd = new HashMap<String, String>();
					coAuthorToAdd.put("authorID", outerMaxAuthorID + "");
					coAuthorToAdd.put("normalizedCoauthor", coauthorsToAdd.get(k));
					//System.out.println("We have to add: " + coAuthorToAdd.get("authorID") + " " + coAuthorToAdd.get("normalizedCoauthor"));

					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertMergedCoAuthor", coAuthorToAdd);
					//TODO update in data structure too
					//INSERT INTO author_coauthor(author_id, normalized_coauthor) VALUES (#{authorID},#{normalizedCoauthor});
				}

				//System.out.println("we delete: " + tmpInnerMaxAuthorID);

				int n=0;
				boolean found = false;

				//search the cluster with the actual authorID and add the new ID
				for (Map<String,List<String>> authorIDs: clusterIDsList) {
					int authorIDsSize = authorIDs.get("IDs").size();
					for (int k=0; k<authorIDsSize; k++) {
						if (outerAuthorID.equals(Integer.valueOf(authorIDs.get("IDs").get(k)))) {
							System.out.println(outerAuthorID + " = " + authorIDs.get("IDs").get(k) + " -> "  + String.valueOf(tmpInnerMaxAuthorID));
							List<String> tmpList = authorIDs.get("IDs"); 
							tmpList.add(String.valueOf(tmpInnerMaxAuthorID));
							//authorIDs.put("IDs", tmpList);
							//clusterIDsList.set(n, authorIDs);
							found = true;
						}
					}
					n++;
				}
				//create a new cluster with the ID that fits in no other cluster
				if (!found) {
					System.out.println("not found -> " + tmpInnerMaxAuthorID);
					Map<String,List<String>> tmpMap = new HashMap<String,List<String>>();
					List<String> tmpList = new ArrayList<String>();
					tmpList.add(String.valueOf(outerMaxAuthorID));
					System.out.println("outer: " + outerMaxAuthorID);
					tmpMap.put("IDs",tmpList);
					if (outerAuthorID != tmpInnerMaxAuthorID) {
						tmpList.add(String.valueOf(tmpInnerMaxAuthorID));
						tmpMap.put("IDs",tmpList);
					}
					System.out.println("map: " + tmpMap);
					clusterIDsList.add(tmpMap);
				}

				//delete the author we merged from DB
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteCoAuthors", tmpInnerMaxAuthorID);
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteAuthor", tmpInnerMaxAuthorID);

				//remove the author we merged from the datastructure
				for (int k=0; k<authorMap.get(authorSet.getKey()).size(); k++) {
					if(Integer.valueOf(authorMap.get(authorSet.getKey()).get(k).get("author_id")) == tmpInnerMaxAuthorID) authorMap.get(authorSet.getKey()).remove(k);
				}

			}
		}
		sessionRkr.commit();
		
		//my own test
		MyOwnTest.test(authorMap);
		
		int countSingleClusters = 0;
		for (Map<String,List<String>> authorIDList: clusterIDsList ) {
			if(authorIDList.get("IDs").size() == 1) countSingleClusters++;
			System.out.println("-----------------------------------------------");
			for (int k=0; k<authorIDList.get("IDs").size(); k++) {
				System.out.println(authorIDList.get("IDs").get(k) + " -> " + authorIDList.get("IDs").size()); 
			}
		}

		System.out.println("countSingleClusters " + countSingleClusters);

		List<Integer> authorsWithoutCoauthors = new ArrayList<Integer>();
		//get the author ids that have no coauthor
		List<Integer> authorIDs = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorIDs");
		for(int k=1; k<authorIDs.get(authorIDs.size()-1); k++) {
			if(!authorIDs.contains(k)) authorsWithoutCoauthors.add(k);
		}

		//for(Integer authorID: authorsWithoutCoauthors) {
		//System.out.println("This one has no coauthor: " + authorID);
		//}
		System.out.println(authorIDs.get(authorIDs.size()-1));

		return clusterIDsList;
		/*
			//Soundex
			Soundex soundex = new Soundex();
			System.out.println("First Name: " + personNames.get(1).getFirstName() + " Last Name: " + personNames.get(1).getLastName());
			System.out.println("Soundex Code Last Name: " + soundex.encode(personNames.get(0).getLastName()));
			System.out.println("Soundex Code First Name: " + soundex.encode(personNames.get(0).getFirstName()));
		 */

	}

	public static void useTitleToMergeClusters(SqlSession sessionRkr, List<Map<String, ArrayList<String>>> authorIDNumberList) throws CorruptIndexException, LockObtainFailedException, IOException {
		//create a document with the titles of the publications for every cluster
		//we use this to find cluster which maybe can be linked

		List<Map<Integer,String>> authorList = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectCoAuthorsLucene");

		analyzer = new StandardAnalyzer(Version.LUCENE_35);
		index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		w = new IndexWriter(index, config);

		int lastAuthorID = 0;
		String titles = "";
		Document doc = new Document();
		for (Map<String,List<String>> clusterIDs: clusterIDsList) { //one document for each cluster
			doc = new Document();
			for (String authorID: clusterIDs.get("IDs")) {
				for (Map<String, ArrayList<String>> authorHashMap: authorIDNumberList) { //get all titles for this cluster
					if (authorID.equals(authorHashMap.get("authorIDs").get(0))) {
						titles += authorHashMap.get("title").get(0) + " ";
						break;
					}
				}
				doc.add(new Field("titles", titles, Field.Store.YES, Field.Index.ANALYZED)); //add normalized name to coauthor field
				w.addDocument(doc);
			}
		}

		w.close();
	}

}
