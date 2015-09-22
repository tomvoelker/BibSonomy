/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.common;

import java.util.Comparator;
import java.util.Set;

/**
 * Compares {@link Pair}s by the native order of their first values
 *
 * @author jensi
 * @param <A> first type in {@link Pair}
 * @param <B> second type in {@link Pair}
 */
public class FirstValuePairComparator<A extends Comparable<? super A>, B> implements Comparator<Pair<A, B>> {
	private final boolean allowEqual;
	
	/**
	 * @param allowEqual whether to return 0 (equality) if the native comparison returns 0. This may be dangerous for {@link Set}s
	 */
	public FirstValuePairComparator(boolean allowEqual) {
		this.allowEqual = allowEqual;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Pair<A, B> o1, Pair<A, B> o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			}
			return -1;
		}
		if (o2 == null) {
			return 1;
		}
		int firstValueCompare = safeCompare(o1.getFirst(), o2.getFirst());
		if (firstValueCompare != 0) {
			return firstValueCompare;
		}
		if (allowEqual) {
			return 0;
		}
		return System.identityHashCode(o1) - System.identityHashCode(o2);
	}

	/**
	 * @param first
	 * @param first2
	 * @return
	 */
	private static <T extends Comparable<? super T>> int safeCompare(T o1, T o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			}
			return -1;
		}
		if (o2 == null) {
			return 1;
		}
		return o1.compareTo(o2);
	}

}
