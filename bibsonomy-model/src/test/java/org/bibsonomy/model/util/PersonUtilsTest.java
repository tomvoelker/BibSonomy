/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.extra.AdditionalKey;
import org.junit.Test;

/**
 * Test class for PersonUtils
 *
 * @author kchoong
 */
public class PersonUtilsTest {

    @Test
    public void testGetAdditionalKey() {
        // Create test person
        Person person = new Person();
        String key1Name = "testId";
        String key1Value = "123456";
        List<AdditionalKey> additionalKeys = person.getAdditionalKeys();
        additionalKeys.add(new AdditionalKey(key1Name, key1Value));
        person.setAdditionalKeys(additionalKeys);

        AdditionalKey testKey = PersonUtils.getAdditionalKey(person, key1Name);
        assertNotNull(testKey);
        assertEquals(key1Value, testKey.getKeyValue());
    }

    @Test
    public void testAddAdditionalKey() {
        // Create test person
        Person person = new Person();

        // add as AdditionalKey
        String key1Name = "testId";
        String key1Value = "123456";
        AdditionalKey newKey = new AdditionalKey(key1Name, key1Value);
        assertTrue(PersonUtils.addAdditionalKey(person, newKey));
        assertNotNull(PersonUtils.getAdditionalKey(person, key1Name));

        // add as name value pair
        String key2Name = "differentId";
        String key2Value = "654321";
        assertTrue(PersonUtils.addAdditionalKey(person, key2Name, key2Value));
        assertNotNull(PersonUtils.getAdditionalKey(person, key2Name));

        // attempt to insert exisiting key
        assertFalse(PersonUtils.addAdditionalKey(person, key2Name, "abcdef"));
    }

    @Test
    public void testRemoveAdditionalKey() {
        // Create test person
        Person person = new Person();
        String key1Name = "testId";
        String key1Value = "123456";
        List<AdditionalKey> additionalKeys = person.getAdditionalKeys();
        additionalKeys.add(new AdditionalKey(key1Name, key1Value));
        person.setAdditionalKeys(additionalKeys);

        // remove existing key
        assertTrue(PersonUtils.removeAdditionalKey(person, key1Name));
        assertNull(PersonUtils.getAdditionalKey(person, key1Name));

        // attempt to remove non-existing key
        assertFalse(PersonUtils.removeAdditionalKey(person, "randomId"));
    }

    @Test
    public void testUpdateAdditionalKey() {
        // Create test person
        Person person = new Person();
        String key1Name = "testId";
        String key1Value = "123456";
        List<AdditionalKey> additionalKeys = person.getAdditionalKeys();
        additionalKeys.add(new AdditionalKey(key1Name, key1Value));
        person.setAdditionalKeys(additionalKeys);

        // update existing key
        String key1NewValue = "654321";
        assertTrue(PersonUtils.updateAdditionalKey(person, key1Name, key1NewValue));

        AdditionalKey testKey = PersonUtils.getAdditionalKey(person, key1Name);
        assertNotNull(testKey);
        assertNotEquals(key1Value, testKey.getKeyValue());
        assertEquals(key1NewValue, testKey.getKeyValue());
    }
}
