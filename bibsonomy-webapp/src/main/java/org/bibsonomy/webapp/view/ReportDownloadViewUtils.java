package org.bibsonomy.webapp.view;

import org.bibsonomy.export.ExportFieldMapping;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.bibsonomy.model.util.PersonNameUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * mappings for reporting
 *
 * @author pda
 */
public final class ReportDownloadViewUtils {

	private final static String DATE_FORMAT = "dd.MM.yyyy";

	public final static List<ExportFieldMapping<Project>> PROJECT_FIELD_MAPPINGS = new LinkedList<>();
	public final static List<ExportFieldMapping<Person>> PERSON_FIELD_MAPPINGS = new LinkedList<>();
	public final static List<ExportFieldMapping<Post<GoldStandardPublication>>> PUBLICATION_FIELD_MAPPINGS = new LinkedList<>();

	static {
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("title", Project::getTitle));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("subtitle", Project::getSubTitle));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("type", Project::getType));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("sponsor", Project::getSponsor));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("description", Project::getDescription));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("externalId", Project::getExternalId));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("budget", p -> formatNumber(p.getBudget())));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("startDate", p -> formatDate(p.getStartDate())));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("endDate", p -> formatDate(p.getEndDate())));
		PROJECT_FIELD_MAPPINGS.add(new ExportFieldMapping<>("projectLeader", p -> p.getCrisLinks().stream().
						filter(l -> l.getLinkType().equals(ProjectPersonLinkType.MANAGER)).map(CRISLink::getTarget).
						filter(Person.class::isInstance).map(Person.class::cast).map(Person::getMainName).
						map(PersonNameUtils::serializePersonName).collect(Collectors.joining(";"))));
	}

	static {
		PERSON_FIELD_MAPPINGS.add(new ExportFieldMapping<>("email", Person::getEmail));
		PERSON_FIELD_MAPPINGS.add(new ExportFieldMapping<>("college", Person::getCollege));
		PERSON_FIELD_MAPPINGS.add(new ExportFieldMapping<>("researcherId", Person::getResearcherid));
		PERSON_FIELD_MAPPINGS.add(new ExportFieldMapping<>("orcid", Person::getOrcid));
		PERSON_FIELD_MAPPINGS.add(new ExportFieldMapping<>("academicDegree", Person::getAcademicDegree));
		PERSON_FIELD_MAPPINGS.add(new ExportFieldMapping<>("homepage", p -> p.getHomepage().toString()));
		PERSON_FIELD_MAPPINGS.add(new ExportFieldMapping<>("mainName", p -> p.getMainName().toString()));
		PERSON_FIELD_MAPPINGS.add(new ExportFieldMapping<>("otherNames", p -> p.getNames().stream().
						map(PersonName::toString).collect(Collectors.joining(" "))));
	}

	static {
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("title", post -> post.getResource().getTitle()));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("authors", post -> PersonNameUtils.serializePersonNames(post.getResource().getAuthor())));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("editors", post -> PersonNameUtils.serializePersonNames(post.getResource().getEditor())));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("year", post -> post.getResource().getYear()));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("journal", post -> post.getResource().getJournal()));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("booktitle", post -> post.getResource().getBooktitle()));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("type", post -> post.getResource().getType()));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("description", Post::getDescription));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("tags", p -> p.getTags().stream().map(Tag::getName).collect(Collectors.joining(" "))));
		PUBLICATION_FIELD_MAPPINGS.add(new ExportFieldMapping<>("date", p -> formatDate(p.getDate())));
	}

	private static String formatNumber(Float number) {
		if (number == null) {
			return "";
		}
		return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(number);
	}

	private static String formatDate(Date date) {
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}
}
