/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.file.temp;

import java.io.File;

import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.TempFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.util.file.AbstractServerFileLogic;

/**
 * @author dzo
 */
public class ServerTempFileLogic extends AbstractServerFileLogic implements TempFileLogic {
	
	/**
	 * @param path
	 */
	public ServerTempFileLogic(String path) {
		super(path);
	}
	
	@Override
	public File getTempFile(String name) {
		final File file = new File(this.getFilePath(name));
		file.setReadOnly();
		return file;
	}
	
	@Override
	public File writeTempFile(UploadedFile file, ExtensionChecker extensionChecker) throws Exception {
		return this.writeFile(file, extensionChecker);
	}

	@Override
	protected String getFilePath(String fileHash) {
		return this.path + "/" + fileHash;
	}

	@Override
	public void deleteTempFile(String name) {
		new File(getFilePath(name)).delete();
	}
}
