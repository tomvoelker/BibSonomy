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
