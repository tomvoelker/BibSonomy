/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jensi
 */
public class FileUploadData implements Data {
	private final MultipartFile uploadedFile;
	
	/**
	 * construct
	 * @param uploadedFile
	 */
	public FileUploadData(final MultipartFile uploadedFile) {
		ValidationUtils.assertNotNull(uploadedFile);
		this.uploadedFile = uploadedFile;
	}
	
	@Override
	public String getMimeType() {
		return uploadedFile.getContentType();
	}

	@Override
	public InputStream getInputStream() {
		try {
			return uploadedFile.getInputStream();
		} catch (IOException ex) {
			throw new RuntimeException("cannot access uploaded file with name '" + uploadedFile.getName() + "'", ex);
		}
	}

	@Override
	public Reader getReader() {
		return new InputStreamReader(getInputStream(), Charset.forName(StringUtils.CHARSET_UTF_8));
	}
}
