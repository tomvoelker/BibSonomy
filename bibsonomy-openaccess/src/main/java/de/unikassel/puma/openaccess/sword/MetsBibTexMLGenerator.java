package de.unikassel.puma.openaccess.sword;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.renderer.impl.JAXBRenderer;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.util.XmlUtils;
import org.xml.sax.SAXParseException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

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
import de.unikassel.puma.openaccess.sword.renderer.xml.PumaUserType;
import de.unikassel.puma.openaccess.sword.renderer.xml.StructMapType;


/**
 * Generates METS-XML-Files for publication depositing 
 * Supported types: METS/EPDCX
 *
 * METS/MODS could possibly be generated with XSLT 
 * 
 * @author:  sven
 * @version: $Id$
 * $Author$
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

	private final PumaRenderer xmlRenderer;

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
		this.xmlRenderer = new PumaRenderer(urlRenderer);
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
		this.post.getPost().setDescription(pumaData.getPost().getDescription());
		this.post.getPost().setContentId(pumaData.getPost().getContentId());
		this.post.getPost().setDate(pumaData.getPost().getDate());
		this.post.getPost().setGroups(pumaData.getPost().getGroups());
		this.post.getPost().setRanking(pumaData.getPost().getRanking());
		this.post.getPost().setResource(pumaData.getPost().getResource());
		this.post.getPost().setTags(pumaData.getPost().getTags());
		this.post.getPost().setUser(pumaData.getPost().getUser());

		this.post.setClassification(pumaData.getClassification());

		this.post.setAuthor(pumaData.getAuthor());
		this.post.setExaminstitution(pumaData.getExaminstitution());
		this.post.setAdditionaltitle(pumaData.getAdditionaltitle());
		this.post.setExamreferee(pumaData.getExamreferee());
		this.post.setPhdoralexam(pumaData.getPhdoralexam());
		this.post.setSponsors(pumaData.getSponsors());
		this.post.setAdditionaltitle(pumaData.getAdditionaltitle());
	}


	private class PumaRenderer extends JAXBRenderer {

		public PumaRenderer(final UrlRenderer urlRenderer) {
			super(urlRenderer);
		}

		protected PumaPost createPumaPost(final PumaData<? extends Resource> pumaData, final User userData)	throws InternServerException {
			final PumaPost myPost = new PumaPost();
			this.fillXmlPost(myPost, pumaData.getPost());

			/*
			 * delete unwanted data from post
			 * - remove all system tags. they should not be sent to repository
			 */
			final Iterator<TagType> tagIterator = myPost.getTag().iterator();
			while (tagIterator.hasNext()) {
				final TagType tag = tagIterator.next();

				if (SystemTagsUtil.isSystemTag(tag.getName())) {
					tagIterator.remove();
				}
			}
			
			/*
			 * remove url. there is no need for this url to be present in repository
			 */
			final BibtexType bibtex = myPost.getBibtex();
			bibtex.setUrl(null);
			myPost.setBibtex(bibtex);

			/*
			 * add more user informations
			 */
			if (null != userData) {
				if (null == myPost.getUser()) {
					myPost.setUser(new PumaUserType());
				}
				myPost.getUser().setName(userData.getName());
				myPost.getUser().setRealname(userData.getRealname());
				myPost.getUser().setEmail(userData.getEmail());
				myPost.getUser().setId(userData.getLdapId());
			}

			/*
			 * add additional metadata
			 */
			final Resource resource = pumaData.getPost().getResource();
			if (resource instanceof BibTex) {
				final BibTex bibtexResource = (BibTex) resource;
				bibtexResource.parseMiscField();
				if (null != myPost.getBibtex()) {
					if (null != bibtexResource.getMiscField("isbn")) {
						myPost.setISBN(bibtexResource.getMiscField("isbn"));
					} 
					if (null != bibtexResource.getMiscField("issn")) {
						myPost.setISSN(bibtexResource.getMiscField("issn"));
					} 
					if (null != bibtexResource.getMiscField("doi")) {
						myPost.setDOI(bibtexResource.getMiscField("doi"));
					} 
					if (null != bibtexResource.getMiscField("location")) {
						myPost.setLocation(bibtexResource.getMiscField("location"));
					} 
					if (null != bibtexResource.getMiscField("dcc")) {
						myPost.setDCC(bibtexResource.getMiscField("dcc"));
					} 
				}

				if (present(pumaData.getAuthor())) {
					for (final PersonName personName : pumaData.getAuthor()) {
						myPost.getAuthor().add(PersonNameUtils.serializePersonName(personName));
					}
				}

				if (null != pumaData.getExaminstitution()) {
					myPost.setExaminstitution(pumaData.getExaminstitution());
				}

				if (null != pumaData.getExamreferee()) {
					for (final String item : pumaData.getExamreferee()) {
						myPost.getExamreferee().add(item);
					}
				}

				if (null != pumaData.getPhdoralexam()) {
					myPost.setPhdoralexam(pumaData.getPhdoralexam());
				}

				if (null != pumaData.getSponsors()) {
					for (final String item : pumaData.getSponsors()) {
						myPost.getSponsors().add(item);
					}
				}

				if (null != pumaData.getAdditionaltitle()) {
					for (final String item : pumaData.getAdditionaltitle()) {
						myPost.getAdditionaltitle().add(item);
					}
				}

				if (null != pumaData.getClassification()) {					
					for (final Entry<String, List<String>> entry : pumaData.getClassification().entrySet()) {
						for (final String listValue : entry.getValue() ) {
							final PumaPost.Classification pptClassification = new PumaPost.Classification();
							pptClassification.setName(entry.getKey().toLowerCase(Locale.getDefault()).replaceAll("/ /",""));
							pptClassification.setValue(listValue);
							myPost.getClassification().add(pptClassification);
						}
					}
				}

				/*
				 * add publisher info / romeo sherpa 
				 */
				// TODO: get info from romeo/sherpa
				//myPost.setPublisherinfo("");
			}
			return myPost;
		}

		@Override
		protected JAXBContext getJAXBContext() throws JAXBException {
			return JAXBContext.newInstance("org.bibsonomy.rest.renderer.xml:de.unikassel.puma.openaccess.sword.renderer.xml", this.getClass().getClassLoader());
		}

		/**
		 * Initializes java xml bindings, builds the document and then marshalls
		 * it to the writer.
		 * @param writer 
		 * @param mets 
		 * 
		 * @throws InternServerException
		 *             if the document can't be marshalled
		 */
		private void serializeMets(final Writer writer, final Mets mets) throws InternServerException {
			try {
				// initialize context for java xml bindings
				final JAXBContext jc = this.getJAXBContext();

				// buildup document model
				final JAXBElement<Mets> webserviceElement = new JAXBElement<Mets>(new QName("http://www.loc.gov/METS/", "mets"), Mets.class, null, mets);

				// create a marshaller
				final Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd");
				
				/*
				 * configure namespace
				 */
				final NamespacePrefixMapper npmapper = new NamespacePrefixMapper() {

					private final String[] namespace_decls = new String[] {
							"mets", "http://www.loc.gov/METS/",
							"bib", "http://www.bibsonomy.org/2010/11/BibSonomy",
							"puma", "http://puma.uni-kassel.de/2010/11/PUMA-SWORD",
							"xsi", "http://www.w3.org/2001/XMLSchema-instance",
							"xlink", "http://www.w3.org/1999/xlink"
					};

					@Override
					public String getPreferredPrefix(final String arg0, final String arg1, final boolean arg2) {
						return null;
					}

					@Override
					public String[] getContextualNamespaceDecls() {
						return this.namespace_decls;
					}

					@Override
					public String[] getPreDeclaredNamespaceUris2() {
						return this.namespace_decls;
					}

				};
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", npmapper);

				if (this.validateXMLOutput) {
					// validate the XML produced by the marshaller
					marshaller.setSchema(schema);
				}

				// marshal to the writer
				marshaller.marshal(webserviceElement, writer);
			} catch (final JAXBException e) {
				final Throwable linkedException = e.getLinkedException();
				if (present(linkedException) && (linkedException.getClass() == SAXParseException.class)) {
					final SAXParseException ex = (SAXParseException) linkedException;
					throw new BadRequestOrResponseException(
							"Error while parsing XML (Line " + ex.getLineNumber() + ", Column "
							+ ex.getColumnNumber() + ": " + ex.getMessage()
					);				
				}						
				throw new InternServerException(e.toString());
			}
		}

	}

	public String generateMets() {
		/*
		 * Helfer
		 */
		final StringWriter sw = new StringWriter();
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
			//			fileItem.setGROUPID("sword-mets-fgid-0");
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

		/*
		 * unser Post
		 */
		final PumaPost pumaPost = this.xmlRenderer.createPumaPost(this.post, this.user);

		xmlData.getAny().add(pumaPost);

		this.xmlRenderer.serializeMets(sw, mets);

		log.debug(sw.toString());
		return XmlUtils.removeXmlControlCharacters(sw.toString());
	}	

}
