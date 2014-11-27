/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.io.xml.FilterInvalidXMLCharsWriter;

import de.unikassel.puma.openaccess.sword.renderer.xml.DivType;
import de.unikassel.puma.openaccess.sword.renderer.xml.DivType.Fptr;
import de.unikassel.puma.openaccess.sword.renderer.xml.FileType;
import de.unikassel.puma.openaccess.sword.renderer.xml.FileType.FLocat;
import de.unikassel.puma.openaccess.sword.renderer.xml.MdSecType;
import de.unikassel.puma.openaccess.sword.renderer.xml.MdSecType.MdWrap;
import de.unikassel.puma.openaccess.sword.renderer.xml.MdSecType.MdWrap.XmlData;
import de.unikassel.puma.openaccess.sword.renderer.xml.Mets;
import de.unikassel.puma.openaccess.sword.renderer.xml.MetsType.FileSec;
import de.unikassel.puma.openaccess.sword.renderer.xml.MetsType.FileSec.FileGrp;
import de.unikassel.puma.openaccess.sword.renderer.xml.MetsType.MetsHdr;
import de.unikassel.puma.openaccess.sword.renderer.xml.MetsType.MetsHdr.Agent;
import de.unikassel.puma.openaccess.sword.renderer.xml.ObjectFactory;
import de.unikassel.puma.openaccess.sword.renderer.xml.PumaPost;
import de.unikassel.puma.openaccess.sword.renderer.xml.StructMapType;


/**
 * Generates METS-XML-Files for publication depositing 
 * Supported types: METS/EPDCX
 *
 * METS/MODS could possibly be generated with XSLT 
 * 
 * @author:  sven
 */
public class MetsBibTexMLGenerator {
	private static final Log log = LogFactory.getLog(MetsBibTexMLGenerator.class);

	/*
	 * FIXME: Check if this class should be thread-safe. If so, don't use 
	 * object attributes to store data.
	 */
	private PumaData<BibTex> post = null;
	private List<String> filenameList = null; 
	private User user = null; 

	private final METSRenderer xmlRenderer;

	/**
	 * @return the _user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the _user to set
	 */
	public void setUser(final User user) {
		this.user = user;
	}

	public MetsBibTexMLGenerator(final UrlRenderer urlRenderer) {
		this.xmlRenderer = new METSRenderer(urlRenderer);
		this.post = new PumaData<BibTex>();
	}

	public String getFilename(final int elementnumber) {
		if (this.filenameList.size() > elementnumber) {
			return this.filenameList.get(elementnumber);
		}
		
		return null;
	}

	/**
	 * @param filenameList the filenameList to set
	 */
	public void setFilenameList(final List<String> filenameList) {
		this.filenameList = filenameList;
	}

	/**
	 * sets metadata
	 * 
	 * @param pumaData
	 */
	public void setMetadata(final PumaData<BibTex> pumaData) {
		final Post<BibTex> pumaPost = pumaData.getPost();
		this.post.getPost().setDescription(pumaPost.getDescription());
		this.post.getPost().setContentId(pumaPost.getContentId());
		this.post.getPost().setDate(pumaPost.getDate());
		this.post.getPost().setGroups(pumaPost.getGroups());
		this.post.getPost().setRanking(pumaPost.getRanking());
		this.post.getPost().setResource(pumaPost.getResource());
		this.post.getPost().setTags(pumaPost.getTags());
		this.post.getPost().setUser(pumaPost.getUser());
		
		this.post.setClassification(pumaData.getClassification());
		
		this.post.setAuthor(pumaData.getAuthor());
		this.post.setExaminstitution(pumaData.getExaminstitution());
		this.post.setAdditionaltitle(pumaData.getAdditionaltitle());
		this.post.setExamreferee(pumaData.getExamreferee());
		this.post.setPhdoralexam(pumaData.getPhdoralexam());
		this.post.setSponsors(pumaData.getSponsors());
		this.post.setAdditionaltitle(pumaData.getAdditionaltitle());
	}

	public void writeMets(OutputStream outputStream) throws IOException {
		/*
		 * helper
		 */
		final Writer writer = new FilterInvalidXMLCharsWriter(new OutputStreamWriter(outputStream, StringUtils.CHARSET_UTF_8));
		final ObjectFactory objectFactory = new ObjectFactory();
		
		/*
		 * METS
		 */
		final Mets mets = objectFactory.createMets();
		mets.setID("sort-mets_mets");
		mets.setOBJID("sword-mets");
		mets.setLABEL("DSpace SWORD Item");
		mets.setPROFILE("DSpace METS SIP Profile 1.0");
		
		/*
		 * METS Hdr
		 */
		final MetsHdr metsHdr = objectFactory.createMetsTypeMetsHdr();
		
		final GregorianCalendar c = new GregorianCalendar();
		XMLGregorianCalendar currentDate;
		try {
			currentDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			metsHdr.setCREATEDATE(currentDate);
		} catch (final DatatypeConfigurationException e) {
			log.warn("DatatypeConfigurationException");
		}
		
		mets.setMetsHdr(metsHdr);
		
		final List<Agent> metsHdrAgentList = metsHdr.getAgent();
		final Agent metsHdrAgent = new Agent();
		metsHdrAgent.setROLE("CUSTODIAN");
		metsHdrAgent.setTYPE("ORGANIZATION");
		metsHdrAgent.setName("PUMA");
		
		metsHdrAgentList.add(metsHdrAgent);
		
		final List<MdSecType> dmdSec = mets.getDmdSec();
		
		final MdSecType mdSec = objectFactory.createMdSecType();
		mdSec.setID("sword-mets-dmd-1");
		mdSec.setGROUPID("sword-mets-dmd-1_group-1");
		dmdSec.add(mdSec);
		
		final MdWrap mdWrap = objectFactory.createMdSecTypeMdWrap();
		mdWrap.setMIMETYPE("text/xml");
		mdWrap.setMDTYPE("OTHER");
		mdWrap.setOTHERMDTYPE("BIBTEXML");
		mdSec.setMdWrap(mdWrap);
		
		final XmlData xmlData = objectFactory.createMdSecTypeMdWrapXmlData();
		mdWrap.setXmlData(xmlData);
		
		/*
		 * METS FileSec
		 */
		final FileSec metsFileSec = objectFactory.createMetsTypeFileSec();
		mets.setFileSec(metsFileSec);
		
		final FileGrp metsFileSecFileGrp = objectFactory.createMetsTypeFileSecFileGrp();
		final List<FileType> fileItemList = new ArrayList<FileType>(); 
		
		metsFileSecFileGrp.setID("sword-mets-fgrp-1");
		metsFileSecFileGrp.setUSE("CONTENT");
		int filenumber = 0;
		for (final Document doc : this.post.getPost().getResource().getDocuments()) {
			final FileType fileItem = new FileType();
			// fileItem.setGROUPID("sword-mets-fgid-0");
			fileItem.setID("sword-mets-file-".concat(String.valueOf(filenumber)));
			// TODO: if file is not pdf, set MIMEtype to something like binary data
			fileItem.setMIMETYPE("application/pdf");
			
			final FLocat fileLocat = new FLocat();
			fileLocat.setLOCTYPE("URL");
			fileLocat.setHref(doc.getFileName());
			fileItem.getFLocat().add(fileLocat);
			
			// add fileitem to filepointerlist for struct section
			fileItemList.add(fileItem);
			
			metsFileSecFileGrp.getFile().add(fileItem);
			filenumber++;
		}
		
		metsFileSec.getFileGrp().add(metsFileSecFileGrp);
		
		/*
		 * METS structMap
		 */
		final StructMapType structMap = new StructMapType();
		
		structMap.setID("sword-mets-struct-1");
		structMap.setLABEL("structure");
		structMap.setTYPE("LOGICAL");
		
		final DivType div1 = new DivType();
		div1.setID("sword-mets-div-1");
		div1.getDMDID().add(mdSec);   // TODO check if msSec is correct, or this must be a string?
		div1.setTYPE("SWORD Object");
		
		final DivType div2 = new DivType();
		div2.setID("sword-mets-div-2");
		div2.setTYPE("File");
		
		for (final FileType fItem : fileItemList) {
			final Fptr fptr = new Fptr();
			fptr.setFILEID(fItem);
			div2.getFptr().add(fptr);
		}
		
		div1.getDiv().add(div2);
		structMap.setDiv(div1);
		mets.getStructMap().add(structMap);
		
		/* our post */
		final PumaPost pumaPost = this.xmlRenderer.createPumaPost(this.post, this.user);
		xmlData.getAny().add(pumaPost);
		this.xmlRenderer.serializeMets(writer, mets);
		writer.close();
	}

}
