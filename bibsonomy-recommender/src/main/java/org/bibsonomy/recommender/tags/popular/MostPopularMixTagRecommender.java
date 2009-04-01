package org.bibsonomy.recommender.tags.popular;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Returns the most popular \rho-mix of the injected recommenders as tag recommendation for the post.
 * 
 *   <p>
 *   The {@link #tagRecommenders} array shall include all the tagrecommenders which should 
 *   be queried. Each recommender's tag scores and confidences are weighted by the corresponding 
 *   value in {@link #weights}.
 *   </p>
 *   
 *   <p>The weights should sum up to 1. If no weights are given, the score (confidence) of each tag 
 *   is multiplied by 1 and added.
 *   
 * 
 * @author rja
 * @version $Id$
 */
public class MostPopularMixTagRecommender implements TagRecommender {
	private static final Logger log = Logger.getLogger(MostPopularMixTagRecommender.class);

	private static final int DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND = 5;

	private int numberOfTagsToRecommend;
	private TagRecommender[] tagRecommenders;
	private double[] weights;

	/**
	 * Initializes the tag recommenders with a {@link MostPopularByUserTagRecommender} 
	 * and {@link MostPopularByResourceTagRecommender} recommender, giving the first one
	 * a weight of 0.4 and the second one a weight of 0.6.
	 */
	public MostPopularMixTagRecommender() {
		this.tagRecommenders = new TagRecommender[] {
				new MostPopularByUserTagRecommender(),
				new MostPopularByResourceTagRecommender()
		};
		this.weights = new double[] {
				0.4,
				0.6
		};
		this.numberOfTagsToRecommend = DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND;
	}


	public void addRecommendedTags(final SortedSet<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {
		recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Most Popular Tags Mix Recommender";
	}

	/**
	 * Returns the resource's overall most popular tags
	 * 
	 * @see org.bibsonomy.services.recommender.TagRecommender#getRecommendedTags(org.bibsonomy.model.Post)
	 */
	public SortedSet<RecommendedTag> getRecommendedTags(final Post<? extends Resource> post) {

		log.debug("Getting tag recommendations for " + post);

		if (tagRecommenders == null) {
			throw new IllegalArgumentException("No tag recommenders available.");
		}

		final Map<String, RecommendedTag> resultMap = new HashMap<String, RecommendedTag>();

		/*
		 * iterate over all given recommenders
		 */
		for (int i = 0; i < tagRecommenders.length; i++) {
			final TagRecommender recommender = tagRecommenders[i];
			final double scoreWeight = (weights != null || weights.length == tagRecommenders.length) ? weights[i] : 1;

			final SortedSet<RecommendedTag> recommendedTags = recommender.getRecommendedTags(post);
			/*
			 * iterate over all tags and add them to result
			 */
			for (final RecommendedTag recommendedTag: recommendedTags) {
				addTag(resultMap, recommendedTag, scoreWeight);
			}

		}

		/*
		 * copy result map into sorted set
		 */
		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		for (final RecommendedTag recommendedTag: resultMap.values()) {
			if (result.size() < numberOfTagsToRecommend) {
				result.add(recommendedTag);	
			} else if (result.last().compareTo(recommendedTag) < 0) {
				/*
				 * new tag is better
				 */
				result.remove(result.last());
				result.add(recommendedTag);
			}
		}
		return result;
	}


	/**
	 * Adds a tag to the result.
	 * 
	 * If the tag is not already contained: multiplies the score and confidence 
	 * of the tag with the weigth and adds the tag to the result.
	 * 
	 * Otherwise, multiplies the score and confidence with the weight and adds 
	 * the result to the score/confidence of the existing tag.
	 * 
	 * @param result - the map into which the recommendedTag should be put 
	 * @param recommendedTag
	 * @param weight the weight the score/confidence of the tag should be weighted with.
	 */
	private void addTag(final Map<String, RecommendedTag> result, final RecommendedTag recommendedTag, final double weight) {
		final double score = recommendedTag.getScore() * weight;
		final double confidence = recommendedTag.getConfidence() * weight;
		final String tagName = recommendedTag.getName();

		if (result.containsKey(tagName)) {
			/*
			 * add score and confidence
			 */
			final RecommendedTag recommendedTag2 = result.get(tagName);
			recommendedTag2.setScore(recommendedTag2.getScore() + score);
			recommendedTag2.setConfidence(recommendedTag2.getConfidence() + confidence);
		} else {
			/*
			 * create new tag with weighted score and confidence
			 */
			result.put(tagName, new RecommendedTag(tagName, score, confidence));
		}
	}



	/**
	 * @return The (maximal) number of tags this recommender shall return.
	 */
	public int getNumberOfTagsToRecommend() {
		return this.numberOfTagsToRecommend;
	}

	/** Set the (maximal) number of tags this recommender shall return. The default is {@value #DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND}.
	 * 
	 * @param numberOfTagsToRecommend
	 */
	public void setNumberOfTagsToRecommend(int numberOfTagsToRecommend) {
		this.numberOfTagsToRecommend = numberOfTagsToRecommend;
	}

	/**
	 * @return The weights used to weight the score/confidence of each recommended tag of each recommender.
	 */
	public double[] getWeights() {
		return this.weights;
	}

	/** The score/confidence of each tag from the tag recommenders in {@link #tagRecommenders} is
	 * weighted by the corresponding weight in {@link #weights}. 
	 *  
	 * @param weights
	 */
	public void setWeights(double[] weights) {
		this.weights = weights;
	}

	/**
	 * @return The tag recommenders used by this tag recommender.
	 */
	public TagRecommender[] getTagRecommenders() {
		return this.tagRecommenders;
	}

	/**
	 * Give this recommender an array of tag recommenders it will query.
	 * 
	 * @param tagRecommenders
	 */
	public void setTagRecommenders(TagRecommender[] tagRecommenders) {
		this.tagRecommenders = tagRecommenders;
	}
}
