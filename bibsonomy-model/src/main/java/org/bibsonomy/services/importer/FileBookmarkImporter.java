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
package org.bibsonomy.services.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * Allows to import lists of bookmarks from a file. 
 * 
 * @author rja
 */
public interface FileBookmarkImporter {

	/**
	 * initializes the file which contains the bookmarks, the current user and his group.
	 * 
	 * @param file 
	 * @param user 
	 * @param groupName 
	 * @throws IOException - if the file could not be opened/read. 
	 * 
	 */
	public void initialize(File file, User user, String groupName) throws IOException;
	
	/**
	 * Returns the bookmarks extracted from the given file (see {@link #initialize(File, User, String)}). 
	 * 
	 * @return A list of bookmark posts, extracted from the given file.
	 */
	public List<Post<Bookmark>> getPosts();
	
}
