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
 * up by the top tags from {@link #thirdTagRecommender}.
 * 
 * @author rja
 * @version $Id$
 */
public class TagsFromFirstWeightedBySecondFilledByThirdTagRecommender extends AbstractTagRecommender {
	private static final Logger log = Logger.getLogger(TagsFromFirstWeightedBySecondFilledByThirdTagRecommender.class);

	private TagRecommender firstTagRecommender;
	private TagRecommender secondTagRecommender;
	private TagRecommender thirdTagRecommender;

	/**
	 * Initializes the recommender with the given recommenders.
	 * 
	 * @param firstTagRecommender - delivers the main tags, which are scored by the secondTagRecommender
	 * @param secondTagRecommender - used to score the tags from the first tag recommender
	 * @param thirdTagRecommender - if the first tag recommender does not provide enough tags, this recommender can fill them up
	 */
	public TagsFromFirstWeightedBySecondFilledByThirdTagRecommender(TagRecommender firstTagRecommender, TagRecommender secondTagRecommender, TagRecommender thirdTagRecommender) {
		super();
		this.firstTagRecommender = firstTagRecommender;
		this.secondTagRecommender = secondTagRecommender;
		this.thirdTagRecommender = thirdTagRecommender;
	}

	/**
	 * Initializes the tag recommenders with a {@link MostPopularByUserTagRecommender} 
	 * and {@link MostPopularByResourceTagRecommender} recommender, giving the first one
	 * a weight of 0.4 and the second one a weight of 0.6.
	 */
	public TagsFromFirstWeightedBySecondFilledByThirdTagRecommender() {
		this.firstTagRecommender = new SimpleContentBasedTagRecommender();
		this.secondTagRecommender = new MostPopularByUserTagRecommender();
	}

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



		/* 
		 * First round:
		 * Iterate over tags from first recommender and check them against second recommender.
		 * Add only those tags, which are contained in the second recommender
		 */
		final Iterator<RecommendedTag> iterator1 = firstRecommendedTags.iterator();
		/*
		 * We need to find the minimum to add the remaining tags with lower scores
		 */
		double minScore = Double.MAX_VALUE;
		while (recommendedTags.size() < numberOfTagsToRecommend && iterator1.hasNext()) {
			final RecommendedTag recommendedTag = iterator1.next();
			if (secondRecommendedTags.contains(recommendedTag)) {
				/*
				 * this tag is also recommended by the second recommender: give it his score
				 */

				final RecommendedTag secondRecommendedTag = secondRecommendedTags.get(recommendedTag);
				recommendedTags.add(secondRecommendedTag);
				/*
				 * remember minimal score
				 */
				final double score = secondRecommendedTag.getScore();
				if (score < minScore) minScore = score;
				/*
				 * remove tag, such that don't use it again in the second round
				 */
				iterator1.remove();
			}
		}
		log.debug("used " + recommendedTags.size() + " tags from the first recommender which occured in second recommender");



		/*
		 * Second round:
		 * add remaining tags from first recommender, scored lower than the tags before
		 */
		final Iterator<RecommendedTag> iterator2 = firstRecommendedTags.iterator();
		int ctr = 0;
		while (recommendedTags.size() < numberOfTagsToRecommend && iterator2.hasNext()) {
			final RecommendedTag recommendedTag = iterator2.next();
			ctr++;
			recommendedTag.setScore(getLowerScore(minScore, ctr));
			recommendedTags.add(recommendedTag);
		}
		log.debug("used another " + ctr + " tags from the first recommender ");



		/*
		 * Third round:
		 * If we have not enough tags, yet, add tags from third recommender until set is complete.
		 */
		if (recommendedTags.size() < numberOfTagsToRecommend) {
			final SortedSet<RecommendedTag> thirdRecommendedTags = thirdTagRecommender.getRecommendedTags(post);
			log.debug("got " + thirdRecommendedTags.size() + " recommendations from " + thirdTagRecommender);

			final Iterator<RecommendedTag> iterator3 = thirdRecommendedTags.iterator();
			while (recommendedTags.size() < numberOfTagsToRecommend && iterator3.hasNext()) {
				final RecommendedTag recommendedTag = iterator3.next();
				if (!recommendedTags.contains(recommendedTag)) {
					/*
					 * tag has not already been added -> set its score lower than min
					 */
					ctr++;
					recommendedTag.setScore(getLowerScore(minScore, ctr));
					recommendedTags.add(recommendedTag);
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("final recommendation: " + recommendedTags);
		}

	}

	/**
	 * Goal of this method: "append" not so good tags on already recommended ("good") tags 
	 * by ensuring that their score is lower than the "good" tags.
	 * 
	 * Depending on the sign of the min score of the already recommended tags, we apply
	 * a strategy to use the ctr as score. 
	 * 
	 * @param minScore
	 * @param ctr
	 * @return
	 */
	private double getLowerScore(double minScore, int ctr) {
		final double newScore;
		if (minScore > 0) {
			/*
			 * go closer to zero (and don't do 'min/1 = min', thus '/ctr + 1')
			 */
			newScore = minScore / (ctr + 1);
		} else {
			/*
			 * go closer to -infinity
			 */
			newScore = minScore - ctr;
		}
		return newScore;
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
