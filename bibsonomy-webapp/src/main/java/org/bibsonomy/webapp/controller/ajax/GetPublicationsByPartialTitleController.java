package org.bibsonomy.webapp.controller.ajax;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.AjaxGetPublicationsByPartialTitleCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author mve
 * @version $Id$
 */

public class GetPublicationsByPartialTitleController<R extends Resource> implements MinimalisticController <AjaxGetPublicationsByPartialTitleCommand> {
	protected LogicInterface logic;
	
	public void setLogic ( LogicInterface logic ) {
		this.logic = logic;
	}
	
	public AjaxGetPublicationsByPartialTitleCommand instantiateCommand() {
		return new AjaxGetPublicationsByPartialTitleCommand();
	}
	
	public View workOn ( AjaxGetPublicationsByPartialTitleCommand command ) {
		command.setResponse(this.PrepareResponse(command));
		return Views.AJAX_GET_PUBLICATION_TITLES;
	}
	
	String PrepareResponse ( AjaxGetPublicationsByPartialTitleCommand cmd ) {
		if(cmd.getTitle() == null) {
			return "{}";
		}
		
		List <String>tags = new ArrayList<String>();
		tags.add(new String("sys:title:"+cmd.getTitle()+"%"));
		List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null,
				tags, null, null, null, 0, 10, null);
		return this.CreateOutputByString(posts);
	}
	
	public String CreateOutputByString ( List<Post<BibTex>> posts ) { //String[] obj) {
		String jsonOutput = new String("{\"items\":[");
		for(int t = 1; t <= posts.size(); t++) {
			jsonOutput += "{"+CreateSingleOutput("title", posts.get(t-1).getResource().getTitle())
			+","+CreateSingleOutput("entry_type", posts.get(t-1).getResource().getEntrytype())
			+","+CreateSingleOutput("author", ""+posts.get(t-1).getResource().getAuthor())
			+","+CreateSingleOutput("year", ""+posts.get(t-1).getResource().getYear())
			+","+CreateSingleOutput("editor", ""+posts.get(t-1).getResource().getEditor())+
			"}";
			if(t < posts.size()) {
				jsonOutput += ",";
			}
		}
		return jsonOutput+"]}";
	}

	public String CreateSingleOutput(String title, String obj) {
		return "\""+title+"\":\""+obj+"\"";
	}
}
