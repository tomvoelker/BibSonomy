/*
 * Created on 11.04.2006
 */
package recommender.logic;

import org.apache.log4j.Logger;

import recommender.model.CombinableTagSimilarity;
import recommender.model.MostSimilarTagInfo;
import recommender.model.SimilarityCategory;
import recommender.model.SimpleTagSimilarity;
import recommender.model.TagSimilarity;
import recommender.model.TagSimilarityListener;

public class NonRecalculationMostSimilarTagInfoUpdater implements TagSimilarityListener {
	private static final Logger log = Logger.getLogger(NonRecalculationMostSimilarTagInfoUpdater.class);
	private int lastTagId = -1;
	private MostSimilarTagInfo msti;
	
	public void accept(SimilarityCategory c, CombinableTagSimilarity sim) {
		if (sim.getLeftTagID() != lastTagId) {
			saveMsti();
			try {
				msti = MultiThreadMostSimilarTagInfoStore.getInstance().getBestList(sim.getLeftTagID());
			} catch (InterruptedException e) {
				log.fatal("Interruption");
				msti = null;
				return;
			}
			lastTagId = sim.getLeftTagID();
		}
		if (msti != null) {
			msti.getBestList(c).evaluate(buildReverseSimilarity(sim));
		}
	}
	
	public void allDone() {
		saveMsti();
	}

	private void saveMsti() {
		if (msti != null) {
			MultiThreadMostSimilarTagInfoStore.getInstance().saveBestList(msti);
		}
	}
	
	public static TagSimilarity buildReverseSimilarity(final TagSimilarity sim) {
		return new SimpleTagSimilarity(sim.getRightTagID(), sim.getLeftTagID(), sim.getSimilarity());
	}
}
