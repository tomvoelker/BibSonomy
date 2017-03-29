/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.BibtexViewCommand;
import org.bibsonomy.webapp.command.LayoutViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.TabCommand;
import org.bibsonomy.webapp.command.TabsCommandInterface;
import org.springframework.web.multipart.MultipartFile;

/**
 * This command takes a the information for displaying the import publication views. 
 * The publications will be entered as a file or string containing the information.
 * 
 * @author ema
 */
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

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the file
	 */
	public MultipartFile getFile() {
		return this.file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
	/**
	 * @return the whitespace
	 */
	public String getWhitespace() {
		return this.whitespace;
	}

	/**
	 * @param whitespace the whitespace to set
	 */
	public void setWhitespace(String whitespace) {
		this.whitespace = whitespace;
	}
	
	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return this.delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	/**
	 * @param editBeforeImport the editBeforeImport to set
	 */
	public void setEditBeforeImport(boolean editBeforeImport) {
		this.editBeforeImport = editBeforeImport;
	}

	/**
	 * @return the editBeforeImport
	 */
	public boolean isEditBeforeImport() {
		return this.editBeforeImport;
	}
	
	/**
	 * @return @see {@link #isEditBeforeImport()}
	 */
	public boolean getEditBeforeImport() {
		return this.editBeforeImport;
	}

	/**
	 * @return the updatedPosts
	 */
	public Map<String, String> getUpdatedPosts() {
		return this.updatedPosts;
	}

	/**
	 * @param updatedPosts the updatedPosts to set
	 */
	public void setUpdatedPosts(Map<String, String> updatedPosts) {
		this.updatedPosts = updatedPosts;
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
	
	/**
	 * @return The list of publication posts.
	 */
	public ListCommand<Post<BibTex>> getPosts() {
		return this.posts;
	}

	/**
	 * @param bibtex
	 */
	public void setPosts(final ListCommand<Post<BibTex>> bibtex) {
		this.posts = bibtex;
	}

	/**
	 * @return the overwrite
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}
	
	/**
	 * @return @see {@link #isOverwrite()}
	 */
	public boolean getOverwrite() {
		return this.overwrite;
	}

	/**
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	/**
	 * @return the updateExistingPost
	 */
	public boolean isUpdateExistingPost() {
		return this.updateExistingPost;
	}

	/**
	 * @param updateExistingPost the updateExistingPost to set
	 */
	public void setUpdateExistingPost(boolean updateExistingPost) {
		this.updateExistingPost = updateExistingPost;
	}

	/**
	 * @return the postsErrorList
	 */
	public Map<String, List<ErrorMessage>> getPostsErrorList() {
		return this.postsErrorList;
	}

	/**
	 * @param postsErrorList the postsErrorList to set
	 */
	public void setPostsErrorList(Map<String, List<ErrorMessage>> postsErrorList) {
		this.postsErrorList = postsErrorList;
	}
	
}
