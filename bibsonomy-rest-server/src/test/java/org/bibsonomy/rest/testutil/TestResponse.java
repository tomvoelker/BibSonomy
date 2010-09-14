package org.bibsonomy.rest.testutil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.Ignore;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
@Ignore
public class TestResponse implements HttpServletResponse {

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

	@Override
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

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.printWriter == null) {
			this.content = new StringWriter();
			this.printWriter = new PrintWriter(this.content);
		}
		return this.printWriter;
	}

	@Override
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * @return the contentLength
	 */
	public int getContentLength() {
		return this.contentLength;
	}

	@Override
	public void addCookie(Cookie arg0) {
	}

	@Override
	public boolean containsHeader(String arg0) {
		return false;
	}

	@Override
	public String encodeURL(String arg0) {
		return null;
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		return null;
	}

	@Override
	public String encodeUrl(String arg0) {
		return null;
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		return null;
	}

	@Override
	public void sendError(final int code, final String msg) throws IOException {
		throw new RuntimeException("code: " + code + " message: " + msg);
	}

	@Override
	public void sendError(final int code) throws IOException {
		throw new RuntimeException("code: " + code);
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {

	}

	@Override
	public void setDateHeader(String arg0, long arg1) {

	}

	@Override
	public void addDateHeader(String arg0, long arg1) {

	}

	@Override
	public void setHeader(String arg0, String arg1) {

	}

	@Override
	public void addHeader(String arg0, String arg1) {

	}

	@Override
	public void setIntHeader(String arg0, int arg1) {

	}

	@Override
	public void addIntHeader(String arg0, int arg1) {

	}

	@Override
	public void setStatus(int arg0) {

	}

	@Override
	public void setStatus(int arg0, String arg1) {

	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public void setCharacterEncoding(String arg0) {

	}

	@Override
	public void setContentType(String arg0) {

	}

	@Override
	public void setBufferSize(int arg0) {

	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {

	}

	@Override
	public void resetBuffer() {

	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {

	}

	@Override
	public void setLocale(Locale arg0) {

	}

	@Override
	public Locale getLocale() {
		return null;
	}
}