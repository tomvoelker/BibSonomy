package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;

import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
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
