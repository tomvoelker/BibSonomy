package de.unikassel.puma.openaccess.sword;


import static org.bibsonomy.model.util.ModelValidationUtils.checkBookmark;
import static org.bibsonomy.model.util.ModelValidationUtils.checkGroup;
import static org.bibsonomy.model.util.ModelValidationUtils.checkPublication;
import static org.bibsonomy.model.util.ModelValidationUtils.checkTag;
import static org.bibsonomy.model.util.ModelValidationUtils.checkUser;
import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.impl.JAXBRenderer;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.DocumentType;
import org.bibsonomy.rest.renderer.xml.DocumentsType;
import org.bibsonomy.rest.renderer.xml.GoldStandardPublicationType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.ReferenceType;
import org.bibsonomy.rest.renderer.xml.ReferencesType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;
import org.xml.sax.SAXParseException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.unikassel.puma.openaccess.sword.renderer.xml.BibtexType;
import de.unikassel.puma.openaccess.sword.renderer.xml.DerPost;
import de.unikassel.puma.openaccess.sword.renderer.xml.DivType;
import de.unikassel.puma.openaccess.sword.renderer.xml.FileType;
import de.unikassel.puma.openaccess.sword.renderer.xml.MdSecType;
import de.unikassel.puma.openaccess.sword.renderer.xml.Mets;
import de.unikassel.puma.openaccess.sword.renderer.xml.ObjectFactory;
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


	private PumaPost<BibTex> _post;
	private ArrayList<String> _filenameList; 

	private static JabrefLayoutRenderer layoutRenderer;

	// contains special characters, symbols, etc...
	private static Properties chars = new Properties();


    

	public MetsBibTexMLGenerator() {
		this._post = new PumaPost<BibTex>();
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

	public void setMetadata(Post<BibTex> post) {
		_post.setClassification(null);
		_post.setDescription(post.getDescription());
		_post.setContentId(post.getContentId());
		_post.setDate(post.getDate());
		_post.setGroups(post.getGroups());
		_post.setRanking(post.getRanking());
		_post.setResource(post.getResource());
		_post.setTags(post.getTags());
		_post.setUser(post.getUser());
	}
	
	public void setMetadata(PumaPost<BibTex> post) {
		_post = post;
	}

	
	private class PumaPost<T extends Resource> extends Post<T> {
		private String classification;

		public String getClassification() {
			return this.classification;
		}

		public void setClassification(String classification) {
			this.classification = classification;
		}
		
	}
	
	private class PumaRenderer extends JAXBRenderer {
		
		protected DerPost createDerPost(Post<? extends Resource> post)	throws InternServerException {

			final DerPost myPostType = new DerPost();
			
			if (post instanceof PumaPost) {
				final PumaPost pumaPost = (PumaPost) post;
				myPostType.setMyAttribute(pumaPost.getClassification());
			}
			final Resource resource = post.getResource();

			fillXmlPost(myPostType, post);
			
			
			if (resource instanceof BibTex) {
				final BibTex bibtex = (BibTex) resource;
				bibtex.parseMiscField();
				if (null != myPostType.getBibtex()) {
					if (null != bibtex.getMiscField("isbn")) 		myPostType.getBibtex().setXISBN(bibtex.getMiscField("isbn")); 
					if (null != bibtex.getMiscField("issn"))		myPostType.getBibtex().setXISSN(bibtex.getMiscField("issn")); 
					if (null != bibtex.getMiscField("doi")) 		myPostType.getBibtex().setXDOI(bibtex.getMiscField("doi")); 
					if (null != bibtex.getMiscField("location"))	myPostType.getBibtex().setXLocation(bibtex.getMiscField("location")); 
					if (null != bibtex.getMiscField("dcc"))			myPostType.getBibtex().setXDCC(bibtex.getMiscField("dcc")); 
				}
				
			}
			return myPostType;
		}

		
		protected void fillXmlPost(final DerPost xmlPost, final Post<? extends Resource> post) {
			checkPost(post);

			// set user
			checkUser(post.getUser());
			final UserType xmlUser = new UserType();
			xmlUser.setName(post.getUser().getName());
			xmlUser.setHref(urlRenderer.createHrefForUser(post.getUser().getName()));
			xmlPost.setUser(xmlUser);
			if (post.getDate() != null)
				xmlPost.setPostingdate(createXmlCalendar(post.getDate()));

			// add tags
			if (post.getTags() != null) {
				for (final Tag t : post.getTags()) {
					checkTag(t);
					final TagType xmlTag = new TagType();
					xmlTag.setName(t.getName());
					xmlTag.setHref(urlRenderer.createHrefForTag(t.getName()));
					xmlPost.getTag().add(xmlTag);
				}
			}

			// add groups
			for (final Group group : post.getGroups()) {
				checkGroup(group);
				final GroupType xmlGroup = new GroupType();
				xmlGroup.setName(group.getName());
				xmlGroup.setHref(urlRenderer.createHrefForGroup(group.getName()));
				xmlPost.getGroup().add(xmlGroup);
			}

			xmlPost.setDescription(post.getDescription());
			
			// check if the resource is a publication
			final Resource resource = post.getResource();
			if (resource instanceof BibTex && !(resource instanceof GoldStandardPublication)) {
				final BibTex publication = (BibTex) post.getResource();
				checkPublication(publication);
				final BibtexType xmlBibtex = new BibtexType();

				xmlBibtex.setHref(urlRenderer.createHrefForResource(post.getUser().getName(), publication.getIntraHash()));

				fillXmlPublicationDetails(publication, xmlBibtex);

				xmlPost.setBibtex(xmlBibtex);
				
				// if the publication has documents …
				final List<Document> documents = publication.getDocuments();
				if (documents != null) {

					checkPublication(publication);
					// … put them into the xml output
					final DocumentsType xmlDocuments = new DocumentsType();
					for (final Document document : documents){
						final DocumentType xmlDocument = new DocumentType();
						xmlDocument.setFilename(document.getFileName());
						xmlDocument.setMd5Hash(document.getMd5hash());
						xmlDocument.setHref(urlRenderer.createHrefForResourceDocument(post.getUser().getName(), publication.getIntraHash(), document.getFileName()));
						xmlDocuments.getDocument().add(xmlDocument);
					}
					xmlPost.setDocuments(xmlDocuments);
				}
			}
			// if resource is a bookmark create a xml representation
			if (resource instanceof Bookmark) {
				final Bookmark bookmark = (Bookmark) post.getResource();
				checkBookmark(bookmark);
				final BookmarkType xmlBookmark = new BookmarkType();
				xmlBookmark.setHref(urlRenderer.createHrefForResource(post.getUser().getName(), bookmark.getIntraHash()));
				xmlBookmark.setInterhash(bookmark.getInterHash());
				xmlBookmark.setIntrahash(bookmark.getIntraHash());
				xmlBookmark.setTitle(bookmark.getTitle());
				xmlBookmark.setUrl(bookmark.getUrl());
				xmlPost.setBookmark(xmlBookmark);
			}
			
			if (resource instanceof GoldStandardPublication) {
				/*
				 * first clear tags; gold standard publications have (currently) no tags
				 */
				xmlPost.getTag().clear();
				
				final GoldStandardPublication publication = (GoldStandardPublication) post.getResource();
				
				final GoldStandardPublicationType xmlPublication = new GoldStandardPublicationType();
				this.fillXmlPublicationDetails(publication, xmlPublication);
				
				/*
				 * add references
				 */
				final ReferencesType xmlReferences = new ReferencesType();
				xmlPublication.setReferences(xmlReferences);

				final List<ReferenceType> referenceList = xmlReferences.getReference();
				
				for (final BibTex reference : publication.getReferences()) {
					final ReferenceType xmlReference = new ReferenceType();
					xmlReference.setInterhash(reference.getInterHash());
					
					referenceList.add(xmlReference);
				}
				
				xmlPost.setGoldStandardPublication(xmlPublication);
			}
			

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
		//metsHdr.setCREATEDATE();

		mets.setMetsHdr(metsHdr);

		final List<Agent> metsHdrAgentList = metsHdr.getAgent();
		final Agent metsHdrAgent = new Agent();
		metsHdrAgent.setROLE("CUSTODIAN");
		metsHdrAgent.setTYPE("ORGANIZATION");
		metsHdrAgent.setName("Sven Stefani");
		
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
		
		
		for(Document doc : _post.getResource().getDocuments()) {
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
		final DerPost derPost = xmlRenderer.createDerPost(_post);
		
		
		//derPost.set
		//"puma", "http://puma.uni-kassel.de/2010/11/PUMA-SWORD"
		
		xmlData.getAny().add(derPost);
		
		xmlRenderer.serializeMets(sw, mets);
		
		return sw.toString();
			
	}	
	
}
