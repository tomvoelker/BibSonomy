package org.bibsonomy.es;


/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class AbstractEsClient implements ESClient {
	/**
	 * waits for the yellow (or green) status to prevent NoShardAvailableActionException later
	 */
	@Override
	public void waitForReadyState() {
		this.getClient().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
	}
}
