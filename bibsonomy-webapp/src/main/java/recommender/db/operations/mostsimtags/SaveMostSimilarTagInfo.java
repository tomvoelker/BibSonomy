/*
 * Created on 20.01.2006
 */
package recommender.db.operations.mostsimtags;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseAction;
import recommender.model.MostSimilarTagInfo;
import recommender.model.SimilarityCategory;


/**
 * Speichert (und überschreibt) Tag-Tag-Ähnlichstenlisten aller Kategorien
 * aus einer MostSimilarTagInfo Struktur in der Datenbank ab.
 * 
 * @author Jens Illig
 */
public class SaveMostSimilarTagInfo extends DatabaseAction<Object> {
	private static final Logger log = Logger.getLogger(SaveMostSimilarTagInfo.class);
	private final MostSimilarTagInfo msti;
	private final boolean tmp;
	
	public SaveMostSimilarTagInfo(final MostSimilarTagInfo msti, boolean tmp) {
		this.msti = msti;
		this.tmp = tmp;
	}
	
	@Override
	protected Object action() {
		final int tagId = msti.getTagID();
		final SimilarityCategory c = SimilarityCategory.CONTENT;// TODO: Schleife, wenn mehr als nur CONTENT-Ähnlichkeit berechnet wird: for (SimilarityCategory c : SimilarityCategory.values()) {
			runDBOperation(new OverwriteMostSimilarTags(c, tagId, msti.getBestList(c), tmp));
		//}

		return null;
	}

}
