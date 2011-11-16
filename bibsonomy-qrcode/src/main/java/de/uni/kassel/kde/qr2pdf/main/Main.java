package de.uni.kassel.kde.qr2pdf.main;
import java.io.IOException;

import de.uni.kassel.kde.qr2pdf.util.ConverterBenchmark;
import de.uni.kassel.kde.qr2pdf.util.MyLogger;
import de.uni.kassel.kde.qr2pdf.util.ParserBenchmark;
import de.uni.kassel.kde.qr2pdf.util.converter.Converter;
import de.uni.kassel.kde.qr2pdf.util.converter.GhostScriptConverter;
import de.uni.kassel.kde.qr2pdf.util.converter.IcePDFConverter;
import de.uni.kassel.kde.qr2pdf.util.converter.PDFBoxConverter;
import de.uni.kassel.kde.qr2pdf.util.converter.PDFRendererConverter;
import de.uni.kassel.kde.qr2pdf.util.parser.ITextParser;
import de.uni.kassel.kde.qr2pdf.util.parser.JPodParser;
import de.uni.kassel.kde.qr2pdf.util.parser.PDFBoxParser;
import de.uni.kassel.kde.qr2pdf.util.parser.PDFClownParser;
import de.uni.kassel.kde.qr2pdf.util.parser.Parser;

public class Main {

	public static final String LOG_PROP = "src/main/resources/log.properties";
	public static final String DIR_IN = "src/main/resources/in/";
	public static final String DIR_OUT = "src/main/resources/out/";
	
	public static void main(String[] args) throws IOException
	{
		long totalRunTime = System.currentTimeMillis();
		
		System.setProperty("java.util.logging.config.file", LOG_PROP);
		
		converterBenchmark();
		parserBenchmark();				
		
		totalRunTime = System.currentTimeMillis() - totalRunTime;
		
		System.out.println();
		System.out.println("Gesamte Benchmark Zeit: " + totalRunTime/1000.0 + "s");
	}
	
	private static void converterBenchmark() throws IOException
	{
		MyLogger gsconvLogger = new MyLogger("src/main/resources/logFiles/converter/ghostscript");
		MyLogger iceconvLogger = new MyLogger("src/main/resources/logFiles/converter/icepdf");
		MyLogger boxconvLogger = new MyLogger("src/main/resources/logFiles/converter/pdfbox");
		MyLogger rendererconvLogger = new MyLogger("src/main/resources/logFiles/converter/pdfrenderer");
		
		Converter gsConverter = new GhostScriptConverter();
		Converter iceConverter = new IcePDFConverter();
		Converter boxConverter = new PDFBoxConverter();
		Converter rendererConverter = new PDFRendererConverter();
		
		ConverterBenchmark gsconvBench = new ConverterBenchmark(gsConverter, DIR_IN, gsconvLogger);
		ConverterBenchmark iceconvBench = new ConverterBenchmark(iceConverter, DIR_IN, iceconvLogger);
		ConverterBenchmark boxconvBench = new ConverterBenchmark(boxConverter, DIR_IN, boxconvLogger);
		ConverterBenchmark rendererconvBench = new ConverterBenchmark(rendererConverter, DIR_IN, rendererconvLogger);
		
		gsconvBench.benchmark();
		iceconvBench.benchmark();
		boxconvBench.benchmark();
		rendererconvBench.benchmark();
		
		gsconvLogger.close();
		iceconvLogger.close();
		boxconvLogger.close();
		rendererconvLogger.close();
	}
	
	private static void parserBenchmark() throws IOException
	{

		MyLogger jpodparseLogger = new MyLogger("src/main/resources/logFiles/parser/jpod");
		MyLogger itextparseLogger = new MyLogger("src/main/resources/logFiles/parser/itext");
		MyLogger boxparseLogger = new MyLogger("src/main/resources/logFiles/parser/pdfbox");
		MyLogger pdfclownparseLogger = new MyLogger("src/main/resources/logFiles/parser/pdfclown");
		
		Parser jpodParser = new JPodParser();
		Parser itextParser = new ITextParser();
		Parser boxParser = new PDFBoxParser();
		Parser pdfclownParser = new PDFClownParser();
		
		ParserBenchmark jpodparseBench = new ParserBenchmark(jpodParser, DIR_IN, DIR_OUT+"jpod/", jpodparseLogger);
		ParserBenchmark itextparseBench = new ParserBenchmark(itextParser, DIR_IN, DIR_OUT+"itext/", itextparseLogger);
		ParserBenchmark boxparseBench = new ParserBenchmark(boxParser, DIR_IN, DIR_OUT+"pdfbox/", boxparseLogger);
		ParserBenchmark pdfclownparseBench = new ParserBenchmark(pdfclownParser, DIR_IN, DIR_OUT+"pdfclown/", pdfclownparseLogger);
		
		jpodparseBench.benchmark();
		itextparseBench.benchmark();
		boxparseBench.benchmark();
		pdfclownparseBench.benchmark();
		
		
		
		jpodparseLogger.close();
		itextparseLogger.close();
		boxparseLogger.close();
		pdfclownparseLogger.close();
	}
}
