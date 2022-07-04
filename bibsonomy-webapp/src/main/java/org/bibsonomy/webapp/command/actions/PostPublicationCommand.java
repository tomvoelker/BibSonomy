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
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.BibtexViewCommand;
import org.bibsonomy.webapp.command.ErrorInfo;
import org.bibsonomy.webapp.command.LayoutViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.springframework.web.multipart.MultipartFile;

/**
 * This command takes a the information for displaying the import publication views. 
 * The publications will be entered as a file or string containing the information.
 * 
 * @author ema
 */
@Getter
@Setter
public class PostPublicationCommand extends EditPublicationCommand implements BibtexViewCommand, LayoutViewCommand {

	/**
	 * stores if the user wants to overwrite existing posts 
	 */
	private boolean overwrite;
	
	/**
	 * the description of the snippet/upload file
	 */
	private String description;

	/**
	 * each intrahash(post) is maped to a list of errors. Erroneous posts cannot be edited later*/
	private Map<String, List<ErrorMessage>> postsErrorList;
	
	/**
	 * list to group the posts by their error messages */
	private List<ErrorInfo> groupedErrorList;
	
	/**
	 * For multiple posts
	 */
	private ListCommand<Post<BibTex>> posts = new ListCommand<>(this);

	/****************************
	 * FOR ALL IMPORTS
	 ****************************/
	/**
	 * this flag determines, whether an existing post is being edited or a new post
	 * should be added and edited**/
	private boolean updateExistingPost;

	/****************************
	 * SPECIAL FOR FILE UPLOAD
	 ****************************/

	/**
	 * the BibTeX/Endnote file
	 */
	private MultipartFile file;

	/**
	 * The whitespace substitute
	 */
	private String whitespace;

	/**
	 * encoding of the file
	 */
	private String encoding;

	/**
	 * the delimiter
	 */
	private String delimiter;

	/**
	 * Determines, if the bookmarks will be saved before being edited or afterwards
	 */
	private boolean editBeforeImport;

	/**
	 * The posts, that were updated during import.
	 */
	private Map<String,String> updatedPosts;

	/****************************
	 * FOR EXTERNAL IMPORT
	 ****************************/
	private String externalId;

	private List<String> workIds;

	private String bulkSnippet;

	/**
	 * constructor
	 * inits the tabs and sets their titles
	 */
	public PostPublicationCommand() {
		/*
		 * defaults:
		 */
		this.whitespace = "_";
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return The list of publication posts.
	 */
	@Override
	public ListCommand<Post<BibTex>> getBibtex() {
		return this.posts;
	}

	/**
	 * @param bibtex
	 */
	public void setBibtex(final ListCommand<Post<BibTex>> bibtex) {
		this.posts = bibtex;
	}
	
}
