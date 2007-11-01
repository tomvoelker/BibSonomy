package org.bibsonomy.database.util;

/**
 * Used to determine whether we want to retrieve an object or a list.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public enum QueryFor {
	/** for queries that return a single object */
	OBJECT,
	/** for queries that return a list of objects */
	LIST;
}