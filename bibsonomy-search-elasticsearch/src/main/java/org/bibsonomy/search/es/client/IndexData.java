package org.bibsonomy.search.es.client;

import java.util.Map;

/**
 * the index data
 *
 * @author dzo
 */
public class IndexData {

	/*+ the routing information for this index data */
	private String routing;

	/** the type information for this index data */
	private String type;

	private Map<String, Object> source;

	/**
	 * @return the routing
	 */
	public String getRouting() {
		return routing;
	}

	/**
	 * @param routing the routing to set
	 */
	public void setRouting(String routing) {
		this.routing = routing;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

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
