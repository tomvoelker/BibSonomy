package org.bibsonomy.util.file;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class FileUtilTest {
	
	/**
	 * tests getFilePath
	 */
	@Test
	public void getFilePath() {
		if (new File("/tmp/").exists() == false) {
			System.err.println("test skipped");
			return;
		}
		Assert.assertEquals("/tmp/ab/abcde" , FileUtil.getFilePath("/tmp", "abcde"));
		Assert.assertEquals("/tmp/ab/abcde" , FileUtil.getFilePath("/tmp/", "abcde"));
	}
}
