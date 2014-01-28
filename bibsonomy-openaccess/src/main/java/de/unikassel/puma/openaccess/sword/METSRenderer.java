package de.unikassel.puma.openaccess.sword;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.renderer.impl.JAXBRenderer;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.xml.sax.SAXParseException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.unikassel.puma.openaccess.sword.renderer.xml.Mets;
import de.unikassel.puma.openaccess.sword.renderer.xml.PumaPost;
import de.unikassel.puma.openaccess.sword.renderer.xml.PumaUserType;

/**
 * @author: sven
 */
public class METSRenderer extends JAXBRenderer {

	/**
	 * default constructor with urlrenderer
	 * @param urlRenderer
	 */
	public METSRenderer(final UrlRenderer urlRenderer) {
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
				final String isbn = bibtexResource.getMiscField("isbn");
				if (present(isbn)) {
					myPost.setISBN(isbn);
				}
				final String issn = bibtexResource.getMiscField("issn");
				if (present(issn)) {
					myPost.setISSN(issn);
				}
				final String doi = bibtexResource.getMiscField("doi");
				if (present(doi)) {
					myPost.setDOI(doi);
				}
				final String location = bibtexResource.getMiscField("location");
				if (present(location)) {
					myPost.setLocation(location);
				}
				final String dcc = bibtexResource.getMiscField("dcc");
				if (present(dcc)) {
					myPost.setDCC(dcc);
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
	public void serializeMets(final Writer writer, final Mets mets) throws InternServerException {
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
