package org.bibsonomy.model.util.file;

import java.io.File;
import java.io.IOException;

/**
 * @author dzo
 * @version $Id$
 */
public interface UploadedFile {

	/**
	 * @return the name of the file
	 */
	public String getFileName();
	
	/**
	 * @return the content as byte array
	 * @throws IOException 
	 */
	public byte[] getBytes() throws IOException;
	
	/**
	 * transfers the file (e. g. in memory to the file system)
	 * @param fileInFileSytem
	 * @throws Exception 
	 */
	public void transferTo(File fileInFileSytem) throws Exception;

}
