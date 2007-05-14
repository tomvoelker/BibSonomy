package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.junit.Test;

public class SimHashTest {

	@Test
	public void getSimHash() {
		final BibTex bibTex = new BibTex();		
		assertEquals("a127fd1f86e4ab650f2216f09992afa4", SimHash.getSimHash(bibTex, HashID.getSimHash(0)));
		assertEquals("23b58def11b45727d3351702515f86af", SimHash.getSimHash(bibTex, HashID.getSimHash(1)));
		assertEquals("7bb0edd98f22430a03b67f853e83c2ca", SimHash.getSimHash(bibTex, HashID.getSimHash(2)));
		assertEquals("", SimHash.getSimHash(bibTex, HashID.getSimHash(3)));
	}
}