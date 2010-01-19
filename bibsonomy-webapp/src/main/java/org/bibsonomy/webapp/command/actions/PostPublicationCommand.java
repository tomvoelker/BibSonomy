package org.bibsonomy.webapp.command.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.TabCommand;
import org.bibsonomy.webapp.command.TabsCommandInterface;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * This command takes a the information for displaying the import publication views. 
 * The publications will be entered as a file or string containing the information.
 * 
 * @author ema
 * @version $Id$
 */
public class PostPublicationCommand extends EditPublicationCommand implements TabsCommandInterface<Object> {
	
	/********************************************
	 * FROM WHERE WAS I CALLED - WHAT TASK TO DO
	 ********************************************/
	public final static String TASK_ENTER_PUBLICATIONS = "ENTER_PUBLICATIONS";
	public final static String TASK_EDIT_PUBLICATIONS 	= "EDIT_PUBLICATIONS";
	
	/**
	 * The URL which the tab header links to.
	 */
	private final String TAB_URL = "/import/publications"; // TODODZ: why not static?
	
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
	 *  id of currently selected tab 
	 */
	protected Integer selTab = null;
	
	/**
	 * @return The index of the currently selected tab.
	 */
	@Override
	public Integer getSelTab() {
		return selTab;
	}
	
	/**
	 * @param selectedTab 
	 */
	@Override
	public void setSelTab(final Integer selectedTab) {
		this.selTab = selectedTab;
	}
	
	/**
	 * holds the tabcommands, containing tuples of the number of a tab and the message key
	 * representing the clickable textheader of the corresponding tab. 
	 */
	private List<TabCommand> tabs;
	
	/**
	 * @return the tabcommands (tabs)
	 */
	@Override
	public List<TabCommand> getTabs() {
		return tabs;
	}

	/**
	 * *not used in general*
	 * @param tabs the tabcommands (tabs) 
	 */
	@Override
	public void setTabs(final List<TabCommand> tabs) {
		this.tabs = tabs;
	}
	
	/**
	 * @param id the index of the new tab to add
	 * @param title the message key of the tab to add (clickable text header)
	 */
	private void addTab(final Integer id, final String title) {
		tabs.add(new TabCommand(id, title));		
	}

	/**
	 * @param titles the message keys of the tabs to add (clickable text header)
	 */
	private void addTabs(final String[] titles) {
		for(int i=0; i<titles.length; i++) { // use: for (String title : titles) ...
			addTab(i, titles[i]);
		}
	}

	/**
	 * URL of the tabheader-anchor-links 
	 * (needed, because postPublication calls this site first, but tabs-hrefs have to be...
	 * ... import/publications)
	 */
	private String tabURL=null; // TODODZ: null initializing ?!
	
	/**
	 * @return the url of the tabbed site
	 */
	public String getTabURL() {
		return this.tabURL;
	}

	/**
	 * @param tabURL the url of the tabbed site
	 */
	public void setTabURL(final String tabURL) {
		this.tabURL = tabURL;
	}
	
	/**
	 * Containing the task name, determining whats to do in the controller.
	 */
	private String taskName;
	
	/**
	 * @returns taskName the task name, determining whats to do in the controller. 
	 * The value is set to TASK_ENTER_PUBLICATIONS, if publications get entered or edited(tags).
	 */
	public String getTaskName() {
		return this.taskName;
	}

	/**
	 * @param taskName the task name, determining whats to do in the controller. 
	 * The value is set to TASK_ENTER_PUBLICATIONS, if publications get entered or edited(tags).
	 */
	public void setTaskName(final String taskName) {
		this.taskName = taskName;
	}

	
	
	/**
	 * Detetermines, if we call import/publications a second time due to errors, the user can fix
	 */
	private boolean extendedView = false; // TODO: unnecessary
	
	/**
	 * @returns True, if we call import/publications a second time due to errors, the user can fix
	 */
	public boolean isExtendedView() {
		return this.extendedView;
	}

	/**
	 * @param extendedView True, if we call import/publications a second time due to errors, the user can fix
	 */
	public void setExtendedView(final boolean extendedView) {
		this.extendedView = extendedView;
	}

	/**
	 * The errors during parsing
	 */
	private List<Integer> erroneousLineNumbers = null; // TODO: unnecessary
	
	public List<Integer> getErroneousLineNumbers() {
		return this.erroneousLineNumbers;
	}

	public void setErroneousLineNumbers(final List<Integer> erroneousLineNumbers) {
		this.erroneousLineNumbers = erroneousLineNumbers;
	}
	
	/****************************
	 * FOR ALL IMPORTS
	 ****************************/
	

	/**
	 * this flag determines, weather the dialogue called was configured to 
	 * edit(delete) or edit(create) existing posts.
	 */
	private boolean deleteCheckedPosts;
	
	/**
	 * @return the flag that determines, weather the dialogue called was configured to 
	 * edit(delete) or edit(create) existing posts.
	 */
	public boolean getDeleteCheckedPosts() {
		return this.deleteCheckedPosts;
	}

	/**
	 * @return the flag that determines, weather the dialogue called was configured to 
	 * edit(delete) or edit(create) existing posts.
	 */
	public boolean isDeleteCheckedPosts() {
		return this.deleteCheckedPosts;
	}
	
	/**
	 * @param deleteCheckedPosts the flag that determines, weather the dialogue called was configured to 
	 * edit(delete) or edit(create) existing posts.
	 */
	public void setDeleteCheckedPosts(final boolean deleteCheckedPosts) {
		this.deleteCheckedPosts = deleteCheckedPosts;
	}
	
	/**
	 * this flag determines, if erroneous posts should be ignored during import
	 * resulting in storing all correct ones. if this is false, no posts will be stored
	 * in case of an error.
	 */
	private boolean saveAllPossible;
	
	/**
	 * @return the flag that determines, if erroneous posts should be ignored during import
	 * resulting in storing all correct ones. if this is false, no posts will be stored
	 * in case of an error.
	 */
	public boolean isSaveAllPossible() {
		return this.saveAllPossible;
	}
	
	/**
	 * @return the flag that determines, if erroneous posts should be ignored during import
	 * resulting in storing all correct ones. if this is false, no posts will be stored
	 * in case of an error.
	 */
	public boolean getSaveAllPossible() {
		return this.saveAllPossible;
	}

	/**
	 * @param saveAllPossible the flag that determines, if erroneous posts should be ignored during import
	 * resulting in storing all correct ones. if this is false, no posts will be stored
	 * in case of an error.
	 */
	public void setSaveAllPossible(final boolean saveAllPossible) {
		this.saveAllPossible = saveAllPossible;
	}
	
	/**
	 * the description of the snippet/upload file
	 */
	private String description;
	
	
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public void setDescription(final String description) {
		this.description = description;
	}
	
	
	
	/****************************
	 * SPECIAL FOR FILE UPLOAD
	 ****************************/

	/**
	 * the BibTeX/Endnote file
	 */
	private CommonsMultipartFile file;
	
	
	public CommonsMultipartFile getFile() {
		return this.file;
	}

	public void setFile(final CommonsMultipartFile file) {
		this.file = file;
	}
	
	
	/**
	 * The whitespace substitute
	 */
	private String whitespace;
	
	public String getWhitespace() {
		return this.whitespace;
	}

	public void setWhitespace(final String whitespace) {
		this.whitespace = whitespace;
	}

	
	/**
	 * encoding of the file
	 */
	private String encoding;
	
	public String getEncoding() {
		return this.encoding;
	}

	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	
	/**
	 * the delimiter
	 */
	private String delimiter;

	public String getDelimiter() {
		return this.delimiter;
	}

	public void setDelimiter(final String delimiter) {
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

	public void setEditBeforeImport(final boolean editBeforeImport) {
		this.editBeforeImport = editBeforeImport;
	}
	
	
	/**
	 * Determines, if the already existing publications will be overwritten by the new ones.
	 */
	private boolean overwrite;

	public boolean getOverwrite() {
		return this.overwrite;
	}
	
	public void setOverwrite(final boolean overwrite) {
		this.overwrite = overwrite;
	}
	
	
	
	/***********************************************
	 * FOR IMPORTS CONTAINING MULTIPLE PUBLICATIONS
	 ***********************************************/
	//TODO REMOVE
	/**
	 * The action that will be started, when hitting the submission button on the edit page
	 */
	private String formAction;
	
	public String getFormAction() {
		return this.formAction;
	}

	public void setFormAction(final String formAction) {
		this.formAction = formAction;
	}
	

	/**
	 * The posts, that were updated during import.
	 */
	private Map<String,String> updatedPosts=null; // TODO: null and HashMap => Map
	
	public Map<String,String> getUpdatedPosts() {
		return this.updatedPosts;
	}

	public void setUpdatedPosts(final Map<String, String> updatedBookmarkEntries) {
		this.updatedPosts = updatedBookmarkEntries;
	}

	

	/**
	 * For multiple posts
	 */
	private ListCommand<Post<BibTex>> posts = new ListCommand<Post<BibTex>>(this);

	public ListCommand<Post<BibTex>> getBibtex() {
		return this.posts;
	}

	public void setBibtex(final ListCommand<Post<BibTex>> bibtex) {
		this.posts = bibtex;
	}
	
	public ListCommand<Post<BibTex>> getPosts() {
		return this.posts;
	}

	public void setPosts(final ListCommand<Post<BibTex>> bibtex) {
		this.posts = bibtex;
	}

	
	@Override
	public List<Object> getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContent(final List<Object> content) {
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
