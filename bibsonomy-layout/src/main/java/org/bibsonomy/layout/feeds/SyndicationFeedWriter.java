package org.bibsonomy.layout.feeds;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.services.URLGenerator;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class SyndicationFeedWriter<RESOURCE extends Resource> {

	private URLGenerator urlGenerator;

	public URLGenerator getUrlGenerator() {
		return urlGenerator;
	}

	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	
	public void writeFeed(final SyndFeed feed, final Writer writer) throws IOException, FeedException {
		  final SyndFeedOutput output = new SyndFeedOutput();
		  output.output(feed, writer);
	}

	public void writeFeed (final String feedType, final String title, final String path, final String description, final List<Post<RESOURCE>> posts, final Writer writer) throws IOException, FeedException {
		writeFeed(createFeed(feedType, title, path, description, posts), writer);
	}

	
	public SyndFeed createFeed (final String feedType, final String title, final String path, final String description, final List<Post<RESOURCE>> posts) {
		final SyndFeed feed = createFeed(feedType, title, path, description);

		final List<SyndEntry> entries = new LinkedList<SyndEntry>();

		for (final Post<RESOURCE> post: posts) {
			final SyndEntry entry = new SyndEntryImpl();
			final RESOURCE resource = post.getResource();
			entry.setTitle(resource.getTitle());
			/*
			 * For publications, we want to point to BibSonomy, for bookmarks to 
			 * their "real" URL.
			 */
			if (resource instanceof Bookmark) {
				entry.setLink(((Bookmark) resource).getUrl());
			} else {
				entry.setLink(urlGenerator.getPostUrl(post));
			}
			entry.setPublishedDate(post.getDate());
			entry.setAuthor(post.getUser().getName());
			entry.setUri(urlGenerator.getPostUrl(post));
			/*
			 * add the tags as categories
			 */
			final List<SyndCategory> categories = new LinkedList<SyndCategory>();
			for (final Tag tag: post.getTags()) {
				final SyndCategory category = new SyndCategoryImpl();
				category.setName(tag.getName());
				category.setTaxonomyUri(urlGenerator.getUserUrl(post.getUser()) + "/");
				categories.add(category);
			}

			entry.setCategories(categories);

			final SyndContent entryDescription = new SyndContentImpl();

			entryDescription.setType("text/plain");
			entryDescription.setValue(post.getDescription());

			entryDescription.setType("text/html");
			entryDescription.setValue("<p>More Bug fixes, mor API changes, some new features and some Unit testing</p>"+
			"<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV03\">Changes Log</a></p>");

			entry.setDescription(entryDescription);
			entries.add(entry);

		}
	    feed.setEntries(entries);

		return feed;	     
	}
	



	public SyndFeed createFeed(final String feedType, final String title, final String path, final String description) {
		final SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(feedType);

		feed.setTitle(title);
		feed.setLink(urlGenerator.getAbsoluteUrl(path));
		feed.setDescription(description);
		return feed;
	}

}
