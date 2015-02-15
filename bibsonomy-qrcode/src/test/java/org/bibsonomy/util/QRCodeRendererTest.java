/**
 * BibSonomy-QRCode - Embbeding QR Codes in PDFs in Bibsonomy
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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author pbu
 */
public class QRCodeRendererTest {

	@Test
	public void testTemplatePDF() {
		File template = new File("src/test/resources/template.pdf");
		
		if (template.exists()) {
			final String encodee = "http://localhost:8080/bibtex/2dfac402f7dac97c1b303bb53764ace82/derbeukatt";

			/*
			 * create executor service
			 */
			ExecutorService pool = Executors.newFixedThreadPool(1);

			QRCodeEmbedder embedder = new QRCodeEmbedder(template.getPath(), encodee);
			Future<String> embedderFuture = pool.submit(embedder);

			/*
			 * get result within 5 seconds or throw an exception
			 */
			String manipulatedFilePath = null;
			
			try {
				/*
				 * we take 10 times the normal time because the build/test system is so slow 
				 */
				manipulatedFilePath = embedderFuture.get(10 * QRCodeEmbedder.WAIT_TIME, TimeUnit.MILLISECONDS);
			} catch (final Exception e) {
				
				/*
				 * if embedding fails, safely shutdown executor and delete output file
				 */
				pool.shutdownNow();
				new File(template.getPath().concat(".qr")).delete();
				
				e.printStackTrace();
			}

			pool.shutdownNow();
			
			new File(template.getPath().concat(".qr")).delete();
			
			assertEquals(template.getPath().concat(".qr"), manipulatedFilePath);
			assertEquals(495.0f, embedder.getX(), 0.0);
			assertEquals(570.0f, embedder.getY(), 0.0);
			assertEquals(117, embedder.getSize());
		}
	}
	
	/**
	 * This test is only for debugging purposes. Only should be invoked when one
	 * has to search for embedding failures -> no asserts
	 * @throws IOException 
	 */
	@Test
	public void errorPDF() {
		File error = new File("src/test/resources/error.pdf");
		
		if (error.exists()) {
			
			final String encodee = "http://localhost:8080/bibtex/2dfac402f7dac97c1b303bb53764ace82/derbeukatt";

			/*
			 * create executor service
			 */
			ExecutorService pool = Executors.newFixedThreadPool(1);

			QRCodeEmbedder embedder = new QRCodeEmbedder(error.getPath(), encodee);
			Future<String> embedderFuture = pool.submit(embedder);

			/*
			 * get result within 5 seconds or throw an exception
			 */
			try {
				embedderFuture.get(QRCodeEmbedder.WAIT_TIME, TimeUnit.MILLISECONDS);
			} catch (final Exception e) {
				
				/*
				 * if embedding fails, safely shutdown executor and delete output file
				 */
				pool.shutdownNow();
				new File(error.getPath().concat(".qr")).delete();
				
				e.printStackTrace();
			}

			pool.shutdownNow();
			
			new File(error.getPath().concat(".qr")).delete();
		} 
	}
	
}
