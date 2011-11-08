package de.uni.kassel.kde.qr2pdf.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MyLogger {

	private PrintWriter out;
	
	public MyLogger() throws IOException
	{
		 this.out = new PrintWriter(new FileWriter("/home/philipp/Dokumente/pdftest/out/log"));
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}
	
}
