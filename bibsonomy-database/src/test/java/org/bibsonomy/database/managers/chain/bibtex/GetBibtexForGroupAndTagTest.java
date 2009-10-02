package org.bibsonomy.database.managers.chain.bibtex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroupAndTag;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * @author Robert Jäschke
 * @version $Id$
 */
public class GetBibtexForGroupAndTagTest extends AbstractDatabaseManagerTest {

//	static {
//		final Properties p = new Properties();
//		p.setProperty("log4j.rootLogger", "DEBUG, A1");
//
//		p.setProperty("log4j.appender.A1", "org.apache.log4j.RollingFileAppender");
//		p.setProperty("log4j.appender.A1.File", "/tmp/LOG.log");
//		// p.setProperty("log4j.appender.A1",
//		// "org.apache.log4j.ConsoleAppender");
//		p.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
//
//		// Print the date in ISO 8601 format
//		p.setProperty("log4j.appender.A1.layout.ConversionPattern", "%d %-5p %c - %m%n");
//
//		// Print only messages of level WARN or above in the package com.foo.
//		p.setProperty("log4j.logger.org.bibsonomy.database.managers.chain.bib", "DEBUG, A1");
//		p.setProperty("log4j.logger.java.sql.Connection", "DEBUG, A1");
//		p.setProperty("log4j.logger.java.sql.Statement", "DEBUG, A1");
//		p.setProperty("log4j.logger.java.sql.PreparedStatement", "DEBUG, A1");
//
//		PropertyConfigurator.configure(p);
//	}

	/**
	 * tests getBibtexForGroupAndTag
	 */
	@Test
	public void getBibtexForGroupAndTag() {
		BibTexParam p = new BibTexParam();

		final Set<Tag> tags = new HashSet<Tag>();
		final List<TagIndex> tagIndex = new LinkedList<TagIndex>();

		/*
		 * change number of requested tags here
		 */
		final int NUMBER_OF_TAGS = 15;

		for (int i = 0; i < NUMBER_OF_TAGS; i++) {
			tags.add(new Tag("a" + i));
			tagIndex.add(new TagIndex("a" + i, i + 1));
		}
		p.setTags(tags);
		p.setTagIndex(tagIndex);

		p.setGrouping(GroupingEntity.GROUP);
		p.setRequestedGroupName("kde");
		p.setRequestedUserName(null);
		p.setHash(null);
		p.setOrder(null);
		p.setSearch("");
		p.setNumSimpleConcepts(0);
		p.setNumSimpleTags(NUMBER_OF_TAGS);
		p.setNumTransitiveConcepts(0);
		p.addGroup(GroupID.PUBLIC.getId());

		GetBibtexForGroupAndTag handler = new GetBibtexForGroupAndTag();

		@SuppressWarnings("unused")
		final List<Post<BibTex>> posts = handler.perform(p, dbSession);
	}
}