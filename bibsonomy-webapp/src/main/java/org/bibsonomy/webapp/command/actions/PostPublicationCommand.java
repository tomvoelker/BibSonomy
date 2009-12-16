package org.bibsonomy.webapp.command.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.TabCommand;
import org.bibsonomy.webapp.command.TabsCommandInterface;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author ema
 * @version $Id$
 */


/**
 * This command takes a the information for displaying the import publication views. 
 * The publications will be entered as a file or string containing the information. 
 *
 */
public class PostPublicationCommand extends EditPublicationCommand implements TabsCommandInterface<Object> {
	
	/********************************************
	 * FROM WHERE WAS I CALLED - WHAT TASK TO DO
	 ********************************************/
	public final static String TASK_ENTER_PUBLICATIONS = "ENTER_PUBLICATIONS";
	public final static String TASK_EDIT_PUBLICATIONS 	= "EDIT_PUBLICATIONS";
	
	/**
	 * URL of the tabheader-anchor-links
	 */
	private String tabURL=null;
	
	public String getTabURL() {
		return this.tabURL;
	}

	public void setTabURL(String tabURL) {
		this.tabURL = tabURL;
	}
	
	/**
	 * Containing the task name, determining whats to do in the controller.
	 */
	private String taskName;
	
	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	
	/**
	 * The URL which the tab header links to.
	 */
	private final String TAB_URL = "/import/publications"; 
	
	/**
	 * Detetermines, if we call import/publications a second time due to errors, the user can fix
	 */
	private boolean extendedView = false;
	
	
	public boolean isExtendedView() {
		return this.extendedView;
	}

	public void setExtendedView(boolean extendedView) {
		this.extendedView = extendedView;
	}

	/**
	 * The errors during parsing
	 */
	private List<Integer> erroneousLineNumbers = null;
	
	public List<Integer> getErroneousLineNumbers() {
		return this.erroneousLineNumbers;
	}

	public void setErroneousLineNumbers(List<Integer> erroneousLineNumbers) {
		this.erroneousLineNumbers = erroneousLineNumbers;
	}
	
	
	
	/****************************
	 * FOR THE TAB FUNCTIONALITY
	 ****************************/
	
	/**
	 * TAB HEADER LOCALIZED
	 */
	private final static String[] tabTitles = {
		"post_bibtex.manual.title", 
		"post_bibtex.pub_snippet.title", 
		"post_bibtex.bibtex_endnote.title", 
		"post_bibtex.doi_isbn.title"
	};
	
	
	/**
	 *  id of current selected tab 
	 */
	protected Integer selTab = null;
	
	
	private void addTab(Integer id, String title) {
		tabs.add(new TabCommand(id, title));		
	}

	private void addTabs(final String[] titles) {
		for(int i=0; i<titles.length; i++) {
			addTab(i, titles[i]);
		}
	}

	/**
	 * dont know what this is for really...
	 */
	private List<TabCommand> tabs;
	
	@Override
	public Integer getSelTab() {
		return selTab;
	}
	
	@Override
	public void setSelTab(Integer selectedTab) {
		this.selTab = selectedTab;
	}

	@Override
	public List<TabCommand> getTabs() {
		return tabs;
	}

	@Override
	public void setTabs(List<TabCommand> tabs) {
		this.tabs = tabs;
	}
	
	
	
	/****************************
	 * FOR ALL IMPORTS
	 ****************************/
	
	/**
	 * the description of the snippet/upload file
	 */
	private String description;
	
	
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	/****************************
	 * SPECIAL FOR FILE UPLOAD
	 ****************************/
	/**
	 * option that, if true, affects the storing process of correctly parsed publications,
	 * even if there were other errors detected within the file.
	 */
	private boolean writeAllCorrectOnes;
	
	public boolean getWriteAllCorrectOnes() {
		return this.writeAllCorrectOnes;
	}
	
	public boolean isWriteAllCorrectOnes() {
		return this.writeAllCorrectOnes;
	}

	public void setWriteAllCorrectOnes(boolean writeAllCorrectOnes) {
		this.writeAllCorrectOnes = writeAllCorrectOnes;
	}

	/**
	 * the BibTeX/Endnote file
	 */
	private CommonsMultipartFile file;
	
	
	public CommonsMultipartFile getFile() {
		return this.file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}
	
	/**
	 * The whitespace substitute
	 */
	private String whitespace;
	
	public String getWhitespace() {
		return this.whitespace;
	}

	public void setWhitespace(String whitespace) {
		this.whitespace = whitespace;
	}

	/**
	 * encoding of the file
	 */
	private String encoding;
	
	public String getEncoding() {
		return this.encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * the delimiter
	 */
	private String delimiter;

	public String getDelimiter() {
		return this.delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
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
	
	/**
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
	
	
	
	/***********************************************
	 * FOR IMPORTS CONTAINING MULTIPLE PUBLICATIONS
	 ***********************************************/
	/**
	 * The action that will be started, when hitting the submission button on the edit page
	 */
	private String formAction;
	
	public String getFormAction() {
		return this.formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}
	

	/**
	 * The posts, that were updated during import.
	 */
	private HashMap<String,String> updatedPosts=null;
	
	public HashMap<String,String> getUpdatedPosts() {
		return this.updatedPosts;
	}

	public void setUpdatedPosts(HashMap<String, String> updatedBookmarkEntries) {
		this.updatedPosts = updatedBookmarkEntries;
	}

	/**
	 * The posts, that were ignored during import. (duplicates)
	 */
	private List<String> ignoredPosts;
	
	public List<String> getIgnoredPosts() {
		return this.ignoredPosts;
	}

	public void setIgnoredPosts(List<String> ignoredPosts) {
		this.ignoredPosts = ignoredPosts;
	}

	/**
	 * For multiple posts
	 */
	private ListCommand<Post<BibTex>> bibtex = new ListCommand<Post<BibTex>>(this);

	public ListCommand<Post<BibTex>> getBibtex() {
		return this.bibtex;
	}

	public void setBibtex(ListCommand<Post<BibTex>> bibtex) {
		this.bibtex = bibtex;
	}

	
	@Override
	public List<Object> getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContent(List<Object> content) {
		// TODO Auto-generated method stub
	}

	

	public PostPublicationCommand(){
		tabs = new ArrayList<TabCommand>();
		// Preparation for all tabs
		//=== make the tabtitle available
		addTabs(tabTitles);

		//=== change default tab to the manual tab
		
		if(!ValidationUtils.present(selTab))
			selTab = 0;
		
		this.setTabURL(TAB_URL);
		erroneousLineNumbers = new ArrayList<Integer>();
			
	}

	
}
