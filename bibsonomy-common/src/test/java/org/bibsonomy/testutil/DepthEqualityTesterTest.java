/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.testutil;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests DepthEqualityTester.
 * 
 * @author Jens Illig
 */
public class DepthEqualityTesterTest {
	private TestBean a;
	private TestBean b;

	private static class TestBean {
		private Set<Integer> property;

		/**
		 * @return set
		 */
		public Set<Integer> getProperty() {
			return this.property;
		}

		/**
		 * @param property
		 */
		public void setProperty(Set<Integer> property) {
			this.property = property;
		}
	}

	/**
	 * Initializes the test beans.
	 */
	@Before
	public void initTestBeans() {
		this.a = new TestBean();
		this.a.setProperty(new HashSet<Integer>(5));
		this.a.getProperty().add(10);
		this.a.getProperty().add(20);
		this.b = new TestBean();
		this.b.setProperty(new HashSet<Integer>(3));
		this.b.getProperty().add(10);
		this.b.getProperty().add(20);
	}

	/**
	 * tests an incomplete HashSet
	 */
	@Test
	public void testIncompleteHashSetProperty() {
		this.b.getProperty().remove(20);
		try {
			CommonModelUtils.assertPropertyEquality(this.a, this.b, 2, null);
			Assert.fail();
		} catch (AssertionError ignored) {
		}
	}

	/**
	 * tests additional entries in the HashSet
	 */
	@Test
	public void testAdditionalEntryHashSetProperty() {
		this.b.getProperty().add(30);
		try {
			CommonModelUtils.assertPropertyEquality(this.a, this.b, 2, null);
			Assert.fail();
		} catch (AssertionError ignored) {
		}
	}

	/**
	 * tests equal HashSets
	 */
	@Test
	public void testEqualHashSetProperty() {
		CommonModelUtils.assertPropertyEquality(this.a, this.b, 2, null);
		this.b.getProperty().add(30);
		try {
			CommonModelUtils.assertPropertyEquality(this.a, this.b, 2, null);
			Assert.fail();
		} catch (AssertionError ignored) {
		}
	}
}