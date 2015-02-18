package org.bibsonomy.es;

/**
 * Used to select which indices should be updated with a single db-scan
 *
 * @author Jens Illig
 */
public enum IndexType {
	LUCENE,
	ELASTICSEARCH,
	BOTH
}
