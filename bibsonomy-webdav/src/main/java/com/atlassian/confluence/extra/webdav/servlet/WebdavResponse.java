package com.atlassian.confluence.extra.webdav.servlet;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

public interface WebdavResponse extends HttpServletResponse {

    String STATUS_URI = "Status-URI";

    String LOCK_TOKEN = "Lock-Token";

    String LAST_MODIFIED = "Last-Modified";

    String ETAG = "ETag";

    String DAV = "DAV";

    String CONTENT_LANGUAGE = "Content-Language";

    /**
     * Status code (413) indicating the server is refusing to process a request
     * because the request entity is larger than the server is willing or able
     * to process.
     */
    int SC_REQUEST_TOO_LONG = 413;

    /**
     * Status code (207) indicating that the response requires providing status
     * for multiple independent operations.
     */
    int SC_MULTI_STATUS = 207;

    /**
     * Status code (418) indicating the entity body submitted with the PATCH
     * method was not understood by the resource.
     */
    int SC_UNPROCESSABLE_ENTITY = 418;

    /**
     * Status code (419) indicating that the resource does not have sufficient
     * spaces to record the state of the resource after the execution of this
     * method.
     */
    int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;

    /**
     * Status code (420) indicating the method was not executed on a particular
     * resource within its scope because some part of the method's execution
     * failed causing the entire method to be aborted.
     */
    int SC_METHOD_FAILURE = 420;

    /**
     * Status code (423) indicating the destination resource of a method is
     * locked, and either the request did not contain a valid Lock-Info header,
     * or the Lock-Info header identifies a lock held by another principal.
     */
    int SC_LOCKED = 423;

    boolean isDavClass1();

    void setDavClass1( boolean isClass1 );

    boolean isDavClass2();

    void setDavClass2( boolean isClass2 );

    void setLockToken( String lockToken );

    void setStatusURI( int status, String uri );

    void setContentLanguage( String contentLanguage );

    void setETag( String eTag );

    void setLastModified( Date creationDate );

}