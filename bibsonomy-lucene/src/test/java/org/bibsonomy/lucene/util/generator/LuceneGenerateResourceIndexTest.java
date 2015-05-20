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
package org.bibsonomy.lucene.util.generator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.manager.LuceneGoldStandardManager;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.GoldStandardPublication;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dzo
 */
public class LuceneGenerateResourceIndexTest {
	
	private static LuceneGoldStandardManager<GoldStandardPublication> manager;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void initLucene() throws Exception {
		manager = (LuceneGoldStandardManager<GoldStandardPublication>) LuceneSpringContextWrapper.getBeanFactory().getBean("luceneGoldStandardPublicationManager");

		// initialize test database
		AbstractDatabaseManagerTest.LOADER.load(AbstractDatabaseManagerTest.DATABASE_CONFIG_FILE, AbstractDatabaseManagerTest.DATABASE_ID);
		
		// delete old indices
		final List<LuceneResourceIndex<GoldStandardPublication>> resourceIndices = manager.getResourceIndeces();
		for (final LuceneResourceIndex<GoldStandardPublication> index : resourceIndices) {
			index.deleteIndex();
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void generateIndex() throws Exception {
		manager.generateIndex(false, 1);
		assertEquals(2, manager.getStatistics().getNumDocs());
	}
	
	@AfterClass
	public static void resetIndex() {
		for (final LuceneResourceIndex<GoldStandardPublication> index : manager.getResourceIndeces()) {
			index.reset();
		}
	}
}
