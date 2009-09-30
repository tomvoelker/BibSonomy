package org.bibsonomy.batch.searchlucene.database;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.batch.searchlucene.database.params.GroupParam;
import org.bibsonomy.batch.searchlucene.database.params.GroupTasParam;
import org.bibsonomy.batch.searchlucene.database.params.TasParam;

public interface LuceneLogic {

	/** get number of TAS entries 
	 * @throws SQLException */
	public int getTasSize() throws SQLException;
	
	/**
	 * get list of group ids with corresponding group names
	 * @return
	 * @throws SQLException
	 */
	public List<GroupParam> getGroupIDs() throws SQLException;
	
	/**
	 * get list of tag assignments, that is: pairs of tag names with corresponding 
	 * content ids
	 * 
	 * @param skip The number of results to ignore
	 * @param max The maximum number of results to return
	 * @return A List of result objects
	 * @throws SQLException
	 */
	public List<TasParam> getTasEntries(Integer skip, Integer max) throws SQLException;
	
	/**
	 * get list of tag assignments, already grouped per post
	 * 
	 * @param skip
	 * @param max
	 * @return pair of content_id with corresponding space separated list of assigned tags
	 * @throws SQLException 
	 */
	public List<TasParam> getGroupedTasEntries(int skip, int max) throws SQLException;

	/**
	 * get list of group ids with corresponding content ids
	 * 
	 * @param skip The number of results to ignore
	 * @param max The maximum number of results to return
	 * @return A List of result objects
	 * @throws SQLException
	 */
	public List<GroupTasParam> getGroupTasEntries(Integer skip, Integer max) throws SQLException;

}
