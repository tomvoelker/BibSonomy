package org.bibsonomy.webapp.command.actions;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.TabCommand;
import org.bibsonomy.webapp.command.TabsCommandInterface;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author ema
 * @version $Id$
 */


/**
 * This command takes a file or string containing publication information.
 *
 */
public class PostPublicationCommand extends EditPublicationCommand implements TabsCommandInterface<Object> {
	
	protected Integer selTab = 1;	

	/** list of defined tabs */
	protected List<TabCommand> tabs = new ArrayList<TabCommand>();
	
	private final static String[] tabTitles = {
		"post_bibtex.manual.title", 
		"post_bibtex.pub_snippet.title", 
		"post_bibtex.bibtex_endnote.title", 
		"post_bibtex.doi_isbn.title"
	};

	public PostPublicationCommand(){
		
		// Preparation for all tabs
		//=== make the tabtitle available
		addTabs(tabTitles);

		//=== change default tab to the manual tab
		selTab = 0;
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

	
	private void addTab(Integer id, String title) {
		tabs.add(new TabCommand(id, title));		
	}

	private void addTabs(final String[] titles) {
		for(int i=0; i<titles.length; i++) {
			addTab(i, titles[i]);
		}
	}

	@Override
	public List<Object> getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getSelTab() {
		return selTab;
	}

	@Override
	public List<TabCommand> getTabs() {
		return tabs;
	}

	@Override
	public void setContent(List<Object> content) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSelTab(Integer selectedTab) {
		this.selTab = selectedTab;
	}

	@Override
	public void setTabs(List<TabCommand> tabs) {
		this.tabs = tabs;
	}

	
}
