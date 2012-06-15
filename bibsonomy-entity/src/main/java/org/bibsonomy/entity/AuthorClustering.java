package org.bibsonomy.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import sun.security.krb5.internal.APOptions;

public class AuthorClustering {
	List<Map<String,Document>> authorTitleMaps = new ArrayList<Map<String,Document>>();
	//save results
	static List<List<Integer>> clusterIDsList = new ArrayList<List<Integer>>();
	static List<Map<String,List<String>>> clustersWithCoauthorNames = new ArrayList<Map<String,List<String>>>();

	//Lucene globals
	static IndexWriter w = null;
	static StandardAnalyzer analyzer = null;
	static Directory index = null;

	public static List<List<Integer>> authorClustering(SqlSession sessionRkr) {

		//for time measurements
		float timeAuthorStart = 0;
		float last10Authors[] = {0,0,0,0,0,0,0,0,0,0};
		int replaceAuthor = 0;

		//clustering the authors with the coauthor relationship
		int threshold = 3;

		final List<Map<String,String>> authorNames = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorNames");

		Map<String,Map<Integer,Set<String>>> authorMap = new HashMap<String,Map<Integer,Set<String>>>(); //authorName->Map with ID->set of coauthors
		Map<Integer,Set<String>> mapOfCoauthorsSets = new HashMap<Integer,Set<String>>(); //authorID -> all coauthors
		Map<Integer,List<String>> authorIDName = new HashMap<Integer,List<String>>(); //authorID -> authorName,firstName,lastName

		//build the datastructure
		for (Map<String,String> tmpAuthor: authorNames) {
			//order with authorIDs
			int tmpAuthorID = Integer.valueOf(String.valueOf(tmpAuthor.get("author_id")));
			//fill mapOfCoauthorsSets
			if (mapOfCoauthorsSets.containsKey(tmpAuthorID)) mapOfCoauthorsSets.get(tmpAuthorID).add(tmpAuthor.get("normalized_coauthor"));
			else {
				Set tmpSet = new HashSet<String>();
				tmpSet.add(tmpAuthor.get("normalized_coauthor"));
				mapOfCoauthorsSets.put(tmpAuthorID,tmpSet);
			}
			
			if (authorMap.containsKey(tmpAuthor.get("normalized_name"))) {
				if (authorMap.get(tmpAuthor.get("normalized_name")).get(tmpAuthorID) == null) {
					Set tmpSet = new HashSet<String>();
					tmpSet.add(tmpAuthor.get("normalized_coauthor"));
					Map tmpMap = authorMap.get(tmpAuthor.get("normalized_name"));
					tmpMap.put(tmpAuthorID, tmpSet);
					authorMap.put(tmpAuthor.get("normalized_name"),tmpMap);
				}
				authorMap.get(tmpAuthor.get("normalized_name")).get(tmpAuthorID).add(tmpAuthor.get("normalized_coauthor"));
			}
			//create new map
			else {
				Set tmpSet = new HashSet<String>();
				tmpSet.add(tmpAuthor.get("normalized_coauthor"));
				Map tmpMap = new HashMap<Integer,Set<String>>(); 
				tmpMap.put(tmpAuthorID, tmpSet);
				authorMap.put(tmpAuthor.get("normalized_name"),tmpMap);
			}

			//order authorIDs to Name
			if (!authorIDName.containsKey(tmpAuthorID)) {
				List<String> tmpList = new ArrayList<String>();
				tmpList.add(tmpAuthor.get("normalized_name"));
				tmpList.add(tmpAuthor.get("first_name"));
				tmpList.add(tmpAuthor.get("last_name"));
				tmpList.add(tmpAuthor.get("user_name"));
				authorIDName.put(tmpAuthorID, tmpList);
			}
		}

		int z=1;
		int endless = 0;
		for(Map.Entry authorSet : authorMap.entrySet()) {

			if (replaceAuthor == 10) {
				replaceAuthor = 0;
				float sum = 0;
				for (float value: last10Authors) {
					sum += value;
				}
				System.out.println("Elapsed time for last 10 authors: " + sum);
			}
			last10Authors[replaceAuthor] = ((System.nanoTime() - timeAuthorStart)/1000000000);
			replaceAuthor++;

			timeAuthorStart = System.nanoTime();

			System.out.println("Author Clustering: " + z + " " + authorSet.getKey() + " endless: " + endless);
			z++;

			Set<String> outerSet = new HashSet<String>();
			int innerMaxID = 0, outerMaxID = 0;
			int counter=0, max=0;

			Map<Integer,Set<String>> authorNameMap = (Map<Integer, Set<String>>) authorSet.getValue();

			while (true) { //do this as long there is something we can merge

				endless++;
				innerMaxID = 0;
				outerMaxID = 0;
				Set<String> innerSetBackup = new HashSet<String>();
				Set<String> outerSetBackup = new HashSet<String>();

				List<Integer> compareNow = new ArrayList<Integer>();
				for(Map.Entry outerAuthorSet: authorNameMap.entrySet()) {
					Integer outerAuthorID = (Integer)outerAuthorSet.getKey();
					compareNow.add(outerAuthorID);
					outerSet =(Set) outerAuthorSet.getValue();
					for(Map.Entry innerAuthorSet: authorNameMap.entrySet()) {
						Integer innerAuthorID = (Integer)innerAuthorSet.getKey();
						if((!compareNow.contains(innerAuthorID)) || (outerAuthorID == innerAuthorID)) break;
						//System.out.println("outerID: " + outerAuthorID + " innerID: " + innerAuthorID);
						Set<String> innerSet = (Set)innerAuthorSet.getValue();

						Set<String> tmpBackup = new HashSet<String>(innerSet); //we would change the set in the datastructe if we change innerSet here
						//cut-set
						tmpBackup.retainAll(outerSet);
						counter = tmpBackup.size();
						if (counter > 0) {
							//System.out.println("Schnittmenge: " + tmpBackup + " outerSet: " + outerSet + " innerSet: " + innerSet);
						}

						//check if the 2 clusters have the most similar coauthors
						if (counter > max) {
							innerSetBackup = new HashSet<String>(innerSet); //we need innerSet later and dont want to change it here
							outerSetBackup = new HashSet<String>(outerSet);
							max = counter;
							innerMaxID = innerAuthorID;
							outerMaxID = outerAuthorID;
							//System.out.println("new inner max: " + max);
						}

						counter = 0;
					}
				}

				//end when there are no more authors to merge
				if (max < threshold) {
					//System.out.println("end this");
					break;
				}

				//System.out.println("Max: " + max + " - Merge " + innerMaxID + " with " + outerMaxID + " name: ");
				max = 0;

				//we delete the inner set so we search all entrys we have to add from inenrSet to outerSet
				//System.out.println("backup: " + innerSetBackup + " outerSet: " + outerSetBackup);
				innerSetBackup.removeAll(outerSetBackup);

				for (String stringToAdd: innerSetBackup) {
					HashMap<String, String> coAuthorToAdd = new HashMap<String, String>();
					coAuthorToAdd.put("authorID", outerMaxID + "");
					coAuthorToAdd.put("normalizedCoauthor", stringToAdd);
					//System.out.println("We have to add: " + coAuthorToAdd.get("authorID") + " " + coAuthorToAdd.get("normalizedCoauthor"));

					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertMergedCoAuthor", coAuthorToAdd);
					//update in data structure too
					//System.out.println("before: " + authorNameMap.get(outerMaxID));
					authorNameMap.get(outerMaxID).addAll(innerSetBackup);
					//System.out.println("after: " + authorNameMap.get(outerMaxID));
				}

				//System.out.println("we delete: " + innerMaxID);

				int n=0;
				boolean found = false;

				//search the cluster with the actual authorID and add the new ID
				for (List<Integer> authorIDs: clusterIDsList) {
					int authorIDsSize = authorIDs.size();
					//there cant be more then 1 ID to add
					for (Integer authorID: authorIDs) {
						//System.out.println("outerAuthorID:" + authorID);
						int idToAdd = 0;
						if (outerMaxID == authorID) idToAdd = innerMaxID;
						if (innerMaxID == authorID) idToAdd = outerMaxID;
						if (idToAdd > 0) {
							authorIDs.add(idToAdd);
							//System.out.println("add new ID to cluster");
							//System.out.println(authorIDs);
							clusterIDsList.set(n, authorIDs);
							found = true;
							break;
						}
					}
					n++;
				}
				//create a new cluster with the ID that fits in no other cluster
				if (!found) {
					//System.out.println("create a new cluster - outer: " + outerMaxID + " inner: " + innerMaxID);
					List<Integer> authorIDs = new ArrayList<Integer>();
					authorIDs.add(outerMaxID);
					if (outerMaxID != innerMaxID) authorIDs.add(innerMaxID);
					//System.out.println("the new cluster:" + authorIDs);
					clusterIDsList.add(authorIDs);
				}

				//delete the author we merged from DB
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteCoAuthors", innerMaxID);
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteAuthor", innerMaxID);

				//remove the author we merged from the datastructure
				authorNameMap.remove(innerMaxID);
			}
		}

		float sum = 0;
		for (float value: last10Authors) {
			sum += value;
		}
		System.out.println("Elapsed time for last author: " + ((System.nanoTime() - timeAuthorStart)/1000000000) + "s");
		System.out.println("Elapsed time for last 10 authors: " + sum);
		sessionRkr.commit();

		//my own test
		//MyOwnTest.test(authorMap);

		int countSingleClusters = 0;
		for (List<Integer> authorIDList: clusterIDsList ) {
			if(authorIDList.size() == 1) countSingleClusters++;
			//System.out.println("-----------------------------------------------");
			//for (int k=0; k < authorIDList.size(); k++) {
			//System.out.println(authorIDList.get(k)); 
			//}
		}


		/*
		for (float alpha=(float)0.2; alpha<1; alpha += 1) {
			System.out.println("countSingleClusters " + countSingleClusters);
			int rightMatches = 0;
			int wrongMatches = 0;

			//"myOwn" clustering
			//check how much same coauthors two clusters have. This can be an indicator for the same person with different names
			int outer = 0;
			Set<Integer> blacklist = new HashSet<Integer>();
			for (List<Integer> outerClusterIDs: clusterIDsList) {
				outer++;
				Set outerSet = new HashSet<String>(); //set of all coauthors for the outer cluster
				//all coauthors in this cluster
				for (Integer outerClusterID: outerClusterIDs) {
					outerSet.addAll(mapOfCoauthorsSets.get(outerClusterID));
				}

				//inner iterations
				for (int k=outer+1; k < clusterIDsList.size(); k++) {
					Set tmpSet = new HashSet(outerSet);
					Set innerSet = new HashSet<String>(); //set of all coauthors for the inner cluster
					for (Integer innerClusterID: clusterIDsList.get(k)) {
						innerSet.addAll(mapOfCoauthorsSets.get(innerClusterID));
					}
					tmpSet.retainAll(innerSet);
					List<String> outerAuthorName = authorIDName.get(clusterIDsList.get(outer).get(0));
					List<String> innerAuthorName =  authorIDName.get(clusterIDsList.get(k).get(0));

					int outerID = clusterIDsList.get(outer).get(0);
					int innerID = clusterIDsList.get(k).get(0);

					if (outerAuthorName.get(1) != null && innerAuthorName.get(1) != null && outerAuthorName.get(2) != null && innerAuthorName.get(2) != null) {
						if (tmpSet.size() > 2) { //check if there enough same coauthors
							if (!innerSet.contains(outerAuthorName.get(0)) && !outerSet.contains(innerAuthorName.get(0))) { //the name shouldnt be in the others coauthor list
								if (!outerAuthorName.get(0).equals(innerAuthorName.get(0))) { //the names shouldnt be the same
									if ((outerAuthorName.get(1).equals(innerAuthorName.get(1))) || (outerAuthorName.get(2).equals(innerAuthorName.get(2)))) { //but first names should be the same
										if((float)tmpSet.size()/((innerSet.size() + outerSet.size())-tmpSet.size()) > alpha) { //check if we are sure enough
											if (!blacklist.contains(outerID) && !blacklist.contains(innerID)) {
												blacklist.clear();
												blacklist.add(outerID);
												blacklist.add(innerID);
												if (outerAuthorName.get(3) != null && innerAuthorName.get(3) != null) {
													if (outerAuthorName.get(3).equals(innerAuthorName.get(3))) rightMatches++;
													else wrongMatches++;
												}

												//compare both sets
												System.out.println("outerUserName: " + outerAuthorName.get(3) + " innerUserName: " + innerAuthorName.get(3));
												System.out.println("outerID: " + clusterIDsList.get(outer) + " innerID: " + clusterIDsList.get(k));
												System.out.println("outerSet: " + outerSet + " name: " + outerAuthorName);
												System.out.println("innerSet: " + innerSet + " name: " + innerAuthorName);
												System.out.println("results: " + ((innerSet.size() + outerSet.size())-tmpSet.size()) + " / " + tmpSet.size() + " resultSet: " + tmpSet);
												System.out.println("end-----------------------------------------------");
											}
										}
									}
								}
							}
						}
					}
				}
			}
			System.out.println("alpha: " + alpha);
			System.out.println("rightMatches: " + rightMatches);
			System.out.println("missMatches: " + wrongMatches);
		}
*/
		//System.exit(1);

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

	}

	public static void useTitleToMergeClustersTest(SqlSession sessionRkr, List<Map<String, ArrayList<String>>> authorIDNumberList) throws CorruptIndexException, LockObtainFailedException, IOException {
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

