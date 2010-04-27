/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class ObjectUtilsTest {
	
	/**
	 * tests {@link ObjectUtils#copyPropertyValues(Object, Object)}
	 */
	@Test
	public void copyPropertyValuesTest() {
		final TestObject o = new TestObject();
		o.setValue("test copy value");
		
		final TestObject target = new TestObject();
		ObjectUtils.copyPropertyValues(o, target);
		
		assertEquals("test copy value", target.getValue());
	}
	
	/**
	 * tests {@link ObjectUtils#copyPropertyValues(Object, Object)}
	 * one property hasn't a setter
	 */
	@Test
	public void copyPropertyValuesTest2() {
		final TestObject o = new TestObject2("test value");
		o.setValue("test value 1");
		
		final TestObject o2 = new TestObject();
		ObjectUtils.copyPropertyValues(o, o2);
		
		assertEquals("test value 1", o2.getValue());
	}
	
	/**
	 * tests {@link ObjectUtils#copyPropertyValues(Object, Object)}
	 * copy properties from A to B where A is a subtype of B (with more properties)
	 */
	@Test
	public void copyPropertyValuesTest3() {
		final TestObject3 o = new TestObject3();
		o.setValue("test value 1");
		o.setValue2("test value 2");
		
		final TestObject o2 = new TestObject();
		ObjectUtils.copyPropertyValues(o, o2);
		
		assertEquals("test value 1", o2.getValue());
	}
	
	private static class TestObject {
		private String value;

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
	}
	
	private static final class TestObject2 extends TestObject {
		private final String value2;
		
		protected TestObject2(String value2) {
			this.value2 = value2;
		}

		/**
		 * @return the value2
		 */
		@SuppressWarnings("unused") // only for java lang reflect
		public String getValue2() {
			return value2;
		}
	}
	
	private static final class TestObject3 extends TestObject {
		private String value2;

		/**
		 * @param value2 the value2 to set
		 */
		public void setValue2(String value2) {
			this.value2 = value2;
		}

		/**
		 * @return the value2
		 */
		@SuppressWarnings("unused") // only for java lang reflect
		public String getValue2() {
			return value2;
		}
	}
}
