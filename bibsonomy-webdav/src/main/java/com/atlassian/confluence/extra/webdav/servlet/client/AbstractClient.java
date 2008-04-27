package com.atlassian.confluence.extra.webdav.servlet.client;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.Logger;

import com.atlassian.confluence.extra.webdav.servlet.WebdavClient;
import com.atlassian.confluence.extra.webdav.servlet.WebdavResponse;
//import com.atlassian.confluence.util.GeneralUtil;

public abstract class AbstractClient implements WebdavClient {

    protected static final BitSet UNSAFE_CHARS;

    protected static final BitSet RESERVED_CHARS;

    protected static final BitSet URL_SAFE_CHARS;

    private static final Logger LOG = Logger.getLogger( AbstractClient.class );

    static {
        // unsafe = CTL | SP | <"> | "#" | "%" | "<" | ">"
        UNSAFE_CHARS = new BitSet( 256 );
        UNSAFE_CHARS.set( 0, 32 ); // CTL
        UNSAFE_CHARS.set( 127 ); // DEL
        UNSAFE_CHARS.set( ' ' ); // SP
        UNSAFE_CHARS.set( '"' );
        UNSAFE_CHARS.set( '#' );
        UNSAFE_CHARS.set( '%' );
        UNSAFE_CHARS.set( '<' );
        UNSAFE_CHARS.set( '>' );

        // reserved = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+"
        RESERVED_CHARS = new BitSet( 256 );

        RESERVED_CHARS.set( ';' );
        RESERVED_CHARS.set( '/' );
        RESERVED_CHARS.set( '?' );
        RESERVED_CHARS.set( ':' );
        RESERVED_CHARS.set( '@' );
        RESERVED_CHARS.set( '&' );
        RESERVED_CHARS.set( '=' );
        RESERVED_CHARS.set( '+' );

        // Safe characters, accoring to the spec.
        URL_SAFE_CHARS = new BitSet( 256 );
        // ALPHA
        URL_SAFE_CHARS.set( 'a', 'z' + 1 );
        URL_SAFE_CHARS.set( 'A', 'Z' + 1 );

        // DIGIT
        URL_SAFE_CHARS.set( '0', '9' + 1 );

        // extra = "!" | "*" | "'" | "(" | ")" | ","
        URL_SAFE_CHARS.set( '!' );
        URL_SAFE_CHARS.set( '*' );
        URL_SAFE_CHARS.set( '\'' );
        URL_SAFE_CHARS.set( '(' );
        URL_SAFE_CHARS.set( ')' );
        URL_SAFE_CHARS.set( ',' );

        // safe = "$" | "-" | "_" | "."
        URL_SAFE_CHARS.set( '$' );
        URL_SAFE_CHARS.set( '-' );
        URL_SAFE_CHARS.set( '_' );
        URL_SAFE_CHARS.set( '.' );

        // clear unsafe
        URL_SAFE_CHARS.andNot( UNSAFE_CHARS );

        // clear reserved.
        URL_SAFE_CHARS.andNot( RESERVED_CHARS );
    }

    private String userAgent;

    public AbstractClient( String userAgent ) {
        this.userAgent = userAgent;
    }

    protected String getUserAgent() {
        return userAgent;
    }

    public String encodeFileName( String name, String encoding ) {
        return encode( URL_SAFE_CHARS, name, encoding );
    }

    protected String encode( BitSet safeChars, String name, String encoding ) {
        if ( encoding == null )
            encoding = WebdavClient.ISO_8859_1_ENCODING;
        
        try {
            return new String( URLCodec.encodeUrl( safeChars, name.getBytes( encoding ) ), encoding );
        } catch ( UnsupportedEncodingException e ) {
            // Just print the stack trace and return the original value.
            LOG.warn( "Unsupported encoding: " + encoding );
            return name;
        }
    }

    public void setContentDisposition( WebdavResponse resp, String filename, String contentEncoding ) {
        // we need to specify a charset for the file name to cater for
        // unicode filenames (rfc2184) -- this is not supported in IE
        // the reason why there are two single quotes "'" separating the
        // character set and the filename is because (straight from the
        // rfc):
        // Note that it is perfectly permissible to leave either the
        // character set or language field blank. Note also that the
        // single quote delimiters MUST be present even when one of the
        // field values is omitted.
        resp.addHeader( "content-disposition", "attachment;filename*=" + contentEncoding.toLowerCase() + "''"
                + /*GeneralUtil.urlEncode(*/ filename /*)*/ + ";" );
    }

    public boolean requiresEmptyCollectionAfterCreate() {
        return false;
    }

    public boolean requiresEmptyCollectionForDelete() {
        return false;
    }

    public boolean requiresMoveBeforeSaving() {
        return false;
    }

}
