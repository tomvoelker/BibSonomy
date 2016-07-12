/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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