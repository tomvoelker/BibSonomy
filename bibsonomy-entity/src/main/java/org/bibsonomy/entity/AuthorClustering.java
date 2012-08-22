package org.bibsonomy.entity;

import java.io.File;
import java.io.FileWriter;
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

	public static List<List<Integer>> authorClustering(SqlSession sessionRkr, Map<Integer,Integer> authorToContent, ArrayList<HashMap<String,String>> authorNames) {

		clusterIDsList.clear();
		clustersWithCoauthorNames.clear();
		
		//the authorID that describes a cluster
		HashSet<Integer> uniqueClusterAuthorID = new HashSet<Integer>();
		//for time measurements
		float timeAuthorStart = System.nanoTime();
		float last10Authors[] = {0,0,0,0,0,0,0,0,0,0};
		int replaceAuthor = 0;

		//clustering the authors with the coauthor relationship
		int threshold = 3;

		//final List<Map<String,String>> authorNames = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorNames");

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
		for(Map.Entry authorSet : authorMap.entrySet()) {

			if (replaceAuthor == 10) {
				replaceAuthor = 0;
				float sum = 0;
				for (float value: last10Authors) {
					sum += value;
				}
			}
			last10Authors[replaceAuthor] = ((System.nanoTime() - timeAuthorStart)/1000000000);
			replaceAuthor++;

			timeAuthorStart = System.nanoTime();

			//System.out.println("Author Clustering: " + z + " " + authorSet.getKey());

			if (z % 10000 == 0) {
				System.out.println("Author Clustering: " + z + " " + authorSet.getKey());
				sessionRkr.commit();
			}

			z++;

			Set<String> outerSet = new HashSet<String>();
			int innerMaxID = 0, outerMaxID = 0;
			int counter=0, max=0;

			Map<Integer,Set<String>> authorNameMap = (Map<Integer, Set<String>>) authorSet.getValue();

			while (true) { //do this as long there is something we can merge
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

					HashMap<String, Integer> contentToAdd = new HashMap<String, Integer>();
					contentToAdd.put("authorID", outerMaxID);
					contentToAdd.put("contentID", authorToContent.get(outerMaxID));
					//System.out.println("We have to add: " + coAuthorToAdd.get("authorID") + " " + coAuthorToAdd.get("normalizedCoauthor"));

					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertMergedCoAuthor", coAuthorToAdd);
					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertMergedContent", contentToAdd);
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
					for (Integer authorID: authorIDs) {
						//System.out.println("outerAuthorID:" + authorID);
						int idToAdd = 0;
						if (outerMaxID == authorID) idToAdd = innerMaxID;
						if (innerMaxID == authorID) idToAdd = outerMaxID;
						if (idToAdd > 0) {
							uniqueClusterAuthorID.add(idToAdd);
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
					uniqueClusterAuthorID.add(outerMaxID);
					if (outerMaxID != innerMaxID) authorIDs.add(innerMaxID);
					//System.out.println("the new cluster:" + authorIDs);
					clusterIDsList.add(authorIDs);
				}

				//delete the author we merged from DB
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteCoAuthors", innerMaxID);
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteAuthorContent", innerMaxID);
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteAuthor", innerMaxID);
				uniqueClusterAuthorID.remove(innerMaxID);

				//remove the author we merged from the tmp datastructure
				authorNameMap.remove(innerMaxID);
			}
		}

		sessionRkr.commit();

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
									if((float)tmpSet.size()/((innerSet.size() + outerSet.size())-tmpSet.size()) >= 0.4) { //check if we are sure enough
										if (!blacklist.contains(outerID) && !blacklist.contains(innerID)) {
											blacklist.clear();
											blacklist.add(outerID);
											blacklist.add(innerID);
											if (outerAuthorName.get(3) != null && innerAuthorName.get(3) != null) {
												if (outerAuthorName.get(3).equals(innerAuthorName.get(3))) rightMatches++;
												else wrongMatches++;
											}

											//merge both clusters
											//add outer to inner and delete outer
											Map<String,String> tmpMap = new HashMap<String,String>();
											tmpMap.put("authorID", String.valueOf(outerID));
											tmpMap.put("firstName", outerAuthorName.get(1));
											tmpMap.put("lastName", outerAuthorName.get(2));
											tmpMap.put("normalizedName", outerAuthorName.get(0));
											sessionRkr.insert("org.mybatis.example.Entity-Identification.insertMergedAuthorName", tmpMap);

											sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteAuthorName", outerID);

											//get the authorID that discriminates the cluster
											HashSet<Integer> tmpSet1 = new HashSet(clusterIDsList.get(outer));
											HashSet<Integer> tmpSet2 = new HashSet(clusterIDsList.get(k));

											tmpSet1.retainAll(uniqueClusterAuthorID);
											tmpSet2.retainAll(uniqueClusterAuthorID);

											Integer[] tmpArray1 = tmpSet1.toArray(new Integer[1]);
											Integer[] tmpArray2 = tmpSet2.toArray(new Integer[1]);

											Map<String,Float> tmpIDs = new HashMap<String,Float>();
											tmpIDs.put("id1", (float)tmpArray1[0]);
											tmpIDs.put("id2", (float)tmpArray2[0]);
											float precision = ((float)(tmpSet.size()) / ((innerSet.size() + outerSet.size())-(float)tmpSet.size()));
											tmpIDs.put("precision", precision);

											sessionRkr.insert("org.mybatis.example.Entity-Identification.insertSimilarCluster", tmpIDs);

											FileWriter writer;
											File ausgabe;
											ausgabe = new File("clustering2.txt");
											try {
												writer = new FileWriter(ausgabe ,true);
												writer.write("name: " + outerAuthorName + "\n");
												writer.write("name: " + innerAuthorName + "\n");
												writer.write("--------------------------\n");
												writer.flush();
												writer.close();
											} catch (IOException e1) {
												//TODO Auto-generated catch block
												e1.printStackTrace();
											}
											
											//compare both sets


											//System.out.println("outerUserName: " + outerAuthorName.get(3) + " innerUserName: " + innerAuthorName.get(3));
											//System.out.println("outerID: " + clusterIDsList.get(outer) + " innerID: " + clusterIDsList.get(k));
											System.out.println("outerSet: " + outerSet + " name: " + outerAuthorName);
											System.out.println("innerSet: " + innerSet + " name: " + innerAuthorName);
											System.out.println("results: " + ((innerSet.size() + outerSet.size())-tmpSet.size()) + " / " + tmpSet.size() + " resultSet: " + tmpSet);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		sessionRkr.commit();

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
}

