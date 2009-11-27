package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.ListCommand;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author ema
 * @version $Id$
 */


/**
 * This command takes a file or string containing publication information.
 *
 */
public class PostMultiplePublicationCommand extends EditPublicationCommand /*implements*/ {
	
	
	/*
	 * The action that will be started, when hitting the submission button on the edit page
	 */
	private String formAction;
	
	public String getFormAction() {
		return this.formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	/*
	 * Determines, if the bookmarks will be saved before being edited or afterwards
	 */
	private boolean editBeforeImport;
	
	public boolean getEditBeforeImport() {
		return this.editBeforeImport;
	}
	
	public boolean isEditBeforeImport() {
		return this.editBeforeImport;
	}

	public void setEditBeforeImport(boolean editBeforeImport) {
		this.editBeforeImport = editBeforeImport;
	}

	/*
	 * Determines, if the already existing publications will be overwritten by the new ones.
	 */
	private boolean overwrite;

	public boolean getOverwrite() {
		return this.overwrite;
	}
	
	public boolean isOverwrite() {
		return this.overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/*
	 * For multiple posts
	 */
	private ListCommand<Post<BibTex>> bibtex = new ListCommand<Post<BibTex>>(this);

	public ListCommand<Post<BibTex>> getBibtex() {
		return this.bibtex;
	}

	public void setBibtex(ListCommand<Post<BibTex>> bibtex) {
		this.bibtex = bibtex;
	}

	/**
	 * the BibTeX file
	 */
	private CommonsMultipartFile file;
	
	/**
	 * @return the file containing the BibTeX entries.
	 */
	public CommonsMultipartFile getFile() {
		return this.file;
	}

	/**
	 * Sets the file containing the BibTeX entries.
	 * 
	 * @param file
	 */
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	/**
	 * the description of the snippet/upload file
	 */
	private String description;
	
	
	/**
	 * @return the string describing the BibTeX snippet.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets the string describing the BibTeX snippet to import.
	 * 
	 * @param description
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	
}
