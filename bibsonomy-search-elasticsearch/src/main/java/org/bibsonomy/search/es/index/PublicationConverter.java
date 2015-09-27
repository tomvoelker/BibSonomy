package org.bibsonomy.search.es.index;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.ESConstants.Fields.Publication;
import org.bibsonomy.util.ValidationUtils;

/**
 * converts a {@link BibTex} to the ElasticSearch representation and vice versa
 *
 * @author dzo
 * @author jensi
 */
public class PublicationConverter extends ResourceConverter<BibTex> {
	private static final Log log = LogFactory.getLog(PublicationConverter.class);
	
	private static final String PERSON_DELIMITER = " & ";
	private static final String NAME_PART_DELIMITER = " ; ";
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertPostInternal(java.util.Map, org.bibsonomy.model.Post)
	 */
	@Override
	protected void convertPostInternal(Map<String, Object> source, Post<BibTex> post) {
		post.setResourcePersonRelations(readPersonRelationsFromIndex(source));
		for (final ResourcePersonRelation rel : post.getResourcePersonRelations()) {
			rel.setPost(post);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertResource(java.util.Map, org.bibsonomy.model.Resource)
	 */
	@Override
	protected void convertResource(Map<String, Object> jsonDocument, BibTex resource) {
		jsonDocument.put("address", resource.getAddress());
		jsonDocument.put("annote", resource.getAnnote());
		jsonDocument.put(Fields.Publication.AUTHOR, convertPersonNames(resource.getAuthor()));
		jsonDocument.put("bkey", resource.getKey());
		jsonDocument.put("abstract", resource.getAbstract());
		jsonDocument.put(Fields.Publication.BIBTEXKEY, resource.getBibtexKey());
		jsonDocument.put("booktitle", resource.getBooktitle());
		jsonDocument.put("chapter", resource.getChapter());
		jsonDocument.put("crossref", resource.getCrossref());
		jsonDocument.put("day", resource.getDay());
		jsonDocument.put("edition", resource.getEdition());
		jsonDocument.put("editor", convertPersonNames(resource.getEditor()));
		jsonDocument.put("entrytype", resource.getEntrytype());
		jsonDocument.put("howPublished", resource.getHowpublished());
		
		jsonDocument.put("institution", resource.getInstitution());
		jsonDocument.put("journal", resource.getJournal());
		jsonDocument.put("misc", resource.getMisc());
		jsonDocument.put("month", resource.getMonth());
		jsonDocument.put("note", resource.getNote());
		jsonDocument.put("number", resource.getNumber());
		jsonDocument.put("organization", resource.getOrganization());
		jsonDocument.put("pages", resource.getPages());
		
		jsonDocument.put("privnote", resource.getPrivnote());
		jsonDocument.put("publisher", resource.getPublisher());
		jsonDocument.put(Fields.Publication.SCHOOL, resource.getSchool());
		jsonDocument.put("series", resource.getSeries());
		
		jsonDocument.put("type", resource.getType());
		jsonDocument.put("url", resource.getUrl());
		jsonDocument.put("volume", resource.getVolume());
		
		jsonDocument.put(Publication.YEAR, resource.getYear());
	}
	
	/**
	 * @param author
	 * @return
	 */
	private static Object convertPersonNames(List<PersonName> author) {
		// FIXME: convert to string?
		return author;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertPostInternal(java.util.Map, org.bibsonomy.model.Post)
	 */
	@Override
	protected void convertPostInternal(Post<BibTex> post, Map<String, Object> jsonDocument) {
		jsonDocument.put(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, getNormalizedEntryType((Post<? extends BibTex>) post));
		
		final List<ResourcePersonRelation> rels = post.getResourcePersonRelations();
		jsonDocument.put(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME, serializeMainNames(rels, PersonResourceRelationType.AUTHOR));
		jsonDocument.put(ESConstants.AUTHOR_ENTITY_IDS_FIELD_NAME, serializePersonIds(rels, PersonResourceRelationType.AUTHOR));
		jsonDocument.put(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME, serializeMainNames(rels, null));
		jsonDocument.put(ESConstants.PERSON_ENTITY_IDS_FIELD_NAME, serializePersonIds(rels, null));
	}
	
	private static String getNormalizedEntryType(final Post<? extends BibTex> post) {
		final BibTex bibtex = post.getResource();
		String normalizedEntryType = null;
		
		final String entryType = bibtex.getEntrytype();
		if (BibTexUtils.PHD_THESIS.equals(entryType)) {
			normalizedEntryType = NormalizedEntryTypes.phdthesis.name();
		}
		if (BibTexUtils.MASTERS_THESIS.equals(entryType)) {
			normalizedEntryType = NormalizedEntryTypes.master_thesis.name();
		}
		if (BibTexUtils.THESIS.equals(entryType)) {
			normalizedEntryType = NormalizedEntryTypes.bachelor_thesis.name();
		}
		
		if (normalizedEntryType != null) {
			String type = bibtex.getType();
			if (type != null) {
				type = type.toLowerCase().trim();
				if ((type.contains("master") || type.equals("mathesis"))) {
					normalizedEntryType = NormalizedEntryTypes.master_thesis.name();
				} else if (type.contains("bachelor")) {
					normalizedEntryType = NormalizedEntryTypes.bachelor_thesis.name();
				} else if (type.contains("habil")) {
					normalizedEntryType = NormalizedEntryTypes.habilitation.name();
				} else if (type.equals("candthesis")) {
					normalizedEntryType = NormalizedEntryTypes.candidate_thesis.name();
				}
			}
		} else {
			normalizedEntryType = bibtex.getEntrytype();
		}
		return normalizedEntryType;
	}
	
	private static String serializeMainNames(final List<ResourcePersonRelation> rels, PersonResourceRelationType type) {
		if (rels == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (ResourcePersonRelation rel : rels) {
			if ((type != null) && (rel.getRelationType() != type)) {
				continue;
			}
			final Person person = rel.getPerson();
			if (person == null) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(PERSON_DELIMITER);
			}
			if (ValidationUtils.present(person.getAcademicDegree())) {
				sb.append(prepareNamePart(person.getAcademicDegree()));
			}
			sb.append(NAME_PART_DELIMITER);
			final PersonName name = person.getMainName();
			if (ValidationUtils.present(name.getFirstName())) {
				sb.append(prepareNamePart(name.getFirstName()));
			}
			sb.append(NAME_PART_DELIMITER);
			if (ValidationUtils.present(name.getLastName())) {
				sb.append(prepareNamePart(name.getLastName()));
			}
		}
		if (sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}
	
	private static String serializePersonIds(final List<ResourcePersonRelation> rels, PersonResourceRelationType type) {
		if (rels == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (ResourcePersonRelation rel : rels) {
			if ((type != null) && (rel.getRelationType() != type)) {
				continue;
			}
			final Person person = rel.getPerson();
			if (person == null) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(getRelatorCodeOrEmptyString(rel)).append(" ");
			sb.append(person.getPersonId());
		}
		if (sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}
	
	private static List<ResourcePersonRelation> readPersonRelationsFromIndex(Map<String, Object> result) {
		final List<ResourcePersonRelation> rels = new ArrayList<>();
		
		final String ids = (String) result.get(ESConstants.PERSON_ENTITY_IDS_FIELD_NAME);
		if (StringUtils.isEmpty(ids)) {
			return rels;
		}
		String[] parts = split(ids, " ");
		
		
		final int personIndexCtr[] = new int[PersonResourceRelationType.values().length];
		for (int i = 0; i+1 < parts.length; i += 2) {
			final String relatorCodeStr = parts[i].trim();
			final PersonResourceRelationType role = PersonResourceRelationType.getByRelatorCode(relatorCodeStr);
			final int personIndex = personIndexCtr[role.ordinal()]++;
			final String id = parts[i+1].trim();
			ResourcePersonRelation rel = new ResourcePersonRelation();
			rel.setRelationType(role);
			rel.setPersonIndex(personIndex);
			rel.setPerson(new Person());
			rel.getPerson().setPersonId(id);
			rels.add(rel);
		}
		
		final String namesField = (String) result.get(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME);
		final String[] names = split(namesField, PERSON_DELIMITER);
		if (names.length != rels.size()) {
			throw new IllegalStateException();
		}
		
		for (int i = 0; i < names.length; ++i) {
			String[] nameParts = split(names[i], NAME_PART_DELIMITER);
			if (nameParts.length < 3) {
				throw new IllegalStateException(); 
			}
			Person p = rels.get(i).getPerson();
			PersonName mainName = buildNameFromParts(nameParts, 1);
			p.setMainName(mainName);
			if (present(nameParts[0])) {
				p.setAcademicDegree(nameParts[0].trim());
			}
			if (nameParts.length % 2 != 1) {
				log.error("wrong number of name parts found for person " + p.getPersonId() + ": " + nameParts);
			} else {
				for (int namePartI = 3; namePartI < nameParts.length; namePartI += 2) {
					p.addName(buildNameFromParts(nameParts, namePartI));
				}
			}
		}
		return rels;
	}
	
	private static PersonName buildNameFromParts(String[] nameParts, int firstPartIndex) {
		PersonName name = new PersonName();
		if (present(nameParts[1])) {
			name.setFirstName(nameParts[firstPartIndex].trim());
		}
		if (present(nameParts[2])) {
			name.setLastName(nameParts[firstPartIndex+1].trim());
		}
		return name;
	}
	
	/**
	 * @param fieldName
	 * @param delimiter
	 * @return
	 */
	private static String[] split(String fieldName, String delimiter) {
		if (fieldName == null) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		String[] rVal = fieldName.split(delimiter);
		if ((rVal.length == 1) && (StringUtils.isEmpty(rVal[0]))) {
			return new String[0];
		}
		return rVal;
	}
	
	private static String getRelatorCodeOrEmptyString(ResourcePersonRelation rel) {
		PersonResourceRelationType type = rel.getRelationType();
		if (type == null) {
			log.error("relation without relatorcode: " + rel);
			return "";
		}
		return type.getRelatorCode();
	}
	
	/**
	 * @param value
	 * @return
	 */
	private static String prepareNamePart(String value) {
		return value.trim().replace(PERSON_DELIMITER, " ").replace(NAME_PART_DELIMITER, " ");
	}

}
