package org.bibsonomy.recommender.tags.meta;

import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;

/**
 * Works like {@link MapBackedSet} but additionally keeps a set of the top 
 * number of tags (according to {@link RecommendedTagComparator}). 
 * Thus, doesn't support the {@link #remove(Object)} method, since we could
 * not 'refill' the top tags after removal of one. Uses the tags name to compare them.
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class TopTagsMapBackedSet extends MapBackedSet<String, RecommendedTag> {

	private final SortedSet<RecommendedTag> sortedTags;
	private final int numberOfTags;
	private final RecommendedTagComparator comp;
	
	/**
	 * @param numberOfTags - maximal number of top tags to keep.
	 */
	public TopTagsMapBackedSet(final int numberOfTags) {
		super(new MapBackedSet.KeyExtractor<String, RecommendedTag>() {
			@Override
			public String getKey(RecommendedTag value) {
				return value.getName();
			}
		});
		this.sortedTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		this.numberOfTags = numberOfTags;
		this.comp = new RecommendedTagComparator();
	}
	
	@Override
	public boolean add(RecommendedTag e) {
		addToSortedSet(e);
		return super.add(e);
	}

	/**
	 * Adds the tag to the sorted set, if the sorted set is smaller than 
	 * {@link #numberOfTags} or if the tag is larger (according to
	 * {@link RecommendedTagComparator} than the last tag (which is then
	 * removed).
	 * 
	 * @param e
	 */
	private void addToSortedSet(final RecommendedTag e) {
		if (sortedTags.size() < numberOfTags) {
			sortedTags.add(e);
		} else if (this.comp.compare(e, sortedTags.last()) < 0) {
			/*
			 * new tag is better than last -> replace it!
			 */
			sortedTags.remove(sortedTags.last());
			sortedTags.add(e);
		}
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("remove() is not supported by this set.");
	}

	/**
	 * @return The top tags sorted by {@link RecommendedTagComparator}. 
	 */
	public SortedSet<RecommendedTag> getTopTags() {
		return this.sortedTags;
	}
	
}
