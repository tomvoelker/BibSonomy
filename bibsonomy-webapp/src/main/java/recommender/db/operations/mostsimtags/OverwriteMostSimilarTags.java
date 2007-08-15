/*
 * Created on 20.01.2006
 */
package recommender.db.operations.mostsimtags;

import java.util.SortedSet;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseAction;
import recommender.db.operations.tags.SetWaitingSim;
import recommender.logic.BestList;
import recommender.model.HalfTagSimilarity;
import recommender.model.MostSimilarTagInfo;
import recommender.model.SimilarityCategory;


/**
 * Speichert (und überschreibt) Tag-Tag-Ähnlichstenlisten aller Kategorien
 * aus einer MostSimilarTagInfo Struktur in der Datenbank ab.
 * 
 * @author Jens Illig
 */
public class OverwriteMostSimilarTags extends DatabaseAction<Object> {
	private static final Logger log = Logger.getLogger(OverwriteMostSimilarTags.class);
	private final boolean tmp;
	private final SimilarityCategory c;
	private final BestList<? extends HalfTagSimilarity> best;
	private final int tagId;
	
	public OverwriteMostSimilarTags(final SimilarityCategory c, final int tagId, final BestList<? extends HalfTagSimilarity> best, boolean tmp) {
		this.tmp = tmp;
		this.c = c;
		this.best = best;
		this.tagId = tagId;
	}
	
	@Override
	protected Object action() {
		runDBOperation(new DeleteMostSimTags( c, tagId, tmp));
		if (best != null) {
			SortedSet<? extends HalfTagSimilarity> bestSet = best.getBest();
			if (bestSet.isEmpty() == false) {
				//if (tmp == true) {
					HalfTagSimilarity worst = best.getWorst();
					if (bestSet.size() == MostSimilarTagInfo.DEFAULT_MOSTSIMILARAMOUNT) {
						bestSet.remove(worst);
					}
					runDBOperation(new SetWaitingSim(c,tagId,worst.getSimilarity()));
				//}
				runDBOperation(new InsertMultipleMostSimTagsEntries(c,tagId,bestSet,tmp));
			}
		}
		
		return null;
	}

}
