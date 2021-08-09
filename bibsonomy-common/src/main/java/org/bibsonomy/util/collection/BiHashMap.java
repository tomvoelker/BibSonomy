/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
