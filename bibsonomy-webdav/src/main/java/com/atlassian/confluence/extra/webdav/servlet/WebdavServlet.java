/*
 * Copyright (c) 2006, David Peterson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of "randombits.org" nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.atlassian.confluence.extra.webdav.servlet;

import com.atlassian.confluence.extra.webdav.servlet.client.GenericClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is a basic servlet supporting the different methods used by the WebDAV
 * standard (<a href="http://www.webdav.org/specs/rfc2518.html">RFC-2518</a>).
 */
public abstract class WebdavServlet extends HttpServlet {
    protected static final String METHOD_HEAD = "HEAD";

    protected static final String METHOD_PROPFIND = "PROPFIND";

    protected static final String METHOD_PROPPATCH = "PROPPATCH";

    protected static final String METHOD_MKCOL = "MKCOL";

    protected static final String METHOD_COPY = "COPY";

    protected static final String METHOD_MOVE = "MOVE";

    protected static final String METHOD_PUT = "PUT";

    protected static final String METHOD_GET = "GET";

    protected static final String METHOD_POST = "POST";

    protected static final String METHOD_DELETE = "DELETE";

    protected static final String METHOD_LOCK = "LOCK";

    protected static final String METHOD_UNLOCK = "UNLOCK";

    protected static final String METHOD_OPTIONS = "OPTIONS";

    protected static final String METHOD_TRACE = "TRACE";

    private static final ThreadLocal<WebdavRequest> CURRENT_REQUEST = new ThreadLocal<WebdavRequest>();

    private static final ThreadLocal<WebdavResponse> CURRENT_RESPONSE = new ThreadLocal<WebdavResponse>();

    public static void setCurrentRequest( WebdavRequest req ) {
        CURRENT_REQUEST.set( req );
    }

    public static void setCurrentResponse( WebdavResponse resp ) {
        CURRENT_RESPONSE.set( resp );
    }

    /**
     * Returns the WebdavRequest for the current thread.
     * 
     * @return
     */
    public static WebdavRequest getCurrentRequest() {
        return ( WebdavRequest ) CURRENT_REQUEST.get();
    }

    public static WebdavResponse getCurrentResponse() {
        return ( WebdavResponse ) CURRENT_RESPONSE.get();
    }

    public static WebdavClient getCurrentClient() {
        WebdavRequest req = getCurrentRequest();
        WebdavClient client = null;
        if ( req != null )
            client = req.getClient();

        return client == null ? new GenericClient( "" ) : client;
    }

    protected abstract void doPropFind( WebdavRequest req, WebdavResponse resp ) throws ServletException,
            IOException;

    protected abstract void doPropPatch( WebdavRequest req, WebdavResponse resp ) throws ServletException,
            IOException;

    protected abstract void doMkCol( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException;

    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        WebdavRequest wreq = asWebdavRequest( req );
        WebdavResponse wresp = asWebdavResponse( resp );

        doGet( wreq, wresp );
        if ( !resp.isCommitted() )
            super.doGet( wreq, wresp );
    }

    protected abstract void doGet( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException;

    protected void doHead( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        WebdavRequest wreq = asWebdavRequest( req );
        WebdavResponse wresp = asWebdavResponse( resp );

        doHead( wreq, wresp );
        // if ( !resp.isCommitted() )
        // super.doHead( wreq, wresp );
    }

    protected abstract void doHead( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException;

    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        WebdavRequest wreq = asWebdavRequest( req );
        WebdavResponse wresp = asWebdavResponse( resp );

        doPost( wreq, wresp );
        if ( !resp.isCommitted() )
            super.doPost( wreq, wresp );
    }

    protected abstract void doPost( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException;

    protected void doDelete( HttpServletRequest req, HttpServletResponse resp ) throws ServletException,
            IOException {
        WebdavRequest wreq = asWebdavRequest( req );
        WebdavResponse wresp = asWebdavResponse( resp );

        doDelete( wreq, wresp );
        if ( !resp.isCommitted() )
            super.doDelete( wreq, wresp );
    }

    protected abstract void doDelete( WebdavRequest req, WebdavResponse resp ) throws ServletException,
            IOException;

    protected void doPut( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        WebdavRequest wreq = asWebdavRequest( req );
        WebdavResponse wresp = asWebdavResponse( resp );

        doPut( wreq, wresp );
        if ( !resp.isCommitted() )
            super.doPut( wreq, wresp );
    }

    protected abstract void doPut( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException;

    protected abstract void doCopy( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException;

    protected abstract void doMove( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException;

    protected abstract void doLock( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException;

    protected abstract void doUnlock( WebdavRequest req, WebdavResponse resp ) throws ServletException,
            IOException;

    protected void doOptions( HttpServletRequest req, HttpServletResponse resp ) throws ServletException,
            IOException {
        WebdavRequest wreq = asWebdavRequest( req );
        WebdavResponse wresp = asWebdavResponse( resp );

        doOptions( wreq, wresp );
        // if ( !resp.isCommitted() )
        // super.doOptions( wreq, wresp );
    }

    private WebdavResponse asWebdavResponse( HttpServletResponse resp ) {
        WebdavResponse wresp;
        if ( resp instanceof WebdavResponse )
            wresp = ( WebdavResponse ) resp;
        else
            wresp = new WebdavResponseWrapper( resp );
        setCurrentResponse( wresp );
        return wresp;
    }

    private WebdavRequest asWebdavRequest( HttpServletRequest req ) {
        WebdavRequest wreq;
        if ( req instanceof WebdavRequest )
            wreq = ( WebdavRequest ) req;
        else
            wreq = new WebdavRequestWrapper( req );
        setCurrentRequest( wreq );
        return wreq;
    }

    protected abstract void doOptions( WebdavRequest req, WebdavResponse resp ) throws ServletException,
            IOException;

    protected void service( HttpServletRequest req, HttpServletResponse resp ) throws ServletException,
            IOException {
        WebdavRequest webdavReq = asWebdavRequest( req );
        WebdavResponse webdavResp = asWebdavResponse( resp );

        service( webdavReq, webdavResp );
    }

    protected void service( WebdavRequest req, WebdavResponse resp ) throws IOException, ServletException {
        String method = req.getMethod();

        if ( METHOD_PROPFIND.equals( method ) )
            doPropFind( req, resp );
        else if ( METHOD_PROPPATCH.equals( method ) )
            doPropPatch( req, resp );
        else if ( METHOD_MKCOL.equals( method ) )
            doMkCol( req, resp );
        else if ( METHOD_GET.equals( method ) )
            doGet( req, resp );
        else if ( METHOD_HEAD.equals( method ) )
            doHead( req, resp );
        else if ( METHOD_POST.equals( method ) )
            doPost( req, resp );
        else if ( METHOD_DELETE.equals( method ) )
            doDelete( req, resp );
        else if ( METHOD_OPTIONS.equals( method ) )
            doOptions( req, resp );
        else if ( METHOD_PUT.equals( method ) )
            doPut( req, resp );
        else if ( METHOD_COPY.equals( method ) )
            doCopy( req, resp );
        else if ( METHOD_MOVE.equals( method ) )
            doMove( req, resp );
        else if ( METHOD_LOCK.equals( method ) )
            doLock( req, resp );
        else if ( METHOD_UNLOCK.equals( method ) )
            doUnlock( req, resp );
        else
            super.service( req, resp );
    }
}
