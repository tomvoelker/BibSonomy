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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.SwordException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.util.HashUtils;
import org.swordapp.client.AuthCredentials;
import org.swordapp.client.ClientConfiguration;
import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.ProtocolViolationException;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDClientException;
import org.swordapp.client.SWORDError;
import org.swordapp.client.ServiceDocument;

/**
 * SWORD Service
 * 
 * @author  sven
 */
@Getter
@Setter
public class SwordService {
	private static final Log log = LogFactory.getLog(SwordService.class);
	
	private static final String SWORD_FILETYPE = "application/zip";
	private static final String SWORD_FORMAT = "https://purl.org/net/sword-types/METSDSpaceSIP"; // TODO doesn't exist anymore

	/** name or url of SWORD server of repository */
	private String swordServer;

	/** port number of SWORD server */
	private int swordPort;

	/** user agent to send to SWORD server */
	private String swordUserAgent;

	/** url to SWORD service document, e.g.: "/sword/servicedocument" */
	private String swordDocumentUrl;

	/** url to deposit SWORD document, e.g. "http://servername:8080/sword/deposit/urn:nbn:de:hebis:12-3456" */
	private String swordDepositUrl;

	/** SWORD authentication username */
	private String swordUsername;

	/** SWORD authentication password */
	private String swordPassword;

	/** API logic interface factory */
	private LogicInterfaceFactory logicInterfaceFactory;

	/** Temp directory path to build zip-file for sword-deposit */
	private String tempPath;

	/** Deposit document path */
	private String documentPath;

	/** API URL renderer */
	private UrlRenderer urlRenderer;

	/**
	 * retrieve service document from sword server
	 * @return
	 */
	private ServiceDocument retrieveServicedocument() {
		// Internationalized Resource Identifiers
		String iri = this.swordServer + ":" + this.swordPort + this.swordDocumentUrl;
		ServiceDocument serviceDocument = null;
		final SWORDClient swordClient = this.createClient();

		try {
			serviceDocument = swordClient.getServiceDocument(iri, new AuthCredentials(this.swordUsername, this.swordPassword));
		} catch (final SWORDClientException e) {
			log.info("SWORDClientException! getServiceDocument" + e.getMessage());
		} catch (ProtocolViolationException e) {
			e.printStackTrace();
		}

		return serviceDocument;
	}
	
	/**
	 * Check if document service is available and repository contains configured deposit collection
	 *
	 * @param doc service document
	 * @param url deposit url
	 * @param accept "application/zip"
	 * @param acceptPackaging "https://purl.org/net/sword-types/METSDSpaceSIP"
	 * @return
	 */
	private boolean checkServiceDocument(final ServiceDocument doc, final String url, final String accept, final String acceptPackaging) {
		// TODO check service document
		return true;
	}
	
	
	/**
	 * collects all information to send Documents with metadata to repository
	 * @param pumaData 
	 * @param user
	 */
	public void submitDocument(final PumaData<?> pumaData, final User user) throws SwordException, FileNotFoundException {
		log.info("starting sword");
		// DepositResponse depositResponse = new DepositResponse(999);
		File swordZipFile = null;

		final Post<?> post = pumaData.getPost(); 
		
		/*
		 * retrieve ZIP-FILE
		 */
		if (post.getResource() instanceof BibTex) {
					
			// fileprefix
			final String fileID = HashUtils.getMD5Hash(user.getName().getBytes()) + "_" + post.getResource().getIntraHash();
					
			// Destination directory 
			final File destinationDirectory = new File(this.tempPath + "/" +fileID);

			// zip-filename
			swordZipFile = new File(destinationDirectory.getAbsoluteFile() + "/" + fileID + ".zip");

			final byte[] buffer = new byte[18024];
					
			log.info("getIntraHash = " + post.getResource().getIntraHash());

			/*
			 * get documents
			 */
			
			// At the moment, there are no Documents delivered by method parameter post.
			// retrieve list of documents from database - workaround
			
			// get documents for post and insert documents into post 
			final BibTex publication = (BibTex) post.getResource();
			publication.setDocuments(this.retrieveDocumentsFromDatabase(user, post.getResource().getIntraHash()));
			
			if (!present(publication.getDocuments())) { 
				// we need at least one document to send it the repository
				log.info("throw SwordException: noPDFattached");
				throw new SwordException("error.sword.noPDFattached");
			}
					
			try {
				// create directory
				final boolean mkdir_success = (new File(destinationDirectory.getAbsolutePath())).mkdir();
				if (mkdir_success) {
					log.info("Directory: " + destinationDirectory.getAbsolutePath() + " created");
				}
						
				// open zip archive to add files to
				log.info("zipFilename: " + swordZipFile);
				final ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(swordZipFile));
				
				final List<String> fileList = new ArrayList<String>();
				for (final Document document : publication.getDocuments()) {
					// get file and store it in hard coded folder "/tmp/"
					fileList.add(document.getFileName());
					final ZipEntry zipEntry = new ZipEntry(document.getFileName());
		
					// Set the compression ratio
					zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
					
					final String inputFilePath = this.documentPath + document.getFileHash().substring(0, 2) + "/" + document.getFileHash();
					final FileInputStream in = new FileInputStream(inputFilePath);
		
					// Add ZIP entry to output stream.
					zipOutputStream.putNextEntry(zipEntry);
					
					// transfer bytes from the current file to the ZIP file
					int len;
					while ((len = in.read(buffer)) > 0) {
						zipOutputStream.write(buffer, 0, len);
					}
					
					zipOutputStream.closeEntry();
									
					// close the current file input stream
					in.close();
				}

				// write meta data into zip archive
				final ZipEntry zipEntry = new ZipEntry("mets.xml");
				zipOutputStream.putNextEntry(zipEntry);

				// create XML-Document
				
				final MetsBibTexMLGenerator metsBibTexMLGenerator = new MetsBibTexMLGenerator(this.urlRenderer);
				metsBibTexMLGenerator.setUser(user);
				metsBibTexMLGenerator.setFilenameList(fileList);
				
				metsBibTexMLGenerator.setMetadata((PumaData<BibTex>) pumaData);
				metsBibTexMLGenerator.writeMets(zipOutputStream);
				zipOutputStream.closeEntry();
				
				// close zip archive  
				zipOutputStream.close();
				
				log.debug("saved to " + swordZipFile.getPath());
			} catch (final MalformedURLException e) {
				log.info("MalformedURLException! " + e.getMessage());
			} catch (final IOException e) {
				log.info("IOException! ", e);
			} catch (final ObjectNotFoundException e) {
				log.warn("ResourceNotFoundException! SwordService-retrievePost");
			}
		}
		/*
		 * end of retrieve ZIP-FILE
		 */
		
		/*
		 * do the SWORD stuff
		 */

		if (swordZipFile != null) {

			// create sword client
			final SWORDClient swordClient = this.createClient();
			
			/*
			 * message file
			 * create directory in temp-folder
			 * store post documents there
			 * store meta data there in format http://purl.org/net/sword-types/METSDSpaceSIP
			 * delete post document files and meta data file
			 */
			
//			final PostMessage swordMessage = new PostMessage();
			// message meta
//			swordMessage.setNoOp(false);
//			swordMessage.setUserAgent(this.repositoryConfig.getHttpUserAgent());
//			swordMessage.setFilepath(swordZipFile.getAbsolutePath());
//			swordMessage.setFiletype("application/zip");
//			swordMessage.setFormatNamespace("http://purl.org/net/sword-types/METSDSpaceSIP"); // sets packaging!
//			swordMessage.setVerbose(false);

			Deposit deposit = new Deposit();
			deposit.setFile(new FileInputStream(swordZipFile));
			deposit.setMimeType("application/zip");
			deposit.setFilename(swordZipFile.getName());
			deposit.setPackaging("http://purl.org/net/sword/package/METSDSpaceSIP");
			//deposit.setMd5(); TODO needed?
			deposit.setInProgress(true);
			//deposit.setSuggestedIdentifier(); TODO needed?

			try {
				// check deposit URL against service document
				if (this.checkServiceDocument(this.retrieveServicedocument(), this.swordDocumentUrl, SWORD_FILETYPE, SWORD_FORMAT)) {

					// transmit sword message (zip file with document metadata and document files
//					swordMessage.setDestination(this.repositoryConfig.getHttpDepositUrl());
//
//					depositResponse = swordClient.postFile(swordMessage);

					DepositReceipt receipt = swordClient.deposit(
							this.swordDepositUrl,
							deposit,
							new AuthCredentials(this.swordUsername, this.swordPassword)
					);

					log.info("SWORD deposit status code: " + receipt.getStatusCode());
					//throw new SwordException("error.sword.errcode"+depositResponse.getHttpResponse());
				}

			} catch (final SWORDClientException e) {
				log.warn("SWORDClientException: " + e.getMessage() + "\n" + e.getCause() + " / " + this.swordDepositUrl);
				throw new SwordException("error.sword.urlnotaccessable");
			} catch (ProtocolViolationException e) {
				e.printStackTrace();
			} catch (SWORDError swordError) {
				// TODO log?
				swordError.printStackTrace();
			}
		}
	}

	private SWORDClient createClient() {
		final ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setUserAgent(this.swordUserAgent);

		final SWORDClient swordClient = new SWORDClient(clientConfiguration);
		return swordClient;
	}
	
	/*
	 * Workaround method to retrieve 
	 */
	private List<Document> retrieveDocumentsFromDatabase(final User user, final String resourceHash) {
		final String username = user.getName();
		/*
		 * getting DB access
		 */
		log.info("getting database access for user " + username);
		final LogicInterface logic = this.logicInterfaceFactory.getLogicAccess(username, user.getApiKey());
		
		// get metadata for post
		try {
			final Post<? extends Resource> post = logic.getPostDetails(resourceHash, username); 
			if (post.getResource() instanceof BibTex) {
				
				// get documents for post
				return ((BibTex) post.getResource()).getDocuments();
			}			
		} catch (final ObjectNotFoundException e) {
			log.warn("ResourceNotFoundException! SwordService-retrieveDocumentsFromDatabase");
		} catch (final ObjectMovedException e) {
			log.warn("ObjectMovedException! SwordService-retrieveDocumentsFromDatabase");
		}
		return null;
	}

}
