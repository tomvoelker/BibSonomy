package org.bibsonomy.recommender.tags.meta;

import java.util.Collection;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Takes the tags from {@link #firstTagRecommender} and orders them by their scores
 * from {@link #secondTagRecommender}. If they're not recommended by {@link #secondTagRecommender},
 * they get a score of 0. If {@link #firstTagRecommender} can't deliver enough tags, they're filled
 * up by the top tags from {@link #thirdTagRecommender}.
 * 
 * @author rja
 * @version $Id$
 */
public class TagsFromFirstWeightedBySecondFilledByThirdTagRecommender extends TagsFromFirstWeightedBySecondTagRecommender {
	private static final Log log = LogFactory.getLog(TagsFromFirstWeightedBySecondFilledByThirdTagRecommender.class);

	private TagRecommender thirdTagRecommender;

	/**
	 * Initializes the recommender with the given recommenders.
	 * 
	 * @param firstTagRecommender - delivers the main tags, which are scored by the secondTagRecommender
	 * @param secondTagRecommender - used to score the tags from the first tag recommender
	 * @param thirdTagRecommender - if the first tag recommender does not provide enough tags, this recommender can fill them up
	 */
	public TagsFromFirstWeightedBySecondFilledByThirdTagRecommender(TagRecommender firstTagRecommender, TagRecommender secondTagRecommender, TagRecommender thirdTagRecommender) {
		super(firstTagRecommender, secondTagRecommender);
		this.thirdTagRecommender = thirdTagRecommender;
	}

	/**
	 * Don't initializes any recommenders - you have to call the setters! 
	 */
	public TagsFromFirstWeightedBySecondFilledByThirdTagRecommender() {
		super(); 
	}

	@Override
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {

		if (firstTagRecommender == null || secondTagRecommender == null || thirdTagRecommender == null) {
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
		 * recommender, we use the MapBackedSet, such that we can easily get tags by their name.
		 */
		final MapBackedSet<String, RecommendedTag> secondRecommendedTags = new MapBackedSet<String, RecommendedTag>(new TopTagsMapBackedSet.DefaultKeyExtractor());
		secondTagRecommender.addRecommendedTags(secondRecommendedTags, post);
		log.debug("got " + secondRecommendedTags.size() + " recommendations from " + secondTagRecommender);



		final double minScore = doFirstRound(recommendedTags, firstRecommendedTags, secondRecommendedTags); 
		log.debug("used " + recommendedTags.size() + " tags from the first recommender which occured in second recommender");



		final int ctr = doSecondRound(recommendedTags, firstRecommendedTags, secondRecommendedTags, minScore); 
		log.debug("used another " + ctr + " tags from the first recommender ");


		final SortedSet<RecommendedTag> thirdRecommendedTags = thirdTagRecommender.getRecommendedTags(post);
		log.debug("got " + thirdRecommendedTags.size() + " recommendations from " + thirdTagRecommender);
		doThirdRound(recommendedTags, thirdRecommendedTags, minScore, ctr);


		if (log.isDebugEnabled()) {
			log.debug("final recommendation: " + recommendedTags);
		}

	}

	@Override
	public String getInfo() {
		return "Using the tags from the second recommender to weight the tags from the first recommender; if necessary, fill up with tags from third recommender.";
	}
	
	/**
	 * @return The third tag recommender.
	 */
	public TagRecommender getThirdTagRecommender() {
		return this.thirdTagRecommender;
	}

	/**
	 * This recommender is used to fill up the recommendations, if the first 
	 * tag recommender can't provide enough tags.
	 * @param thirdTagRecommender
	 */
	public void setThirdTagRecommender(TagRecommender thirdTagRecommender) {
		this.thirdTagRecommender = thirdTagRecommender;
	}
}
