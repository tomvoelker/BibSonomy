package org.bibsonomy.model.util;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.BibTex;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SimHashTest {

	@Test
	public void getSimHash() {
		final BibTex bibTex = new BibTex();		
		assertEquals("a127fd1f86e4ab650f2216f09992afa4", SimHash.getSimHash(bibTex, ConstantID.getSimHash(0)));
		assertEquals("23b58def11b45727d3351702515f86af", SimHash.getSimHash(bibTex, ConstantID.getSimHash(1)));
		assertEquals("7bb0edd98f22430a03b67f853e83c2ca", SimHash.getSimHash(bibTex, ConstantID.getSimHash(2)));
		assertEquals("", SimHash.getSimHash(bibTex, ConstantID.getSimHash(3)));
	}
}