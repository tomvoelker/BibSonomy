/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.util.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bibsonomy.model.enums.ImportFormat;

/**
 * @author jensi
 */
public class FileData implements Data {
	private final File file;
	private final String mimeType;
	
	/**
	 * @param file
	 * @param format
	 */
	public FileData(File file, ImportFormat format) {
		this(file, format.getMimeType());
	}
	
	/**
	 * @param file
	 * @param mimeType
	 */
	public FileData(File file, String mimeType) {
		this.file = file;
		this.mimeType = mimeType;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getInputStream() {
		return getInputStream(file);
	}
	
	protected InputStream getInputStream(File file) {
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public Reader getReader() {
		return new InputStreamReader(getInputStream());
	}
}
