package org.bibsonomy.es;

import java.util.ArrayList;
import java.util.Map;

/**
 * TODO: add documentation to this class
 * 
 * @author lutful
 */
public interface IndexUpdater {

	/**
	 * @return LastLogDate
	 */
	long getLastLogDate();

	/**
	 * @return LastTasId
	 */
	Integer getLastTasId();
	
	/**
	 * @param postsToInsert
	 */
	void insertNewPosts(ArrayList<Map<String, Object>> postsToInsert);

	/**
	 * @param contentId
	 */
	void deleteDocumentForContentId(final Integer contentId);

	/**
	 * @param userName
	 */
	void deleteIndexForForUser(String userName);

	/**
	 * @param indexId
	 */
	void deleteIndexForIndexId(long indexId);
}
