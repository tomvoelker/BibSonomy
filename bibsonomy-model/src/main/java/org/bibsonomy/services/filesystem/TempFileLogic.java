/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.services.filesystem;

import java.io.File;

import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;

/**
 * @author dzo
 */
public interface TempFileLogic {
	
	/**
	 * @param name
	 * @return the file with the specified name (read only)
	 */
	public File getTempFile(final String name);
	
	/**
	 * write file to tmp directory
	 * @param file
	 * @param extensionChecker
	 * @return the file written (read only)
	 * @throws Exception TODO
	 */
	public File writeTempFile(final UploadedFile file, ExtensionChecker extensionChecker) throws Exception;
	
	/**
	 * the tmp file to delete
	 * @param name
	 */
	public void deleteTempFile(final String name);
}
