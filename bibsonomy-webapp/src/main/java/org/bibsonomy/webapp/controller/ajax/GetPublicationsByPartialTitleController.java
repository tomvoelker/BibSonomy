package org.bibsonomy.webapp.controller.ajax;


import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.AjaxGetPublicationsByPartialTitleCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

public class GetPublicationsByPartialTitleController<R extends Resource> implements MinimalisticController <AjaxGetPublicationsByPartialTitleCommand> {
	protected LogicInterface logic;
	protected final BibTexDatabaseManager db = BibTexDatabaseManager.getInstance();

	public void setLogic ( LogicInterface logic ) {
		this.logic = logic;
	}
	
	public AjaxGetPublicationsByPartialTitleCommand instantiateCommand() {
		return new AjaxGetPublicationsByPartialTitleCommand();
	}
	
	public View workOn ( AjaxGetPublicationsByPartialTitleCommand command ) {
		if(command.getTitle() != null) {
			DBSessionFactory dbSessionFactory = new IbatisDBSessionFactory();
			DBSession dbSession = dbSessionFactory.getDatabaseSession();
			Set<String> allowedGroups = new TreeSet<String>();
			allowedGroups.add(GroupID.PUBLIC.name());
			//List<Post<BibTex>> getPostsByTitleLucene(final String search, final int groupId, final String requestedUserName, final String userName, final Set<String> requestedGroupName, final int limit, final int offset, final DBSession session);
			List<Post<BibTex>> titles = this.db.getPostsByTitleLucene(command.getTitle()+"%", 0, null, command.getUserName(), allowedGroups, 10, 0, dbSession);
			dbSession.close();
		}
		
		
		return Views.AJAX_GET_PUBLICATION_TITLES;
	}
}
