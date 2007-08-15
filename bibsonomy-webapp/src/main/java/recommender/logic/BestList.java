/*
 * Created on 17.02.2006
 */
package recommender.logic;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class BestList<T> {
	private Comparator<T> comp;
	private int listLength;
	private final TreeSet<T> best;
	
	public BestList(int listLength, Comparator<T> comp) {
		this.listLength = listLength;
		this.comp = comp;
		this.best = new TreeSet<T>(comp);
	}
	
	/**
	 * ggf. in Bestenliste einsortieren
	 * @param obj Das evtl. einzufügende Element
	 */
	public boolean evaluate(T obj) {
		if (best.size() < listLength) {
			best.add(obj);
			return true;
		} else {
			T worst = getWorst();
			if (comp.compare(worst,obj) < 0) {
				best.remove(worst);
				best.add(obj);
				return true;
			}
		}
		return false;
	}
	
	public SortedSet<T> getBest() {
		return best;
	}
	
	public void merge(BestList<T> list) {
		// schade, dass TreeSets nicht ohne weiteres rückwärts durchiteriert werden können
		for (T t : list.getBest()) {
			evaluate(t);
		}
	}
	
	public T getWorst() {
		return best.first();
	}
	
	public T removeWorst() {
		T worst = getWorst();
		best.remove(worst);
		return worst;
	}
}
