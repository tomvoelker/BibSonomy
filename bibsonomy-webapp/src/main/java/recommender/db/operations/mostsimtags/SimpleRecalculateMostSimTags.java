/*
 * Created on 20.01.2006
 */
package recommender.db.operations.mostsimtags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import recommender.db.backend.Database;
import recommender.db.backend.DatabaseAction;
import recommender.db.operations.tagvector.AbstractGetVectorEntries;
import recommender.db.operations.tagvector.DeleteModifiedTagsInUse;
import recommender.db.operations.tagvector.GetModifiedVectorEntries;
import recommender.db.operations.tagvector.GetVectorEntries;
import recommender.db.operations.tagvector.MarkModified;
import recommender.db.operations.tagvector.SetModifiedTagsInUse;
import static recommender.db.operations.tagvector.AbstractGetVectorEntries.Category.CONTENT;
import recommender.logic.MultiTagSimilarityCalculator;
import recommender.logic.MultiThreadIterableAdapter;
import recommender.logic.MultiThreadMostSimilarTagInfoStore;
import recommender.logic.NonRecalculationMostSimilarTagInfoUpdater;
import recommender.logic.SimpleMostSimilarTagsCalculation;
import recommender.model.MostSimilarTagInfo;
import recommender.model.TagVector;


/**
 * Berechnet die ÄhnlichstenListen aller Tags komplett neu.
 * 
 * @author Jens Illig
 */
public class SimpleRecalculateMostSimTags extends DatabaseAction<Object> {
	private static final Logger log = Logger.getLogger(SimpleRecalculateMostSimTags.class);
	private static final int NUM_THREADS = 2;
	public static final int NUM_SIMULTANEOUS_CALCULATIONS_PER_THREAD = 2000; // TODO: hochsetzen wenn viel RAM verfügbar
	private final ExecutorService[] execs = new ExecutorService[2]; // zwei ExecutorServices mit jeweils NUM_THREADS (für gleichzeitiges fetchen und berechnen der nächsten Tags wenn die bisherigen noch am Rechnen oder abspeichern sind
	private final ExecutorService saveResultExec = Executors.newSingleThreadExecutor();
	private Future<Object> saveProcess = null;
	private final boolean doCompleteRecalculation;
	
	public SimpleRecalculateMostSimTags(boolean doCompleteRecalculation) {
		this.doCompleteRecalculation = doCompleteRecalculation;
	}
	
	@Override
	protected Object action() {
		execs[0] = Executors.newFixedThreadPool(NUM_THREADS);
		execs[1] = Executors.newFixedThreadPool(NUM_THREADS);
		
		final int tvisPerIteration = NUM_THREADS * NUM_SIMULTANEOUS_CALCULATIONS_PER_THREAD;

		AbstractGetVectorEntries getAllContentVectorEntries4Global;
		if (doCompleteRecalculation == true) {
			getAllContentVectorEntries4Global = new GetVectorEntries(CONTENT,false);
		} else {
			DatabaseAction<GetModifiedVectorEntries> transaction = new DatabaseAction<GetModifiedVectorEntries>() {

				@Override
				protected GetModifiedVectorEntries action() {
					MarkModified.GetModifiedTagIds gmtids = new MarkModified.GetModifiedTagIds(true,false);
					try {
						runDBOperation(gmtids);
						if (gmtids.hasNext() == true) {
							log.error("tagsimilarity-update already running or died uncleanly.");
							return null;
						}
					} finally {
						gmtids.close();
					}
					runDBOperation(new SetModifiedTagsInUse(AbstractGetVectorEntries.Category.CONTENT));
					return new GetModifiedVectorEntries(AbstractGetVectorEntries.Category.CONTENT, true, false);
				}
				
			};
			new Database(true, transaction);
			getAllContentVectorEntries4Global = transaction.getReturnValue();
			if (getAllContentVectorEntries4Global == null) {
				log.error("exiting prematurely");
				return null;
			}
			HashSet<Integer> tags2Recalculate = new HashSet<Integer>();
			MarkModified.GetModifiedTagIds gmtids = new MarkModified.GetModifiedTagIds(true,false);
			try {
				runDBOperation(gmtids);
				for (Integer tId: gmtids) {
					tags2Recalculate.add(tId);
				}
			} finally {
				gmtids.close();
			}
			MultiThreadMostSimilarTagInfoStore.buildInstance(tags2Recalculate);
		}
		int runs = 1;
		do {
			try {
				runDBOperation(getAllContentVectorEntries4Global);
				if (getAllContentVectorEntries4Global.hasNext() == false) {
					log.warn("no content");
					break;
				}
				
				int runCount = 0;
				while (getAllContentVectorEntries4Global.hasNext() == true) {
					Collection<Future<List<MostSimilarTagInfo>>> futures = new ArrayList<Future<List<MostSimilarTagInfo>>>(NUM_THREADS);
					int tags4ThreadsCount = 0;
					
					List<MultiTagSimilarityCalculator> mcalcByThread = new ArrayList<MultiTagSimilarityCalculator>(NUM_THREADS);
					for (int threadNr = 0; threadNr < NUM_THREADS; ++threadNr) {
						mcalcByThread.add(new MultiTagSimilarityCalculator());
					}
					int lastTveTagId = -1;
					do {
						TagVector.Entry tve2Use = getAllContentVectorEntries4Global.next();
						if (tve2Use != null) {
							if (lastTveTagId != tve2Use.getTagId()) {
								if (++tags4ThreadsCount >= tvisPerIteration) {
									break; // es muss sofort abgebrochen werden, denn sonst wird tve2Use noch in diesem Durchlauf und alle anderen TagVector.Entries im nächsten verarbeitet, was falsche ergebnisse mit sich bringen würde
								}
								lastTveTagId = tve2Use.getTagId();
							}
							mcalcByThread.get(tags4ThreadsCount % NUM_THREADS).addCalculationTagVectorEntry(tve2Use);
						}
					} while ((getAllContentVectorEntries4Global.hasNext() == true) && (tags4ThreadsCount < tvisPerIteration));
						
					GetVectorEntries getAllContentVectorEntries = new GetVectorEntries(CONTENT,false);
					try {
						runDBOperation(getAllContentVectorEntries);
						if (tags4ThreadsCount > 0) {
							if ((NUM_THREADS > 1) && (tags4ThreadsCount > 1)) {
								Iterable<TagVector.Entry> allContentTvesIterable4Threads = new MultiThreadIterableAdapter<TagVector.Entry>(NUM_THREADS, 1998, getAllContentVectorEntries.iterator());
								for (int threadNr = 0; threadNr < NUM_THREADS; ++threadNr) {
									futures.add( execs[runCount % 2].submit( new SimpleMostSimilarTagsCalculation( mcalcByThread.get(threadNr), allContentTvesIterable4Threads.iterator(), (doCompleteRecalculation == false) ? new NonRecalculationMostSimilarTagInfoUpdater() : null )) );
								}
							} else {
								futures.add( execs[runCount % 2].submit(new SimpleMostSimilarTagsCalculation( mcalcByThread.get(tags4ThreadsCount % NUM_THREADS), getAllContentVectorEntries.iterator(), (doCompleteRecalculation == false) ? new NonRecalculationMostSimilarTagInfoUpdater() : null )) );
							}
							saveResults(futures);
						}
						++runCount;
						getAllContentVectorEntries = new GetVectorEntries(CONTENT,false);
					} finally {
						getAllContentVectorEntries.close();
					}
				}
				
			} finally {
				getAllContentVectorEntries4Global.close();
			}
			if (saveProcess != null) {
				try {
					saveProcess.get();
				} catch (InterruptedException e) {
					log.fatal(e);
				} catch (ExecutionException e) {
					log.fatal(e);
				}
			}
			log.info("run " + runs + ": all done");
			if (doCompleteRecalculation == false) {
				runDBOperation(new MergeWithTmpSimTables());
				runDBOperation(new DeleteModifiedTagsInUse(AbstractGetVectorEntries.Category.CONTENT,(runs > 1)));
				++runs;
				getAllContentVectorEntries4Global = new GetModifiedVectorEntries(AbstractGetVectorEntries.Category.CONTENT, true, true);
				MarkModified.GetModifiedTagIds gmtids = new MarkModified.GetModifiedTagIds(true,true);
				try {
					runDBOperation(gmtids);
					for (Integer tId: gmtids) {
						MultiThreadMostSimilarTagInfoStore.getInstance().addTagIdToRecalculate(tId);
					}
				} finally {
					gmtids.close();
				}
			}
		} while ((doCompleteRecalculation == false) && (runs < 3));
		saveResultExec.shutdown();
		execs[0].shutdown();
		execs[1].shutdown();
		try {
			if (execs[0].awaitTermination(10,TimeUnit.SECONDS) == false) {
				log.fatal("ExecutorService does not shutdown");
			}
			if (execs[1].awaitTermination(1,TimeUnit.SECONDS) == false) {
				log.fatal("ExecutorService does not shutdown");
			}
			if (saveResultExec.awaitTermination(10,TimeUnit.SECONDS) == false) {
				log.fatal("SaveResultExecutorService does not shutdown");
			}
		} catch (InterruptedException e) {
		}
		if ((doCompleteRecalculation == false) && (runs == 3)) {
			log.debug("deleting need to recalculate what now already has been recalculated");
			runDBOperation(new DeleteModifiedTagsInUse(AbstractGetVectorEntries.Category.CONTENT,true));
		}
		return null;
	}
	
	/*private void saveCurTagUserFrequency(int tagId, int userFrequency) {
		runDBOperation( new SaveUserFreq( tagId, userFrequency) );
	}*/
	
	private void saveResults(final Collection<Future<List<MostSimilarTagInfo>>> futures) {
		// warten bis Speichern der Ergebnisse des letzten durchlaufs fertig ist:
		if (saveProcess != null) {
			try {
				saveProcess.get();
			} catch (InterruptedException e) {
				log.fatal(e);
				return;
			} catch (ExecutionException e) {
				log.fatal(e);
				return;
			}
		}
		
		// nächste Ergebnisse speichern
		saveProcess = saveResultExec.submit(new Callable<Object>() {
			public Object call() {
				for (final Future<List<MostSimilarTagInfo>> f : futures) {
					try {
						Database db = new Database (true, new DatabaseAction<Object>() {
							@Override
							protected Object action() {
								try {
									for (MostSimilarTagInfo msti : f.get()) {
										System.out.println(msti);
										runDBOperation( new SaveMostSimilarTagInfo(msti,false) );
									}
								} catch (InterruptedException e) {
									log.error("while waiting for future: ", e);
								} catch (ExecutionException e) {
									log.error("while waiting for future: ", e);
								}
								return null;
							}
						});
					} catch (RuntimeException e) {
						log.fatal(e);
						e.printStackTrace();
					}
				}
				futures.clear(); // um gc das Leben einfacher zu machen
				
				return null;
			}
		});
	}
}
