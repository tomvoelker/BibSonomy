package org.bibsonomy.search.es.client;

/**
 * abstract class for data to index or delete
 *
 * @author dzo
 */
public abstract class AbstractData {
	/** the routing information for this data */
	private String routing;

	/** the type information for this data */
	private String type;

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
}
