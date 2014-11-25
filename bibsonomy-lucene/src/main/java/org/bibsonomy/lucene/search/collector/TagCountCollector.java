/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.lucene.search.collector;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.model.Tag;

/**
 * experimental hits collector for calculating author tag cloud
 * 
 * FIXME: springify this
 * 
 * @author fei
 */
public class TagCountCollector extends Collector {
	private static final Log log = LogFactory.getLog(TagCountCollector.class);
	
	private static final String CFG_LIST_DELIMITER = " ";
	
	private final Map<Integer,IndexReader> docToReaderMap = new HashMap<Integer, IndexReader>();
	private IndexReader lastReader = null;

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return false;
	}

	@Override
	public void collect(final int doc) throws IOException {
		this.docToReaderMap.put(doc, this.lastReader);
	}

	@Override
	public void setNextReader(final IndexReader reader, final int docBase) throws IOException {
		this.lastReader  = reader;
	}

	@Override
	public void setScorer(final Scorer scorer) throws IOException {
	}
	
	/**
	 * fetches tags and their corresponding counts from collected documents
	 * 
	 * @param searcher index searcher for accessing documents
	 * @return the tags and their corresponding counts from collected documents
	 */
	public List<Tag> getTags(final IndexSearcher searcher) {
		final Map<String,Integer> tagCounter = new HashMap<String,Integer>();
		
		log.debug("Start extracting tags from index...");
		final List<Tag> tags = new LinkedList<Tag>();
		for (final Integer docId : this.docToReaderMap.keySet()) {
			try {
				final FieldSelector tasSelector = new MapFieldSelector(LuceneFieldNames.TAS); 
				final Document doc = this.docToReaderMap.get(docId).document(docId, tasSelector);
				final String tagsString = doc.get(LuceneFieldNames.TAS);
				if (present(tagsString)) {
					for (final String tag : tagsString.split(CFG_LIST_DELIMITER)) {
						Integer oldCnt = tagCounter.get(tag);
						if (!present(oldCnt) ) {
							oldCnt = 1;
						} else {
							oldCnt += 1;
						}
						tagCounter.put(tag, oldCnt);
					}
				}
			} catch (final IOException e) {
				log.error("Error fetching document " + docId + " from index.", e);
			}

		}
		log.debug("Done extracting tags from index...");
		
		// extract all tags
		for (final Map.Entry<String,Integer> entry : tagCounter.entrySet()) {
			final Tag transientTag = new Tag();
			transientTag.setName(entry.getKey());
			transientTag.setUsercount(entry.getValue());
			transientTag.setGlobalcount(entry.getValue()); // FIXME: we set user==global count
			tags.add(transientTag);
		}
		log.debug("Done converting tag list");
		
		return tags;
	}
}
