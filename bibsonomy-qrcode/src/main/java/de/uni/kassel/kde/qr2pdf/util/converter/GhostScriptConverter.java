package de.uni.kassel.kde.qr2pdf.util.converter;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class GhostScriptConverter extends Converter{

	public BufferedImage convertToImage(String fileName) throws Exception
	{
		//gs -q -sDEVICE=pnggray -dBATCH -dNOPAUSE -dFirstPage=1 -dLastPage=1 -r300 -sOutputFile=test.png test.pdf
		
		BufferedImage img = null;
		
		Process gsProcess = null;
       
		ArrayList<String> argList = new ArrayList<String>();
		argList.add("/usr/bin/gs");
		argList.add("-q");
		argList.add("-sDEVICE=png48");
		argList.add("-dBATCH");
		argList.add("-dNOPAUSE");
		argList.add("-dFirstPage=1");
		argList.add("-dLastPage=1");
		//argList.add("-r150");
		argList.add("-sOutputFile=%stdout%"); // printerName = Name des Druckers
		argList.add(fileName); // fileName = Pfad zu der zu druckenden PDF-Datei

		ProcessBuilder processBuilder = new ProcessBuilder(
				argList);
		gsProcess = processBuilder.start();

		// Konsumieren der Ausgaben von Ghostscript:
		BufferedInputStream inputStream = new BufferedInputStream(gsProcess.getInputStream());

		img = ImageIO.read(inputStream);

		int returnValue = gsProcess.waitFor();
		
		// Alle Streams schließen
		gsProcess.getInputStream().close();
		gsProcess.getOutputStream().close();
		gsProcess.getErrorStream().close();
		
		if (returnValue != 0) {
			throw new Exception("Return value != 0");
		}
         
		return img;
	}
}
