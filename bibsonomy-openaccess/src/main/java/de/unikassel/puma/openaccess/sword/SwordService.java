package de.unikassel.puma.openaccess.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.database.DBLogicApiInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.HashUtils;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDException;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.client.Client;
import org.purl.sword.client.PostMessage;
import org.purl.sword.client.SWORDClientException;




/**
 * Sword main
 * 
 * @author:  sven
 * @version: 
 * 
 */
public class SwordService {
	private static final Log log = LogFactory.getLog(SwordService.class);
	
	private static final String SWORDFILETYPE = "application/zip";
	private static final String SWORDFORMAT = "http://purl.org/net/sword-types/METSDSpaceSIP"; 
	
//	private String dirTemp;
//	private String httpServer;
//	private int httpPort;
//	private String httpUserAgent;
//	private String authUsername;
//	private String authPassword;
//	private String httpServicedocumentUrl;
//	private String httpDepositUrl;
	private SwordConfig repositoryConfig;
	
	private String projectDocumentPath;

	/**
	 * retrieve service document from sword server
	 * @return
	 */
	private ServiceDocument retrieveServicedocument(){
		ServiceDocument serviceDocument = null;
		// get an instance of SWORD-Client
		Client swordClient = new Client();
		swordClient.setServer(repositoryConfig.getHttpServer(), repositoryConfig.getHttpPort());
		swordClient.setUserAgent(repositoryConfig.getHttpUserAgent());
		swordClient.setCredentials(repositoryConfig.getAuthUsername(), repositoryConfig.getAuthPassword());
		try {
			serviceDocument = swordClient.getServiceDocument(repositoryConfig.getHttpServicedocumentUrl());
		} catch (SWORDClientException e) {
			log.info("SWORDClientException! getServiceDocument" + e.getMessage());
		}
		return serviceDocument;
	}
	
	/**
	 * Check if servicedocument is available and repository contains configured deposit collection   
	 * @param doc Servicedocument
	 * @param url deposit url
	 * @param accept "application/zip"
	 * @param acceptPackaging "http://purl.org/net/sword-types/METSDSpaceSIP"
	 * @return
	 */
	private boolean checkServicedokument(ServiceDocument doc, String url, String accept, String acceptPackaging) {
		// TODO: check service document
		return true;
	}
	
	
	/**
	 * collects all informations to send Documents with metadata to repository 
	 */
	public DepositResponse submitDocument(Post<?> post, User user) {
		log.info("starting sword");
		DepositResponse depositResponse = null; 
		File swordZipFile = null;

		// -------------------------------------------------------------------------------
		/*
		 * retrieve ZIP-FILE
		 */
		if (post.getResource() instanceof BibTex) {
					
			// fileprefix
			String fileID = HashUtils.getMD5Hash(user.getName().getBytes()) + "_"+post.getResource().getIntraHash();
					
			// Destination directory 
			File destinationDirectory = new File(repositoryConfig.getDirTemp()+"/"+fileID);

			// zip-filename
			swordZipFile = new File(destinationDirectory.getAbsoluteFile()+"/"+fileID+".zip");

			byte[] buffer = new byte[18024];
					
			log.info("getIntraHash = " + post.getResource().getIntraHash());

			/*
			 * get documents
			 */
			
			// At the moment, there are no Documents delivered by method parameter post.
			// retrieve list of documents from database - workaround
			
			// get documents for post and insert documents into post 
			((BibTex) post.getResource()).setDocuments(retrieveDocumentsFromDatabase(user, post.getResource().getIntraHash()));
			
					
			try {
				// create directory
				boolean mkdir_success = (new File(destinationDirectory.getAbsolutePath())).mkdir();
				if (mkdir_success) {
					log.info("Directory: " + destinationDirectory.getAbsolutePath() + " created");
				}    
				
						
				// open zip archive to add files to
				log.info("zipFilename: "+swordZipFile);
				ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(swordZipFile));
				
				ArrayList<String> fileList = new ArrayList<String>();
				
				for (final Document document : ((BibTex) post.getResource()).getDocuments()) {

					//getpostdetails
					// get file and store it in hard coded folder "/tmp/"
					//final Document document2 = logic.getDocument(user.getName(), post.getResource().getIntraHash(), document.getFileName());
					
					// move file to user folder with username_resource-hash as folder name
					
					// File (or directory) to be copied 
					//File fileToZip = new File(document.getFileHash());
					
					fileList.add(document.getFileName());
							
			
					
					// Move file to new directory 
					//boolean rename_success = fileToCopy.renameTo(new File(destinationDirectory, fileToMove.getName()));
					/*
					if (!rename_success) { 
						// File was not successfully moved } 
						log.info("File was not successfully moved: "+fileToMove.getName());
					}
					*/
					ZipEntry zipEntry = new ZipEntry(document.getFileName());
		
								
					// Set the compression ratio
					zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
		
					
					String inputFilePath = projectDocumentPath+document.getFileHash().substring(0, 2)+"/"+document.getFileHash();
					FileInputStream in = new FileInputStream(inputFilePath);
		
					// Add ZIP entry to output stream.
					zipOutputStream.putNextEntry(zipEntry);
					
					// Transfer bytes from the current file to the ZIP file
					//out.write(buffer, 0, in.read(buffer));
		
					int len;
					while ((len = in.read(buffer)) > 0)
					{
						zipOutputStream.write(buffer, 0, len);
					}
					
					zipOutputStream.closeEntry();
									
					// Close the current file input stream
					in.close();			
				}

				// write meta data into zip archive
				ZipEntry zipEntry = new ZipEntry("mets.xml");
				zipOutputStream.putNextEntry(zipEntry);				

				// create XML-Document
				// PrintWriter from a Servlet
						
				MetsBibTexMLGenerator metsBibTexMLGenerator = new MetsBibTexMLGenerator();
				metsBibTexMLGenerator.setFilenameList(fileList);
				//metsGenerator.setMetadata(metadataMap);
				metsBibTexMLGenerator.setMetadata((Post<BibTex>) post);
				//StreamResult streamResult = new StreamResult(zipOutputStream);
						
				zipOutputStream.write(metsBibTexMLGenerator.generateMets().getBytes());

				zipOutputStream.closeEntry();
							
				// close zip archive  
				zipOutputStream.close();
										
				log.debug("saved to "+swordZipFile.getPath());
						
			} catch (MalformedURLException e) {
				// e.printStackTrace();
				log.info("MalformedURLException! " + e.getMessage());
			} catch (IOException e) {
				//e.printStackTrace();
				log.info("IOException! " + e.getMessage());
				
			} catch (ResourceNotFoundException e) {
				// e.printStackTrace();
				log.warn("ResourceNotFoundException! SwordService-retrievePost");
			}
		}
		/*
		 * end of retrieve ZIP-FILE
		 */
		//---------------------------------------------------
		
		/*
		 * do the SWORD stuff
		 */

		if (null != swordZipFile) {

			// get an instance of SWORD-Client
			Client swordClient = new Client();

			PostMessage swordMessage = new PostMessage();

			// create sword post message

			// message file
			// create directory in temp-folder
			// store post documents there
			// store meta data there in format http://purl.org/net/sword-types/METSDSpaceSIP
			// delete post document files and meta data file

			// add files to zip archive
			// -- send zip archive
			// -- delete zip archive


			// message meta
			swordMessage.setNoOp(false);
			swordMessage.setUserAgent(repositoryConfig.getHttpUserAgent());
			swordMessage.setFilepath(swordZipFile.getAbsolutePath());
			swordMessage.setFiletype("application/zip");
			swordMessage.setFormatNamespace("http://purl.org/net/sword-types/METSDSpaceSIP"); // sets packaging!
			swordMessage.setVerbose(false);


			try {
				// check depositurl against service document
				if (checkServicedokument(retrieveServicedocument(), repositoryConfig.getHttpServicedocumentUrl(), SWORDFILETYPE, SWORDFORMAT)) {
					// transmit sword message (zip file with document metadata and document files
					swordMessage.setDestination(repositoryConfig.getHttpDepositUrl());
	
					depositResponse = swordClient.postFile(swordMessage);
					if (depositResponse.getHttpResponse()>=300) {
						try {
							log.info("depositResponse: "+ depositResponse.getErrorDocument().getErrorURI());
						} catch (SWORDException e) {
							log.warn("SWORDException! " + e.getMessage());
						}
					} else {
						log.info("depositResponse: OK!");
					}
				}

			} catch (SWORDClientException e) {
				log.warn("SWORDClientException");
			}

		}

		return depositResponse;
	}

	/*
	 * returns true, if sword server response is positive (http status is less than 300. status codes 200, 201, and 202 are possible), otherwise false
	 */
	public boolean checkDepositResponse(DepositResponse response) {
		// see http://www.swordapp.org/docs/sword-profile-1.3.html
		if (response.getHttpResponse() < 300) return true; else return false;
	}
	
	
	/*
	 * Workaround method to retrieve 
	 */
	private List<Document> retrieveDocumentsFromDatabase(User user, String resourceHash) {

		/*
		 * getting DB access
		 */
		final String username = user.getName();

		log.info("getting database access for user " + username);
		final LogicInterface logic;

		final DBLogicApiInterfaceFactory factory = new DBLogicApiInterfaceFactory();
		factory.setDbSessionFactory(new IbatisDBSessionFactory());

		logic = factory.getLogicAccess(username, user.getApiKey());
		
		// get meta data for post
		try {
			final Post<? extends Resource> post = logic.getPostDetails(resourceHash, username); 
			if (post.getResource() instanceof BibTex) {
				
				// get documents for post
				return ((BibTex) post.getResource()).getDocuments();
			}			
		} catch (ResourceNotFoundException e) {
			// e.printStackTrace();
			log.warn("ResourceNotFoundException! SwordService-retrieveDocumentsFromDatabase");
		} catch (ResourceMovedException e) {
			log.warn("ResourceMovedException! SwordService-retrieveDocumentsFromDatabase");
		}
		return null;
	}


	/**
	 * Configuration of Sword-Server (Repository) 
	 * 
	 * @param repositoryConfig the repositoryConfig to set
	 */
	public void setRepositoryConfig(SwordConfig repositoryConfig) {
		this.repositoryConfig = repositoryConfig;
	}


	
	/**
	 * The path to the documents.
	 * 
	 * @param projectDocumentPath
	 */
	public void setProjectDocumentPath(String projectDocumentPath) {
		this.projectDocumentPath = projectDocumentPath;
	}
	
}



