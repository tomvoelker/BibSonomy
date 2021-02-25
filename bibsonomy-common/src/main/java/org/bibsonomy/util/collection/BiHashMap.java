package org.bibsonomy.util.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * a hashMap based implementation of {@link BiMap}
 *
 * XXX: very simple
 *
 * @author dzo
 * @param <K>
 * @param <V>
 */
public class BiHashMap<K, V> extends HashMap<K, V> implements BiMap<K, V> {

	private Map<V, K> reverseMap = new HashMap<>();

	@Override
	public V put(K key, V value) {
		final V result = super.put(key, value);
		this.reverseMap.put(value, key);
		return result;
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		super.putAll(m);

		this.reverseMap.putAll(reverseMap(m));
	}

	@Override
	public K getKeyByValue(final V value) {
		return this.reverseMap.get(value);
	}

	private static final <K, V> Map<V, K> reverseMap(final Map<K, V> map) {
		final Map<V, K> reversedMap = new HashMap<>();
		for (final Entry<K, V> entry : map.entrySet()) {
			reversedMap.put(entry.getValue(), entry.getKey());
		}

		return reversedMap;
	}
}
