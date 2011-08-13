package org.bibsonomy.wiki.tags.old;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.wiki.tags.AbstractTag;

import info.bliki.htmlcleaner.Utils;

/**
 * 
 * @author Bernd
 *
 */

public class HeaderTag extends AbstractTag {
	public static final String TAG_NAME = "header";
	
	public HeaderTag() {
		super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		StringBuilder renderedHTML = new StringBuilder();
		final String name = this.requestedUser.getName();
     	final String realName = this.requestedUser.getRealname();
     	final String location = this.requestedUser.getPlace();
     	final String birthday = this.requestedUser.getBirthday().toString();
     	final String profession = this.requestedUser.getProfession();
     	final String institution = this.requestedUser.getInstitution();
     	final String homepage = this.requestedUser.getHomepage().toExternalForm();
     	
     	renderedHTML.append("<div id='personalData'><table class='align personalData' style='float:left;'>");
     	//TODO: ResourceBundle Impl.
     	if(present(realName)) {
     		if(present(homepage)){
     			renderedHTML.append("<tr><td>"+Utils.escapeXmlChars("name")+"</td><td class='secondTD'><a href='"+Utils.escapeXmlChars(homepage) +"'>"+Utils.escapeXmlChars(realName) +"</a></td></tr>");
     		}else {
     			renderedHTML.append(createRow(/*messageSource.getMessage("cv.name", null, Locale.GERMAN)*/"name",realName)); //cv.name
     		}
     	}else{
     		if(present(homepage)){
     			renderedHTML.append("<tr><td>"+Utils.escapeXmlChars("website")+"</td><td class='secondTD'><a href='"+Utils.escapeXmlChars(homepage) +"'>"+Utils.escapeXmlChars("My Website") +"</a></td></tr>");
     		}
     	}
     	if(present(location))	renderedHTML.append(createRow("location",location)); //cv.location
     	if(present(birthday))	renderedHTML.append(createRow("birthday",birthday)); //cv.birthday
     	if(present(profession))	renderedHTML.append(createRow("profession",profession));//cv.profession
     	if(present(institution))renderedHTML.append(createRow("institution", institution));//cv.institution
     	renderedHTML.append("</table>");
     	//TODO: Probably use an URL generator
     	renderedHTML.append("<img style='clear:both;' src='/picture/user/"+Utils.escapeXmlChars(name)+"'>");
     	renderedHTML.append("</div><br />");
     	
     	return renderedHTML;
	}
	//TODO: swap up
	protected String createRow(String s1, String s2) {
		return "<tr><td>"+Utils.escapeXmlChars(s1)+"</td><td class='secondTD'>"+Utils.escapeXmlChars(s2)+"</td></tr>";
	}

}
