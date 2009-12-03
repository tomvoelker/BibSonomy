package org.bibsonomy.webapp.controller;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.CvPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Philipp Beau
 * @version $Id$
 */
public class CvPageController extends ResourceListController implements MinimalisticController<CvPageCommand> {
	private static final Log log = LogFactory.getLog(CvPageController.class);


		/**
		 * implementation of {@link MinimalisticController} interface
		 */
		public View workOn(CvPageCommand command) {
			command.setPageTitle("Curriculum vitae");
			
			final String requUser = command.getRequestedUser();
			User requUserDetail;

			if(!(requUser == null)) {
			requUserDetail = this.logic.getUserDetails(requUser);
			command.setUser(requUserDetail);
			} else {
				
				return Views.ERROR;
			}
			
			final String groupingName = command.getRequestedUser();
			final GroupingEntity groupingEntity = GroupingEntity.USER;
						
			/*
			 * retrieve and set the requested bibtex(s)
			 */
			for (final Class<? extends Resource> resourceType : listsToInitialise) {

				final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();		
				this.setList(command, resourceType, groupingEntity, requUser, null, null, null, null, null, entriesPerPage);
				
				if (GroupingEntity.ALL.equals(groupingEntity)) {
					/* 
					 * retrieve total count with given hash 
					 * (only for /bibtex/HASH)
					 */
					this.setTotalCount(command, resourceType, groupingEntity, requUser, null, null, null, null, null, entriesPerPage, null);
				} else if (GroupingEntity.USER.equals(groupingEntity)) {
					/*
					 * complete post details for a single post of a given user 
					 * (only for /bibtex/HASH/USER)
					 */

					final ArrayList<Post<BibTex>> bibtex = new ArrayList<Post<BibTex>>();
					for (final Post<BibTex> b : command.getBibtex().getList()){
						for(Tag t : b.getTags())
							//if settings page exists 
							//if(t.equals(requUserDetail.getMYOWNTAG)) {
							if(t.getName().equals("myown")) {
								bibtex.add((Post<BibTex>) this.logic.getPostDetails(b.getResource().getIntraHash(), b.getUser().getName()));
							}
					}			
					
					
					command.getBibtex().setList(bibtex);			
				}
			}

			return Views.CVPAGE;
		}

		/**
		 * implementation of {@link MinimalisticController} interface
		 */
		public CvPageCommand instantiateCommand() {
			return new CvPageCommand();
		}	
	}

	
