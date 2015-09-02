package org.bibsonomy.services.person;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.util.BibTexUtils;
import org.springframework.context.MessageSource;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class PersonRoleRenderer {
	
	private final MessageSource messageSource;
	
	/**
	 * @param messageSource 
	 */
	public PersonRoleRenderer(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	/**
	 * @param rel
	 * @param locale
	 * @return the name of a person and her role wrt an associated publication
	 */
	public String getExtendedPersonName(ResourcePersonRelation rel, Locale locale, boolean asHtml) {
		final BibTex res = rel.getPost().getResource();
		final Person person = rel.getPerson();
		final PersonName personName = person.getMainName();
		final StringBuilder extendedNameBuilder = new StringBuilder();
		appendPersonName(personName, extendedNameBuilder);
		if (asHtml) {
			String htmlName = StringEscapeUtils.escapeHtml(extendedNameBuilder.toString());
			extendedNameBuilder.setLength(0);
			extendedNameBuilder.append("<span class=\"authorName\">").append(htmlName).append("</span>");
		}
		if (present(person.getAcademicDegree())) {
			extendedNameBuilder.append(", ").append(person.getAcademicDegree());
		}
		if ((rel.getRelationType() != PersonResourceRelationType.AUTHOR) || (!containsName(res, personName))) {
			extendedNameBuilder.append(' ');
			final String relationStr = messageSource.getMessage("person.show." + rel.getRelationType().getRelatorCode() + ".of", null, locale);
			extendedNameBuilder.append(relationStr);
			extendedNameBuilder.append(' ');
			if (asHtml) {
				StringBuilder tmpNameBuilder = new StringBuilder();
				appendAuthorsOrEditors(tmpNameBuilder, rel.getPost().getResource());
				extendedNameBuilder.append(StringEscapeUtils.escapeHtml(tmpNameBuilder.toString()));
			} else {
				appendAuthorsOrEditors(extendedNameBuilder, rel.getPost().getResource());
			}
			
		}
		
		if (present(res)) {
			appendDisambiguatingBibTexInfo(extendedNameBuilder, res);
		}
		return extendedNameBuilder.toString();
	}
	
	/**
	 * @param res
	 * @param personName
	 * @return
	 */
	private boolean containsName(BibTex res, PersonName personName) {
		final StringBuilder sb = new StringBuilder();
		appendAuthorsOrEditors(sb, res);
		return (sb.indexOf(personName.getLastName()) >= 0);
	}

	private static void appendPersonName(PersonName personName, final StringBuilder extendedNameBuilder) {
		if (present(personName.getFirstName())) {
			extendedNameBuilder.append(personName.getFirstName()).append(" ");
		}
		extendedNameBuilder.append(personName.getLastName());
	}

	private static void appendDisambiguatingBibTexInfo(final StringBuilder extendedNameBuilder, BibTex res) {
		String entryType = res.getEntrytype();
		if (entryType.toLowerCase().endsWith("thesis")) {
			if (present(res.getSchool())) {
				extendedNameBuilder.append(", ").append(BibTexUtils.cleanBibTex(res.getSchool()));
			}
		}
		if (present(res.getYear())) {
			extendedNameBuilder.append(", ").append(BibTexUtils.cleanBibTex(res.getYear()));
		}
		if (present(res.getTitle())) {
			extendedNameBuilder.append(", \"").append(BibTexUtils.cleanBibTex(res.getTitle())).append('"');
		}
	}
	
	/**
	 * @param pub
	 * @return
	 */
	public String getExtendedPublicationName(BibTex pub, Locale locale, boolean asHtml) {
		final StringBuilder extendedNameBuilder = new StringBuilder();
		appendAuthorsOrEditors(extendedNameBuilder, pub);
		appendDisambiguatingBibTexInfo(extendedNameBuilder, pub);
		String rVal = extendedNameBuilder.toString();
		if (asHtml) {
			return StringEscapeUtils.escapeHtml(rVal);
		} else {
			return rVal;
		}
	}

	private void appendAuthorsOrEditors(final StringBuilder extendedNameBuilder, BibTex pub) {
		final List<PersonName> names;
		if (present(pub.getAuthor())) {
			names = pub.getAuthor();
		} else {
			names = pub.getEditor();
		}
		for (PersonName personName : names) {
			appendPersonName(personName, extendedNameBuilder);
			extendedNameBuilder.append(", ");
		}
		if (extendedNameBuilder.length() >= 2) {
			extendedNameBuilder.setLength(extendedNameBuilder.length() - 2);
		}
	}
}
