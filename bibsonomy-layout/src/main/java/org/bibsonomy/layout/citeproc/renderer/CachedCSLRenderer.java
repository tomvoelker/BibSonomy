/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.citeproc.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.citeproc.CSLUtils;
import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameParser;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * FIXME: unused
 */
public class CachedCSLRenderer {
	private static final Log LOG = LogFactory.getLog(CachedCSLRenderer.class);

//	/**
//	 * Initial value for {@link #nThreads}. Set to a sensible value that doesn't hog all threads.
//	 */
//	private static final int MAX_THREADS = max(1, Runtime.getRuntime().availableProcessors() - 1);
//	/**
//	 * Initial value for {@link #minItemsPerThread}
//	 */
//	private static final int MIN_ITEMS_PER_THREAD = 15;
//	private static ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS, new CSLThreadFactory("CachedCSLRenderer"));
//	/**
//	 * Number of threads to render publications. It has initially a sensible value, but it can be tuned according
//	 * to the current machine (see {@link #tuneNThreads()}).
//	 */
//	private static int nThreads = MAX_THREADS;
//	/**
//	 * Only create new thread if rendering enough items due to CSL creation overhead. It has a sensible value, but it
//	 * can be better tuned according to the performance of the current machine (see {@link #tuneNThreads()}).
//	 */
//	private static int minItemsPerThread = MIN_ITEMS_PER_THREAD;
//
//	public static void tune() {
//		tuneNThreads();
//		tuneMinItemsPerThread();
//	}
//
//	/**
//	 * Measures rendering performance and sets accordingly the optimal number of threads and min items per thread
//	 * (see {@link #nThreads} and {@link #minItemsPerThread}).
//	 * This method should be invoked as isolated as possible.
//	 */
//	static void tuneNThreads() {
//		final int N_REPETITIONS = 10;
//
//		// We are considerate to other tasks in the server and don't want to hog all threads.
//		// Note.- available processor should be always recalculated. See https://docs.oracle.com/javase/6/docs/api/java/lang/Runtime.html#availableProcessors%28%29
//		final int MAX_THREADS = max(1, Runtime.getRuntime().availableProcessors() - 1);
//		final int N_ITEMS = MAX_THREADS * 50;
//		final String DEFAULT_LANG = "en";
//
//		LOG.info("Running in a machine with " + MAX_THREADS + " available logical threads.");
//
//		final List<Post<BibTex>> dummyPosts = createDummyPosts(N_ITEMS);
//
//		// warm-up
//		for (int i = 0; i < 10; i++) {
//			try {
//				CachedCSLRenderer.renderPosts(dummyPosts, CSLStyle. .APA.getFilename(), DEFAULT_LANG);
//			} catch (final InterruptedException ignored) {
//			}
//		}
//
//		// set minItemsPerThread to a very low value, to force the segmentation of items in as so many chunks as threads
//		final int origMinItemsPerThread = getMinItemsPerThread();
//		setMinItemsPerThread(1);
//
//		int optimalNThreads = 1;
//		long bestTime = Long.MAX_VALUE;
//
//		for (int i = 1; i <= MAX_THREADS; i++) {
//			setnThreads(i);
//			LOG.debug("Measuring rendering of " + N_ITEMS + " items with " + i + " threads.");
//			final long begin = System.nanoTime();
//			try {
//				for (int repetition = 0; repetition < N_REPETITIONS; repetition++) {
//					CachedCSLRenderer.renderPosts(dummyPosts, CSLStyle.APA.getFilename(), DEFAULT_LANG);
//				}
//			} catch (final InterruptedException ignored) {
//			}
//			final long ellapsedNanos = System.nanoTime() - begin;
//			LOG.debug("Rendered " + N_REPETITIONS + " x " + N_ITEMS + " in " + ellapsedNanos / 1000000L + " ms using " + i + " threads");
//			if (ellapsedNanos < bestTime) {
//				optimalNThreads = i;
//				bestTime = ellapsedNanos;
//			}
//		}
//		LOG.info("Found and set optimal number of threads = " + optimalNThreads);
//		setMinItemsPerThread(origMinItemsPerThread);
//		setnThreads(optimalNThreads);
//	}
//
//	/**
//	 * Calculates a sensible value for {@link #minItemsPerThread}: how many items could be rendered during the time
//	 * required for creating a CSL instance, so we don't create many threads if there are only a few items to be
//	 * rendered.
//	 */
//	static void tuneMinItemsPerThread() {
//		final int N_MAX_ITEMS = 100;
//
//		final int origNThreads = getnThreads();
//
//		// Only interested in performance of a single thread
//		setnThreads(1);
//
//		// warm-up
//		for (int i = 0; i < 10; i++) {
//			measureRenderTime(N_MAX_ITEMS);
//		}
//
//		// T(rendering of 1 single item)
//		final long ellapsedNanos1 = measureRenderTime(1);
//
//		// T(rendering of N_MAX_ITEMS items)
//		final long ellapsedNanosMaxItems = measureRenderTime(N_MAX_ITEMS);
//
//		final long tRenderOfOneItem = (ellapsedNanosMaxItems - ellapsedNanos1) / (N_MAX_ITEMS - 1);
//		final long tOverheadPerThread = (ellapsedNanosMaxItems + ellapsedNanos1 - (tRenderOfOneItem * (N_MAX_ITEMS + 1))) / 2;
//
//		LOG.info(String.format("Time to render one item:  %,d ns", tRenderOfOneItem));
//		LOG.info(String.format("Overhead time per thread: %,d ns", tOverheadPerThread));
//
//		final int optimalMinItemsPerThread = Math.toIntExact(tOverheadPerThread / tRenderOfOneItem);
//		LOG.info("Found and set minItemsPerThread = " + optimalMinItemsPerThread);
//		setMinItemsPerThread(optimalMinItemsPerThread);
//
//		// Verify
//		final long ellapsedNanos250 = measureRenderTime(250);
//
//		final long expected = tOverheadPerThread + tRenderOfOneItem * 250;
//		LOG.info(String.format("Verification: Time to render 250 items = %,d ns", ellapsedNanos250));
//		LOG.info(String.format("Verification: Expected: %,d + 250 x %d = %,d ns", tOverheadPerThread, tRenderOfOneItem, expected));
//		LOG.info(String.format("Verification: Diff: %,d ns; Rel. diff: %5.2f",
//				expected - ellapsedNanos250, (expected - ellapsedNanos250) * 1.0 / (expected * 1.0)));
//
//		setnThreads(origNThreads);
//	}
//
//	private static long measureRenderTime(final int nItems) {
//		final int N_REPETITIONS = 10;
//		final String DEFAULT_LANG = "en";
//
//		// The selection of the STYLE for this tuning is very important, as the time required to createa CSL instance
//		// is proportional to the complexity of the style.
//		// XXX: AG 2020-11-10 - I'm using a style that is not yet cached. If we implement a good caching policy, we
//		// could go with minItemsPerThread = 0, that is, always fragment the list of items in as many chuncks as
//		// there are threads, because the corresponding CSL will be already available.
//
////		final String STYLE = CSLStyle.APA.getFilename();
//		// Very simplistic style for tuning.
//		final String STYLE = "<style xmlns=\"http://purl.org/net/xbiblio/csl\" version=\"1.0\">\n" +
//				"  <bibliography>\n" +
//				"    <layout suffix=\".\">\n" +
//				"      <group delimiter=\", \">\n" +
//				"        <text variable=\"title\"/>\n" +
//				"      </group>\n" +
//				"    </layout>\n" +
//				"  </bibliography>\n" +
//				"</style>\n";
//
//		final List<Post<BibTex>> dummyPosts = createDummyPosts(nItems);
//		final long begin = System.nanoTime();
//		try {
//			for (int repetition = 0; repetition < N_REPETITIONS; repetition++) {
//				CachedCSLRenderer.renderPosts(dummyPosts, STYLE, DEFAULT_LANG);
//			}
//		} catch (final InterruptedException ignored) {
//		}
//		return (System.nanoTime() - begin) / N_REPETITIONS;
//	}
//
//	private static List<Post<BibTex>> createDummyPosts(final int nItems) {
//		// prepare some items
//		final List<Post<BibTex>> dummyPosts = new ArrayList<>();
//
//		for (int i = 2020 - nItems; i < 2020; i++) {
//			dummyPosts.add(createDummyPost(i));
//		}
//		return dummyPosts;
//	}
//
//	private static Post<BibTex> createDummyPost(final int year) {
//		final Post<BibTex> post = new Post<>();
//		final BibTex publication = new BibTex();
//		publication.setEntrytype(BibTexUtils.ARTICLE);
//		publication.setTitle("Title " + year);
//		try {
//			publication.setAuthor(PersonNameParser.parse("Test, Person"));
//		} catch (final PersonNameParser.PersonListParserException ignored) {
//		}
//		publication.setYear(String.valueOf(year));
//		publication.recalculateHashes();
//
//		post.setUser(new User("testuser" + year));
//		post.setResource(publication);
//		return post;
//	}
//
//	public static int getnThreads() {
//		return nThreads;
//	}
//
//	public static void setnThreads(final int nThreads) {
//		CachedCSLRenderer.nThreads = nThreads;
//		executor = Executors.newFixedThreadPool(nThreads);
//	}
//
//	public static int getMinItemsPerThread() {
//		return minItemsPerThread;
//	}
//
//	public static void setMinItemsPerThread(final int minItemsPerThread) {
//		// cannot be 0
//		CachedCSLRenderer.minItemsPerThread = max(1, minItemsPerThread);
//	}
//
//	/**
//	 * Creates an html representation of the BibTeX posts using the provided style.
//	 *
//	 * @param posts list of posts with bibtex resources to render as html
//	 * @param style the citation style to use. May either be a serialized XML representation of the style or a
//	 *              style's name such as {@code ieee}. In the latter case, the processor loads the style
//	 *              from the classpath (e.g. {@code /ieee.csl})
//	 * @return a map of postIDs to their corresponding rendered html
//	 */
//	public static Map<String, CSLItemDataRenderedResult> renderPosts(final List<? extends Post<BibTex>> posts, final String style, final String lang) throws InterruptedException {
//		if (posts == null || posts.isEmpty()) {
//			return (Map<String, CSLItemDataRenderedResult>) Collections.EMPTY_MAP;
//		}
//		final CSLItemDataConversionResult[] cslConvertedItems = CSLUtils.convertConcurretlyToCslItemData(posts);
//
//		// Divide the list of items in a few chunks before sending them for conversion (at most, nThreads chuncks,
//		// and sparing threads if there are not enough items for a thread to compensate for the overhead)
//		final int nChuncks = min(nThreads, 1 + (int) Math.floor(1.0 * cslConvertedItems.length / minItemsPerThread));
//
//		// each thread will render so many items, ocassionally there will be threads rendering one more item
//		final int nItemsPerThread = cslConvertedItems.length / nChuncks;
//
//		// if cslItemData.length / nChuncks is not a natural number, there is a rest that must be distributed between
//		// some threads
//		final int nOverloadedThreads = cslConvertedItems.length % nChuncks;
//
//		LOG.debug("Rendering " + cslConvertedItems.length + " posts with " + nChuncks + " threads");
//		final CSLItemDataConversionResult[][] chuncks = new CSLItemDataConversionResult[nChuncks][];
//		int finalIndex = 0;
//		for (int i = 0; i < nChuncks; i++) {
//			final int initialIndex = finalIndex;         // initialIndex is inclusive
//			finalIndex = initialIndex + nItemsPerThread; // finalIndex is exclusive
//			// if this thread is overloaded, render one more item
//			if (i < nOverloadedThreads) {
//				finalIndex++;
//			}
//			LOG.trace("Thread " + i + " will render items [" + initialIndex + "," + (finalIndex - 1) + "] of [0," + (cslConvertedItems.length - 1) + "]");
//			chuncks[i] = Arrays.copyOfRange(cslConvertedItems, initialIndex, finalIndex);
//		}
//
//		final Map<String, CSLItemDataRenderedResult> result = new HashMap<>();
//		final List<CSLRenderTask> taskList = Arrays.stream(chuncks)
//				.map(items -> new CSLRenderTask(items, style, lang))
//				.collect(Collectors.toList());
//		executor.invokeAll(taskList)
//				.stream()
//				.map(future -> {
//					try {
//						return future.get();
//					} catch (final Exception e) {
//						throw new IllegalStateException(e);
//					}
//				})
//				.forEach(result::putAll);
//
//		return result;
//	}
//
//	/**
//	 * The default thread factory doesn't allow for setting the thread names. This class is almost a verbatim copy
//	 * of {@code java.util.concurrent.Executors.DefaultThreadFactory}, only allowing to set a prefix for the thread
//	 * names.
//	 */
//	private static class CSLThreadFactory implements ThreadFactory {
//		private static final AtomicInteger poolNumber = new AtomicInteger(1);
//		private final ThreadGroup group;
//		private final AtomicInteger threadNumber = new AtomicInteger(1);
//		private final String namePrefix;
//
//		/**
//		 * Creates a new ThreadFactory where threads are created with a name prefix of <tt>prefix</tt>.
//		 *
//		 * @param prefix Thread name prefix. Never use a value of "pool" as in that case you might as well have used
//		 *               {@link Executors#defaultThreadFactory()}.
//		 */
//		CSLThreadFactory(final String prefix) {
//			final SecurityManager s = System.getSecurityManager();
//			group = (s != null) ? s.getThreadGroup() :
//					Thread.currentThread().getThreadGroup();
//			namePrefix = prefix + "-pool-" +
//					poolNumber.getAndIncrement() +
//					"-thread-";
//		}
//
//		public Thread newThread(final Runnable r) {
//			final Thread t = new Thread(group, r,
//					namePrefix + threadNumber.getAndIncrement(),
//					0);
//			if (t.isDaemon())
//				t.setDaemon(false);
//			if (t.getPriority() != Thread.NORM_PRIORITY)
//				t.setPriority(Thread.NORM_PRIORITY);
//			return t;
//		}
//	}
//
//	private static final class CSLRenderTask implements Callable<Map<String, CSLItemDataRenderedResult>> {
//		private static final ThreadLocal<CSLProcessorCache> localCSLCache = ThreadLocal.withInitial(CSLProcessorCache::new);
//		private final CSLItemDataConversionResult[] items;
//		private final String style;
//		private final String lang;
//
//		public CSLRenderTask(final CSLItemDataConversionResult[] items, final String style, final String lang) {
//			this.items = items;
//			this.style = style;
//			this.lang = lang;
//		}
//
//		@Override
//		public Map<String, CSLItemDataRenderedResult> call() throws Exception {
//			LOG.trace("Rendering " + items.length + " items from thread " + Thread.currentThread().getName());
//			final CSLProcessor cslProcessor = localCSLCache.get().getCSLProcessor(style, lang);
//			return Arrays.stream(items)
//					.collect(Collectors.toMap(
//							item -> item.getItemData().getId(),
//							item -> {
//								LOG.trace("Rendering item " + item.getItemData().getId() + " from thread " + Thread.currentThread().getName());
//								return new CSLItemDataRenderedResult(
//										cslProcessor.makeBibliography(item.getItemData()),
//										item.getIssues().stream().map(CmsMessageContainer::key).collect(Collectors.joining("<br/>")));
//							}));
//		}
//	}
//
//	public static class CSLItemDataRenderedResult {
//		private final String itemRenderedHtml;
//		private final String itemIssuesHtml;
//
//		public CSLItemDataRenderedResult(final String itemRenderedHtml, final String itemIssuesHtml) {
//
//			this.itemRenderedHtml = itemRenderedHtml;
//			this.itemIssuesHtml = itemIssuesHtml;
//		}
//
//		public String getItemIssuesHtml() {
//			return itemIssuesHtml;
//		}
//
//		public String getItemRenderedHtml() {
//			return itemRenderedHtml;
//		}
//
//		@Override
//		public String toString() {
//			return "CSLItemDataRenderedResult{" +
//					"itemRenderedHtml='" + itemRenderedHtml + '\'' +
//					", itemIssuesHtml='" + itemIssuesHtml + '\'' +
//					'}';
//		}
//
//		@Override
//		public boolean equals(final Object o) {
//			if (this == o) return true;
//			if (o == null || getClass() != o.getClass()) return false;
//			final CSLItemDataRenderedResult that = (CSLItemDataRenderedResult) o;
//			return Objects.equals(itemRenderedHtml, that.itemRenderedHtml) &&
//					Objects.equals(itemIssuesHtml, that.itemIssuesHtml);
//		}
//
//		@Override
//		public int hashCode() {
//			return Objects.hash(itemRenderedHtml, itemIssuesHtml);
//		}
//	}
}
