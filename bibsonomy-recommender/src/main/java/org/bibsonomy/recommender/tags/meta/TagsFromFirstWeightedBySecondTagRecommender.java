package org.bibsonomy.recommender.tags.meta;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.AbstractTagRecommender;
import org.bibsonomy.recommender.tags.popular.MostPopularByResourceTagRecommender;
import org.bibsonomy.recommender.tags.popular.MostPopularByUserTagRecommender;
import org.bibsonomy.recommender.tags.simple.SimpleContentBasedTagRecommender;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Takes the tags from {@link #firstTagRecommender} and orders them by their scores
 * from {@link #secondTagRecommender}. If they're not recommended by {@link #secondTagRecommender},
 * they get a score of 0. If {@link #firstTagRecommender} can't deliver enough tags, they're filled
 * up by the top tags from {@link #secondTagRecommender}.
 * 
 * @author rja
 * @version $Id$
 */
public class TagsFromFirstWeightedBySecondTagRecommender extends AbstractTagRecommender {
	private static final Logger log = Logger.getLogger(TagsFromFirstWeightedBySecondTagRecommender.class);

	private TagRecommender firstTagRecommender;
	private TagRecommender secondTagRecommender;

	/**
	 * Initializes the recommender with the given recommenders.
	 * 
	 * @param firstTagRecommender
	 * @param secondTagRecommender
	 */
	public TagsFromFirstWeightedBySecondTagRecommender(TagRecommender firstTagRecommender, TagRecommender secondTagRecommender) {
		super();
		this.firstTagRecommender = firstTagRecommender;
		this.secondTagRecommender = secondTagRecommender;
	}
	
	/**
	 * Initializes the tag recommenders with a {@link MostPopularByUserTagRecommender} 
	 * and {@link MostPopularByResourceTagRecommender} recommender, giving the first one
	 * a weight of 0.4 and the second one a weight of 0.6.
	 */
	public TagsFromFirstWeightedBySecondTagRecommender() {
		this.firstTagRecommender = new SimpleContentBasedTagRecommender();
		this.secondTagRecommender = new MostPopularByUserTagRecommender();
	}
	
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {

		if (firstTagRecommender == null || secondTagRecommender == null) {
			throw new IllegalArgumentException("No tag recommenders available.");
		}

		/*
		 * Get recommendation from first recommender.
		 */
		final SortedSet<RecommendedTag> firstRecommendedTags = firstTagRecommender.getRecommendedTags(post);
		log.debug("got " + firstRecommendedTags.size() + " recommendations from " + firstTagRecommender);
		if (log.isDebugEnabled()) {
			log.debug(firstRecommendedTags);
		}

		/*
		 * Get recommendation from second tag recommender.
		 * 
		 * Since we need to get the scores from this recommender for the tags from the first
		 * recommender, we use the TopTagsMapBackedSet, such that we can easily get tags by their name.
		 * Additionally, we might need to fill up the tags with the top tags (according to 
		 * the RecommendedTagComparator) from the second recommender. We get those from the 
		 * TopTagsMapBackedSet, too. 
		 */
		final TopTagsMapBackedSet secondRecommendedTags = new TopTagsMapBackedSet(numberOfTagsToRecommend);
		secondTagRecommender.addRecommendedTags(secondRecommendedTags, post);
		log.debug("got " + secondRecommendedTags.size() + " recommendations from " + secondTagRecommender);

		/*
		 * Iterate over tags from first recommender until we have enough tags
		 * Put only those into result, which occur in second recommendation.
		 */
		final Iterator<RecommendedTag> iterator = firstRecommendedTags.iterator();
		/*
		 * The scores from the tags in the next 'fill up round' should be lower 
		 * as the scores from this 'round'. Thus, we find the smallest value 
		 */
		double min = Double.MAX_VALUE;
		while (recommendedTags.size() < numberOfTagsToRecommend && iterator.hasNext()) {
			final RecommendedTag recommendedTag = iterator.next();
			if (!recommendedTags.contains(recommendedTag) && secondRecommendedTags.contains(recommendedTag)) {
				/*
				 * add tag
				 */
				final RecommendedTag secondRecommendedTag = secondRecommendedTags.get(recommendedTag);
				recommendedTags.add(secondRecommendedTag);
				/*
				 * find minimal score (for next round)
				 */
				final double score = secondRecommendedTag.getScore();
				if (score < min) min = score;
			} 
		}
		
		log.debug("used " + recommendedTags.size() + " tags from the first recommender");
		
		/*
		 * If we have not enough tags, yet, add tags from second until set is complete.
		 */
		if (recommendedTags.size() < numberOfTagsToRecommend) {
			/*
			 * we want to get the top tags, not ordered alphabetically!
			 */
			final SortedSet<RecommendedTag> topRecommendedTags = secondRecommendedTags.getTopTags();
			final Iterator<RecommendedTag> iterator2 = topRecommendedTags.iterator();
			int ctr = 0;
			while (recommendedTags.size() < numberOfTagsToRecommend && iterator2.hasNext()) {
				final RecommendedTag recommendedTag = iterator2.next();
				if (!recommendedTags.contains(recommendedTag)) {
					ctr++;
					/*
					 * re-compute score
					 */
					if (min > 0) {
						/*
						 * go closer to zero (and don't do 'min/1 = min', thus '/ctr + 1')
						 */
						recommendedTag.setScore(min / (ctr + 1));
					} else {
						/*
						 * go closer to -infinity
						 */
						recommendedTag.setScore(min - ctr);
					}
					/*
					 * FIXME: remember to request "almost all" tags from MostPopularByUser when configuring using Spring ...
					 */
					recommendedTags.add(recommendedTag);
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("final recommendation: " + recommendedTags);
		}

	}

	public String getInfo() {
		return "Using the tags from the second recommender to weight the recommended tags from the first recommender.";
	}

	/**
	 * @return The first tag recommender.
	 */
	public TagRecommender getFirstTagRecommender() {
		return this.firstTagRecommender;
	}


	/** This tag recommender's tags are ordered by their respective score
	 * from the second tag recommender. 
	 * 
	 * @param firstTagRecommender
	 */
	public void setFirstTagRecommender(TagRecommender firstTagRecommender) {
		this.firstTagRecommender = firstTagRecommender;
	}


	/**
	 * @return The second tag recommender.
	 */
	public TagRecommender getSecondTagRecommender() {
		return this.secondTagRecommender;
	}


	/**
	 * The scores of this recommender are used to weight the tags from the first
	 * tag recommender.
	 *  
	 * @param secondTagRecommender
	 */
	public void setSecondTagRecommender(TagRecommender secondTagRecommender) {
		this.secondTagRecommender = secondTagRecommender;
	}
}
