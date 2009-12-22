package org.bibsonomy.webapp.controller.ajax;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.ajax.AjaxGetPublicationsByPartialTitleCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

public class GetPublicationsByPartialTitleController<R extends Resource> implements MinimalisticController <AjaxGetPublicationsByPartialTitleCommand> {
	protected LogicInterface logic;
	
	public void setLogic ( LogicInterface logic ) {
		this.logic = logic;
	}
	
	public AjaxGetPublicationsByPartialTitleCommand instantiateCommand() {
		return new AjaxGetPublicationsByPartialTitleCommand();
	}
	
	public View workOn ( AjaxGetPublicationsByPartialTitleCommand command ) {
		if(command.getTitle() != null) {
			List <String>tags = new ArrayList<String>();
			tags.add(new String("sys:title:"+command.getTitle()+"%"));
			
			ListCommand<Post<BibTex>> postListCommand = new ListCommand<Post<BibTex>>(command);
			postListCommand.setList(this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null,
					tags, null, null, null, 0, 10, null));
			command.setPosts(postListCommand);
		}

		
		
		return Views.AJAX_GET_PUBLICATION_TITLES;
	}
}
