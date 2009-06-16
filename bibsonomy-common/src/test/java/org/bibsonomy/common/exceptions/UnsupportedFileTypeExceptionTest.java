package org.bibsonomy.common.exceptions;

import static org.junit.Assert.*;

import org.bibsonomy.util.file.HandleFileUpload;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class UnsupportedFileTypeExceptionTest {

	@Test
	public void testUnsupportedFileTypeException() {
		
		try {
			throw new UnsupportedFileTypeException(HandleFileUpload.fileUploadExt);
		} catch (UnsupportedFileTypeException e) {
			assertEquals("Please check your file. Only PDF, PS, DJV, DJVU, or TXT files are accepted.", e.getMessage());
		}
		
		try {
			throw new UnsupportedFileTypeException(HandleFileUpload.firfoxImportExt);
		} catch (UnsupportedFileTypeException e) {
			assertEquals("Please check your file. Only HTML files are accepted.", e.getMessage());
		}
		
	}

}
