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
 * @version $Id$
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