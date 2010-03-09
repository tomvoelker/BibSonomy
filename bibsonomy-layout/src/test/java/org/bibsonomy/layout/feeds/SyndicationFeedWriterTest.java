package org.bibsonomy.layout.feeds;

import static org.junit.Assert.fail;

import java.util.List;

import org.bibsonomy.layout.feeds.SyndicationFeedWriter;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class SyndicationFeedWriterTest {

	final List<Post<Bookmark>> bookmarks = ModelUtils.getBookmarks();

	
	private SyndicationFeedWriter<Bookmark> getSyndicationFeedWriterBookmark() {
		final SyndicationFeedWriter<Bookmark> sfw = new SyndicationFeedWriter<Bookmark>();
		sfw.setUrlGenerator(new URLGenerator("http://www.bibsonomy.org/"));
		return sfw;
	}
	
	
	@Test
	public void testCreateFeedStringListOfPostOfRESOURCE() {
		final SyndicationFeedWriter<Bookmark> sfw = getSyndicationFeedWriterBookmark();
		final SyndFeed feed = sfw. createFeed("BibSonomy's bookmarks for /tag/web", "/tag/web", "", bookmarks);
		
		  final SyndFeedOutput output = new SyndFeedOutput();
		  try {
			System.out.println(output.outputString(feed));
		} catch (FeedException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateFeedString() {
		//fail("Not yet implemented");
	}

}
