package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.BibTexKeySystemTag;
import org.bibsonomy.database.systemstags.search.UserSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.BibtexkeyCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for BibtexKey * 
 *
 * @author Flori, Dominik Benz
 * @version $Id$
 */
public class BibtexkeyPageController extends SingleResourceListController implements MinimalisticController<BibtexkeyCommand> {
	private static final Log log = LogFactory.getLog(BibtexkeyPageController.class);

	@Override
	public View workOn(BibtexkeyCommand command) {
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());		
		
		if (!present(command.getRequestedKey())) {
			throw new MalformedURLSchemeException("error.bibtexkey_no_key");
		}
		
		// add bibtexkey as the only systemtag (sys:user:USERNAME is handeled below)
		command.getRequestedTagsList().clear();
		command.getRequestedTagsList().add(SystemTagsUtil.buildSystemTagString(BibTexKeySystemTag.NAME, command.getRequestedKey()));		
		
		// default grouping entity / grouping name
		GroupingEntity groupingEntity = GroupingEntity.ALL;
		String groupingName = null;
				
		// check for systemtag sys:user:USERNAME
		List<String> sysTags = SystemTagsUtil.extractSearchSystemTagsFromString(command.getRequestedTags(), " ");		
		final String systemTagUser = extractSystemTagUser(sysTags);		
		if (systemTagUser != null) {
			command.setRequestedUser(systemTagUser);
		}

		// check if user was given via /bibtexkey/KEY/USERNAME or systemtag
		if (present(command.getRequestedUser())) {
			groupingEntity = GroupingEntity.USER;
			groupingName = command.getRequestedUser();
		}
						
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			setList(command, resourceType, groupingEntity, groupingName, command.getRequestedTagsList(), null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());			
			postProcessAndSortList(command, resourceType);
		}
						
		// html format - fetch tags and return HTML view
		if (command.getFormat().equals("html")) {
			// tags
			setTags(command, BibTex.class, groupingEntity, groupingName, null, command.getRequestedTagsList(), null, 1000, null);
			if (command.getTagcloud().getTags().size() > 999) {
				log.error("Found bibtex entries by bibtex keys with more than 1000 tags assigned!!");
			}
			// pagetitle
			String pageTitle = "bibtexkey :: " + command.getRequestedKey();
			if (GroupingEntity.USER.equals(groupingEntity)) {
				pageTitle += " :: " + command.getRequestedUser() ;
			}
			command.setPageTitle(pageTitle);			
			this.endTiming();
			return Views.BIBTEXKEYPAGE;	
		}
		
		// export - return the appropriate view
		this.endTiming();
		return Views.getViewByFormat(command.getFormat());				
	}

	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public BibtexkeyCommand instantiateCommand() {
		return new BibtexkeyCommand();
	}
	
	/**
	 * Check if 
	 * @param sysTags
	 * 		- a list of system tags (strings)
	 * @return
	 * 		- the value of the user system tag, if present (i.e. USERNAME if sys:user:USERNAME is present, 
	 *        null otherwise 
	 */
	private String extractSystemTagUser(List<String> sysTags) {
		for (String sysTag : sysTags) {
			if (SystemTagsUtil.isSystemTagWithPrefix(sysTag, UserSystemTag.NAME)) {
				return SystemTagsUtil.extractArgument(sysTag);
			}
		}
		return null;
	}	

}
