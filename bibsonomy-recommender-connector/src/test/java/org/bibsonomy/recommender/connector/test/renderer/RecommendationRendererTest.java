package org.bibsonomy.recommender.connector.test.renderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import junit.framework.Assert;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedPost;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.recommender.connector.model.BibsonomyItemRendererFactoryWrapper;
import org.bibsonomy.recommender.connector.model.BibsonomyTagRendererFactoryWrapper;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess;
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.interfaces.renderer.RecommendationRenderer;
import recommender.impl.model.RecommendedItem;

/**
 * Tests the implementations of {@link RecommendationRenderer} for tag and item recommendeations
 * via the wrapped {@link RendererFactory}.
 * 
 * @author lukas
 *
 */
public class RecommendationRendererTest {

	private static RendererFactory factory;
	
	@BeforeClass
	public static void init() {
		factory = new RendererFactory(new UrlRenderer("api/"));
	}
	
	/**
	 * tests the serialization of the {@link BibsonomyTagRendererFactoryWrapper}  for {@link TagRecommendationEntity}s
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTagRecommendationEntityRenderer() {
		
		final RecommendationRenderer<TagRecommendationEntity, recommender.impl.model.RecommendedTag> renderer = new BibsonomyTagRendererFactoryWrapper(factory);
		
		// create dummy post
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("dummy title");
		bibtex.setIntraHash("hash");
		bibtex.setYear("2013");
		bibtex.setBibtexKey("key");
		final List<PersonName> author = new ArrayList<PersonName>();
		author.add(new PersonName("test", "user"));
		bibtex.setAuthor(author);
		bibtex.setEntrytype(BibTexUtils.ARTICLE);
		post.setResource(bibtex);
		post.setUser(new User("dummy user"));
		
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final Writer writer = new PrintWriter(out);
		
		renderer.serializeRecommendationEntity(writer, new PostWrapper<BibTex>(post));
		
		final Renderer bibsonomyRenderer = factory.getRenderer(RenderingFormat.XML);
		final Post<BibTex> entity = (Post<BibTex>) bibsonomyRenderer.parsePost(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())), NoDataAccessor.getInstance());
		
		// check correct deserialization of attributes
		Assert.assertEquals(post.getResource().getTitle(), entity.getResource().getTitle());
		Assert.assertEquals(post.getResource().getIntraHash(), entity.getResource().getIntraHash());
		Assert.assertEquals(post.getUser().getName(), entity.getUser().getName());
		Assert.assertEquals(post.getResource().getYear(), entity.getResource().getYear());
		Assert.assertEquals(post.getResource().getBibtexKey(), entity.getResource().getBibtexKey());
		Assert.assertEquals(post.getResource().getEntrytype(), entity.getResource().getEntrytype());
		Assert.assertEquals(post.getResource().getAuthor(), entity.getResource().getAuthor());
		
	}
	
	/**
	 * tests the deserialization of {@link recommender.impl.model.RecommendedTag}s via the {@link BibsonomyTagRendererFactoryWrapper}
	 */
	@Test
	public void testRecommendedTagRenderer() {
		
		final RecommendationRenderer<TagRecommendationEntity, recommender.impl.model.RecommendedTag> renderer = new BibsonomyTagRendererFactoryWrapper(factory);
		
		final List<RecommendedTag> tags = new ArrayList<RecommendedTag>();
		final RecommendedTag firstTag = new RecommendedTag("dummy", 1.0, 1.0);
		final RecommendedTag secondTag = new RecommendedTag("tag", 0.0, 0.0);
		
		tags.add(firstTag);
		tags.add(secondTag);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new PrintWriter(out);
		
		final Renderer bibsonomyRenderer = factory.getRenderer(RenderingFormat.XML);
		
		bibsonomyRenderer.serializeRecommendedTags(writer, tags);
		
		final SortedSet<RecommendedTag> results = RecommendationUtilities.getRecommendedTags(
				renderer.parseRecommendationResultList(new InputStreamReader(new ByteArrayInputStream(out.toByteArray()))));
		
		Iterator<RecommendedTag> it = results.iterator();
		RecommendedTag firstResult = it.next();
		Assert.assertEquals(firstTag.getName(), firstResult.getName());
		Assert.assertEquals(firstTag.getScore(), firstResult.getScore());
		Assert.assertEquals(firstTag.getConfidence(), firstResult.getConfidence());
		
		RecommendedTag secondResult = it.next();
		Assert.assertEquals(secondTag.getName(), secondResult.getName());
		Assert.assertEquals(secondTag.getScore(), secondResult.getScore());
		Assert.assertEquals(secondTag.getConfidence(), secondResult.getConfidence());
		
		out = new ByteArrayOutputStream();
		writer = new PrintWriter(out);
		
		bibsonomyRenderer.serializeRecommendedTag(writer, firstTag);
		
		final recommender.impl.model.RecommendedTag result = renderer.parseRecommendationResult(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
		
		Assert.assertEquals(firstTag.getName(), result.getName());
		Assert.assertEquals(firstTag.getScore(), result.getScore());
		Assert.assertEquals(firstTag.getConfidence(), result.getConfidence());
		
	}
	
	/**
	 * tests the serialization of {@link ItemRecommendationEntity}s via the {@link BibsonomyItemRendererFactoryWrapper}
	 */
	@Test
	public void testItemRecommendationEntityRenderer() {
		
		final RecommendationRenderer<ItemRecommendationEntity, RecommendedItem> renderer = new BibsonomyItemRendererFactoryWrapper<BibTex>(factory);
		
		final User user = new User("testuser");
		
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final Writer writer = new PrintWriter(out);
		
		renderer.serializeRecommendationEntity(writer, new UserWrapper(user));
			
		final Renderer bibsonomyRenderer = factory.getRenderer(RenderingFormat.XML);
		final User entity = bibsonomyRenderer.parseUser(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
		
		Assert.assertEquals(user.getName(), entity.getName());
	}
	
	/**
	 * tests the deserialization of {@link RecommendedItem}s via the {@link BibsonomyItemRendererFactoryWrapper}
	 */
	@Test
	public void testItemRecomendationResultRenderer() {
		final BibsonomyItemRendererFactoryWrapper<BibTex> renderer = new BibsonomyItemRendererFactoryWrapper<BibTex>(factory);
		renderer.setDbAccess(new DummyMainItemAccess());
		renderer.setResourceType(BibTex.class);
		
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("dummy title");
		bibtex.setIntraHash("hash");
		bibtex.setYear("2013");
		bibtex.setBibtexKey("key");
		final List<PersonName> author = new ArrayList<PersonName>();
		author.add(new PersonName("test", "user"));
		bibtex.setAuthor(author);
		bibtex.setEntrytype(BibTexUtils.ARTICLE);
		post.setResource(bibtex);
		post.setUser(new User("dummy"));
		
		final RecommendedPost<BibTex> recommended = new RecommendedPost<BibTex>(post);
		recommended.setScore(1.0);
		recommended.setConfidence(1.0);
		
		final List<RecommendedPost<BibTex>> posts = new ArrayList<RecommendedPost<BibTex>>();
		
		posts.add(recommended);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new PrintWriter(out);
		
		final Renderer bibsonomyRenderer = factory.getRenderer(RenderingFormat.XML);
		
		bibsonomyRenderer.serializeRecommendedPosts(writer, posts, new ViewModel());
				
		SortedSet<RecommendedItem> results = renderer.parseRecommendationResultList(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
		
		Assert.assertEquals(bibtex.getTitle(), results.first().getTitle());
		Assert.assertEquals(recommended.getScore(), results.first().getScore());
		Assert.assertEquals(recommended.getConfidence(), results.first().getConfidence());
		
		out = new ByteArrayOutputStream();
		writer = new PrintWriter(out);
		
		bibsonomyRenderer.serializeRecommendedPost(writer, recommended, new ViewModel());
		
		final RecommendedItem result = renderer.parseRecommendationResult(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
		
		Assert.assertEquals(bibtex.getTitle(), result.getTitle());
		Assert.assertEquals(recommended.getScore(), result.getScore());
		Assert.assertEquals(recommended.getConfidence(), result.getConfidence());
	}
	
}
