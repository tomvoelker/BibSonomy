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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.Logger;

import com.atlassian.confluence.extra.webdav.servlet.client.GenericClient;
import com.atlassian.confluence.extra.webdav.servlet.client.MacWebdavfsClient;
import com.atlassian.confluence.extra.webdav.servlet.client.MsInternetExplorerClient;
import com.atlassian.confluence.extra.webdav.servlet.client.MsWebFolderClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA. User: david Date: Feb 18, 2006 Time: 2:59:26 AM To
 * change this template use File | Settings | File Templates.
 */
public class WebdavRequestWrapper extends HttpServletRequestWrapper implements WebdavRequest {
    private static final Logger LOG = Logger.getLogger( WebdavRequestWrapper.class );

    private static final String DEFAULT_ENCODING = "ISO-8859-1";

    // User-agent strings
    private static final String MAC_WEBDAVFS_USER_AGENT = "webdavfs/";

    private static final String MS_WEBFOLDERS_USER_AGENT = "Microsoft Data Access Internet Publishing Provider DAV";

    private String userAgent;

    private String encoding = DEFAULT_ENCODING;

    private WebdavClient client;

    public WebdavRequestWrapper( HttpServletRequest httpServletRequest ) {
        super( httpServletRequest );

        // Check for common user agents.
        userAgent = getHeader( USER_AGENT );

        if ( userAgent != null ) {
            if ( userAgent.toLowerCase().startsWith( MAC_WEBDAVFS_USER_AGENT ) )
                client = new MacWebdavfsClient( userAgent );
            else if ( userAgent.startsWith( MS_WEBFOLDERS_USER_AGENT ) )
                client = new MsWebFolderClient( userAgent );
            else if ( userAgent.indexOf( "MSIE" ) != -1 )
                client = new MsInternetExplorerClient( userAgent );
        }

        if ( client == null )
            client = new GenericClient( userAgent );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavRequest#getDepth()
     */
    public int getDepth() {
        String depth = getHeader( DEPTH );

        if ( "0".equals( depth ) )
            return DEPTH_0;
        else if ( "1".equals( depth ) )
            return DEPTH_1;
        else
            return DEPTH_INFINITY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavRequest#getDestination()
     */
    public String getDestination() {
        try {
            String dest = getHeader( DESTINATION );
            if ( dest != null )
                return new String( URLCodec.decodeUrl( dest.getBytes( encoding ) ), encoding );
        } catch ( DecoderException e ) {
            LOG.warn( "Decoder exception: " + e.getMessage() );
        } catch ( UnsupportedEncodingException e ) {
            LOG.warn( "Unsupported encoding: " + encoding );
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavRequest#getLockToken()
     */
    public String getLockToken() {
        return getHeader( LOCK_TOKEN );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavRequest#isOverwrite()
     */
    public boolean isOverwrite() {
        return !"F".equals( getHeader( OVERWRITE ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavRequest#getUserAgent()
     */
    public String getUserAgent() {
        return userAgent;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding( String encoding ) {
        this.encoding = encoding;
    }

    public WebdavClient getClient() {
        return client;
    }
}
