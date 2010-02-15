package org.bibsonomy.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class NullResponse implements HttpServletResponse {

	private ServletOutputStream servletOutputStream;
	private PrintWriter printWriter;
	private StringWriter content;
	private int contentLength;

	/**
	 * @return content as String
	 */
	public String getContent() {
		return this.content.toString();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (this.servletOutputStream == null) {
			this.servletOutputStream = new ServletOutputStream() {
				@Override
				public void write(final int b) throws IOException {
					getWriter().write(b);
				}
			};
		}
		return this.servletOutputStream;
	}

	public PrintWriter getWriter() throws IOException {
		if (this.printWriter == null) {
			this.content = new StringWriter();
			this.printWriter = new PrintWriter(this.content);
		}
		return this.printWriter;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * @return the contentLength
	 */
	public int getContentLength() {
		return this.contentLength;
	}

	public void addCookie(Cookie arg0) {
	}

	public boolean containsHeader(String arg0) {
		return false;
	}

	public String encodeURL(String arg0) {
		return null;
	}

	public String encodeRedirectURL(String arg0) {
		return null;
	}

	public String encodeUrl(String arg0) {
		return null;
	}

	public String encodeRedirectUrl(String arg0) {
		return null;
	}

	public void sendError(final int code, final String msg) throws IOException {
		throw new RuntimeException("code: " + code + " message: " + msg);
	}

	public void sendError(final int code) throws IOException {
		throw new RuntimeException("code: " + code);
	}

	public void sendRedirect(String arg0) throws IOException {

	}

	public void setDateHeader(String arg0, long arg1) {

	}

	public void addDateHeader(String arg0, long arg1) {

	}

	public void setHeader(String arg0, String arg1) {

	}

	public void addHeader(String arg0, String arg1) {

	}

	public void setIntHeader(String arg0, int arg1) {

	}

	public void addIntHeader(String arg0, int arg1) {

	}

	public void setStatus(int arg0) {

	}

	public void setStatus(int arg0, String arg1) {

	}

	public String getCharacterEncoding() {
		return null;
	}

	public String getContentType() {
		return null;
	}

	public void setCharacterEncoding(String arg0) {

	}

	public void setContentType(String arg0) {

	}

	public void setBufferSize(int arg0) {

	}

	public int getBufferSize() {
		return 0;
	}

	public void flushBuffer() throws IOException {

	}

	public void resetBuffer() {

	}

	public boolean isCommitted() {
		return false;
	}

	public void reset() {

	}

	public void setLocale(Locale arg0) {

	}

	public Locale getLocale() {
		return null;
	}
}