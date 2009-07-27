package org.bibsonomy.recommender.tags.multiplexer.modifiers;

import java.util.Collection;

import org.bibsonomy.model.RecommendedTag;

/**
 * Replaces invalid scoring values:
 *    *  NaN : Integer.MIN_VALUE
 *    * -inf : Integer.MIN_VALUE
 *    * +inf : Integer.MAX_VALUE
 * @author fei
 * @version $Id$
 */
public class InvalidScoringFilter implements RecommendedTagModifier {

	@Override
	public void alterTags(Collection<RecommendedTag> tags) {
		for( RecommendedTag tag : tags ) {
			double score      = tag.getScore();
			double confidence = tag.getConfidence();
			
			// filter score
			if( Double.isNaN(score) ) {
				tag.setScore(Integer.MIN_VALUE);
			} else if( score==Double.NEGATIVE_INFINITY ) {
				tag.setScore(Integer.MIN_VALUE);
			} else if( score==Double.POSITIVE_INFINITY ) {
				tag.setScore(Integer.MAX_VALUE);
			}

			// filter confidence
			if( Double.isNaN(confidence) ) {
				tag.setConfidence(Integer.MIN_VALUE);
			} else if( confidence==Double.NEGATIVE_INFINITY ) {
				tag.setConfidence(Integer.MIN_VALUE);
			} else if( confidence==Double.POSITIVE_INFINITY ) {
				tag.setConfidence(Integer.MAX_VALUE);
			}
			
		}
	}

}
