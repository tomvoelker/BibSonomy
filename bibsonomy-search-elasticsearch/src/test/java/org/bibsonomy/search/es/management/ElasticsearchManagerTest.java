package org.bibsonomy.search.es.management;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.search.ElasticsearchPublicationSearch;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests for {@link ElasticsearchManager}
 *
 * @author dzo
 */
public class ElasticsearchManagerTest extends AbstractEsIndexTest {
	
	private static final AdminDatabaseManager adminDatabaseManager = AdminDatabaseManager.getInstance();
	private static ElasticsearchManager<BibTex> publicationManager;
	private static ElasticsearchPublicationSearch<BibTex> publicationSearch;
	
	/**
	 * inits the manager
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static final void initManager() {
		publicationManager = EsSpringContextWrapper.getContext().getBean("elasticsearchPublicationManager", ElasticsearchManager.class);
		publicationSearch = EsSpringContextWrapper.getContext().getBean("elasticsearchPublicationSearch", ElasticsearchPublicationSearch.class);
	}
	
	/**
	 * tests {@link ElasticsearchManager#updateIndex()}
	 */
	@Test
	public void testUpdateIndexWithSpammer() {
		final String userToFlag = "testuser3";
		final ResultList<Post<BibTex>> postsBefore = publicationSearch.getPosts(userToFlag, userToFlag, null, null, Collections.<String>emptyList(), null, "test", null, null, null, null, null, null, null, null, Order.ADDED, 10, 0);
		assertEquals(1, postsBefore.size());
		
		final User user = new User(userToFlag);
		user.setSpammer(Boolean.TRUE);
		user.setAlgorithm("unittest");
		adminDatabaseManager.flagSpammer(user, "admin", this.dbSession);
		publicationManager.updateIndex();
		
		final ResultList<Post<BibTex>> posts = publicationSearch.getPosts(userToFlag, userToFlag, null, null, Collections.<String>emptyList(), null, null, null, null, null, null, null, null, null, null, Order.ADDED, 10, 0);
		assertEquals(0, posts.size());
		
		user.setSpammer(Boolean.FALSE);
		user.setAlgorithm("admin");
		user.setPrediction(null); // FIXME: side effects :(
		adminDatabaseManager.flagSpammer(user, "admin", this.dbSession);
		
		publicationManager.updateIndex();
		
		final ResultList<Post<BibTex>> readded = publicationSearch.getPosts(userToFlag, userToFlag, null, null, Collections.<String>emptyList(), null, null, null, null, null, null, null, null, null, null, Order.ADDED, 10, 0);
		assertEquals(postsBefore.size(), readded.size());
	}
}
