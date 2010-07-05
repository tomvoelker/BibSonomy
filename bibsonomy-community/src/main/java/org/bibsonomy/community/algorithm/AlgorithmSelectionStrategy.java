package org.bibsonomy.community.algorithm;

public interface AlgorithmSelectionStrategy {
	/** 
	 * get the id of a different clustering for the given user based on the implemented
	 * selection strategy
	 *  
	 * @param userName
	 * @return
	 * @throws Exception 
	 */
	public Integer getNewClustering(String userName) throws Exception;
	
	/** 
	 * get the id of a new run set based on the implemented selection strategy
	 * 
	 * @param userName
	 * @return
	 */
	public Integer getNewRunSet(String userName);
}
