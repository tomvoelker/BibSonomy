/*
 * Created on 16.12.2005
 */
package recommender.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import recommender.db.operations.mostsimtags.SimpleRecalculateMostSimTags;
import recommender.db.operations.tagvector.AbstractGetVectorEntries;
import recommender.model.CombinableTagSimilarity;
import recommender.model.HalfTagSimilarity;
import recommender.model.MostSimilarTagInfo;
import recommender.model.SimilarityCategory;
import recommender.model.TagSimilarity;
import recommender.model.TagSimilarityListener;
import recommender.model.TagVector;

/**
 * Durchläuft nur einen Iterator der Vektoreinträge (User oder Content) mehrerer anderer Tags
 * und baut daraus die Listen der in der Kategorie jeweils Ähnlichsten
 *  
 * @author Jens Illig
 */
public class SimpleMostSimilarTagsCalculation implements Callable<List<MostSimilarTagInfo>> {
	private static final Log log = LogFactory.getLog(SimpleMostSimilarTagsCalculation.class);
	private static final Comparator<HalfTagSimilarity> iDComp = new HalfTagSimilarityIDComparator();
	private static final int categoryCount = AbstractGetVectorEntries.Category.values().length;
	
	private final MultiTagSimilarityCalculator mcalc;
	/** Liste der MostSimilarTagInfos unterschiedlicher Tags */
	private final Map<Integer,MostSimilarTagInfo> mstis;
	/** TagVector.Entry-Iterator der zu untersuchenden Kategorie */
	private final Iterator<TagVector.Entry> tveIterator;
	
	private final TagSimilarity[] similaritiesOfNextTag = new TagSimilarity[categoryCount];
	/** TagVector.Entry, das bereits zum nächsten tag gehört */
	private TagVector.Entry nextEntry;
	private final Collection<Integer> calculationTagIds = new ArrayList<Integer>(SimpleRecalculateMostSimTags.NUM_SIMULTANEOUS_CALCULATIONS_PER_THREAD);
	private final TagSimilarityListener listener;
	
	public SimpleMostSimilarTagsCalculation(final MultiTagSimilarityCalculator mcalc, Iterator<TagVector.Entry> contentTagVectorEntries, final TagSimilarityListener listener) {
		this.mcalc = mcalc;
		this.listener = listener;
		mstis = new HashMap<Integer,MostSimilarTagInfo>();
		tveIterator = contentTagVectorEntries;
		if (tveIterator.hasNext() == true) {
			nextEntry = tveIterator.next();
		}
		for (Iterator<Integer> tagIdIt = mcalc.tagIdIterator(); tagIdIt.hasNext(); ) {
			Integer id = tagIdIt.next();
			calculationTagIds.add(id);
			mstis.put(id, new MostSimilarTagInfo(id));
		}
		log.debug("beginning recalculation with " + calculationTagIds);
	}
	
	/**
	 *  Füttert mcalc mit TagVectorEntries des Tags mit curTagId aus dem Iterator
	 */
	private void feedMcalcWithNextTagsEntries() {
		boolean somethingDone;
		
		int curTagId = nextEntry.getTagId();
		do {
			mcalc.addToCalculation(nextEntry);
			if (tveIterator.hasNext() == true) {
				nextEntry = tveIterator.next();
			} else {
				nextEntry = null;
			}
		} while ((nextEntry != null) && (nextEntry.getTagId() == curTagId));
	}

	public List<MostSimilarTagInfo> call() {
		try {
			log.debug(getClass().getSimpleName() + " start");
			// durchläuft TagVectorEntry-Iteratoren und in innerer Schleife die zu untersuchenden tags (jedes hat einen tagsimilaritycalculator zum aktuellen tagid-eintrag aus den entry-iteratoren.
			while (nextEntry != null) {
				mcalc.resetForNextCalculations(nextEntry.getTagId());
				feedMcalcWithNextTagsEntries();
				for (Integer tagId : calculationTagIds) {
					CombinableTagSimilarity sim = mcalc.getSimilarity(tagId);
					if ((sim.getLeftTagID() != tagId) && (sim.getSimilarity() > 0)) {
						mstis.get(tagId).getContentBased().evaluate( sim );
						if (listener != null) {
							listener.accept(SimilarityCategory.CONTENT, sim);
						}
					}
				}
			}
			if (listener != null) {
				listener.allDone();
			}
			List<MostSimilarTagInfo> rVal = new ArrayList<MostSimilarTagInfo>(mstis.size());
			for (MostSimilarTagInfo msti : mstis.values()) {
				rVal.add(msti);
			}
			return rVal;
		} catch (Throwable e) {
			log.fatal(e,e);
			throw new RuntimeException(e);
		}
	}
}
