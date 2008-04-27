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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import com.atlassian.confluence.extra.webdav.servlet.WebdavRequest;
import com.atlassian.confluence.extra.webdav.servlet.WebdavResponse;
import com.atlassian.confluence.extra.webdav.servlet.WebdavServlet;
import com.atlassian.confluence.extra.webdav.servlet.WebdavUtil;
import com.atlassian.confluence.extra.webdav.util.IOUtils;

/**
 * This is an implementation of the WebDAV standard which uses a metaphore of
 * {@link Resource}s to access a WebDAV store. <p/> To use this servlet, you
 * should specify the following:
 * <ul>
 * <li><b>url-pattern:<b> [path-to-servlet]* (eg. '/webdav*')</li>
 * <li><b>parameters:</b></li>
 * <ul>
 * <li><i>ResourceBackendClass:</i> The class name which implements
 * {@link ResourceBackend}.</li>
 * <li><i>RootPrefix:</i> The extra path which should be ignored, after the
 * servlet name and before the start of the WebDAV root. This will not usually
 * need to be set.</li>
 * <li><i>RequireAuthentication:</i> If 'yes', the servlet will require user
 * authentication before allowing access. The backend implementation will need
 * to provide a Principal for valid users with it's
 * {@link ResourceBackend#authenticateUser(String, String)} method. Defaults to
 * 'no'.</li>
 * </ul>
 * </ul>
 */
public class WebdavResourceServlet extends WebdavServlet {
	private static final long serialVersionUID = 2563082346512726474L;

	private static final Logger LOG = Logger.getLogger( WebdavResourceServlet.class );

    private static final String RESOURCE_BACKEND_CLASS = "ResourceBackendClass";

    private static final String ROOT_PREFIX = "RootPrefix";

    private static final String REQUIRE_AUTHENTICATION = "RequireAuthentication";

    private static final String DAV_NSURI = "DAV:";

    private static final Namespace DAV_NS = Namespace.get( DAV_NSURI );

    private static final String DATA_NSURI = "urn:uuid:c2f41010-65b3-11d1-a29f-00aa00c14882/";

    private static final String DATA_PREFIX = "b";

    private static final Namespace DATA_NS = Namespace.get( DATA_PREFIX, DATA_NSURI );

    private static final String PROPFIND_EL = "propfind";

    private static final String ALLPROP_EL = "allprop";

    private static final String PROPNAME_EL = "propname";

    private static final String PROP_EL = "prop";

    private static final String MULTISTATUS_EL = "multistatus";

    private static final String RESPONSE_EL = "response";

    private static final String HREF_EL = "href";

    private static final String PROPSTAT_EL = "propstat";

    private static final String STATUS_EL = "status";

    private static final String DISPLAYNAME_EL = "displayname";

    private static final String CREATIONDATE_EL = "creationdate";

    private static final String LASTMODIFIED_EL = "getlastmodified";

    private static final String CONTENTLANGUAGE_EL = "getcontentlanguage";

    private static final String CONTENTLENGTH_EL = "getcontentlength";

    private static final String CONTENTTYPE_EL = "getcontenttype";

    private static final String ETAG_EL = "getetag";

    private static final String RESOURCETYPE_EL = "resourcetype";

    private static final String COLLECTION_EL = "collection";

    private static final String PROPERTYUPDATE_EL = "propertyupdate";

    // TODO: Implement locks
    /*
     * private static final String SOURCE_EL = "source";
     */

    private static final String LOCKDISCOVERY_EL = "lockdiscovery";

    private static final String SUPPORTEDLOCK_EL = "supportedlock";

    // [WBDV-2]
	// private static final String ACTIVELOCK_EL = "activelock";

	// private static final String LOCKTYPE_EL = "locktype";

	// private static final String WRITE_EL = "write";

	// private static final String LOCKSCOPE_EL = "lockscope";

	// private static final String EXCLUSIVE_EL = "exclusive";

	// private static final String SHARED_EL = "shared";

	// private static final String DEPTH_EL = "depth";

	// private static final String OWNER_EL = "owner";

	// private static final String LOCKTOCKEN_EL = "locktoken";

	// private static final String TIMEOUT_EL = "timeout";

    /**
     * Simple date format for the creation date ISO representation (partial).
     */
    protected static final DateFormat ISO8601_FORMAT;

    protected static final DateFormat HTTP_DATE_FORMAT;

    private static final String CONTENT_ENCODING = "ContentEncoding";

    private static final String DEFAULT_ATTACHMENT_ENCODING = "utf-8";

    private static final URLCodec URL_CODEC = new URLCodec();
    
    private static final String TEMPORARY_ITEMS = ".TemporaryItems";
    
    private static final String DS_STORE = ".DS_Store";
    
    protected static String[] TEMP_FILE_NAMES = null;
    
    static {
        ISO8601_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
        ISO8601_FORMAT.setTimeZone( TimeZone.getTimeZone( "GMT" ) );

        HTTP_DATE_FORMAT = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss 'GMT'" );
        HTTP_DATE_FORMAT.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        
        TEMP_FILE_NAMES = new String[] {DS_STORE, TEMPORARY_ITEMS};
    }

    private static class Location {
        private Resource parent;

        private String child;

        Location( Resource parent, String child ) {
            this.parent = parent;
            this.child = child;
        }

        public Resource getParent() {
            return parent;
        }

        public String getChildName() {
            return child;
        }
    }

    private ResourceBackend backend;

    private String rootPrefix;

    private boolean requireAuthentication;

    private String contentEncoding;

    protected void setBackend( ResourceBackend backend ) {
        if ( this.backend == null )
            this.backend = backend;
    }

    public ResourceBackend getBackend() {
        return backend;
    }

    public void destroy() {
        super.destroy();
    }

    public void init() throws ServletException {
        /*
         * I18N note - Errors messages from servlet init should be not i18n'ed.
         * There's no purpose to that.
         */
        if ( backend == null ) {
            String backendClassname = getInitParameter( RESOURCE_BACKEND_CLASS );
            if ( backendClassname == null )
                throw new ServletException( WebdavUtil.getText(
                        "webdav.resourceservlet.error.missingbackendinitparam",
                        new Object[]{RESOURCE_BACKEND_CLASS} ) );

            try {
                Class<?> backendClass = Class.forName( backendClassname );
                setBackend( ( ResourceBackend ) backendClass.newInstance() );
            } catch ( ClassNotFoundException e ) {
                throw new ServletException( "Unable to find backend class: " + backendClassname, e );
            } catch ( IllegalAccessException e ) {
                throw new ServletException( "Unable to construct the backend class: " + e.getMessage(), e );
            } catch ( InstantiationException e ) {
                throw new ServletException( "Unable to instantiate the backend class: " + e.getMessage(), e );
            }
        }

        try {
            backend.init( getServletContext() );
        } catch ( BackendException e ) {
            throw new ServletException( "A problem occurred while initialising the backend: " + e.getMessage(), e );
        }

        rootPrefix = getInitParameter( ROOT_PREFIX );

        requireAuthentication = false;
        String auth = getInitParameter( REQUIRE_AUTHENTICATION );
        if ( auth != null ) {
            auth = auth.toLowerCase();
            requireAuthentication = ( "yes".equals( auth ) || "true".equals( auth ) );
        }

        contentEncoding = getInitParameter( CONTENT_ENCODING );
        if ( StringUtils.isBlank( contentEncoding ) )
            contentEncoding = DEFAULT_ATTACHMENT_ENCODING;
    }

    protected void doPropFind( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        Resource resource = findResource( req );
        if ( resource == null ) {
            resp.sendError( WebdavResponse.SC_NOT_FOUND );
            return;
        }

        fixCollectionPath( resource, req, resp );

        // HACK - pretend virtual files can be deleted for Macs. - WBDV-32
        if ( req.getClient().requiresEmptyCollectionForDelete() ) {
            LOG.debug( "Checking for virtual delete: " + resource.getUriPath( req.getClient() ) );
            if ( resource.isVirtual() && HiddenResourceUtil.getInstance().isHidden( resource ) ) {
                LOG.debug( "Virtually deleted: " + resource.getUriPath( req.getClient() ) );
                resp.sendError( WebdavResponse.SC_NOT_FOUND );
                return;
            }
        }

        // Process the request
        if ( req.getContentLength() > 0 ) {
            try {
                Document doc = parseDocument( req.getInputStream() );
                // DEBUG: Output the document
                writeDocumentDebugging( doc );

                Element root = doc.getRootElement();
                if ( DAV_NSURI.equals( root.getNamespaceURI() ) && PROPFIND_EL.equals( root.getName() ) ) {
                    List children = root.elements();
                    int cmdCount = 0;

                    Iterator i = children.iterator();
                    while ( i.hasNext() ) {
                        Element cmd = ( Element ) i.next();

                        if ( DAV_NSURI.equals( cmd.getNamespaceURI() ) ) {
                            if ( cmdCount > 0 ) {
                                resp.sendError( WebdavResponse.SC_BAD_REQUEST, WebdavUtil
                                        .getText( "webdav.resourceservlet.error.toomanypropertyrequestcommand" ) );
                                return;
                            }

                            if ( PROP_EL.equals( cmd.getName() ) )
                                doPropFindProp( req, resp, resource, cmd );
                            else if ( ALLPROP_EL.equals( cmd.getName() ) )
                                doPropFindAllProp( req, resp, resource, false );
                            else if ( PROPNAME_EL.equals( cmd.getName() ) )
                                doPropFindAllProp( req, resp, resource, true );

                            cmdCount++;
                        }

                    }

                    if ( cmdCount == 0 ) // default to allprop'
                        resp.sendError( WebdavResponse.SC_BAD_REQUEST, WebdavUtil
                                .getText( "webdav.resourceservlet.error.invalidpropfindrequest" ) );
                } else {
                    resp.sendError( WebdavResponse.SC_BAD_REQUEST, WebdavUtil
                            .getText( "webdav.resourceservlet.error.invalidxmlpropfindrequest" ) );
                }
            } catch ( DocumentException e ) {
                LOG.error( "A DocumentException occured.", e );
                resp.sendError( WebdavResponse.SC_BAD_REQUEST, WebdavUtil
                        .getText( "webdav.resourceservlet.error.cantparserequestxml" ) );
            }
        } else {
            doPropFindAllProp( req, resp, resource, false );
        }

        // HACK - clear the list of deleted virtual files for Macs. - WBDV-31
        if ( req.getClient().requiresEmptyCollectionForDelete() ) {
            if ( resource instanceof CollectionResource ) {
                HiddenResourceUtil.getInstance().revealResource( resource );
            }
        }
    }

    private void fixCollectionPath( Resource resource, WebdavRequest req, WebdavResponse resp ) {
        // TODO: Make this actually work. Redirect? Is this even important?
//        String path = req.getPathInfo();
    	String path = req.getRequestURI();
        if ( resource instanceof CollectionResource
                && path.charAt( path.length() - 1 ) != Resource.PATH_SEPARATOR_CHAR ) {
            resp.addHeader( "Content-Location", makeAbsoluteURI( req, resource ) );
        }
    }

    private void doPropFindProp( WebdavRequest req, WebdavResponse resp, Resource resource, Element cmd )
            throws IOException {
        Document doc = DocumentHelper.createDocument();
        Element multistatus = doc.addElement( new QName( MULTISTATUS_EL, DAV_NS ) );

        int depth = req.getDepth();

        appendPropResponses( req, resource, multistatus, cmd.elements(), depth );

        resp.setStatus( WebdavResponse.SC_MULTI_STATUS );
        resp.setContentType( "text/xml; charset=\"utf-8\"" );

        // DEBUG: Output the document
        writeDocumentDebugging( doc );
        writeDocument( resp.getOutputStream(), doc );
    }

    private void appendPropResponses( WebdavRequest req, Resource resource, Element multistatus, List props,
            int depth ) throws IOException {
        Element response = multistatus.addElement( new QName( RESPONSE_EL, DAV_NS ) );

        addHrefElement( response, req, resource );

        Element successPropstat = createElement( PROPSTAT_EL );
        Element success = successPropstat.addElement( new QName( PROP_EL, DAV_NS ) );
        Element notfoundPropstat = createElement( PROPSTAT_EL );
        Element notfound = notfoundPropstat.addElement( new QName( PROP_EL, DAV_NS ) );

        Iterator i = props.iterator();
        String name;

        while ( i.hasNext() ) {
            Element prop = ( Element ) i.next();

            if ( DAV_NSURI.equals( prop.getNamespaceURI() ) ) {
                name = prop.getName();
                // First, add the standard properties
                if ( DISPLAYNAME_EL.equals( name ) )
                    addProperty( success, DISPLAYNAME_EL, resource.getDisplayName(), false );
                else if ( CREATIONDATE_EL.equals( name ) )
                    addProperty( success, CREATIONDATE_EL, resource.getCreationDate(), false, true );
                else if ( LASTMODIFIED_EL.equals( name ) )
                    addProperty( success, LASTMODIFIED_EL, resource.getLastModified(), false, false );
                else if ( CONTENTLANGUAGE_EL.equals( name ) )
                    addProperty( success, CONTENTLANGUAGE_EL, resource.getContentLanguage(), false );
                // Extra props used by MS
                // else if (NAME_EL.equals(name))
                // addProperty(success, NAME_EL, resource.getName(), false);
                // else if (PARENTNAME_EL.equals(name) && resource.getParent()
                // != null)
                // addProperty(success, PARENTNAME_EL,
                // resource.getParent().getName(), false);
                // else if (ISHIDDEN_EL.equals(name))
                // addProperty(success, ISHIDDEN_EL, NO_VAL, false);
                // else if (ISREADONLY_EL.equals(name))
                // addProperty(success, ISREADONLY_EL, resource.isVirtual() ?
                // YES_VAL : NO_VAL, false);
                // else if (ISSTRUCTUREDDOCUMENT_EL.equals(name))
                // addProperty(success, ISSTRUCTUREDDOCUMENT_EL, NO_VAL, false);
                // else if (ISROOT_EL.equals(name))
                // addProperty(success, ISROOT_EL, resource.getParent() == null
                // ? YES_VAL : NO_VAL, false);
                else if ( SUPPORTEDLOCK_EL.equals( name ) ) {
                    // TODO: Add proper lock support.
                    addProperty( success, SUPPORTEDLOCK_EL, "", false );
                } else if ( LOCKDISCOVERY_EL.equals( name ) ) {
                    // TODO: Add proper lock support.
                    addProperty( success, LOCKDISCOVERY_EL, "", false );
                } else if ( resource instanceof SingleResource ) {
                    SingleResource src = ( SingleResource ) resource;
                    if ( CONTENTTYPE_EL.equals( name ) )
                        addProperty( success, CONTENTTYPE_EL, src.getContentType(), false );
                    else if ( CONTENTLENGTH_EL.equals( name ) )
                        addProperty( success, CONTENTLENGTH_EL, src.getContentLength(), false );
                    else if ( ETAG_EL.equals( name ) )
                        addProperty( success, ETAG_EL, src.getETag(), false );
                    else if ( RESOURCETYPE_EL.equals( name ) )
                        addProperty( success, RESOURCETYPE_EL, "", false );
                    // else if (ISCOLLECTION_EL.equals(name))
                    // addProperty(success, ISCOLLECTION_EL, NO_VAL, false);
                    else {
                        addElement( notfound, prop.createCopy() );
                    }
                } else if ( resource instanceof CollectionResource ) {
                    if ( RESOURCETYPE_EL.equals( name ) ) {
                        addProperty( success, RESOURCETYPE_EL, createElement( COLLECTION_EL ), false );
                        // } else if (ISCOLLECTION_EL.equals(name)) {
                        // addProperty(success, ISCOLLECTION_EL, YES_VAL,
                        // false);
                    } else {
                        addElement( notfound, prop.createCopy() );
                    }
                    // TODO: Implement locks
                    /*
                     * else if (resource instanceof LockedResource) {
                     * LockedResource lrc = (LockedResource) resource; if
                     * (SUPPORTEDLOCKS_EL.equals(name) addProperty(success,
                     * SUPPORTEDLOCKS_EL, lrc.getSupportedLocks(), false); }
                     */
                } else {
                    addElement( notfound, prop.createCopy() );
                }
            } else {
                addElement( notfound, prop.createCopy() );
            }
        }

        // Add the propstats
        if ( success.elements().size() > 0 ) {
            addStatusElement( successPropstat, WebdavResponse.SC_OK );
            response.add( successPropstat );
        }

        if ( notfound.elements().size() > 0 ) {
            addStatusElement( notfoundPropstat, WebdavResponse.SC_NOT_FOUND );
            response.add( notfoundPropstat );
        }

        if ( depth != 0 && depth > Integer.MIN_VALUE && resource instanceof CollectionResource ) {
            List children = ( ( CollectionResource ) resource ).getChildren();
            i = children.iterator();
            while ( i.hasNext() ) {
                Resource childResource = ( Resource ) i.next();

                // HACK: don't report virtual files which have been 'deleted'
                // on the Mac. - WBDV-31
                if ( req.getClient().requiresEmptyCollectionForDelete() ) {
                    LOG.debug( "Checking for virtual delete: " + childResource.getUriPath( req.getClient() ) );
                    if ( childResource.isVirtual() && HiddenResourceUtil.getInstance().isHidden( childResource ) ) {
                        LOG.debug( "Virtually deleted: " + childResource.getUriPath( req.getClient() ) );
                        continue;
                    }
                }

                appendPropResponses( req, childResource, multistatus, props, depth - 1 );
            }
        }
    }

    private Document parseDocument( InputStream inputStream ) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        return saxReader.read( new InputSource( inputStream ) );
    }

    private void doPropFindAllProp( WebdavRequest req, WebdavResponse resp, Resource resource, boolean namesOnly )
            throws IOException {
        Document doc = DocumentHelper.createDocument();
        Element multistatus = doc.addElement( new QName( MULTISTATUS_EL, DAV_NS ) );

        int depth = req.getDepth();

        appendAllPropResponses( req, resource, multistatus, namesOnly, depth );

        resp.setStatus( WebdavResponse.SC_MULTI_STATUS );
        resp.setContentType( "text/xml; charset=\"utf-8\"" );

        // DEBUG: Document dumping.
        writeDocumentDebugging( doc );
        writeDocument( resp.getOutputStream(), doc );
    }

    private void writeDocument( OutputStream out, Document doc ) throws IOException {
        OutputFormat format = OutputFormat.createCompactFormat();
        XMLWriter writer = new XMLWriter( out, format );
        writer.write( doc );
        out.close();
    }

    private void writeDocumentDebugging( Document doc ) throws IOException {
        // if (true) // turn off debugging
        // return;

        if ( LOG.isDebugEnabled() ) {
            OutputFormat format = OutputFormat.createPrettyPrint();
            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter( sw, format );
            writer.write( doc );
            LOG.debug( sw.toString() );
        }
    }

    private void appendAllPropResponses( WebdavRequest req, Resource resource, Element multistatus,
            boolean namesOnly, int depth ) throws IOException {
        Element response = multistatus.addElement( new QName( RESPONSE_EL, DAV_NS ) );

        addHrefElement( response, req, resource );

        Element successPropstats = response.addElement( new QName( PROPSTAT_EL, DAV_NS ) );
        Element success = successPropstats.addElement( new QName( PROP_EL, DAV_NS ) );

        // First, add the standard properties
        addProperty( success, DISPLAYNAME_EL, resource.getDisplayName(), namesOnly );
        addProperty( success, CREATIONDATE_EL, resource.getCreationDate(), namesOnly, true );
        addProperty( success, LASTMODIFIED_EL, resource.getLastModified(), namesOnly, false );
        addProperty( success, CONTENTLANGUAGE_EL, resource.getContentLanguage(), namesOnly );

        if ( resource instanceof SingleResource ) {
            SingleResource src = ( SingleResource ) resource;
            addProperty( success, CONTENTTYPE_EL, src.getContentType(), namesOnly );
            addProperty( success, CONTENTLENGTH_EL, src.getContentLength(), namesOnly );
            addProperty( success, ETAG_EL, src.getETag(), namesOnly );
            addProperty( success, RESOURCETYPE_EL, "", namesOnly );
        }

        if ( resource instanceof CollectionResource ) {
            addProperty( success, RESOURCETYPE_EL, createElement( COLLECTION_EL ), namesOnly );
        }

        // TODO: Implement locks properly.
        addProperty( success, SUPPORTEDLOCK_EL, "", namesOnly );
        addProperty( success, LOCKDISCOVERY_EL, "", namesOnly );

        /*
         * if (resource instanceof LockedResource) { LockedResource lrc =
         * (LockedResource) resource; addProperty(success, SUPPORTEDLOCKS_EL,
         * lrc.getSupportedLocks(), namesOnly); }
         */

        addStatusElement( successPropstats, WebdavResponse.SC_OK );

        if ( depth != 0 && depth > Integer.MIN_VALUE && resource instanceof CollectionResource ) {
            List children = ( ( CollectionResource ) resource ).getChildren();
            Iterator i = children.iterator();
            while ( i.hasNext() ) {
                Resource childResource = ( Resource ) i.next();

                // HACK - don't report virtual files which have been 'deleted'
                // on the Mac.
                if ( req.getClient().requiresEmptyCollectionForDelete() ) {
                    LOG.debug( "Checking for virtual delete: " + childResource.getUriPath( req.getClient() ) );
                    if ( childResource.isVirtual() && HiddenResourceUtil.getInstance().isHidden( childResource ) ) {
                        LOG.debug( "Virtually deleted: " + childResource.getUriPath( req.getClient() ) );
                        continue;
                    }
                }

                appendAllPropResponses( req, childResource, multistatus, namesOnly, depth - 1 );
            }
        }
    }

    private Element addProperty( Element parent, String name, Element value, boolean namesOnly ) {
        if ( value != null ) {
            return addElement( parent, createElement( name, namesOnly ? null : value ) );
        }
        return null;
    }

    private Element createElement( String name ) {
        return DocumentHelper.createElement( new QName( name, DAV_NS ) );
    }

    private Element createElement( String name, Element element ) {
        return createElement( new QName( name, DAV_NS ), element );
    }

    private Element createElement( QName name, Element element ) {
        Element e = DocumentHelper.createElement( name );
        if ( e != null )
            e.add( element );

        return e;
    }

    private Element addProperty( Element parent, String name, long value, boolean namesOnly ) {
        if ( value >= 0 ) {
            return addElement( parent, createElement( name, namesOnly ? null : String.valueOf( value ) ) );
        }
        return null;
    }

    private Element addElement( Element parent, Element child ) {
        parent.add( child );
        return child;
    }

    private Element addProperty( Element parent, String name, String value, boolean namesOnly ) {
        if ( value != null ) {
            return addElement( parent, createElement( name, namesOnly ? null : value ) );
        }
        return null;
    }

    private Element addProperty( Element parent, String name, Date value, boolean namesOnly, boolean iso8601Date ) {
        if ( value != null ) {
            String svalue = namesOnly ? null : iso8601Date ? ISO8601_FORMAT.format( value ) : HTTP_DATE_FORMAT
                    .format( value );
            Element e = addElement( parent, createElement( name, svalue ) );
            if ( e != null )
                e.addAttribute( new QName( "dt", DATA_NS ), iso8601Date ? "dateTime.tz" : "dateTime.rfc1123" );
        }
        return null;
    }

    private Element createElement( String name, String text ) {
        return createElement( new QName( name, DAV_NS ), text );
    }

    private Element createElement( QName name, String text ) {
        Element e = DocumentHelper.createElement( name );
        if ( text != null )
            e.setText( text );
        return e;
    }

    private void addStatusElement( Element parent, int status ) {
        parent.addElement( new QName( STATUS_EL, DAV_NS ) ).addText( getHttpStatus( status ) );
    }

    private String getHttpStatus( int status ) {
        return "HTTP/1.1 " + status + " " + WebdavUtil.getStatusMessage( status );
    }

    private void addHrefElement( Element response, WebdavRequest req, Resource resource )
            throws UnsupportedEncodingException {
        // String absolute = encodeUrl(makeAbsoluteURI(req, resource));
        String absolute = makeAbsoluteURI( req, resource );
        response.addElement( new QName( HREF_EL, DAV_NS ) ).addText( absolute );
    }

    private String makeAbsoluteURI( WebdavRequest req, Resource resource ) {
        StringBuffer uri = makePrefixURI( req );

        uri.append( resource.getUriPath( req.getClient() ) );

        return uri.toString();
    }

    private StringBuffer makePrefixURI( WebdavRequest req ) {
        StringBuffer uri = new StringBuffer();

        // Prefix the server name.
        // uri.append(req.getScheme()).append("://");
        // uri.append(req.getServerName());
        // if ("http".equals(req.getScheme()) && req.getServerPort() != 80
        // || "https".equals(req.getScheme()) && req.getServerPort() != 443)
        // uri.append(":").append(req.getServerPort());

        // Prefix the context and servlet paths.
        uri.append( req.getContextPath() ).append( req.getServletPath() ).append( rootPrefix );
        return uri;
    }

    private Location findLocation( WebdavRequest req ) {
        String path = req.getPathInfo();
        return findLocation( req, path );
    }

    private Location findLocation( WebdavRequest req, String path ) {
        // Drop the server name since it is often distorted by mod_proxy, etc
        int protocol = path.indexOf( "://" );
        if ( protocol >= 0 ) {
            path = path.substring( path.indexOf( "/", protocol + 3 ) + req.getContextPath().length()
                    + req.getServletPath().length() );
        }

        // Then, drop the root prefix, if it is specified
        if ( rootPrefix != null && path.startsWith( rootPrefix ) )
            path = path.substring( rootPrefix.length() );

        String[] names = path.split( Resource.PATH_SEPARATOR );

        Resource rsc = findResource( names, names.length - 1 );
        if ( rsc != null )
            return new Location( rsc, decode( names[names.length - 1] ) );
        else
            return null;
    }

    private String decode( String name ) {
        try {
            return URL_CODEC.decode( name );
        } catch ( DecoderException e ) {
            throw new InternalServerException( WebdavUtil.getText( "webdav.resourceservlet.error.cantdecodename",
                    new Object[]{name} ) );
        }
    }

    private Resource findResource( WebdavRequest req ) {
//        String path = req.getPathInfo();
    	String path = req.getRequestURI();

        if ( rootPrefix != null && path.startsWith( rootPrefix ) )
            path = path.substring( rootPrefix.length() );

        String[] names = path.split( Resource.PATH_SEPARATOR );

        return findResource( names, names.length );
    }

    private Resource findResource( String[] names, int endAt ) {
        Resource resource = backend.getRootResource();
        String name;
        
        for ( int i = 0; i < endAt; i++ ) {
            if ( resource instanceof CollectionResource ) {
                name = names[i];
                if ( name.length() > 0 ) {
                    // Decode the name
                    name = decode( name );
                    resource = ( ( CollectionResource ) resource ).getChild( name );
                }
            } else {
                return null;
            }
        }

        return resource;
    }

    protected void doPropPatch( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        Resource resource = findResource( req );
        if ( resource == null ) {
            resp.sendError( WebdavResponse.SC_NOT_FOUND );
            return;
        }

        checkReadOnly( req );

        fixCollectionPath( resource, req, resp );

        // Process the request
        if ( req.getContentLength() > 0 ) {
            try {
                Document reqDoc = parseDocument( req.getInputStream() );

                // DEBUG: Output the document
                writeDocumentDebugging( reqDoc );

                Element root = reqDoc.getRootElement();
                if ( DAV_NSURI.equals( root.getNamespaceURI() ) && PROPERTYUPDATE_EL.equals( root.getName() ) ) {
                    Document respDoc = DocumentHelper.createDocument();
                    Element multistatus = respDoc.addElement( new QName( MULTISTATUS_EL, DAV_NS ) );

                    Element response = multistatus.addElement( new QName( RESPONSE_EL, DAV_NS ) );

                    addHrefElement( response, req, resource );

                    Element forbiddenPropstats = response.addElement( new QName( PROPSTAT_EL, DAV_NS ) );

                    List children = root.elements();
                    int cmdCount = 0;

                    Iterator i = children.iterator();
                    while ( i.hasNext() ) {
                        Element cmd = ( Element ) i.next();

                        if ( DAV_NSURI.equals( cmd.getNamespaceURI() ) ) {
                            // TODO: Figure out how to support property updates
                            // (remove/set).
                            // For now, mark everything as "Forbidden"
                            Element prop, value;
                            if ( cmd.elements() != null && cmd.elements().size() == 1 ) {
                                prop = ( Element ) cmd.elements().get( 0 );
                                if ( prop.elements() != null && cmd.elements().size() > 0 ) {
                                    value = ( Element ) prop.elements().get( 0 );
                                    value.clearContent();
                                    Element newProp = forbiddenPropstats.addElement( new QName( PROP_EL, DAV_NS ) );
                                    newProp.add( value.createCopy() );
                                }
                            } else {
                                resp.sendError( WebdavResponse.SC_BAD_REQUEST, WebdavUtil
                                        .getText( "webdav.resourceservlet.error.invalidproppatchrequest" ) );
                            }

                            cmdCount++;
                        }

                    }

                    addStatusElement( forbiddenPropstats, WebdavResponse.SC_FORBIDDEN );

                    if ( cmdCount == 0 ) {
                        // default to allprop'
                        resp.sendError( WebdavResponse.SC_BAD_REQUEST, WebdavUtil
                                .getText( "webdav.resourceservlet.error.invalidproppatchrequest" ) );
                    } else {
                        resp.setStatus( WebdavResponse.SC_MULTI_STATUS );
                        resp.setContentType( "text/xml; charset=\"utf-8\"" );

                        // DEBUG: Output the document
                        writeDocumentDebugging( respDoc );
                        writeDocument( resp.getOutputStream(), respDoc );
                    }
                } else {
                    resp.sendError( WebdavResponse.SC_BAD_REQUEST, WebdavUtil
                            .getText( "webdav.resourceservlet.error.invalidnamespaceproppatchrequest" ) );
                }
            } catch ( DocumentException e ) {
                LOG.error( "A DocumentException has occured", e );
                resp.sendError( WebdavResponse.SC_BAD_REQUEST, WebdavUtil
                        .getText( "webdav.resourceservlet.error.cantparserequestxml" ) );
            }
        } else {
            doPropFindAllProp( req, resp, resource, false );
        }
    }
    
    
    
    protected void doMkCol( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        if ( req.getContentLength() > 0 ) {
            // MKCOL requests with body are not supported
            resp.sendError( WebdavResponse.SC_UNSUPPORTED_MEDIA_TYPE );
            return;
        }

        Location loc = findLocation( req );
        if ( loc != null ) {
            checkReadOnly( req );

            Resource parent = loc.getParent();
            if ( parent instanceof CollectionResource ) {
                CollectionResource crc = ( CollectionResource ) parent;
                String childName = loc.getChildName();
                
                if(childName.endsWith(TEMPORARY_ITEMS)) {
                	return;
                }

                Resource child = crc.getChild( childName );
                if ( child == null ) {
                    crc.createChildCollection( childName );
                    resp.setStatus( WebdavResponse.SC_CREATED ); // [WBDV-25]
                    // TODO: Temporarily hide the parent and it's decendents on
                    // the Mac
                    // if (req.isMacWebfsAgent())
                    // HiddenResourceUtil.getInstance().hideResource(parent);
                } else {
                    resp.sendError( WebdavResponse.SC_METHOD_NOT_ALLOWED, WebdavUtil
                            .getText( "webdav.resourceservlet.error.resourcealreadyexist" ) );
                }
            } else if ( parent != null ) {
                resp.sendError( WebdavResponse.SC_FORBIDDEN, WebdavUtil
                        .getText( "webdav.resourceservlet.error.parentcantcontainresource" ) );
            }
        } else {
            resp.sendError( WebdavResponse.SC_CONFLICT );
        }
    }

    private void checkReadOnly( WebdavRequest req ) {
        if ( isReadOnly( req ) )
            throw new ForbiddenException();
    }

    private boolean isReadOnly( WebdavRequest req ) {
        return backend.isReadOnly();
    }

    protected void doGet( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        handleResponse( req, resp, true );
    }

    private void handleResponse( WebdavRequest req, WebdavResponse resp, boolean outputBody ) throws IOException {
        Resource resource = findResource( req );

        fixCollectionPath( resource, req, resp );

        if ( resource == null ) {
            resp.sendError( WebdavResponse.SC_NOT_FOUND );
            return;
        } else if ( resource instanceof SingleResource ) {
            SingleResource src = ( SingleResource ) resource;
            resp.setContentType( src.getContentType() );

            if ( src.getContentLength() >= 0 )
                resp.setContentLength( src.getContentLength() );

            resp.setContentLanguage( src.getContentLanguage() );
            resp.setETag( src.getETag() );
            resp.setLastModified( src.getLastModified() );

            if ( outputBody ) {
                InputStream in = src.getInputStream();
                OutputStream out = resp.getOutputStream();
                IOUtils.pipe( in, out );
                in.close();
                // out.close();

                // DEBUG: Checking file lengths
                // log.debug("content-length: " + src.getContentLength() + ";
                // actual length: " + length);
            }

            req.getClient().setContentDisposition( resp, resource.getName(), contentEncoding );

            resp.setStatus( WebdavResponse.SC_OK );
        } else if ( resource instanceof CollectionResource ) {
            if ( outputBody ) {
                // This will only be called by browsers or other such
                // interfaces.
                // Give them a simple HTML list of the resource contents.
                CollectionResource crc = ( CollectionResource ) resource;

                PrintStream out = new PrintStream( resp.getOutputStream() );

                String xhtml = backend.getCollectionXHTML( crc, req, resp );

                out.print( xhtml );
            }

            resp.setContentType( "text/html" );

            resp.setStatus( WebdavResponse.SC_OK );
        }

        resp.flushBuffer();
    }

    protected void doHead( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        handleResponse( req, resp, false );
    }

    /**
     * Handles a POST request.
     * 
     * @param req
     *            The request.
     * @param resp
     *            The response.
     * @throws ServletException
     *             if there was a problem within the servlet.
     * @throws IOException
     *             if there was a problem with IO.
     */
    protected void doPost( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        // Just ignore any post values and do a get.
        doGet( req, resp );
    }

    protected void doDelete( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
    	// HACK - try to avoid the URI startsWith ._, which are temporary file created by MAC
    	// if will have MAC error code -43 if the ._ uri pass to the findResource(req)
    	String uri = req.getRequestURI();
    	if(StringUtils.isNotBlank(uri) && uri.indexOf("._") != -1) {
    		String[] values = uri.split(Resource.PATH_SEPARATOR);
    		if(values != null) {
    			int length = values.length;
    			if(StringUtils.isNotBlank(values[length - 1]) && values[length -1].startsWith("._"))
    				return;
    		}
    	}
        Resource resource = findResource( req );
        if ( resource == null ) {
            resp.sendError( WebdavResponse.SC_NOT_FOUND );
            return;
        }

        // HACK - pretend to delete virtual files for Macs. - WBDV-32
        if ( req.getClient().requiresEmptyCollectionForDelete() && resource.isVirtual() ) {
            LOG.debug( "virtual delete: " + req.getRequestURI() );
            HiddenResourceUtil.getInstance().hideResource( resource );
            resp.setStatus( WebdavResponse.SC_NO_CONTENT );
            return;
        }

        checkReadOnly( req );

        ErrorReport errs = new ErrorReport( makePrefixURI( req ).toString() );
        errs.setDescription( WebdavUtil.getText( "webdav.resourceservlet.info.deletingresource",
                new Object[]{makeAbsoluteURI( req, resource )} ) );

        if ( resource.delete( errs ) ) {
            resp.setStatus( WebdavResponse.SC_NO_CONTENT );
        } else {
            sendMultiStatus( resp, errs );
        }
    }
    
    protected boolean isTempFile(String childName) {
    	if(TEMP_FILE_NAMES != null && TEMP_FILE_NAMES.length > 0) {
    		for(int i = 0; i < TEMP_FILE_NAMES.length; i++) {
    			if(childName.endsWith(TEMP_FILE_NAMES[i]))
    				return true;
    		}
    	}
    	return false;
    }
    
    protected void doPut( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        LOG.debug( "doPut-contentLength=" + req.getContentLength() + ",contentType=" + req.getContentType() );
        checkReadOnly( req );

        // Check for options we don't support
        if ( req.getHeader( "Content-Range" ) != null ) {
            resp.sendError( WebdavResponse.SC_NOT_IMPLEMENTED );
            return;
        }

        Location loc = findLocation( req );

        if ( loc != null ) {
            Resource parent = loc.getParent();
            if ( parent instanceof CollectionResource ) {
                CollectionResource crc = ( CollectionResource ) parent;
                String childName = loc.getChildName();

                String contentType = req.getContentType();
                if ( contentType == null )
                    contentType = getServletContext().getMimeType( childName );
                if ( contentType == null )
                    contentType = ContentTypes.getContentType( childName );

                LOG.debug( "doPut-childName=" + childName + ", contentType=" + contentType );
                
                if ( !isTempFile(childName) && crc.saveChildData( childName, req.getInputStream(), req.getContentLength(), contentType, req
                        .getCharacterEncoding() ) ) {
                    if ( req.getContentLength() == 0 ) {
                        // [WBDV-2]
                        resp.setStatus( WebdavResponse.SC_NO_CONTENT );
                    } else {
                        resp.setStatus( WebdavResponse.SC_CREATED );
                    }
                } else {
                    resp.setStatus( WebdavResponse.SC_OK );

                    // HACK - clear virtual deleted files for Macs.
                    if ( req.getClient().requiresEmptyCollectionForDelete()
                            || req.getClient().requiresEmptyCollectionAfterCreate() ) {
                        HiddenResourceUtil.getInstance().revealResource( parent );
                    }
                }
            }
        } else {
            resp.sendError( WebdavResponse.SC_CONFLICT );
        }
    }

    protected void doCopy( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
    	
    	dumpHeaders2(req, true);
        checkReadOnly( req );

        ErrorReport errs = new ErrorReport( makePrefixURI( req ).toString() );
        
        // First, find the source
        Resource source = findResource( req );
        if ( source == null ) {
        	LOG.warn("Resource is null here ");
            resp.sendError( WebdavResponse.SC_NOT_FOUND );
            return;
        } else if ( req.getDestination().endsWith( makeAbsoluteURI( req, source ) ) ) {
            resp.sendError( WebdavResponse.SC_FORBIDDEN, WebdavUtil
                    .getText( "webdav.resourceservlet.error.sourceanddestaresame" ) );
            return;
        }

        // First, get the parent of the target destination
        String path = req.getDestination();
        Location loc = findLocation( req, path );

        if ( loc != null ) {
            Resource parent = loc.getParent();
            if ( parent instanceof CollectionResource ) {
                CollectionResource crc = ( CollectionResource ) parent;
                Resource target = crc.getChild( loc.getChildName() );

                if ( target != null ) {
                    // 8.8.4
                    if ( req.isOverwrite() ) {
                        target.delete( errs );
                        resp.setStatus( WebdavResponse.SC_NO_CONTENT );
                    } else {
                        resp.sendError( WebdavResponse.SC_PRECONDITION_FAILED );
                        return;
                    }
                } else {
                    resp.setStatus( WebdavResponse.SC_CREATED );
                }

                if ( !source.copyTo( crc, loc.getChildName(), req.isOverwrite(), req.getDepth() != 1, errs ) ) {
                    sendMultiStatus( resp, errs );
                }
            } else {
                resp.sendError( WebdavResponse.SC_CONFLICT );
            }
        } else {
            resp.sendError( WebdavResponse.SC_CONFLICT );
        }
    }

    private void sendMultiStatus( WebdavResponse resp, ErrorReport errs ) throws IOException {
        sendMultiStatus( resp, errs, WebdavResponse.SC_MULTI_STATUS );
    }

    private void sendMultiStatus( WebdavResponse resp, ErrorReport errs, int status ) throws IOException {
        resp.setStatus( status );
        resp.setContentType( "text/xml; charset=utf-8" );

        Document doc = errs.createXML();
        writeDocument( resp.getOutputStream(), doc );
    }

    protected void doMove( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        // removed [...]
    }

    protected void doLock( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        // HACK - allows saving from word documents under Windows
        // if ( req.isMicrosoftAgent() )
//        resp.sendError( WebdavResponse.SC_PRECONDITION_FAILED );

        // TODO: Implement Locking.
//        Document doc = DocumentHelper.createDocument();
//		Element prop = doc.addElement(new QName(PROP_EL, DAV_NS));
//		Element lockDiscovery = prop.addElement(new QName(LOCKDISCOVERY_EL,
//				DAV_NS));
//		Element activeLock = lockDiscovery.addElement(new QName(ACTIVELOCK_EL,
//				DAV_NS));
//		activeLock.addElement(new QName(LOCKTYPE_EL, DAV_NS)).addElement(
//				new QName(WRITE_EL, DAV_NS));
//		activeLock.addElement(new QName(LOCKSCOPE_EL, DAV_NS)).addElement(
//				new QName(SHARED_EL, DAV_NS));
//		activeLock.addElement(new QName(DEPTH_EL, DAV_NS)).setText("0");
//		activeLock.addElement(new QName(OWNER_EL, DAV_NS)).addElement(
//				new QName(HREF_EL, DAV_NS)).setText("default-owner");
//		activeLock.addElement(new QName(TIMEOUT_EL, DAV_NS)).setText(
//				"Second-599");
//		activeLock.addElement(new QName(LOCKTOCKEN_EL, DAV_NS)).addElement(
//				new QName(HREF_EL, DAV_NS)).setText(
//				"opaquelocktoken:confluenceToken");

		resp.setStatus(WebdavResponse.SC_OK);
		resp.setContentType( "text/xml; charset=\"utf-8\"" );

        // DEBUG: Output the document
        // writeDocumentDebugging( doc );
        // writeDocument( resp.getOutputStream(), doc );
    }

    protected void doUnlock( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        resp.setStatus( WebdavResponse.SC_OK );
        resp.setContentType( "text/xml; charset=\"utf-8\"" );
        Document doc = DocumentHelper.createDocument();

        // DEBUG: Output the document
        writeDocumentDebugging( doc );
        writeDocument( resp.getOutputStream(), doc );
    }

    protected void doOptions( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        resp.setDavClass2( true );

        String methods = "OPTIONS, TRACE, GET, HEAD, PUT, PROPFIND";
        if ( !isReadOnly( req ) )
            methods += ", PROPPATCH, MKDIR, DELETE, COPY, LOCK, UNLOCK";
        resp.addHeader( "Allow", methods );
        resp.addHeader( "Public", methods );

    }

    protected void service( WebdavRequest req, WebdavResponse resp ) throws ServletException, IOException {
        // if (LOG.isDebugEnabled()) {
        // // DEBUG: Header dump
        // dumpHeaders(req, false);
        // }

        try {
            // Get Authorization header
            if ( requireAuthentication && !authenticateUser( req ) ) {
                // && !METHOD_OPTIONS.equals(req.getMethod())
                requestAuthentication( resp );
                return;
            }

            backend.initRequest( req );

            super.service( req, resp );

        } catch ( InsufficientAuthorizationException e ) {
            LOG.debug( "An InsufficientAuthorizationException has occured", e );
            if ( requireAuthentication && !backend.isUserAuthenticated() )
                requestAuthentication( resp );
            else {
                String msg = e.getMessage();
                if ( msg == null )
                    resp.sendError( HttpServletResponse.SC_FORBIDDEN );
                else
                    resp.sendError( HttpServletResponse.SC_FORBIDDEN, msg );

            }
        } catch ( ErrorResponseException e ) {
            LOG.error( "An ErrorResponseException has occured", e );
            if ( e.getMessage() != null )
                resp.sendError( e.getStatus(), e.getMessage() );
            else
                resp.sendError( e.getStatus() );
        } catch ( UnsupportedOperationException e ) {
            LOG.error( "An UnsupportedOperationException has occured", e );
            resp.sendError( HttpServletResponse.SC_FORBIDDEN, e.getMessage() );
        } catch ( UnexpectedProblemException e ) {
            LOG.error( "An UnexpectedProblemException hass occured", e );
            resp.sendError( WebdavResponse.SC_INTERNAL_SERVER_ERROR );
        } finally {
            // clear authenticated user
            backend.clearUserAuthentication();
        }
    }

    private void requestAuthentication( HttpServletResponse resp ) throws IOException {
        // Authorisation is required for the requested action.
        resp.setHeader( "WWW-Authenticate", "BASIC realm=\"" + backend.getName() + "\"" );
        resp.sendError( HttpServletResponse.SC_UNAUTHORIZED );
    }

    private boolean authenticateUser( HttpServletRequest req ) {
        String auth = req.getHeader( "Authorization" );

        if ( auth != null && auth.toUpperCase().startsWith( "BASIC " ) ) {
            // Get encoded user and password, comes after "BASIC "
            String userpassEncoded = auth.substring( 6 );

            try {
                // Decode it, using any base 64 decoder (we use
                // com.oreilly.servlet)
                String userpassDecoded = new String( Base64
                        .decodeBase64( userpassEncoded.getBytes( "iso-8859-1" ) ) );

                // Check our user list to see if that user and password are
                // "allowed"
                String username, password;
                int split = userpassDecoded.indexOf( ':' );
                if ( split >= 0 ) {
                    username = userpassDecoded.substring( 0, split );
                    password = userpassDecoded.substring( split + 1, userpassDecoded.length() );

                    return backend.authenticateUser( username, password );
                }
            } catch ( UnsupportedEncodingException e ) {
                LOG.error( "An UnsupportedEncodingException occurred", e );
            }
        }

        return false;
    }

    private void dumpHeaders( HttpServletRequest req, boolean verbose ) {
    	Enumeration e = req.getHeaderNames();
    	String name;
    	
    	LOG.debug( req.getMethod() + " " + req.getRequestURI() + " " + req.getProtocol() + " "
    			+ req.getContentLength() );
    	
    	if ( verbose ) {
    		while ( e.hasMoreElements() ) {
    			name = ( String ) e.nextElement();
    			
    			String value = req.getHeader( name );
    			LOG.debug( name + ": " + value );
    		}
    		LOG.debug( "" );
    	}
    }
    
    private void dumpHeaders2( HttpServletRequest req, boolean verbose ) {
    	Enumeration e = req.getHeaderNames();
    	String name;
    	
    	LOG.warn( req.getMethod() + " " + req.getRequestURI() + " " + req.getProtocol() + " "
    			+ req.getContentLength() );
    	
    	if ( verbose ) {
    		while ( e.hasMoreElements() ) {
    			name = ( String ) e.nextElement();
    			
    			String value = req.getHeader( name );
    			LOG.warn( name + ": " + value );
    		}
    		LOG.warn( "" );
    	}
    }

}
