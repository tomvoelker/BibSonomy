/*
 * Created on 06.03.2006
 */
package recommender.model;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleMapEntry<T,U> implements Map.Entry<T,U> {
	U value;
	final T key;
	
	public SimpleMapEntry(T key, U value) {
		this.value = value;
		this.key = key;
	}
	
	public T getKey() {
		return key;
	}

	public U getValue() {
		return value;
	}

	public U setValue(U arg0) {
		U old = value;
		value = arg0;
		return old;
	}
	
	public static class ValueComparator<W extends Comparable<W>> implements Comparator<Map.Entry<?,W>> {

		public int compare(Entry<?, W> o1, Entry<?, W> o2) {
			int i = o2.getValue().compareTo(o1.getValue());
			return (i != 0) ? i : (System.identityHashCode(o1) - System.identityHashCode(o2)); 
		}
		
	}
}