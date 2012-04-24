package org.bibsonomy.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

public class AuthorClustering {
	public static List<List<Integer>> authorClustering(SqlSession session, SqlSession sessionRkr) {

		//clustering the authors with the coauthor relationship
		int threshold = 2;
		List<String> authorNames = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectAuthorNames");
		List<List<Integer>> authorIDsList = new ArrayList<List<Integer>>();

		//check the name for every author
		for(int m=0; m < authorNames.size(); m++) {
			//do this as long there is something we can merge
			while (true) {
				//merge authors who have the same coauthors
				//TODO List<Map<String,String>> testX = getAuthorsWithNameLikeX("r.wille", sessionRkr);
				List<Map<Integer,String>> authorsWithNameX = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectCoAuthors", authorNames.get(m));
				if (authorsWithNameX.isEmpty()) {
					//System.out.println("its empty");
					m++;
					continue;
				}

				//cluster the author table
				Iterator outerItr = authorsWithNameX.iterator();

				int innerAuthorID=0, maxAuthorID=0;
				int outerAuthorID = 0, outerMaxAuthorID=0, tmpInnerMaxAuthorID=0;
				int counter=0, max=0;
				int outerMax = 0;

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

				//System.out.println("OuterMax: " + outerMax + " - Merge " + outerAuthorID + " with " + tmpInnerMaxAuthorID);

				//end when there are no more authors to merge
				if (outerMax < threshold) {
					//System.out.println("end this");
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
					//System.out.println("We have to add: " + coAuthorToAdd.get("authorID") + " " + coAuthorToAdd.get("normalizedCoauthor"));

					sessionRkr.insert("org.mybatis.example.Entity-Identification.insertMergedCoAuthor", coAuthorToAdd);			 
				}

				//System.out.println("we delete: " + tmpInnerMaxAuthorID);

				//search the list with the actual authorID and save the redundant ID
				int n=0;
				boolean found = false;
				for (List<Integer> authorIDs: authorIDsList) {
					int authorIDsSize = authorIDs.size();
					for (int k=0; k<authorIDsSize; k++) {
						//System.out.println("outerAuthorID:" + outerAuthorID + " " + authorIDs.get(k));
						if (outerAuthorID == authorIDs.get(k)) {
							authorIDs.add(tmpInnerMaxAuthorID);
							//TODO we cant merge the same author IDs if (tmpInnerMaxAuthorID != outerAuthorID) 
							authorIDsList.set(n, authorIDs);
							found = true;
						}
					}
					n++;
				}
				if (!found) {
					List<Integer> authorIDs = new ArrayList<Integer>();
					authorIDs.add(outerAuthorID);
					authorIDs.add(tmpInnerMaxAuthorID);
					authorIDsList.add(authorIDs);
				}

				//delete the author we merged
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteCoAuthors", tmpInnerMaxAuthorID);
				sessionRkr.delete("org.mybatis.example.Entity-Identification.deleteAuthor", tmpInnerMaxAuthorID);
			}
		}
		sessionRkr.commit();

		for (List<Integer> authorIDList: authorIDsList ) {
			System.out.println("-----------------------------------------------");
			for (int k=0; k < authorIDList.size(); k++) {
				System.out.println(authorIDList.get(k)); 
			}
		}

		return authorIDsList;
		/*
			//Soundex
			Soundex soundex = new Soundex();
			System.out.println("First Name: " + personNames.get(1).getFirstName() + " Last Name: " + personNames.get(1).getLastName());
			System.out.println("Soundex Code Last Name: " + soundex.encode(personNames.get(0).getLastName()));
			System.out.println("Soundex Code First Name: " + soundex.encode(personNames.get(0).getFirstName()));
		 */

		//compare the results
		//create data for gnuplot to plot the results
	}

}
