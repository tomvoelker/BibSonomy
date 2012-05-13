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
	static List<List<Integer>> clusterIDsList = new ArrayList<List<Integer>>();

	//Lucene globals
	static IndexWriter w = null;
	static StandardAnalyzer analyzer = null;
	static Directory index = null;

	public static List<List<Integer>> authorClustering(SqlSession sessionRkr) {

		//clustering the authors with the coauthor relationship
		int threshold = 2;
		List<String> authorNames = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorNames");

		for(int m=0; m < authorNames.size(); m++) { //check the name for every author
			System.out.println("new author: " + authorNames.get(m) + "-----------------------------------------------------");
			while (true) { //do this as long there is something we can merge
				System.out.println("Author Clustering: " + m);
				//merge authors who have the same coauthors
				List<Map<Integer,String>> coauthors = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectCoAuthors", authorNames.get(m));
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
				List<String> coAuthorNamesOuterIteration = new ArrayList<String>();		
				List<String> coAuthorNamesInnerIteration = new ArrayList<String>();

				//here we save our tmp results we use later to merge the both authors 
				List<String> innerCoauthors = new ArrayList<String>();		
				List<String> outerCoauthors = new ArrayList<String>();

				while (outerItr.hasNext()) { //compare every coauthor for this authorName
					Map<String,String> outerCoAuthor = (Map)outerItr.next();
					coAuthorNamesOuterIteration.add(outerCoAuthor.get("normalized_coauthor"));

					//System.out.println("outerCoAuthor: " + outerCoAuthor.get("normalized_coauthor") + " ID: " + String.valueOf(outerCoAuthor.get("author_id")));
					//System.out.println("coAuthorNamesOuterIteration: ");
					for (String test: coAuthorNamesOuterIteration) {
						//System.out.print(test + " ");
					}
					//System.out.println(" ");

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

					//if (tmpInnerMaxAuthorID == 416) System.exit(1);
				}

				//System.out.println("we delete: " + tmpInnerMaxAuthorID);

				int n=0;
				boolean found = false;

				//search the cluster with the actual authorID and add the new ID
				for (List<Integer> authorIDs: clusterIDsList) {
					int authorIDsSize = authorIDs.size();
					for (int k=0; k<authorIDsSize; k++) {
						//System.out.println("outerAuthorID:" + outerAuthorID + " " + authorIDs.get(k));
						if (outerAuthorID == authorIDs.get(k)) {
							authorIDs.add(tmpInnerMaxAuthorID);
							clusterIDsList.set(n, authorIDs);
							found = true;
						}
					}
					n++;
				}
				//create a new cluster with the ID that fits in no other cluster
				if (!found) {
					List<Integer> authorIDs = new ArrayList<Integer>();
					authorIDs.add(outerAuthorID);
					if (outerAuthorID != tmpInnerMaxAuthorID) authorIDs.add(tmpInnerMaxAuthorID);
					clusterIDsList.add(authorIDs);
				}

				//delete the author we merged from DB
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteCoAuthors", tmpInnerMaxAuthorID);
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteAuthor", tmpInnerMaxAuthorID);
			}
		}
		sessionRkr.commit();

		int countSingleClusters = 0;
		for (List<Integer> authorIDList: clusterIDsList ) {
			if(authorIDList.size() == 1) countSingleClusters++;
			//System.out.println("-----------------------------------------------");
			//for (int k=0; k < authorIDList.size(); k++) {
			//System.out.println(authorIDList.get(k)); 
			//}
		}

		System.out.println("countSingleClusters " + countSingleClusters);

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
		for (List<Integer> clusterIDs: clusterIDsList) { //one document for each cluster
			doc = new Document();
			for (Integer authorID: clusterIDs) {
				for (Map<String, ArrayList<String>> authorHashMap: authorIDNumberList) { //get all titles for this cluster
					if (authorID.equals(Integer.valueOf(authorHashMap.get("authorIDs").get(0)))) {
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
