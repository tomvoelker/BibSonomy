package org.bibsonomy.database.managers.chain.bibtex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForGroupAndTag;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * @author Robert Jäschke
 * @version $Id$
 */
public class GetBibtexForGroupAndTagTest extends AbstractDatabaseManagerTest {

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

		GetResourcesForGroupAndTag<BibTex, BibTexParam> handler = new GetResourcesForGroupAndTag<BibTex, BibTexParam>();
		
		handler.perform(p, dbSession);
	}
}