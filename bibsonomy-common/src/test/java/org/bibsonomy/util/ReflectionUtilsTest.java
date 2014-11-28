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
package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;

import org.junit.Test;


/**
 * @author dzo
 */
public class ReflectionUtilsTest {
	
	/**
	 * tests {@link ReflectionUtils#getActualTypeArguments(Class)}
	 */
	@Test
	public void testGetActualTypeArguments() {
		Type[] actualTypeArguments = ReflectionUtils.getActualTypeArguments(Y.class);
		assertEquals(3, actualTypeArguments.length);
		assertEquals(A.class, actualTypeArguments[0]);
		assertEquals(B.class, actualTypeArguments[1]);
		assertEquals(C.class, actualTypeArguments[2]);
		
		actualTypeArguments = ReflectionUtils.getActualTypeArguments(Z.class);
		assertEquals(3, actualTypeArguments.length);
		assertEquals(A.class, actualTypeArguments[0]);
		assertEquals(B.class, actualTypeArguments[1]);
		assertEquals(C.class, actualTypeArguments[2]);
		
		actualTypeArguments = ReflectionUtils.getActualTypeArguments(M.class);
		assertEquals(3, actualTypeArguments.length);
		assertEquals(C.class, actualTypeArguments[0]);
		assertEquals(B.class, actualTypeArguments[1]);
		assertEquals(C.class, actualTypeArguments[2]);
	}
	
	private static class A {}
	
	private static final class B extends A {}
	
	private static final class C extends A {}
	
	private static abstract class X<G extends A, H extends A, I extends A> {
		// only for testing
	}
	
	private class Y extends X<A, B, C> {
		// only for testing
	}
	
	private final class Z extends Y { }
	private final class M extends X<C, B, C> { }
}
