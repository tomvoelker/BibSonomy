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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: david Date: Feb 18, 2006 Time: 3:16:46 AM To
 * change this template use File | Settings | File Templates.
 */
public class WebdavResponseWrapper extends HttpServletResponseWrapper implements WebdavResponse {
    public static final String MS_AUTHOR_VIA = "MS-Author-Via";

    private static final Logger LOG = Logger.getLogger( WebdavResponseWrapper.class );

    private boolean class1 = true;

    private boolean class2 = false;

    public WebdavResponseWrapper( HttpServletResponse resp ) {
        super( resp );
        resetDavClass();
        // HACK: Work around MS Web Folder problems
        setHeader( MS_AUTHOR_VIA, "DAV" );
    }

    private void resetDavClass() {
        StringBuffer value = new StringBuffer();
        if ( class1 ) {
            value.append( "1" );
            if ( class2 )
                value.append( ",2" );
        }
        setHeader( DAV, value.toString() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#isDavClass1()
     */
    public boolean isDavClass1() {
        return class1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#setDavClass1(boolean)
     */
    public void setDavClass1( boolean isClass1 ) {
        if ( !isClass1 ) {
            class1 = false;
            class2 = false;
        } else {
            class1 = true;
        }
        resetDavClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#isDavClass2()
     */
    public boolean isDavClass2() {
        return class2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#setDavClass2(boolean)
     */
    public void setDavClass2( boolean isClass2 ) {
        this.class2 = isClass2;
        resetDavClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#setLockToken(java.lang.String)
     */
    public void setLockToken( String lockToken ) {
        setHeader( LOCK_TOKEN, lockToken );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#setStatusURI(int,
     *      java.lang.String)
     */
    public void setStatusURI( int status, String uri ) {
        setHeader( STATUS_URI, status + "<" + uri + ">" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#setContentLanguage(java.lang.String)
     */
    public void setContentLanguage( String contentLanguage ) {
        setHeader( CONTENT_LANGUAGE, contentLanguage );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#setETag(java.lang.String)
     */
    public void setETag( String eTag ) {
        setHeader( ETAG, eTag );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.confluence.extra.webdav.servlet.WebdavResponse#setLastModified(java.util.Date)
     */
    public void setLastModified( Date creationDate ) {
        setDateHeader( LAST_MODIFIED, creationDate.getTime() );
    }

    public void setStatus( int status ) {
        LOG.debug( "status: " + status );
        super.setStatus( status );
    }

    public void setStatus( int status, String message ) {
        LOG.debug( "status: " + status + " " + message );
        super.setStatus( status, message );
    }

    public void sendError( int status ) throws IOException {
        LOG.debug( "error: " + status );
        super.sendError( status );
    }

    public void sendError( int status, String message ) throws IOException {
        LOG.debug( "error: + " + status + " " + message );
        super.sendError( status, message );
    }
}
