/**
 * BibSonomy - A blue social bookmark and publication sharing system.
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
package org.bibsonomy.entity.matcher;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.matchers.AbstractMatchListener;

/**
 * stores user matches to a map
 * 
 * @author fei
 */
public class UserEntityMatcher extends AbstractMatchListener {
	
	private class MatchingComparator implements Comparator<UserMatch> {

		@Override
		public int compare(final UserMatch o1, final UserMatch o2) {
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}
			/*
			 * names equal: regard them as equal
			 */
			if (o1.equals(o2)) {
				return 0;
			}
			/*
			 * the highest score should come first (in the set) - hence, 
			 * do o2 - o1 
			 */
			final int signum = new Double(Math.signum(o2.getConfidence() - o1.getConfidence())).intValue();
			if (signum != 0) {
				return signum;
			}
			/*
			 * scores and confidence equal (but tag names not): return using compareTo from Tag.
			 */
			return o1.compareTo(o2);
		}
		
	}
	
	/** map external user names to BibSonomy user names */
	private final Map<String, SortedSet<UserMatch>> backend = new HashMap<String, SortedSet<UserMatch>>(); 

	@Override
	public void matches(final Record r1, final Record r2, final double confidence) {
		// r2...BibSonomy user r1...External user
		final String bibID = r2.getValue("user_realname");
		final String extID = r1.getValue("user_realname");
		
		// get matching
		SortedSet<UserMatch> matching;
		if (!this.getMatching().containsKey(extID)) {
			matching = new TreeSet<UserMatch>(new MatchingComparator());
			this.getMatching().put(extID, matching);
		} else {
			matching = this.getMatching().get(extID);
		}
		
		// store back matching
		matching.add(new UserMatch(bibID, r2, confidence));
	}

	/**
	 * @return the matching
	 */
	public Map<String, SortedSet<UserMatch>> getMatching() {
		return this.backend;
	}

}
