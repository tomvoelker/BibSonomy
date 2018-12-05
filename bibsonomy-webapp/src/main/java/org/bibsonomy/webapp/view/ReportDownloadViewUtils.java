package org.bibsonomy.webapp.view;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.bibsonomy.model.util.PersonNameUtils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ReportDownloadViewUtils {
	INSTANCE;

	private final static String DATE_FORMAT = "dd.MM.yyyy";

	private final static HashMap<String, Function<Project, String>> PROJECT_FIELD_MAPPINGS = new HashMap<>();
	private final static HashMap<String, Function<Person, String>> PERSON_FIELD_MAPPINGS = new HashMap<>();
	private final static HashMap<String, Function<Post<BibTex>, String>> PUBLICATION_FIELD_MAPPINGS = new HashMap<>();

	static {
		PROJECT_FIELD_MAPPINGS.put("title", Project::getTitle);
		PROJECT_FIELD_MAPPINGS.put("subtitle", Project::getSubTitle);
		PROJECT_FIELD_MAPPINGS.put("type", Project::getType);
		PROJECT_FIELD_MAPPINGS.put("description", Project::getDescription);
		PROJECT_FIELD_MAPPINGS.put("externalId", Project::getExternalId);
		PROJECT_FIELD_MAPPINGS.put("budget", p -> Float.toString(p.getBudget()));
		PROJECT_FIELD_MAPPINGS.put("startDate", p -> formatDate(p.getStartDate()));
		PROJECT_FIELD_MAPPINGS.put("endDate", p -> formatDate(p.getEndDate()));
		PROJECT_FIELD_MAPPINGS.put("projectLeader", p -> p.getCrisLinks().stream().
						filter(l -> l.getLinkType().equals(ProjectPersonLinkType.MANAGER)).map(CRISLink::getTarget).
						filter(Person.class::isInstance).map(Person.class::cast).map(Person::getMainName).
						map(PersonNameUtils::serializePersonName).collect(Collectors.joining(";")));
	}

	static {
		PERSON_FIELD_MAPPINGS.put("email", Person::getEmail);
		PERSON_FIELD_MAPPINGS.put("college", Person::getCollege);
		PERSON_FIELD_MAPPINGS.put("researcherId", Person::getResearcherid);
		PERSON_FIELD_MAPPINGS.put("orcid", Person::getOrcid);
		PERSON_FIELD_MAPPINGS.put("academicDegree", Person::getAcademicDegree);
		PERSON_FIELD_MAPPINGS.put("homepage", p -> p.getHomepage().toString());
		PERSON_FIELD_MAPPINGS.put("mainName", p -> p.getMainName().toString());
		PERSON_FIELD_MAPPINGS.put("otherNames", p -> p.getNames().stream().
						map(PersonName::toString).collect(Collectors.joining(" ")));
		//TODO add cris links
	}

	static {
		PUBLICATION_FIELD_MAPPINGS.put("description", Post::getDescription);
		PUBLICATION_FIELD_MAPPINGS.put("tags", p -> p.getTags().stream().map(Tag::getName).
						collect(Collectors.joining(" ")));
		PUBLICATION_FIELD_MAPPINGS.put("date", p -> formatDate(p.getDate()));
		//TODO what to include from posts?
	}

	private static String formatDate(Date date) {
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}

	public Map<String, Function<Project, String>> getProjectMappings() {
		return PROJECT_FIELD_MAPPINGS;
	}

	public Map<String, Function<Person, String>> getPersonMappings() {
		return PERSON_FIELD_MAPPINGS;
	}

	public Map<String, Function<Post<BibTex>, String>> getPublicationMappings() {
		return PUBLICATION_FIELD_MAPPINGS;
	}

	public Map<String, Function<?, String>> getSubMap(Map<String, Function<?, String>> fullMap,
																										Collection<String> subKeys) {
		HashMap<String, Function<?, String>> subMap = new HashMap<>(subKeys.size());
		for (String key : subKeys) {
			subMap.put(key, fullMap.get(key));
		}
		return subMap;
	}
}
