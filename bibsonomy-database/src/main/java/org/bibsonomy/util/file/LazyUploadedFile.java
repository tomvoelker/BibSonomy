/**
 *
 *  BibSonomy-Web-Common - Common things for web
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

package org.bibsonomy.util.file;

import java.io.File;
import java.io.IOException;

import org.bibsonomy.model.util.file.FilePurpose;
import org.bibsonomy.model.util.file.FileSystemFile;
import org.bibsonomy.model.util.file.UploadedFile;


/**
 * Implementation of {@link UploadedFile} with on-demand load of file.
 * 
 * @author cunis
 * @version $Id:$
 */
public abstract class LazyUploadedFile implements UploadedFile
{
	/* the file if once requested */
	private FileSystemFile _file;
	
	/**
	 * Creates a new LazyUploadedFile.
	 */
	public LazyUploadedFile ()
	{
		_file = null;
	}
	
	/**
	 * Returns the uploaded file.
	 * @return the file as File
	 */
	protected FileSystemFile getFile ()
	{
		if ( _file == null )
		{
			File file = requestFile();
			_file = new FileSystemFile( file, file.getName() );
		}
			
		//anyway:
		return _file;
	}
	
	/**
	 * Returns the uploaded file.<br/>
	 * This method is called one time ever if needed. 
	 * @return the file as File
	 */
	protected abstract File requestFile ();
	
	@Override
	public String getFileName() {
		FileSystemFile file = getFile();
		return file.getFileName();
	}
	
	@Override
	public String getAbsolutePath ()
	{
		FileSystemFile file = getFile();
		return file.getAbsolutePath();
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		FileSystemFile file = getFile();
		return file.getBytes();
	}
	
	@Override
	public void transferTo(File fileInFileSytem) throws Exception {
		FileSystemFile file = getFile();
		file.transferTo( fileInFileSytem );
	}
	
	@Override
	public FilePurpose getPurpose ()
	{
		return FilePurpose.DOWNLOAD;
	}
}
