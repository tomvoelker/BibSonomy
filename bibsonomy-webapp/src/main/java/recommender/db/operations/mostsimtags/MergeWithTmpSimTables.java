/*
 * Created on 11.04.2006
 */
package recommender.db.operations.mostsimtags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import recommender.db.backend.Database;
import recommender.db.backend.DatabaseAction;
import recommender.db.operations.tags.GetWaitingSims;
import recommender.db.operations.tagvector.AbstractGetVectorEntries;
import recommender.db.operations.tagvector.MarkModified;
import recommender.logic.BestList;
import recommender.model.HalfTagSimilarity;
import recommender.model.MostSimilarTagInfo;
import recommender.model.SimilarityCategory;
import recommender.model.TagSimilarity;

public class MergeWithTmpSimTables extends DatabaseAction<Object> {
	private static final Logger log = Logger.getLogger(MergeWithTmpSimTables.class);
	private final List<HalfTagSimilarity> distinctMostSimilarCandidates = new ArrayList<HalfTagSimilarity>(2*MostSimilarTagInfo.DEFAULT_MOSTSIMILARAMOUNT);
	private final Map<Integer,HalfTagSimilarity> simTagIndex = new HashMap<Integer,HalfTagSimilarity>();
	private final Map<Integer,Database.DatabaseOperation> updateOperations = new HashMap<Integer,Database.DatabaseOperation>();
	private final Collection<Database.DatabaseOperation> todoOperations = new ArrayList<Database.DatabaseOperation>();
	private final HashSet<Integer> newBestListSimTagIndex = new HashSet<Integer>();
	private final Collection<Integer> toRecalculateCompletely = new ArrayList<Integer>();
	
	@Override
	protected Object action() {
		final Map<Integer,Double> waitingSims;
		GetWaitingSims gws = new GetWaitingSims(SimilarityCategory.CONTENT, true);
		try {
			runDBOperation(gws);
			if (gws.hasNext() == true) {
				waitingSims = gws.next();
			} else {
				waitingSims = null;
			}
		} finally {
			gws.close();
		}
		GetMostSimTags gmstTmp = new GetMostSimTags(SimilarityCategory.CONTENT,true);
		try {
			runDBOperation(gmstTmp);
			if (log.isDebugEnabled() == true) {
				for (TagSimilarity tmpSim : gmstTmp) {
					log.debug(tmpSim.getRightTagID() + " -> " + tmpSim.getLeftTagID() + " " + tmpSim.getSimilarity());
				}
				gmstTmp = new GetMostSimTags(SimilarityCategory.CONTENT,true);
				runDBOperation(gmstTmp);
			}
			int lastTagId = -1;
			
			boolean lastRun = false;
			for (TagSimilarity tmpSim : gmstTmp) {
				if (log.isDebugEnabled() == true) {
					log.debug(tmpSim.getRightTagID() + " -> " + tmpSim.getLeftTagID() + " " + tmpSim.getSimilarity());
				}
				if (tmpSim.getRightTagID() != lastTagId) {
					if (distinctMostSimilarCandidates.isEmpty() == false) {
						evaluateAndSaveDiff(lastTagId, waitingSims);
					}
					log.info(tmpSim.getRightTagID());
					GetMostSimTags gmst = new GetMostSimTags(tmpSim.getRightTagID(), SimilarityCategory.CONTENT, false);
					try {
						runDBOperation(gmst);
						simTagIndex.clear();
						distinctMostSimilarCandidates.clear();
						updateOperations.clear();
						for (TagSimilarity sim : gmst) {
							distinctMostSimilarCandidates.add(sim);
							simTagIndex.put(sim.getLeftTagID(),sim);
						}
						if (log.isDebugEnabled() == true) {
							log.debug("already present: " + simTagIndex.keySet());
						}
					} finally {
						gmst.close();
					}
					lastTagId = tmpSim.getRightTagID();
				}
				HalfTagSimilarity oldSim = simTagIndex.get(tmpSim.getLeftTagID());
				if ((oldSim != null) && (oldSim.getSimilarity() != tmpSim.getSimilarity())) {
					log.info("updating " + distinctMostSimilarCandidates.set( distinctMostSimilarCandidates.indexOf(oldSim), tmpSim).getLeftTagID());
					updateOperations.put( tmpSim.getLeftTagID(), new UpdateMostSimTagsEntry( SimilarityCategory.CONTENT, tmpSim.getRightTagID(), tmpSim.getLeftTagID(), tmpSim.getSimilarity(), false));
				} else {
					distinctMostSimilarCandidates.add(tmpSim);
				}
			}
			// die letzte Liste nicht vergessen zu speichern:
			if (distinctMostSimilarCandidates.isEmpty() == false) {
				evaluateAndSaveDiff(lastTagId, waitingSims);
			}
		} finally {
			gmstTmp.close();
		}
		runDBOperation(new DeleteMostSimTags(SimilarityCategory.CONTENT, null, true));
		runDBOperation(new MarkModified(toRecalculateCompletely,AbstractGetVectorEntries.Category.CONTENT, true));
		
		return null;
	}

	private void evaluateAndSaveDiff(final int lastTagId, final Map<Integer,Double> waitingSims) {
		BestList<HalfTagSimilarity> newBest = new BestList<HalfTagSimilarity>(MostSimilarTagInfo.DEFAULT_MOSTSIMILARAMOUNT - 1, MostSimilarTagInfo.simComp);
		for (HalfTagSimilarity sim : distinctMostSimilarCandidates) {
			newBest.evaluate(sim);
		}
		int lowSimCounter = 0;
		double lowSimBorderline = waitingSims.get(lastTagId);
		todoOperations.clear();
		newBestListSimTagIndex.clear();
		if (log.isDebugEnabled() == true) {
			log.debug("new list: " + newBest.getBest());
		}
		for (HalfTagSimilarity sim : newBest.getBest()) {
			if (sim.getSimilarity() < lowSimBorderline) {
				++lowSimCounter;
			}
			Database.DatabaseOperation op = updateOperations.get(sim.getLeftTagID());
			if ((op == null) && (simTagIndex.containsKey(sim.getLeftTagID()) == false)) {
				log.info("inserting " + sim.getLeftTagID());
				op = new InsertMostSimTagsEntry(SimilarityCategory.CONTENT, lastTagId, sim);
			}
			if (op != null) {
				todoOperations.add(op);
			}
			newBestListSimTagIndex.add(sim.getLeftTagID());
		}
		if (lowSimCounter < 3) {
			for (Integer i : simTagIndex.keySet()) {
				if (newBestListSimTagIndex.contains(i) == false) {
					log.info("deleting " + i);
					runDBOperation( new DeleteSingleMostSimTagsEntry(SimilarityCategory.CONTENT,false,lastTagId,i));
				}
			}
			for (Database.DatabaseOperation op : todoOperations) {
				runDBOperation(op);
			}
		} else {
			toRecalculateCompletely.add(lastTagId);
		}
	}
}
