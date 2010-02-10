package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.ConceptUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.ConceptAjaxCommand;
import org.bibsonomy.webapp.controller.AjaxController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This controller is used to pick and unpick one or all concepts of the logged in user.
 * 
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class ConceptController extends AjaxController implements MinimalisticController<ConceptAjaxCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(ConceptController.class);

	private LogicInterface logic;	
	private Errors errors;
	
	@Override
	public View workOn(ConceptAjaxCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		if (!command.getContext().getUserLoggedIn()){
			log.debug("someone tried to access this ajax controller manually and isn't logged in");
			return new ExtendedRedirectView("/");
		}
		
		//check if ckey is valid
		if (!command.getContext().isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		// decide which action will done
		if("show".equals(command.getAction())){
			logic.updateConcept(new Tag(command.getTag()), GroupingEntity.USER, command.getContext().getLoginUser().getName(), ConceptUpdateOperation.PICK);
		} 
		if ("hide".equals(command.getAction())){
			logic.updateConcept(new Tag(command.getTag()), GroupingEntity.USER, command.getContext().getLoginUser().getName(), ConceptUpdateOperation.UNPICK);
		} 
		if ("all".equals(command.getAction())){
			if ("show".equals(command.getTag())){
				logic.updateConcept(null, GroupingEntity.USER, command.getContext().getLoginUser().getName(), ConceptUpdateOperation.PICK_ALL);
			} else if ("hide".equals(command.getTag())){
				logic.updateConcept(null, GroupingEntity.USER, command.getContext().getLoginUser().getName(), ConceptUpdateOperation.UNPICK_ALL);
			}
		} 
		
		// if forward is available redirect to that destination (in case of javascript disabled)
		if (present(command.getForward())) {
			return new ExtendedRedirectView("/" + command.getForward());
		}
		
		// create the response string
		command.setResponseString(prepareResponseString(command.getContext().getLoginUser().getName()));
		
		return Views.AJAX_RESPONSE;
	}
	
	/*
	 * This private method gets the list of picked concepts and
	 * transform them into XML which will serialized as a string and returned.
	 * 
	 * @param groupingname
	 * @return String
	 */
	private String prepareResponseString(String groupingname){
		StringWriter response = new StringWriter();  
		final List<Tag> pickedConcepts = this.logic.getConcepts(null, GroupingEntity.USER, groupingname, null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);

		try {
			// create new doc
			DocumentBuilderFactory dbf = DocumentBuilderFactoryImpl.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			
			// append root node
			Element relations = doc.createElement("relations");
			relations.setAttribute("user", groupingname);
			doc.appendChild(relations);
			
			Element relation;
			Element upper;
			Element lowers;
			Element lower;
			
			// append all other informations
			for(Tag tag : pickedConcepts){
				relation = doc.createElement("relation");
				relations.appendChild(relation);
				upper = doc.createElement("upper");
				upper.setTextContent(tag.getName());
				relation.appendChild(upper);
				lowers = doc.createElement("lowers");
				lowers.setAttribute("id", tag.getName());
				relation.appendChild(lowers);

				for(Tag subTag : tag.getSubTags()){
					lower = doc.createElement("lower");
					lower.setTextContent(subTag.getName());
					lowers.appendChild(lower);	
				}

			}
			
			// serialize xml
			OutputFormat format = new OutputFormat (doc);
			XMLSerializer serial = new XMLSerializer (response, format);
			serial.serialize(doc);
		
			// return it as string
            return response.toString();
			
		} catch (ParserConfigurationException ex) {
			log.error("Could not parse XML " + ex.getMessage());
		} catch (IOException ex) {
			log.error("Could not serialize XML " + ex.getMessage());
		}
		
		return null;
	}

	@Override
	public ConceptAjaxCommand instantiateCommand() {
		return new ConceptAjaxCommand();
	}
	
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	
	/**
	 * 
	 * @param logic
	 */
	@Override
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
