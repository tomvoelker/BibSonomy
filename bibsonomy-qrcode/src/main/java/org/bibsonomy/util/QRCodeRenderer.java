/**
 * BibSonomy-QRCode - Embbeding QR Codes in PDFs in Bibsonomy
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		final ExecutorService pool = Executors.newFixedThreadPool(1);
		
		final Future<String> embedderFuture = pool.submit(new QRCodeEmbedder(filePath, encodee));
		
		try {
			/*
			 * get result within 5 seconds or throw an exception
			 */
			return embedderFuture.get(QRCodeEmbedder.WAIT_TIME, TimeUnit.MILLISECONDS);
		} catch (final Exception e) {
			
			/*
			 * if embedding fails, safely shutdown executor and delete output file
			 */
			new File(filePath.concat(".qr")).delete();
			
			throw new Exception(e);
		} finally {
			pool.shutdownNow();
		}
	}

	/**
	 * @param projectHome the projectHome to set
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}
}
