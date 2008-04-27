package com.atlassian.confluence.extra.webdav.servlet.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a simple extension/mime-type lookup database.
 * 
 * @author David Peterson
 */
public final class ContentTypes {
    private static final Map<String, String> TYPES = new HashMap<String, String>();

    static {
        // MS Word
        TYPES.put( ".doc", "application/vnd.ms-word" );
        TYPES.put( ".docm", "application/vnd.ms-word.document.macroEnabled.12" );
        TYPES.put( ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" );

        // MS PowerPoint
        TYPES.put( ".ppt", "application/vnd.ms-powerpoint" );
        TYPES.put( ".ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow" );
        TYPES.put( ".pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12" );
        TYPES.put( ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" );

        // MS Excel
        TYPES.put( ".xls", "application/vnd.ms-excel" );
        TYPES.put( ".xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12" );
        TYPES.put( ".xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12" );
        TYPES.put( ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
        TYPES.put( ".xltm", "application/vnd.ms-excel.template.macroEnabled.12" );

        // PDF
        TYPES.put( ".pdf", "application/pdf" );

        // Images
        TYPES.put( ".gif", "image/gif" );
        TYPES.put( ".jpg", "image/jpeg" );
        TYPES.put( ".jpeg", "image/jpeg" );
        TYPES.put( ".png", "imate/png" );
    }

    private ContentTypes() {
    }

    public static String getContentType( String filename ) {
        String type = null;

        int dot = filename.lastIndexOf( '.' );
        if ( dot >= 0 )
            type = ( String ) TYPES.get( filename.substring( dot ).toLowerCase() );

        return type == null ? "application/octet-stream" : type;
    }
}