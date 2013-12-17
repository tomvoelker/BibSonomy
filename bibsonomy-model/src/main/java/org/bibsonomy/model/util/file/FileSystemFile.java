/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @author dzo
  */
public class FileSystemFile implements UploadedFile {
	
	private final File file;
	private final String fileName;
	
	/**
	 * @param file the file 
	 * @param fileName the fileName
	 */
	public FileSystemFile(final File file, final String fileName) {
		this.file = file;
		this.fileName = fileName;
	}
	
	@Override
	public String getFileName() {
		return this.fileName;
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
