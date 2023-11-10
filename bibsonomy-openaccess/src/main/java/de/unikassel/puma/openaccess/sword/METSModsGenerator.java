/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
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
package de.unikassel.puma.openaccess.sword;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import de.unikassel.puma.openaccess.sword.renderer.xml.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.util.io.xml.FilterInvalidXMLCharsWriter;

/**
 * Generates METS-XML-Files for publication depositing using MODS format for the inner data
 *
 * @author kchoong
 */
@Getter
@Setter
public class METSModsGenerator extends AbstractMETSGenerator {
    private static final Log log = LogFactory.getLog(METSModsGenerator.class);

    public METSModsGenerator(UrlRenderer urlRenderer) {
        super(urlRenderer);
    }

    @Override
    public Mets generateMETS(PumaData<BibTex> pumaData) {
        final ObjectFactory objectFactory = new ObjectFactory();

        final Mets mets = objectFactory.createMets();
        mets.setID("sort-mets_mets");
        mets.setOBJID("sword-mets");
        mets.setLABEL("METS/MODS SWORD Item");
        mets.setPROFILE("METS/MODS SIP Profile 1.0");

        // METS Hdr
        final MetsType.MetsHdr metsHdr = objectFactory.createMetsTypeMetsHdr();
        final GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar currentDate;
        try {
            currentDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            metsHdr.setCREATEDATE(currentDate);
        } catch (final DatatypeConfigurationException e) {
            log.warn("DatatypeConfigurationException");
        }

        mets.setMetsHdr(metsHdr);

        final List<MetsType.MetsHdr.Agent> metsHdrAgentList = metsHdr.getAgent();
        final MetsType.MetsHdr.Agent metsHdrAgent = new MetsType.MetsHdr.Agent();
        metsHdrAgent.setROLE("CUSTODIAN");
        metsHdrAgent.setTYPE("ORGANIZATION");
        metsHdrAgent.setName("PUMA");
        metsHdrAgentList.add(metsHdrAgent);

        // METS DmdSec
        final List<MdSecType> dmdSec = mets.getDmdSec();
        final MdSecType mdSec = objectFactory.createMdSecType();
        mdSec.setID("sword-mets-dmd-1");
        mdSec.setGROUPID("sword-mets-dmd-1_group-1");
        dmdSec.add(mdSec);

        final MdSecType.MdWrap mdWrap = objectFactory.createMdSecTypeMdWrap();
        mdWrap.setLABEL("SWAP Metadata"); // TODO check what this is for
        mdWrap.setMIMETYPE("text/xml");
        mdWrap.setMDTYPE("MODS");
        mdSec.setMdWrap(mdWrap);

        final MdSecType.MdWrap.XmlData xmlData = objectFactory.createMdSecTypeMdWrapXmlData();
        mdWrap.setXmlData(xmlData);

        // METS MODS data for DmdSec
        ModsDefinition mods = generateMods(pumaData.getPost(), objectFactory);

        JAXBElement<ModsDefinition> modsJAXB = new JAXBElement<>(new QName("http://www.loc.gov/mods/v3", "mods"), ModsDefinition.class, null, mods);
        xmlData.getAny().add(modsJAXB);

        // METS FileSec
        final MetsType.FileSec metsFileSec = objectFactory.createMetsTypeFileSec();
        mets.setFileSec(metsFileSec);

        final MetsType.FileSec.FileGrp metsFileSecFileGrp = objectFactory.createMetsTypeFileSecFileGrp();
        final List<FileType> fileItemList = new ArrayList<>();

        metsFileSecFileGrp.setID("sword-mets-fgrp-1");
        metsFileSecFileGrp.setUSE("CONTENT");
        int filenumber = 0;
        for (final Document doc : pumaData.getPost().getResource().getDocuments()) {
            final FileType fileItem = new FileType();
            fileItem.setGROUPID("sword-mets-fgid-".concat(String.valueOf(filenumber)));
            fileItem.setID("sword-mets-file-".concat(String.valueOf(filenumber)));
            fileItem.setCHECKSUMTYPE("MD5");
            fileItem.setCHECKSUM(doc.getMd5hash());
            // TODO: if file is not pdf, set MIMEtype to something like binary data
            fileItem.setMIMETYPE("application/pdf");

            final FileType.FLocat fileLocat = new FileType.FLocat();
            fileLocat.setLOCTYPE("URL");
            fileLocat.setHref(doc.getFileName());
            fileItem.getFLocat().add(fileLocat);

            // add fileitem to filepointerlist for struct section
            fileItemList.add(fileItem);

            metsFileSecFileGrp.getFile().add(fileItem);
            filenumber++;
        }

        metsFileSec.getFileGrp().add(metsFileSecFileGrp);

        // METS structMap
        final StructMapType structMap = new StructMapType();

        structMap.setID("sword-mets-struct-1");
        structMap.setLABEL("structure");
        structMap.setTYPE("LOGICAL");

        final DivType div1 = new DivType();
        div1.setID("sword-mets-div-0");
        div1.getDMDID().add(mdSec);   // TODO check if msSec is correct, or this must be a string?
        div1.setTYPE("SWORD Object");

        final DivType div2 = new DivType();
        div2.setID("sword-mets-div-1");
        div2.setTYPE("File");

        for (final FileType fItem : fileItemList) {
            final DivType.Fptr fptr = new DivType.Fptr();
            fptr.setFILEID(fItem);
            div2.getFptr().add(fptr);
        }

        div1.getDiv().add(div2);
        structMap.setDiv(div1);
        mets.getStructMap().add(structMap);

        return mets;
    }

    private ModsDefinition generateMods(Post<BibTex> post, ObjectFactory objectFactory) {
        BibTex resource = post.getResource();
        ModsDefinition mods = objectFactory.createModsDefinition();
        List<Object> elements = mods.getModsGroup();

        // Type of resource, always text
        TypeOfResourceDefinition typeOfResource = objectFactory.createTypeOfResourceDefinition();
        typeOfResource.setValue("text");
        elements.add(typeOfResource);

        // Entrytype TODO probably needs to be converted
        GenreDefinition genre = objectFactory.createGenreDefinition();
        genre.setValue(resource.getEntrytype());
        elements.add(genre);

        // Title
        TitleInfoDefinition titleInfo = objectFactory.createTitleInfoDefinition();
        List<Object> titleInfoList = titleInfo.getTitleOrSubTitleOrPartNumber();
        StringPlusLanguage titleString = objectFactory.createStringPlusLanguage();
        titleString.setValue(resource.getTitle());
        titleInfoList.add(objectFactory.createTitle(titleString));
        elements.add(titleInfo);

        // Related Item (Journal and Book for inbook)
        if (present(resource.getBooktitle()) || present(resource.getJournal())) {
            RelatedItemDefinition relatedItem = objectFactory.createRelatedItemDefinition();
            relatedItem.setType("host");
            List<Object> relatedItemList = relatedItem.getModsGroup();

            // Build Journal/Book title
            TitleInfoDefinition relatedTitleInfo = objectFactory.createTitleInfoDefinition();
            List<Object> relatedTitleInfoList = relatedTitleInfo.getTitleOrSubTitleOrPartNumber();
            StringPlusLanguage journalBookTitle = objectFactory.createStringPlusLanguage();
            journalBookTitle.setValue(present(resource.getBooktitle()) ? resource.getBooktitle() : resource.getJournal());
            relatedTitleInfoList.add(objectFactory.createTitle(journalBookTitle));
            relatedItemList.add(relatedTitleInfo);

            // Build parts
            PartDefinition part = objectFactory.createPartDefinition();
            List<Object> partDetails = part.getDetailOrExtentOrDate();
            if (present(resource.getVolume())) {
                partDetails.add(generatePartDetail(resource.getVolume(), "volume", objectFactory));
            }
            if (present(resource.getChapter())) {
                partDetails.add(generatePartDetail(resource.getChapter(), "chapter", objectFactory));
            }
            if (present(resource.getNumber())) {
                partDetails.add(generatePartDetail(resource.getNumber(), "issue", objectFactory));
            }

            if (present(partDetails)) {
                relatedItemList.add(part);
            }
            elements.add(relatedItem);
        }

        // Personals (Authors)
        resource.getAuthor().forEach((author -> {
            NameDefinition nameObj = objectFactory.createNameDefinition();
            nameObj.setType("personal");
            NamePartDefinition familyNameObj = objectFactory.createNamePartDefinition();
            familyNameObj.setType("family");
            familyNameObj.setValue(author.getLastName());
            nameObj.getNamePartOrDisplayFormOrAffiliation().add(objectFactory.createNamePart(familyNameObj));
            NamePartDefinition givenNameObj = objectFactory.createNamePartDefinition();
            givenNameObj.setType("given");
            givenNameObj.setValue(author.getFirstName());
            nameObj.getNamePartOrDisplayFormOrAffiliation().add(objectFactory.createNamePart(givenNameObj));

            RoleDefinition roleObj = objectFactory.createRoleDefinition();
            RoleTermDefinition roleTermObj = objectFactory.createRoleTermDefinition();
            roleTermObj.setValue("author");
            roleObj.getRoleTerm().add(roleTermObj);
            nameObj.getNamePartOrDisplayFormOrAffiliation().add(objectFactory.createRole(roleObj));

            elements.add(nameObj);
        }));

        // Abstract
        if (present(resource.getAbstract())) {
            AbstractDefinition postAbstract = objectFactory.createAbstractDefinition();
            postAbstract.setValue(resource.getAbstract());
            elements.add(postAbstract);
        }

        // Subject with their topics (Tags)
        if (present(post.getTags())) {
            SubjectDefinition subject = objectFactory.createSubjectDefinition();
            List<JAXBElement<?>> topics = subject.getTopicOrGeographicOrTemporal();
            post.getTags().forEach((tag -> {
                StringPlusLanguagePlusAuthority tagString = objectFactory.createStringPlusLanguagePlusAuthority();
                tagString.setValue(tag.getName());
                topics.add(objectFactory.createTopic(tagString));
            }));
            elements.add(subject);
        }

        // Origin Info (Publisher, place and published dates)
        OriginInfoDefinition originInfo = objectFactory.createOriginInfoDefinition();
        List<JAXBElement<?>> originList = originInfo.getPlaceOrPublisherOrDateIssued();
        if (present(resource.getPublisher())) {
            StringPlusLanguagePlusSupplied publisherStr = objectFactory.createStringPlusLanguagePlusSupplied();
            publisherStr.setValue(resource.getPublisher());
            originList.add(objectFactory.createPublisher(publisherStr));
        }
        if (present(resource.getAddress())) {
            PlaceDefinition place = objectFactory.createPlaceDefinition();
            PlaceTermDefinition placeTerm = objectFactory.createPlaceTermDefinition();
            placeTerm.setValue(resource.getAddress());
            placeTerm.setType(CodeOrText.TEXT);
            List<PlaceTermDefinition> placeTermList = place.getPlaceTerm();
            placeTermList.add(placeTerm);
            originList.add(objectFactory.createPlace(place));
        }

        DateDefinition publishedDate = objectFactory.createDateDefinition();
        publishedDate.setEncoding("iso8601");
        publishedDate.setValue(BibTexUtils.getPublishedDate(resource));
        originList.add(objectFactory.createDateIssued(publishedDate));

        elements.add(originInfo);

        // Identifiers (DOI, ISBN, ISSN, etc.) TODO any others?
        Map<String, String> misc = resource.getMiscFields();
        if (present(misc)) {
            List<String> miscIdentifiers = Arrays.asList("doi", "isbn", "issn");
            miscIdentifiers.forEach(key -> {
                if (misc.containsKey(key)) {
                    IdentifierDefinition identifier = objectFactory.createIdentifierDefinition();
                    identifier.setType(key);
                    identifier.setValue(misc.get(key));
                    elements.add(identifier);
                }
            });

            // Language
            if (misc.containsKey("language")) {
                LanguageDefinition language = objectFactory.createLanguageDefinition();
                LanguageTermDefinition languageTerm = objectFactory.createLanguageTermDefinition();
                languageTerm.setType(CodeOrText.CODE);
                languageTerm.setAuthority("rfc3066");
                languageTerm.setValue(misc.get("language"));
                List<LanguageTermDefinition> languageTermList = language.getLanguageTerm();
                languageTermList.add(languageTerm);
                elements.add(language);
            }
        }

        // Access condition
        AccessConditionDefinition accessCondition = objectFactory.createAccessConditionDefinition();
        elements.add(accessCondition);

        return mods;
    }

    private DetailDefinition generatePartDetail(String number, String type, ObjectFactory objectFactory) {
        DetailDefinition detail = objectFactory.createDetailDefinition();
        detail.setType(type);
        List<JAXBElement<StringPlusLanguage>> detailList = detail.getNumberOrCaptionOrTitle();
        StringPlusLanguage numberStr = objectFactory.createStringPlusLanguage();
        numberStr.setValue(number);
        detailList.add(objectFactory.createNumber(numberStr));
        return detail;
    }

    private StringPlusLanguage generateStringPlusLanguage(String str, ObjectFactory objectFactory) {
        StringPlusLanguage stringObj = objectFactory.createStringPlusLanguage();
        stringObj.setValue(str);
        return stringObj;
    }

    @Override
    public void writeMETS(OutputStream outputStream) throws IOException {
        final Writer writer = new FilterInvalidXMLCharsWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        Mets mets = this.generateMETS(this.pumaData);
        this.xmlRenderer.serializeMETS(writer, mets);
    }

}
