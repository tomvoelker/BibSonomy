package org.bibsonomy.recommender.tags;

import java.util.Properties;

import org.bibsonomy.services.recommender.TagRecommender;


/**
 * @author fei
 * @version $Id$
 */
public interface TagRecommenderConnector extends TagRecommender {
	/**
	 * Initialize object.
	 * @param props specific properties
	 * @return true on success, false otherwise
	 * @throws Exception Exception describing problem.
	 */
	public boolean initialize(Properties props) throws Exception;
	
	/**
	 * Establish connection to recommender.
	 * @return true on success, false otherwise
	 * @throws Exception Exception describing problem.
	 */
	public boolean connect() throws Exception;
	
	/**
	 * Terminate connection from recommender.
	 * @return true on success, false otherwise
	 * @throws Exception Exception describing problem.
	 */
	public boolean disconnect() throws Exception;
	
	/**
	 * Arbitrary auxiliary informations.
	 *  
	 * @return the meta informations
	 */
	public byte[] getMeta();

	/**
	 * Description for identifying this recommender.
	 * @return the id
	 */
	public String getId();
}
