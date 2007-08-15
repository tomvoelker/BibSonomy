/*
 * Created on 10.04.2006
 */
package recommender.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import recommender.db.backend.Database;
import recommender.db.backend.DatabaseAction;
import recommender.db.operations.mostsimtags.GetMostSimTags;
import recommender.db.operations.mostsimtags.SaveMostSimilarTagInfo;
import recommender.model.MostSimilarTagInfo;
import recommender.model.SimilarityCategory;
import recommender.model.TagSimilarity;


public class MultiThreadMostSimilarTagInfoStore {
	private static final Logger log = Logger.getLogger(MultiThreadMostSimilarTagInfoStore.class);
	/** Ähnlichstenlisten von tags, die gerade im moment nicht komplett neuberechnet, aber mit einzelnen Ähnlichkeiten updated werden */
	private final Map<Integer,MostSimilarTagInfo> updatingLists = new HashMap<Integer,MostSimilarTagInfo>();
	private final Map<Integer,Integer> awaitedBestLists = new HashMap<Integer,Integer>();
	/** tagIds von tags, deren Ähnlichstenlisten gerade komplett neu berechnet werden, es schon wurden oder noch werden. */
	private final Set<Integer> tagsToRecalculate;
	
	private static MultiThreadMostSimilarTagInfoStore instance = null;
	
	private MultiThreadMostSimilarTagInfoStore(final HashSet<Integer> tagsToRecalculate) {
		this.tagsToRecalculate = tagsToRecalculate;
	}
	
	public static void buildInstance(final HashSet<Integer> tagsToRecalculate) {
		instance = new MultiThreadMostSimilarTagInfoStore(tagsToRecalculate);
	}
	
	public static MultiThreadMostSimilarTagInfoStore getInstance() {
		if (instance == null) {
			log.error("buildInstance must be called first");
			throw new IllegalStateException("buildInstance must be called first");
		}
		return instance;
	}
	
	public MostSimilarTagInfo getBestList(Integer tagId) throws InterruptedException {
		MostSimilarTagInfo rVal;
		if (tagsToRecalculate.contains(tagId) == true) {
			if (log.isDebugEnabled() == true) {
				log.debug(tagId + " is to be completely recalculated => no need to update partially");
			}
			return null;
		}
		if (log.isDebugEnabled() == true) {
			log.debug(tagId + " is loaded");
		}
		synchronized (updatingLists) {
			rVal = updatingLists.get(tagId);
			if (rVal == null) {
				rVal = load(tagId);
				updatingLists.put(tagId,rVal);
			}
			synchronized (rVal) {
				Integer awaitingCounter = awaitedBestLists.get(tagId);
				if (awaitingCounter == null) {
					awaitingCounter = 1;
				} else {
					awaitingCounter++;
				}
				awaitedBestLists.put(tagId,awaitingCounter);
			}
		}
		synchronized (rVal) {
			Integer awaitingCounter = awaitedBestLists.get(tagId);
			if (awaitingCounter > 1) {
				rVal.wait();
			}
		}
		return rVal;
	}
	
	public void saveBestList(MostSimilarTagInfo msti) {
		if (log.isDebugEnabled() == true) {
			log.debug(msti.getTagID() + " is saved");
		}
		synchronized (updatingLists) {
			synchronized (msti) {
				Integer tagId = msti.getTagID();
				Integer awaitingCounter = awaitedBestLists.get(tagId);
				awaitingCounter = awaitedBestLists.get(tagId);
				if (awaitingCounter != null) {
					awaitingCounter--;
					if (awaitingCounter < 1) {
						awaitedBestLists.remove(tagId);
						log.debug("not awaited => saving now");
						save(msti);
						updatingLists.remove(msti);
					} else {
						awaitedBestLists.put(tagId,awaitingCounter);
						log.debug("already awaited => saving later");
						msti.notify();
					}
				} else {
					log.fatal("should never happen: awaitingcounter == null");
				}
			}
		}
		
	}
	
	private static void save(final MostSimilarTagInfo msti) {
		new Database(true, new SaveMostSimilarTagInfo( msti, true));
	}
	
	private static MostSimilarTagInfo load(final int tagId) {
		DatabaseAction<MostSimilarTagInfo> getMstiAction = new DatabaseAction<MostSimilarTagInfo>() {

			@Override
			protected MostSimilarTagInfo action() {
				MostSimilarTagInfo rVal = new MostSimilarTagInfo(tagId);
				GetMostSimTags getSims = new GetMostSimTags( tagId, SimilarityCategory.CONTENT, true);
				try {
					runDBOperation(getSims);
					for (TagSimilarity sim : getSims) {
						rVal.getBestList(SimilarityCategory.CONTENT).evaluate(sim);
					}
				} finally {
					getSims.close();
				}
				return rVal;
			}
			
		};
		new Database(false, getMstiAction);
		return getMstiAction.getReturnValue();
	}
	
	public void addTagIdToRecalculate(final Integer tagId) {
		tagsToRecalculate.add(tagId);
	}
}
