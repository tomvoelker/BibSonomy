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
	/**
	 * store a single user matching
	 */
	public class UserMatch implements Comparable<UserMatch> {
		private String id;
		private String name;
		private double confidence;
		
		public UserMatch(String id, String name, double confidence) {
			this.id = id;
			this.setName(name);
			this.confidence = confidence;
		}

		@Override
		public boolean equals(Object other) {
			if (this.id==null || other==null) {
				return false;
			}
			return this.getId().equals(((UserMatch) other).getId());
		}
		
		@Override
		public int compareTo(UserMatch o) {
			if (this.id == null) return -1;
			if (o == null || o.id == null ) return 1;

			return this.id.compareToIgnoreCase(o.id);
		}
		
		public void setId(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}
		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}
		public double getConfidence() {
			return confidence;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}
	
	public class MatchingComparator implements Comparator<UserMatch> {

		@Override
		public int compare(UserMatch o1, UserMatch o2) {
			if (o1 == null) return -1;
			if (o2 == null) return 1;
			/*
			 * names equal: regard them as equal
			 */
			if (o1.equals(o2)) return 0;
			/*
			 * the highest score should come first (in the set) - hence, 
			 * do o2 - o1 
			 */
			int signum = new Double(Math.signum(o2.getConfidence() - o1.getConfidence())).intValue();
			if (signum != 0) return signum;
			/*
			 * scores and confidence equal (but tag names not): return using compareTo from Tag.
			 */
			return o1.compareTo(o2);
		}
		
	}
	
	/** map external user names to BibSonomy user names */
	private Map<String, SortedSet<UserMatch>> backend = new HashMap<String, SortedSet<UserMatch>>(); 

	@Override
	public void matches(Record r1, Record r2, double confidence) {
		// r2...BibSonomy user r1...External user
		String bibID = r2.getValue("user_realname");
		String extID = r1.getValue("user_realname");
		
		// get matching
		SortedSet<UserMatch> matching;
		if (!getMatching().containsKey(extID)) {
			matching = new TreeSet<UserMatch>(new MatchingComparator());
			getMatching().put(extID, matching);
		} else {
			matching = getMatching().get(extID);
		}
		
		// store back matching
		matching.add(new UserMatch(bibID, r2.getValue("user_name"), confidence));
	}

	public void setMatching(Map<String, SortedSet<UserMatch>> backend) {
		this.backend = backend;
	}

	public Map<String, SortedSet<UserMatch>> getMatching() {
		return backend;
	}

}
