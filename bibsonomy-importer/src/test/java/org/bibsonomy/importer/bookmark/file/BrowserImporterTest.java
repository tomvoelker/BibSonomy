package org.bibsonomy.importer.bookmark.file;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * tests for {@link BrowserImporter}
 * 
 * @author dzo
 * @version $Id$
 */
public class BrowserImporterTest {

	@Test
	public void testDelicious() throws IOException {
		final BrowserImporter importer = new BrowserImporter();
		importer.initialize(new File("src/test/resources/delicious.html"), new User("testuser"), "public");
		final List<Post<Bookmark>> posts = importer.getPosts();
		assertEquals(1, posts.size());
		
		testTags(posts);
	}
	
	private void testTags(List<Post<Bookmark>> posts) {
		for (Post<Bookmark> post : posts) {
			for (final Tag tag : post.getTags()) {
				assertThat(tag.getName(), not(containsString(" ")));
			}
		}
	}

	@Test
	public void testFirefox() throws IOException {
		final BrowserImporter importer = new BrowserImporter();
		importer.initialize(new File("src/test/resources/firefox_20.html"), new User("testuser"), "public");
		final List<Post<Bookmark>> posts = importer.getPosts();
		assertEquals(10, posts.size());
		testTags(posts);
		
		final Post<Bookmark> example = posts.get(6);
		final Bookmark exampleBookmark = example.getResource();
		assertEquals("BibSonomy", exampleBookmark.getTitle());
		assertEquals("http://www.bibsonomy.org", exampleBookmark.getUrl());
	}
	
	@Test
	public void testOpera() throws IOException {
		final BrowserImporter importer = new BrowserImporter();
		importer.initialize(new File("src/test/resources/opera_12.15.html"), new User("testuser"), "public");
		final List<Post<Bookmark>> posts = importer.getPosts();
		assertEquals(46, posts.size());
		testTags(posts);
		
		// test one of the extracted bookmarks
		final Post<Bookmark> example = posts.get(15);
		final Bookmark exampleBookmark = example.getResource();
		assertEquals("AVG", exampleBookmark.getTitle());
		assertEquals("http://redir.opera.com/bookmarks/AVG/", exampleBookmark.getUrl());
		assertEquals("Security, pc tune up, anti-virus", example.getDescription());
	}
	
	@Test
	public void testSafari() throws IOException {
		final BrowserImporter importer = new BrowserImporter();
		importer.initialize(new File("src/test/resources/safari_6.0.4.html"), new User("testuser"), "public");
		final List<Post<Bookmark>> posts = importer.getPosts();
		assertEquals(21, posts.size());
		testTags(posts);
		
		// test one of the extracted bookmarks
		final Post<Bookmark> example = posts.get(12);
		final Bookmark exampleBookmark = example.getResource();
		assertEquals("The Wall Street Journal", exampleBookmark.getTitle());
		assertEquals("http://online.wsj.com/home-page", exampleBookmark.getUrl());
	}
	@Test
	public void testChrome() throws IOException {
		final BrowserImporter importer = new BrowserImporter();
		importer.initialize(new File("src/test/resources/chrome_13.06.13.html"), new User("testuser"), "public");
		final List<Post<Bookmark>> posts = importer.getPosts();
		assertEquals(9, posts.size());
		testTags(posts);
		
		// test one of the extracted bookmarks
		final Post<Bookmark> example = posts.get(4);
		final Bookmark exampleBookmark = example.getResource();
		assertEquals("BibSonomy :: home", exampleBookmark.getTitle());
		assertEquals("http://www.bibsonomy.org/", exampleBookmark.getUrl());
	}
}
