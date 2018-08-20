package org.bibsonomy.util.collection;

import java.util.Map;

/**
 * a map that preserves the uniqueness of its values and keys
 *
 * @author dzo
 */
public interface BiMap<K, V> extends Map<K, V> {

	/**
	 * @param value
	 * @return the key for the value
	 */
	public K getKeyByValue(V value);
}
