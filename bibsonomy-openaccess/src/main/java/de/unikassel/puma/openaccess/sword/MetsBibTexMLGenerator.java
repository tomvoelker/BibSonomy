package de.unikassel.puma.openaccess.sword;


import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
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
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.impl.JAXBRenderer;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.xml.sax.SAXParseException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.unikassel.puma.openaccess.sword.renderer.xml.DivType;
import de.unikassel.puma.openaccess.sword.renderer.xml.FileType;
import de.unikassel.puma.openaccess.sword.renderer.xml.MdSecType;
import de.unikassel.puma.openaccess.sword.renderer.xml.Mets;
import de.unikassel.puma.openaccess.sword.renderer.xml.ObjectFactory;
import de.unikassel.puma.openaccess.sword.renderer.xml.PumaPostType;
import de.unikassel.puma.openaccess.sword.renderer.xml.StructMapType;
import de.unikassel.puma.openaccess.sword.renderer.xml.DivType.Fptr;
import de.unikassel.puma.openaccess.sword.renderer.xml.FileType.FLocat;
import de.unikassel.puma.openaccess.sword.renderer.xml.MdSecType.MdWrap;
import de.unikassel.puma.openaccess.sword.renderer.xml.MdSecType.MdWrap.XmlData;
import de.unikassel.puma.openaccess.sword.renderer.xml.MetsType.FileSec;
import de.unikassel.puma.openaccess.sword.renderer.xml.MetsType.MetsHdr;
import de.unikassel.puma.openaccess.sword.renderer.xml.MetsType.FileSec.FileGrp;
import de.unikassel.puma.openaccess.sword.renderer.xml.MetsType.MetsHdr.Agent;


/**
 * Generates METS-XML-Files for publication depositing 
 * Supported types: METS/EPDCX
 *
 * METS/MODS could possibly be generated with XSLT 
 * 
 * @author:  sven
 * @version: $Id$
 * $Author$
 * 
 */
public class MetsBibTexMLGenerator {
	private static final Log log = LogFactory.getLog(MetsBibTexMLGenerator.class);

	/*
	 * FIXME: Check if this class should be thread-safe. If so, don't use 
	 * object attributes to store data.
	 * 
	 */
	private PumaData<BibTex> _post;
	private ArrayList<String> _filenameList; 

	// contains special characters, symbols, etc...
	private static Properties chars = new Properties();


    

	public MetsBibTexMLGenerator() {
		this._post = new PumaData<BibTex>();
	}

	public String getFilename(int elementnumber) {
		if (_filenameList.size() > elementnumber) {
			return _filenameList.get(elementnumber);
		} else {
			return null;
		}
	}


	public void setFilenameList(ArrayList<String> filenameList) {
		_filenameList = filenameList;
	}


	/**
	 * Fills url and title of bookmark.
	 * 
	 * @param url
	 * @return
	 */

	public void setMetadata(PumaData<BibTex> pumaData) {
		_post.getPost().setDescription(pumaData.getPost().getDescription());
		_post.getPost().setContentId(pumaData.getPost().getContentId());
		_post.getPost().setDate(pumaData.getPost().getDate());
		_post.getPost().setGroups(pumaData.getPost().getGroups());
		_post.getPost().setRanking(pumaData.getPost().getRanking());
		_post.getPost().setResource(pumaData.getPost().getResource());
		_post.getPost().setTags(pumaData.getPost().getTags());
		_post.getPost().setUser(pumaData.getPost().getUser());
		
		_post.setClassification(pumaData.getClassification());

		_post.setExaminstitution(pumaData.getExaminstitution());
		_post.setAdditionaltitle(pumaData.getAdditionaltitle());
		_post.setExamreferee(pumaData.getExamreferee());
		_post.setPhdoralexam(pumaData.getPhdoralexam());
		_post.setSponsors(pumaData.getSponsors());
		_post.setAdditionaltitle(pumaData.getAdditionaltitle());	
		
	}


	
	private class PumaRenderer extends JAXBRenderer {
		
		protected PumaPostType createPumaPost(final PumaData<? extends Resource> pumaData)	throws InternServerException {

			final PumaPostType myPost = new PumaPostType();
			
			fillXmlPost(myPost, pumaData.getPost());

			
			/*
			 * delete unwanted data from post
			 */
			
			/*
			 * remove from post
			 */
			
			/*
			 *  remove all system tags. they should not be sent to repository
			 */
			List<TagType> tags = myPost.getTag();
			Iterator<TagType> tagIterator = tags.iterator();
			while (tagIterator.hasNext()) {
				if (tagIterator.next().getName().startsWith("sys:")) {
					tagIterator.remove();
				}
			}
			
			
			/*
			 * remove from bibtex 
			 */
			
			/*
			 * remove url. there is no need for this url to be present in repository
			 */
			final BibtexType bibtex = myPost.getBibtex();
			bibtex.setUrl(null);
			
			myPost.setBibtex(bibtex);

			
			
			/*
			 * add additional metadata
			 */
			final Resource resource = pumaData.getPost().getResource();
			if (resource instanceof BibTex) {
				final BibTex bibtexResource = (BibTex) resource;
				bibtexResource.parseMiscField();
				if (null != myPost.getBibtex()) {
					if (null != bibtexResource.getMiscField("isbn")) 		myPost.setISBN(bibtexResource.getMiscField("isbn")); 
					if (null != bibtexResource.getMiscField("issn"))		myPost.setISSN(bibtexResource.getMiscField("issn")); 
					if (null != bibtexResource.getMiscField("doi")) 		myPost.setDOI(bibtexResource.getMiscField("doi")); 
					if (null != bibtexResource.getMiscField("location"))	myPost.setLocation(bibtexResource.getMiscField("location")); 
					if (null != bibtexResource.getMiscField("dcc"))			myPost.setDCC(bibtexResource.getMiscField("dcc")); 
				}
			
				
				if (null != pumaData.getExaminstitution()) {
					myPost.setExaminstitution(pumaData.getExaminstitution());
				}
				
				if (null != pumaData.getExamreferee()) {
					for (String item : pumaData.getExamreferee()) {
						myPost.getExamreferee().add(item);
					}
				}
				
				if (null != pumaData.getPhdoralexam()) {
					myPost.setPhdoralexam(pumaData.getPhdoralexam());
				}
				
				if (null != pumaData.getSponsors()) {
					for (String item : pumaData.getSponsors()) {
						myPost.getSponsors().add(item);
					}
				}
				
				if (null != pumaData.getAdditionaltitle()) {
					for (String item : pumaData.getAdditionaltitle()) {
					myPost.getAdditionaltitle().add(item);
					}
				}
				
				if (null != pumaData.getClassification()) {					
					for (Entry<String, List<String>> entry : pumaData.getClassification().entrySet()) {
						for (String listValue : entry.getValue() ) {
							PumaPostType.Classification pptClassification = new PumaPostType.Classification();
							pptClassification.setName(entry.getKey());
							pptClassification.setValue(listValue);
							myPost.getClassification().add(pptClassification);
						}
					}
				}
			}
			return myPost;
		}
		
		@Override
		protected JAXBContext getJAXBContext() throws JAXBException {
			return JAXBContext.newInstance("org.bibsonomy.rest.renderer.xml:de.unikassel.puma.openaccess.sword.renderer.xml", this.getClass().getClassLoader());
		}

		@Override
		public RenderingFormat getRenderingFormat() {
			return RenderingFormat.XML;
		}
		
		/**
		 * Initializes java xml bindings, builds the document and then marshalls
		 * it to the writer.
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
				
				
				//xsi:schemaLocation="http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd";>

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
					public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
						
						//return "mets";
					
						
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String[] getContextualNamespaceDecls() {
						return namespace_decls;
					}
					
					@Override
					public String[] getPreDeclaredNamespaceUris2() {
						
						//return new String[] { "mets", "http://www.loc.gov/METS/" };
						return namespace_decls;
						
						//return null;
					}
					
				};
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", npmapper);
				
				if (this.validateXMLOutput) {
					// validate the XML produced by the marshaller
					marshaller.setSchema(schema);
				}

				// marshal to the writer
				marshaller.marshal(webserviceElement, writer);
				// TODO log
				// log.debug("");
			} catch (final JAXBException e) {
				final Throwable linkedException = e.getLinkedException();
				if (present(linkedException) && linkedException.getClass() == SAXParseException.class) {
					final SAXParseException ex = (SAXParseException) linkedException;
					throw new BadRequestOrResponseException(
							"Error while parsing XML (Line " 
							+ ex.getLineNumber() + ", Column "
							+ ex.getColumnNumber() + ": "
							+ ex.getMessage()
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
		final PumaRenderer xmlRenderer = new PumaRenderer();
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


		//mets xmlns=\"http://www.loc.gov/METS/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd\">\n";
		
		/*
		 * METS Hdr
		 */

		final MetsHdr metsHdr = objectFactory.createMetsTypeMetsHdr();
		
		
		GregorianCalendar c = new GregorianCalendar();
		XMLGregorianCalendar currentDate;
		try {
			currentDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			metsHdr.setCREATEDATE(currentDate);
		} catch (DatatypeConfigurationException e) {
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
		
		metsFileSecFileGrp.setID("sword-mets-fgrp-1");
		metsFileSecFileGrp.setUSE("CONTENT");
		
		FileType fileItem = new FileType();
		fileItem.setGROUPID("sword-mets-fgid-0");
		fileItem.setID("sword-mets-file-1");
		fileItem.setMIMETYPE("application/pdf");
		
		
		for(Document doc : _post.getPost().getResource().getDocuments()) {
			FLocat fileLocat = new FLocat();
			fileLocat.setLOCTYPE("URL");
			fileLocat.setHref(doc.getFileName());
			fileItem.getFLocat().add(fileLocat);
		}		
		
		
		metsFileSecFileGrp.getFile().add(fileItem);
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
		
		Fptr fptr = new Fptr();
		fptr.setFILEID(fileItem);
		
		div2.getFptr().add(fptr);
		
		//xmlDocument += "<div ID=\"sword-mets-div-1\" DMDID=\"sword-mets-dmd-1\" TYPE=\"SWORD Object\">\n";
		// fptr
		//xmlDocument += "<fptr FILEID=\"sword-mets-file-1\"/>\n";

		div1.getDiv().add(div2);
		
		structMap.setDiv(div1);

		mets.getStructMap().add(structMap);
		
		
		/*
		 * unser Post
		 */
		final PumaPostType pumaPost = xmlRenderer.createPumaPost(_post);
		
		
		//derPost.set
		//"puma", "http://puma.uni-kassel.de/2010/11/PUMA-SWORD"
		
		xmlData.getAny().add(pumaPost);
		
		xmlRenderer.serializeMets(sw, mets);
		
		// DEBUG
		log.debug(sw.toString());
		
		return sw.toString();
			
	}	
	
}
