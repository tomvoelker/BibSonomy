package org.bibsonomy.util;

import java.util.Map;

/**
 * Adapter to access a {@link Map} via the {@link GetProvider} interface
 *
 * @author jensi
 * @param <F> Key type of the {@link Map}
 * @param <T> Value type of the {@link Map}
 */
public class MapGetProvider<F, T> implements GetProvider<F, T> {
	private final Map<F,T> map;
	
	/**
	 * @param map the backend map
	 */
	public MapGetProvider(final Map<F,T> map) {
		this.map = map;
	}

	@Override
	public T get(F arg) {
		return map.get(arg);
	}
}
