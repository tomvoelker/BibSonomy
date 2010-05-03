package org.bibsonomy.email;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class CodeUtilsTest {

	@Test
	public void test1() throws Exception {
		final String foo = "foofoofoo";
		System.out.println(foo);
		System.out.println(CodeUtils.convertToBase64(foo));
		assertEquals("Zm9vZm9vZm9v", CodeUtils.convertToBase64(foo));
		
		System.out.println("---------------------");
		
		final String hex = "969abea3f0cf579697e231e9a27dbf16";
		final byte[] bytes = CodeUtils.convertToByte(hex);
		System.out.println(hex);
		System.out.println(bytes);
		System.out.println(CodeUtils.convertToBase64(bytes));

	}
	
}
