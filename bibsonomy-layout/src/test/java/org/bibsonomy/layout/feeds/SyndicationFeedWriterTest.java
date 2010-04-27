/**
 *  
 *  BibSonomy-Layout - Layout engine for the webapp.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
		// allowed feed types are rss_0.91N, rss_0.93, rss_0.92, rss_1.0, rss_0.94, rss_2.0, rss_0.91U
		// rss_0.9, atom_1.0, atom_0.3
		sfw.setFeedType("rss_2.0");		
		return sfw;
	}
	
	
	@Test
	public void testCreateFeedStringListOfPostOfRESOURCE() {
		final SyndicationFeedWriter<Bookmark> sfw = getSyndicationFeedWriterBookmark();
		final SyndFeed feed = sfw.createFeed("BibSonomy's bookmarks for /tag/web", "/tag/web", "", bookmarks);
				
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
