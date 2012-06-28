package org.bibsonomy.util;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * singleton class to embed qr code into pdf file
 * 
 * @author pbu
 * @version $Id$
 */
public class QRCodeRenderer {

	/**
	 * project home. important for URL to encode
	 */
	private String projectHome = null; 

	/**
	 * method to manipulate pdf document. only return
	 * manipulated file location on success.
	 * 
	 * @param filePath the input file path 
	 * @param requestedUser the user who has requested embedding
	 * @param intraHash the intraHash of the document
	 * @return the path to the manipulated pdf file
	 * @throws Exception if something goes wrong during process
	 */
	public String manipulate(String filePath, String requestedUser, String intraHash) throws Exception {		
		/*
		 * build URL: e.g. http://www.bibsonomy.org/bibtex/INTRAHASH/USERNAME
		 */
		final String encodee = projectHome + "bibtex/" + intraHash + "/" + requestedUser;

		/*
		 * create executor service
		 */
		ExecutorService pool = Executors.newFixedThreadPool(1);

		Future<String> embedderFuture = pool.submit(new QRCodeEmbedder(filePath, encodee));

		/*
		 * get result within 5 seconds or throw an exception
		 */
		String manipulatedFilePath = null;
		
		try {
			manipulatedFilePath = embedderFuture.get(QRCodeEmbedder.WAIT_TIME, TimeUnit.MILLISECONDS);
		} catch (final Exception e) {
			
			/*
			 * if embedding fails, safely shutdown executor and delete output file
			 */
			pool.shutdownNow();
			new File(filePath.concat(".qr")).delete();
			
			throw new Exception();
		}

		pool.shutdownNow();
		
		/*
		 * return the manipulated file path
		 */
		return manipulatedFilePath;
	}

	/**
	 * @param projectHome the projectHome to set
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}

	
	
}
