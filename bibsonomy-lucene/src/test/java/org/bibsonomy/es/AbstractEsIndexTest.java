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
package org.bibsonomy.es;

import java.util.Map;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.beans.factory.BeanFactory;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class AbstractEsIndexTest {
	
	private static ESTestClient testClient;
	
	@BeforeClass
	public static void beforeClass() {
		initTestDatabase();
		ESTestClientInitializer testClientInitializer = (ESTestClientInitializer) EsSpringContextWrapper.getBeanFactory().getBean("esClientInitializer");
		testClientInitializer.init();
		testClient = testClientInitializer.getEsClient();
		
	}

	private static void initTestDatabase() {
		AbstractDatabaseManagerTest.LOADER.load(AbstractDatabaseManagerTest.DATABASE_CONFIG_FILE, AbstractDatabaseManagerTest.DATABASE_ID);
	}
	
	@AfterClass
	public static void afterClass() {
		if (testClient != null) {
			testClient.shutdown();
		}
		BeanFactory bf = EsSpringContextWrapper.getBeanFactory();
		closeAllLuceneIndices(bf);
	}

	public static void closeAllLuceneIndices(BeanFactory bf) {
		final Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>> managers = (Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>>) bf.getBean("allLuceneResourceManagers");
		for (LuceneResourceManager<?> lrm : managers.values()) {
			lrm.close();
		}
	}
	
	protected void updatePublicationIndex() {
		LuceneResourceManager<BibTex> luceneBibTexUpdater = (LuceneResourceManager<BibTex>) EsSpringContextWrapper.getBeanFactory().getBean("lucenePublicationManager");
		luceneBibTexUpdater.updateAndReloadIndex();
	}
}
