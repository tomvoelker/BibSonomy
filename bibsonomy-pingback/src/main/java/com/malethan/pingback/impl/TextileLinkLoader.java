//    Copyright (c) 2009 Elwyn Malethan
//
//    This file is part of java-pingback.
//
//    java-pingback is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    java-pingback is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

//    You should have received a copy of the GNU General Public License
//    along with java-pingback.  If not, see <http://www.gnu.org/licenses/>.

package com.malethan.pingback.impl;

import com.malethan.pingback.LinkLoader;
import com.malethan.pingback.Link;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TextileLinkLoader implements LinkLoader{
    //----------------------------------------------------------------------- Static Properties and Constants

    private static final Log log = LogFactory.getLog(TextileLinkLoader.class);

    /**
     * This regex checks for both Textile ("blah":url) and HTML (&lt;a href="url"&gt;blah&lt;/a&gt;) links.
     */
    public static final String URL_REGEX = "((\":)|href=\")((http(s?)\\:\\/\\/|~/|/)?((\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel|[a-z]{2}))|localhost)(:[\\d]{1,5})?(((/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|/)+|\\?|#)?((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)(&([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?)";

    //----------------------------------------------------------------------- Static Methods
    //----------------------------------------------------------------------- Instance Properties

    private String title;
    private String pingbackUrl;
    private boolean success;

    //----------------------------------------------------------------------- Constructors
    //----------------------------------------------------------------------- Getters and Setters
    //----------------------------------------------------------------------- Instance Methods

    public Link loadLink(String linkUrl) {
        reset();
        try {
            URL url = new URL(linkUrl);
            URLConnection con = url.openConnection();
            pingbackUrl = con.getHeaderField("X-Pingback");
            if(con.getContentType().indexOf("text/html") > -1) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String headContent = readHeadSectionOfPage(reader);
                processHtmlForPingbackUrlAndTitle(headContent);
                success = true;
            }
        } catch (IOException e) {
            log.error("Had a problem with url: '" + linkUrl + "'", e);
        }

        return new Link(title, linkUrl, pingbackUrl, success);
    }

    private void reset() {
        this.title = null;
        this.pingbackUrl = null;
        this.success = false;
    }

    public List<String> findLinkAddresses(String textileText) {
        Pattern linkPattern = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
        List<String> linkUrls = new ArrayList<String>();
        Matcher linkMatcher = linkPattern.matcher(textileText);
        while(linkMatcher.find()) {
            linkUrls.add(linkMatcher.group(3));
        }
        return linkUrls;
    }

    public boolean containsLink(String htmlText, String link) {
        for (String linkInText : findLinkAddresses(htmlText)) {
            if(linkInText.equalsIgnoreCase(link)) {
                return true;
            }
        }
        return false;
    }

    public String loadPageContents(String linkUrl) {
        try {
            URL url = new URL(linkUrl);
            URLConnection con = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return readPage(reader);
        } catch (IOException e) {
            log.error("Had a problem with url: '" + linkUrl + "'", e);
        }
        return null;   
    }

    protected String readHeadSectionOfPage(BufferedReader reader) throws IOException {
        String content = "";
        String line = reader.readLine();
        while(line != null && !reachedEndOfHeadSection(line)) {
            content += line;
            line = reader.readLine();
        }
        return content;
    }

    protected String readPage(BufferedReader reader) throws IOException {
        String content = "";
        String line = reader.readLine();
        while(line != null) {
            content += line;
            line = reader.readLine();
        }
        return content;
    }

    protected boolean reachedEndOfHeadSection(String line) {
        return line.matches("<body[^>]+>|</head>");
    }

    protected void processHtmlForPingbackUrlAndTitle(String content) {
        if (pingbackUrl == null) {
            pingbackUrl = getPingbackUrlFromHtml(content);
        }
        title = getTitleFromHtml(content);
    }

    protected String getTitleFromHtml(String html) {
        Pattern titlePattern = Pattern.compile("<title>([^<>]+)</title>");
        Matcher matcher = titlePattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("&[^;]+;", "").replaceAll("\\s+", " ");
        }
        return null;
    }

    protected String getPingbackUrlFromHtml(String html) {
        Pattern pingbackUrlPattern = Pattern.compile("<link rel=\"pingback\" href=\"([^\"]+)\" ?/?>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pingbackUrlPattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
