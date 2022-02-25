/**
 * BibSonomy-Model - Java- and JAXB-Model.
 * <p>
 * Copyright (C) 2006 - 2021 Data Science Chair,
 * University of Würzburg, Germany
 * https://www.informatik.uni-wuerzburg.de/datascience/home/
 * Information Processing and Analytics Group,
 * Humboldt-Universität zu Berlin, Germany
 * https://www.ibi.hu-berlin.de/en/research/Information-processing/
 * Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * https://www.kde.cs.uni-kassel.de/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * https://www.l3s.de/
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * @author Christian Schenk
 */
public class GroupUtilsTest {

    /**
     * tests the groups
     */
    @Test
    public void getGroup() {
        for (final Group group : new Group[]{GroupUtils.buildPublicGroup(), GroupUtils.buildPrivateGroup(), GroupUtils.buildFriendsGroup(), GroupUtils.buildInvalidGroup()}) {
            assertNotNull(group);
            assertTrue(present(group.getName()));
            assertTrue(present(group.getDescription()));
            assertTrue(present(group.getGroupId()));
            assertTrue(present(group.getPrivlevel()));
        }

        final Group g1 = GroupUtils.buildPublicGroup();
        final Group g2 = GroupUtils.buildPublicGroup();
        // equals should be enough before: assertSame(g1, g2);
        assertEquals(g1, g2);
    }

    /**
     * tests {@link GroupUtils#isExclusiveGroup(Group)}
     */
    @Test
    public void testIsExclusiveGroupGroup() {
        assertTrue(GroupUtils.isExclusiveGroup(GroupUtils.buildPrivateGroup()));
        assertTrue(GroupUtils.isExclusiveGroup(GroupUtils.buildPrivateSpamGroup()));
        assertTrue(GroupUtils.isExclusiveGroup(GroupUtils.buildPublicGroup()));
        assertTrue(GroupUtils.isExclusiveGroup(GroupUtils.buildPublicSpamGroup()));
        assertFalse(GroupUtils.isExclusiveGroup(GroupUtils.buildFriendsGroup()));
        assertFalse(GroupUtils.isExclusiveGroup(GroupUtils.buildFriendsSpamGroup()));
    }

    /**
     * tests {@link GroupUtils#isExclusiveGroup(int)}
     */
    @Test
    public void testIsExclusiveGroupGroupId() {
        assertTrue(GroupUtils.isExclusiveGroup(GroupID.PRIVATE.getId()));
        assertTrue(GroupUtils.isExclusiveGroup(GroupID.PRIVATE_SPAM.getId()));
        assertTrue(GroupUtils.isExclusiveGroup(GroupID.PUBLIC.getId()));
        assertTrue(GroupUtils.isExclusiveGroup(GroupID.PUBLIC_SPAM.getId()));
        assertFalse(GroupUtils.isExclusiveGroup(GroupID.FRIENDS.getId()));
        assertFalse(GroupUtils.isExclusiveGroup(GroupID.FRIENDS_SPAM.getId()));
    }

    @Test
    public void testAddPresetTag() {
        Group group = new Group();
        String tagName = "tagtagtag";
        List<Tag> groupPresetTags = new ArrayList<>(Collections.singletonList(new Tag(tagName)));
        group.setPresetTags(groupPresetTags);

        // case tag doesn't exist yet
        assertTrue(GroupUtils.addOrUpdatePresetTag(group, "newTag1", null));
        assertTrue(GroupUtils.addOrUpdatePresetTag(group, "newTag2", null));

        // case tag exists already
        assertTrue(GroupUtils.addOrUpdatePresetTag(group, "newTag1", null));
        String updatedDescription = "updated tag description";
        assertTrue(GroupUtils.addOrUpdatePresetTag(group, "tagtagtag", updatedDescription));

        // check size of group's preset tags
        assertEquals(3, group.getPresetTags().size());

        Tag testTag = group.getPresetTags()
                .stream().filter(x -> x.getName().equals(tagName))
                .findFirst()
                .orElse(new Tag());
        assertEquals(testTag.getName(), tagName);
        assertEquals(testTag.getDescription(), updatedDescription);
    }

    @Test
    public void testDeletePresetTag() {
        Group group = new Group();
        List<Tag> groupPresetTags = new ArrayList<>();
        groupPresetTags.add(new Tag("tag1"));
        groupPresetTags.add(new Tag("tag2"));
        groupPresetTags.add(new Tag("tag3"));
        group.setPresetTags(groupPresetTags);

        // case tag doesn't exist
        assertFalse(GroupUtils.deletePresetTag(group, "tag4"));

        // case tag exists already
        assertTrue(GroupUtils.deletePresetTag(group, "tag1"));
        assertTrue(GroupUtils.deletePresetTag(group, "tag3"));

        // check size of group's preset tags
        assertEquals(1, group.getPresetTags().size());
    }

    @Test
    public void testExtractPresetTagsForGroup() {
        Group group = new Group("testgroup");
        List<Tag> groupPresetTags = new ArrayList<>();
        groupPresetTags.add(new Tag("tag1"));
        groupPresetTags.add(new Tag("tag2"));
        groupPresetTags.add(new Tag("tag3"));
        group.setPresetTags(groupPresetTags);

        Set<Tag> testTags = new HashSet<>();
        testTags.add(new Tag("sys:group:testgroup:tag1"));
        testTags.add(new Tag("nonpreset"));
        testTags.add(new Tag("test"));
        testTags.add(new Tag("sys:group:testgroup:tag3"));

        Set<Tag> extractedTags = GroupUtils.extractPresetTagsForGroup(group, testTags);
        assertEquals(2, extractedTags.size());
        for (Tag exTag : extractedTags) {
            assertFalse(exTag.getName().startsWith("sys:group:"));
            assertTrue(groupPresetTags.contains(exTag));
        }
    }

    @Test
    public void testRemovePresetTags() {
        Set<Tag> testTags = new HashSet<>();
        testTags.add(new Tag("sys:group:testgroup:tag1"));
        testTags.add(new Tag("nonpreset"));
        testTags.add(new Tag("test"));
        testTags.add(new Tag("sys:group:testgroup:tag3"));

        GroupUtils.removePresetTags(testTags);

        assertEquals(2, testTags.size());
        assertTrue(testTags.contains(new Tag("nonpreset")));
        assertTrue(testTags.contains(new Tag("test")));
    }

    @Test
    public void testRemoveNonPresetTags() {
        List<Tag> groupPresetTags = new ArrayList<>();
        groupPresetTags.add(new Tag("tag1"));
        groupPresetTags.add(new Tag("tag2"));
        groupPresetTags.add(new Tag("tag3"));

        Set<Tag> testTags = new HashSet<>();
        testTags.add(new Tag("tag1"));
        testTags.add(new Tag("nonpreset"));
        testTags.add(new Tag("test"));
        testTags.add(new Tag("tag3"));

        GroupUtils.removeNonPresetTags(testTags, groupPresetTags);

        assertEquals(2, testTags.size());
        assertTrue(testTags.contains(new Tag("tag1")));
        assertTrue(testTags.contains(new Tag("tag3")));
    }

    @Test
    public void testRemovePresetTagsForGroup() {
        Group group = new Group("testgroup");
        List<Tag> groupPresetTags = new ArrayList<>();
        groupPresetTags.add(new Tag("tag1"));
        groupPresetTags.add(new Tag("tag2"));
        groupPresetTags.add(new Tag("tag3"));
        group.setPresetTags(groupPresetTags);

        Set<Tag> testTags = new HashSet<>();
        testTags.add(new Tag("sys:group:testgroup:tag1"));
        testTags.add(new Tag("nonpreset"));
        testTags.add(new Tag("test"));
        testTags.add(new Tag("sys:group:testgroup:tag3"));
        testTags.add(new Tag("sys:group:diffgroup:tag3"));
        testTags.add(new Tag("sys:group:diffgroup:anothertag"));

        GroupUtils.removePresetTagsForGroup(group, testTags);

        assertEquals(4, testTags.size());
        for (Tag tag : testTags) {
            assertFalse(tag.getName().startsWith("sys:group:" + group.getName()));
        }
    }
}