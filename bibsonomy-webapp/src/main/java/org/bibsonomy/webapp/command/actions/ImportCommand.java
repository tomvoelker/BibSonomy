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
package org.bibsonomy.webapp.command.actions;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.webapp.command.BaseCommand;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mwa
 */
@Getter
@Setter
public class ImportCommand extends BaseCommand {
	
	/** when true, duplicate entries will be overwritten **/
	private boolean overwrite;
	
	/** the import-type describes which kind of import will be used 
	 *  e.g. browser import, Delicious import etc.. **/
	// TODO: introduce enum
	private String importType;
	
	/** 
	 * login credentials for service from which
	 * bookmarks are imported
	 * required for importing resources form a remote service
	 **/
	private String importUsername;
	private String importPassword;
	
	/** the group: private or public **/
	private String group;
	
	private int totalCount;
	
	/** the file to import **/
	private MultipartFile file;
	
	private Map<String, String> newBookmarks;

	private Map<String, String> updatedBookmarks;

	private Map<String, String> nonCreatedBookmarks;
	
	private List<String> storedConcepts;
	
	/** for delicious import only, import bookmarks or bundles? **/
	// TODO: introduce an enum class
	private String importData;

}