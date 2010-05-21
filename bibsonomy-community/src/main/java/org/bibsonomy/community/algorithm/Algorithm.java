package org.bibsonomy.community.algorithm;

/**
 * interface for managing different algorithms
 * @author fei
 *
 */
public interface Algorithm {
	
	/**
	 * get a short name describing the algorithm
	 * @return
	 */
	public String getName();
	
	/**
	 * get serialized meta informations for the given algorithm (e.g., parameter settings)
	 * @return
	 */
	public String getMeta();
}
