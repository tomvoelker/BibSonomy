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
package com.atlassian.confluence.extra.webdav.servlet.resource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import com.atlassian.confluence.extra.webdav.servlet.WebdavServlet;
import com.atlassian.confluence.extra.webdav.servlet.WebdavUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a &lt;multistatus&gt; response, and abstracts away some
 * of the creation process.
 */
public class ErrorReport {
    private static final Namespace DAV = Namespace.get( "DAV:" );

    private String prefix;

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    private static class Response {
        private int status;

        private List<Resource> resources;

        Response( int status ) {
            this.status = status;
            resources = new LinkedList<Resource>();
        }

        void addResource( Resource resource ) {
            resources.add( resource );
        }

        List<Resource> getResources() {
            return resources;
        }

        int getStatus() {
            return status;
        }

        private Element createXML( String prefix ) {
            Element response = DocumentHelper.createElement( new QName( "response", DAV ) );

            Iterator<Resource> i = getResources().iterator();
            while ( i.hasNext() ) {
                Resource resource = i.next();
                response.addElement( new QName( "href", DAV ) ).setText(
                        prefix + resource.getUriPath( WebdavServlet.getCurrentClient() ) );
            }

            response.addElement( new QName( "status", DAV ) ).setText(
                    "HTTP/1.1 " + String.valueOf( status ) + " " + WebdavUtil.getStatusMessage( getStatus() ) );

            return response;
        }
    }

    private Map<Integer, Response> responses;

    private String description;

    public ErrorReport( String prefix ) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean hasErrors() {
        return responses != null && responses.size() > 0;
    }

    /**
     * Adds an error report of the specified error code against the specified
     * HREF.
     * 
     * @param resource
     * @param status
     */
    public void addError( Resource resource, int status ) {
        if ( responses == null )
            responses = new HashMap<Integer, Response>();

        Integer key = new Integer( status );

        Response response = responses.get( key );
        if ( response == null ) {
            response = new Response( status );
            responses.put( key, response );
        }

        response.addResource( resource );
    }

    public Document createXML() {
        Document doc = DocumentHelper.createDocument();
        Element multistatus = doc.addElement( new QName( "multistatus", DAV ) );

        if ( responses != null ) {
            Iterator<Response> i = responses.values().iterator();
            while ( i.hasNext() ) {
                Response response = i.next();
                multistatus.add( response.createXML( prefix ) );
            }
        }

        if ( description != null )
            multistatus.addElement( new QName( "responsedescription", DAV ) ).setText( description );

        return doc;
    }
}