package org.bibsonomy.testutil;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @version $Id$
 * @author  Jens Illig
 * $Author$
 *
 */
public class DepthEqualityTesterTest {
	private TestBean a;
	private TestBean b;
	
	private static class TestBean {
		private Set<Integer> property;

		public Set<Integer> getProperty() {
			return this.property;
		}

		public void setProperty(Set<Integer> property) {
			this.property = property;
		} 
	}
	
	private void initTestBeans() {
		a = new TestBean();
		a.setProperty(new HashSet<Integer>(5));
		a.getProperty().add(10);
		a.getProperty().add(20);
		b = new TestBean();
		b.setProperty(new HashSet<Integer>(3));
		b.getProperty().add(10);
		b.getProperty().add(20);
	}
	
	@Test
	public void testIncompleteHashSetProperty() {
		initTestBeans();
		b.getProperty().remove(20);
		try {
			ModelUtils.assertPropertyEquality(a, b, 2, null);
			Assert.fail();
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testAdditionalEntryHashSetProperty() {
		initTestBeans();
		b.getProperty().add(30);
		try {
			ModelUtils.assertPropertyEquality(a, b, 2, null);
			Assert.fail();
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testEqualHashSetProperty() {
		initTestBeans();
		
		ModelUtils.assertPropertyEquality(a, b, 2, null);
		b.getProperty().add(30);
		try {
			ModelUtils.assertPropertyEquality(a, b, 2, null);
			Assert.fail();
		} catch (AssertionError e) {
		}
	}
}
