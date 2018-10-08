package org.bibsonomy.search.es.client;

import java.util.Map;

/**
 * the index data
 *
 * @author dzo
 */
public class IndexData extends AbstractData {

	private Map<String, Object> source;

	/**
	 * @return the source
	 */
	public Map<String, Object> getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Map<String, Object> source) {
		this.source = source;
	}
}
