package org.bibsonomy.model.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @author dzo
 * @version $Id$
 */
public class FileSystemFile implements UploadedFile {
	
	private final File file;
	
	/**
	 * @param file the file 
	 */
	public FileSystemFile(final File file) {
		this.file = file;
	}
	
	@Override
	public String getFileName() {
		return file.getName();
	}

	@Override
	public byte[] getBytes() throws IOException {
		final RandomAccessFile f = new RandomAccessFile(file.getAbsoluteFile().getAbsolutePath(), "r");
		byte[] b = new byte[(int)f.length()];
		f.read(b);
		f.close();
		return b;
	}

	@Override
	public void transferTo(File fileInFileSytem) throws Exception {
		final FileInputStream fileInputStream = new FileInputStream(this.file);
		final FileOutputStream fileOutputStream = new FileOutputStream(fileInFileSytem);
		final FileChannel src = fileInputStream.getChannel();
		final FileChannel dest = fileOutputStream.getChannel();
		dest.transferFrom(src, 0, src.size());
		fileInputStream.close();
		fileOutputStream.close();
	}

}
