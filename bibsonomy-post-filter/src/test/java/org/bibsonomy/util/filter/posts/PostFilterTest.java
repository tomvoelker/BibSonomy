package org.bibsonomy.util.filter.posts;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.filter.posts.comparator.Equals;
import org.bibsonomy.util.filter.posts.comparator.GreaterOrEqual;
import org.bibsonomy.util.filter.posts.comparator.Matches;
import org.bibsonomy.util.filter.posts.matcher.BeanPropertyMatcher;
import org.bibsonomy.util.filter.posts.matcher.BooleanAllAndMatcher;
import org.bibsonomy.util.filter.posts.matcher.BooleanOrMatcher;
import org.bibsonomy.util.filter.posts.matcher.Matcher;
import org.bibsonomy.util.filter.posts.modifier.PropertyModifier;
import org.junit.Test;

import bibtex.parser.ParseException;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class PostFilterTest {

	private static final String SPRING_BEAN_DEFINITION_FILE = "postFilterTest-beans.xml";
	private static final String EXAMPLE_BIBTEX_FILE = "postFilterTest.bib";

	@Test
	public void testGetFilteredAndUpdatedPosts() {
		final List<Post<? extends Resource>> posts = getPosts();

		/*
		 * configure matcher
		 */
		final BeanPropertyMatcher<String> yearMatcher = new BeanPropertyMatcher<String>("resource.year", new GreaterOrEqual<String>(), "1992");
		final BeanPropertyMatcher<String> addressMatcher = new BeanPropertyMatcher<String>("resource.address", new Matches(), ".*Berlin.*");
		final BeanPropertyMatcher<String> address2Matcher = new BeanPropertyMatcher<String>("resource.address", new Matches(), ".*Heidelberg.*");
		final BeanPropertyMatcher<String> publisherMatcher = new BeanPropertyMatcher<String>("resource.publisher", new Equals<String>(), "Springer");

		/*
		 * combine them
		 */
		final BooleanAllAndMatcher andMatcher = new BooleanAllAndMatcher(new Matcher[] {
				yearMatcher, publisherMatcher, new BooleanOrMatcher(addressMatcher, address2Matcher)
		});

		/*
		 * configure modifier
		 */
		final PropertyModifier<String> addressModifier = new PropertyModifier<String>("resource.address", "Berlin/Heidelberg");
		/*
		 * configure filter
		 */
		final PostFilter filter = new PostFilter(andMatcher, addressModifier);

		/*
		 * filter posts
		 */
		final List<Post<? extends Resource>> filteredPosts = filter.getFilteredPosts(posts);
		System.out.println("Got " + filteredPosts.size() + " from filter.");
		Assert.assertEquals(13, filteredPosts.size());

		/*
		 * modify posts
		 */
		final List<Post<? extends Resource>> filteredAndUpdatedPosts = filter.getFilteredAndUpdatedPosts(posts);
		System.out.println("Got " + filteredAndUpdatedPosts.size() + " from filter");
		Assert.assertEquals(10, filteredAndUpdatedPosts.size());

		/*
		 * check result
		 */
		for (Post<? extends Resource> post : filteredPosts) {
			final BibTex resource = (BibTex) post.getResource();
			Assert.assertEquals("Berlin/Heidelberg", resource.getAddress());
		}


	}

	private List<Post<? extends Resource>> getPosts() {
		/*
		 * get file contents
		 */
		String streamAsString = null;
		try {
			streamAsString = getStreamAsString(PostFilterTest.class.getClassLoader().getResourceAsStream(EXAMPLE_BIBTEX_FILE));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		/*
		 * parse bibtex
		 */
		List<BibTex> bibtex = null;
		try {
			bibtex = new SimpleBibTeXParser().parseBibTeXs(streamAsString);
		} catch (ParseException e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());		}
		System.out.println("Got " + bibtex.size() + " posts from file.");
		Assert.assertEquals(300, bibtex.size());

		/*
		 * copy into posts
		 */
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		for (final BibTex bib: bibtex) {
			final Post<BibTex> post = new Post<BibTex>();
			post.setResource(bib);
			posts.add(post);
		}
		return posts;
	}
	/**
	 * The same test as {@link #testGetFilteredAndUpdatedPosts()} but now 
	 * configured via Spring XML bean definition.
	 */
	@Test
	public void testGetFilteredAndUpdatedPostsUsingSpringXML() {
		final PostFilter filter = new PostFilterFactory().getPostFilterFromBeanDefinitionInClasspath(SPRING_BEAN_DEFINITION_FILE);
		System.err.println(filter.getMatcher());
		/*
		 * get posts
		 */
		final List<Post<? extends Resource>> posts = getPosts();
		/*
		 * filter posts
		 */
		final List<Post<? extends Resource>> filteredPosts = filter.getFilteredPosts(posts);
		System.out.println("Got " + filteredPosts.size() + " from filter.");
		Assert.assertEquals(13, filteredPosts.size());

		/*
		 * modify posts
		 */
		final List<Post<? extends Resource>> filteredAndUpdatedPosts = filter.getFilteredAndUpdatedPosts(posts);
		System.out.println("Got " + filteredAndUpdatedPosts.size() + " from filter");
		Assert.assertEquals(10, filteredAndUpdatedPosts.size());

		/*
		 * check result
		 */
		for (Post<? extends Resource> post : filteredPosts) {
			final BibTex resource = (BibTex) post.getResource();
			Assert.assertEquals("Berlin/Heidelberg", resource.getAddress());
		}
	}

	private static String getFileAsString (final String fileName) throws IOException {
		return getStreamAsString(new FileInputStream(fileName));
	}

	private static String getStreamAsString(final InputStream inputStream) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		final StringBuffer content = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
		reader.close();

		return content.toString();
	}

}

