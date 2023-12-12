/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search.es.index.converter.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.index.utils.FileContentExtractorService;
import org.bibsonomy.util.Sets;
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

	private static final Predicate<ResourcePersonRelation> buildPersonResourceRelationByTypesFilter(final PersonResourceRelationType ... types) {
		return (relation -> Sets.asSet(types).contains(relation.getRelationType()));
	}

	private static Map<Integer, ResourcePersonRelation> getPersonResourceRelationsByTypeIndexedByPersonIndex(final List<ResourcePersonRelation> personRelations, final Predicate<ResourcePersonRelation> selector) {
		if (!present(personRelations)) {
			return Collections.emptyMap();
		}

		final Map<Integer, ResourcePersonRelation> result = new HashMap<>();

		for (ResourcePersonRelation relation : personRelations) {
			if (selector.test(relation)) {
				result.put(relation.getPersonIndex(), relation);
			}
		}

		return result;
	}

	private FileContentExtractorService fileContentExtractorService;

	/**
	 * @param systemURI
	 * @param fileContentExtractorService
	 */
	public PublicationConverter(URI systemURI, final FileContentExtractorService fileContentExtractorService) {
		super(systemURI);
		this.fileContentExtractorService = fileContentExtractorService;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.converter.post.ResourceConverter#createNewResource()
	 */
	@Override
	protected BibTex createNewResource() {
		return new BibTex();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.converter.post.ResourceConverter#convertResourceInternal(org.bibsonomy.model.Resource, java.util.Map, boolean)
	 */
	@Override
	protected void convertResourceInternal(final Post<BibTex> post, Map<String, Object> source, final boolean loadDocuments) {
		final BibTex publication = post.getResource();
		publication.setAddress((String) source.get(Fields.Publication.ADDRESS));
		publication.setAnnote((String) source.get(Fields.Publication.ANNOTE));
		publication.setKey((String) source.get(Fields.Publication.KEY));
		publication.setAbstract((String) source.get(Fields.Publication.ABSTRACT));
		publication.setBibtexKey((String) source.get(Fields.Publication.BIBTEXKEY));
		publication.setBooktitle((String) source.get(Fields.Publication.BOOKTITLE));
		publication.setChapter((String) source.get(Fields.Publication.CHAPTER));
		publication.setCrossref((String) source.get(Fields.Publication.CROSSREF));
		publication.setDay((String) source.get(Fields.Publication.DAY));
		publication.setEdition((String) source.get(Fields.Publication.EDITION));

		setPersonNames(Fields.Publication.EDITORS, BibTex::setEditor, publication, source);
		setPersonNames(Fields.Publication.AUTHORS, BibTex::setAuthor, publication, source);
		publication.setEntrytype((String) source.get(Fields.Publication.ENTRY_TYPE));
		publication.setHowpublished((String) source.get(Fields.Publication.HOWPUBLISHED));
		publication.setInstitution((String) source.get(Fields.Publication.INSTITUTION));
		publication.setJournal((String) source.get(Fields.Publication.JOURNAL));
		publication.setMisc((String) source.get(Fields.Publication.MISC));
		publication.setMonth((String) source.get(Fields.Publication.MONTH));
		publication.setNote((String) source.get(Fields.Publication.NOTE));
		publication.setNumber((String) source.get(Fields.Publication.NUMBER));
		publication.setOrganization((String) source.get(Fields.Publication.ORGANIZATION));
		publication.setPages((String) source.get(Fields.Publication.PAGES));
		publication.setPrivnote((String) source.get(Fields.Publication.PRIVNOTE));
		publication.setPublisher((String) source.get(Fields.Publication.PUBLISHER));
		publication.setSchool((String) source.get(Fields.Publication.SCHOOL));
		publication.setSeries((String) source.get(Fields.Publication.SERIES));
		publication.setType((String) source.get(Fields.Publication.TYPE));
		publication.setUrl((String) source.get(Fields.Publication.URL));
		publication.setVolume((String) source.get(Fields.Publication.VOLUME));
		publication.setYear((String) source.get(Fields.Publication.YEAR));

		if (loadDocuments) {
			final String userName;
			final User user = post.getUser();
			if (present(user)) {
				userName = user.getName();
			} else {
				userName = null;
			}
			publication.setDocuments(convertDocuments(source.get(Fields.Publication.DOCUMENTS), userName));
		}
	}

	/**
	 * @param object
	 * @param userName
	 * @return
	 */
	private static List<Document> convertDocuments(final Object object, final String userName) {
		final LinkedList<Document> documents = new LinkedList<>();
		if (object instanceof List) {
			@SuppressWarnings("unchecked") final List<Map<String, String>> docMaps = (List<Map<String, String>>) object;
			for (Map<String, String> docMap : docMaps) {
				final Document document = new Document();
				document.setFileName(docMap.get(Fields.Publication.Document.NAME));
				document.setFileHash(docMap.get(Fields.Publication.Document.HASH));
				document.setMd5hash(docMap.get(Fields.Publication.Document.CONTENT_HASH));
				document.setDate(ElasticsearchUtils.parseDate(docMap.get(Fields.Publication.Document.DATE)));
				document.setUserName(userName);
				documents.add(document);
			}
		}
		return documents;
	}

	/**
	 * @param publication
	 * @param source
	 */
	private static void setPersonNames(final String fieldName, final BiConsumer<BibTex, List<PersonName>> personNameSetter, BibTex publication, Map<String, Object> source) {
		final Object rawPersonNamesFieldValue = source.get(fieldName);
		if (rawPersonNamesFieldValue instanceof List) {
			@SuppressWarnings("unchecked") final List<Map<String, String>> personNamesList = (List<Map<String, String>>) rawPersonNamesFieldValue;
			final StringBuilder personNameStringBuilder = new StringBuilder();

			final Iterator<Map<String, String>> personNameIterator = personNamesList.iterator();
			while (personNameIterator.hasNext()) {
				final Map<String, String> embeddedObject = personNameIterator.next();
				final String personname = embeddedObject.get(Fields.Publication.PERSON_NAME);
				personNameStringBuilder.append(personname);
				if (personNameIterator.hasNext()) {
					personNameStringBuilder.append(PersonNameUtils.PERSON_NAME_DELIMITER);
				}
			}

			personNameSetter.accept(publication, PersonNameUtils.discoverPersonNamesIgnoreExceptions(personNameStringBuilder.toString()));
		} else if (rawPersonNamesFieldValue != null) {
			log.error("person name not a list; was " + rawPersonNamesFieldValue.getClass());
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.converter.post.ResourceConverter#convertPostInternal(java.util.Map, org.bibsonomy.model.Post)
	 */
	@Override
	protected void convertPostInternal(final Map<String, Object> source, Post<BibTex> post) {
		post.setResourcePersonRelations(readPersonRelationsFromIndex(source));
		for (final ResourcePersonRelation rel : post.getResourcePersonRelations()) {
			rel.setPost(post);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.converter.post.ResourceConverter#convertResource(java.util.Map, org.bibsonomy.model.Resource)
	 */
	@Override
	protected void convertResource(final Map<String, Object> jsonDocument, Post<BibTex> post) {
		final BibTex resource = post.getResource();
		jsonDocument.put(Fields.Publication.ADDRESS, resource.getAddress());
		jsonDocument.put(Fields.Publication.ANNOTE, resource.getAnnote());
		jsonDocument.put(Fields.Publication.KEY, resource.getKey());
		jsonDocument.put(Fields.Publication.ABSTRACT, resource.getAbstract());
		jsonDocument.put(Fields.Publication.BIBTEXKEY, resource.getBibtexKey());
		jsonDocument.put(Fields.Publication.BOOKTITLE, resource.getBooktitle());
		jsonDocument.put(Fields.Publication.CHAPTER, resource.getChapter());
		jsonDocument.put(Fields.Publication.CROSSREF, resource.getCrossref());
		jsonDocument.put(Fields.Publication.DAY, resource.getDay());
		jsonDocument.put(Fields.Publication.EDITION, resource.getEdition());

		final List<ResourcePersonRelation> personResourceRelations = post.getResourcePersonRelations();
		final List<PersonName> editors = resource.getEditor();
		if (present(editors)) {
			jsonDocument.put(Fields.Publication.EDITORS, convertPersonNames(editors, personResourceRelations, PersonResourceRelationType.EDITOR));
		}

		final List<PersonName> authors = resource.getAuthor();
		if (present(authors)) {
			jsonDocument.put(Fields.Publication.AUTHORS, convertPersonNames(authors, personResourceRelations, PersonResourceRelationType.AUTHOR));
		}

		jsonDocument.put(Fields.Publication.ENTRY_TYPE, resource.getEntrytype());
		jsonDocument.put(Fields.Publication.HOWPUBLISHED, resource.getHowpublished());

		jsonDocument.put(Fields.Publication.INSTITUTION, resource.getInstitution());
		jsonDocument.put(Fields.Publication.JOURNAL, resource.getJournal());

		/*
		 * insert misc field
		 * parse it and insert all misc fields as separate fields in elasticsearch
		 */
		jsonDocument.put(Fields.Publication.MISC, resource.getMisc());
		if (!resource.isMiscFieldParsed()) {
			try {
				resource.parseMiscField();
			} catch (final InvalidModelException e) {
				log.warn("parsing misc field failed", e);
			}
		}

		/*
		 * handle the misc field
		 */
		final Map<String, String> parsedMiscField = resource.getMiscFields();
		if (present(parsedMiscField)) {
			// handle special misc fields
			for (final String specialMiscField : ESConstants.Fields.Publication.SPECIAL_MISC_FIELDS) {
				final String specialMiscFieldValue = getSpecialMiscFieldValue(parsedMiscField, specialMiscField);
				if (present(specialMiscFieldValue)) {
					jsonDocument.put(specialMiscField, specialMiscFieldValue);
				}
			}

			// convert all misc fields to a nested field
			final List<Map<String, String>> miscFields = new LinkedList<>();
			for (final Entry<String, String> miscFieldEntry : parsedMiscField.entrySet()) {
				final String key = normKey(miscFieldEntry.getKey());
				final String value = miscFieldEntry.getValue();

				final Map<String, String> miscField = new HashMap<>();
				miscField.put(Fields.Publication.MISC_KEY, key);
				miscField.put(Fields.Publication.MISC_VALUE, value);

				miscFields.add(miscField);
			}

			jsonDocument.put(Fields.Publication.MISC_FIELDS, miscFields);
		}

		jsonDocument.put(Fields.Publication.MONTH, normalizeMonth(resource.getMonth()));
		jsonDocument.put(Fields.Publication.NOTE, resource.getNote());
		jsonDocument.put(Fields.Publication.NUMBER, resource.getNumber());
		jsonDocument.put(Fields.Publication.ORGANIZATION, resource.getOrganization());
		jsonDocument.put(Fields.Publication.PAGES, resource.getPages());

		jsonDocument.put(Fields.Publication.PRIVNOTE, resource.getPrivnote());
		jsonDocument.put(Fields.Publication.PUBLISHER, resource.getPublisher());
		jsonDocument.put(Fields.Publication.SCHOOL, resource.getSchool());
		jsonDocument.put(Fields.Publication.SERIES, resource.getSeries());

		jsonDocument.put(Fields.Publication.TYPE, resource.getType());
		jsonDocument.put(Fields.Publication.URL, resource.getUrl());
		jsonDocument.put(Fields.Publication.VOLUME, resource.getVolume());

		jsonDocument.put(Fields.Publication.YEAR, resource.getYear());

		jsonDocument.put(Fields.Publication.DOCUMENTS, convertDocuments(resource.getDocuments()));
		buildSortingAttributesFromResource(jsonDocument, resource);
	}

	private static String getSpecialMiscFieldValue(Map<String, String> miscField, String key) {
		for (final Entry<String, String> miscFieldEntry : miscField.entrySet()) {
			if (key.equalsIgnoreCase(miscFieldEntry.getKey())) {
				return miscFieldEntry.getValue();
			}
		}

		return null;
	}

	/**
	 * @param documents
	 * @return the converted documents
	 */
	public List<Map<String, String>> convertDocuments(final List<Document> documents) {
		final List<Map<String, String>> list = new LinkedList<>();
		if (!present(documents)) {
			return list;
		}

		for (final Document document : documents) {
			final Map<String, String> documentMap = new HashMap<>();
			documentMap.put(Fields.Publication.Document.NAME, document.getFileName());
			documentMap.put(Fields.Publication.Document.HASH, document.getFileHash());
			documentMap.put(Fields.Publication.Document.CONTENT_HASH, document.getMd5hash());
			final String content = this.fileContentExtractorService.extractContent(document);
			if (present(content)) {
				documentMap.put(Fields.Publication.Document.TEXT, content);
			}
			documentMap.put(Fields.Publication.Document.DATE, ElasticsearchUtils.dateToString(document.getDate()));
			list.add(documentMap);
		}

		return list;
	}

	private static void buildSortingAttributesFromResource(Map<String, Object> jsonDocument, BibTex resource) {
		jsonDocument.put(Fields.Sort.BOOKTITLE, BibTexUtils.cleanBibTex(resource.getBooktitle()));
		jsonDocument.put(Fields.Sort.JOURNAL, BibTexUtils.cleanBibTex(resource.getJournal()));
		jsonDocument.put(Fields.Sort.SERIES, BibTexUtils.cleanBibTex(resource.getSeries()));
		jsonDocument.put(Fields.Sort.PUBLISHER, BibTexUtils.cleanBibTex(resource.getPublisher()));
		jsonDocument.put(Fields.Sort.SCHOOL, BibTexUtils.cleanBibTex(resource.getSchool()));
		jsonDocument.put(Fields.Sort.INSTITUTION, BibTexUtils.cleanBibTex(resource.getInstitution()));
		jsonDocument.put(Fields.Sort.ORGANIZATION, BibTexUtils.cleanBibTex(resource.getOrganization()));
		if (present(resource.getEditor())) {
			String eds = BibTexUtils.cleanBibTex(convertToPersonIndex(resource.getEditor()));
			jsonDocument.put(Fields.Sort.EDITOR, eds);
			// adding editors as authors first as fallback, when publication has no authors
			jsonDocument.put(Fields.Sort.AUTHOR, eds);
		}
		if (present(resource.getAuthor())) {
			jsonDocument.put(Fields.Sort.AUTHOR, BibTexUtils.cleanBibTex(convertToPersonIndex(resource.getAuthor())));
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private static String normKey(String key) {
		return org.bibsonomy.util.StringUtils.removeNonNumbersOrLetters(key).toLowerCase();
	}

	/**
	 * Convert the month string into a numeric string for sorting with index.
	 *
	 * @param month
	 * @return
	 */
	private static String normalizeMonth(String month) {
		if (present(month)) {
			String normMonth = BibTexUtils.getMonthAsNumber(month);
			if (normMonth.length() == 1) {
				normMonth = "0" + normMonth;
			}
			return normMonth;
		}
		return month;
	}

	/**
	 * @param persons
	 * @return
	 */
	private static List<Map<String, String>> convertPersonNames(final List<PersonName> persons, final List<ResourcePersonRelation> personRelations, final PersonResourceRelationType requiredType) {
		final Map<Integer, ResourcePersonRelation> personIndexRelationMap = getPersonResourceRelationsByTypeIndexedByPersonIndex(personRelations, buildPersonResourceRelationByTypesFilter(requiredType));
		final List<Map<String, String>> serializedPersonNames = new LinkedList<>();

		// XXX: zipWithIndex would be great here
		int index = 0;
		for (final PersonName person : persons) {
			final Map<String, String> convertedPerson = new HashMap<>();
			convertedPerson.put(Fields.Publication.PERSON_NAME, PersonNameUtils.serializePersonName(person));

			/*
			 * if there is a person resource relation at the current index add the person id
			 * and the college of the person to the nested field
			 */
			final Integer key = Integer.valueOf(index);
			if (personIndexRelationMap.containsKey(key)) {
				final Person claimedPerson = personIndexRelationMap.get(key).getPerson();
				convertedPerson.put(Fields.Publication.PERSON_ID, claimedPerson.getPersonId());
				convertedPerson.put(Fields.Publication.PERSON_COLLEGE, claimedPerson.getCollege());
			}

			serializedPersonNames.add(convertedPerson);
			index++;

		}

		return serializedPersonNames;
	}

	/**
	 * @param persons
	 * @return
	 */
	private static String convertToPersonIndex(List<PersonName> persons) {
		/*
		 * Some publications are having too many persons for a keyword type.
		 * We limit the number of persons to 10, that will be used for sorting.
		 * NOTE: This will not affect the actual document in the index, just the sorting.
		 */
		if (persons.size() > 10) {
			return PersonNameUtils.serializePersonNames(persons.subList(0, 10), true, " ");
		}
		return PersonNameUtils.serializePersonNames(persons, true, " ");
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.converter.post.ResourceConverter#convertPostInternal(java.util.Map, org.bibsonomy.model.Post)
	 */
	@Override
	protected void convertPostInternal(final Post<BibTex> post, final Map<String, Object> jsonDocument) {
		jsonDocument.put(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, getNormalizedEntryType(post));
	}

	/** TODO: remove!
	 * @param jsonDocument
	 * @param rels
   */
	@Deprecated
	public void updateDocumentWithPersonRelation(final Map<String, Object> jsonDocument, final List<ResourcePersonRelation> rels) {
		jsonDocument.put(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME, serializeMainNames(rels, PersonResourceRelationType.AUTHOR));
		jsonDocument.put(ESConstants.AUTHOR_ENTITY_IDS_FIELD_NAME, serializePersonIds(rels, PersonResourceRelationType.AUTHOR));
		jsonDocument.put(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME, serializeMainNames(rels, null));
		jsonDocument.put(Fields.PERSON_ENTITY_IDS_FIELD_NAME, serializePersonIds(rels, null));
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

		final String ids = (String) result.get(Fields.PERSON_ENTITY_IDS_FIELD_NAME);
		if (StringUtils.isEmpty(ids)) {
			return rels;
		}
		String[] parts = split(ids, " ");

		final int personIndexCtr[] = new int[PersonResourceRelationType.values().length];
		for (int i = 0; i + 1 < parts.length; i += 2) {
			final String relatorCodeStr = parts[i].trim();
			final PersonResourceRelationType role = PersonResourceRelationType.getByRelatorCode(relatorCodeStr);
			final int personIndex = personIndexCtr[role.ordinal()]++;
			final String id = parts[i + 1].trim();
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
			name.setLastName(nameParts[firstPartIndex + 1].trim());
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
