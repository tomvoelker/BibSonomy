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

import static junit.framework.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * <p>Copyright &copy; 2009 Elwyn Malethan</p>
 */
public class TextileLinkLoaderTest {
    //----------------------------------------------------------------------- Static Properties and Constants

    private static final String TOP_OF_HTML_PAGE = "" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-US\">\n" +
            "\n" +
            "    <head profile=\"http://gmpg.org/xfn/11\">\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "\n" +
            "    <title>Something blah blah &laquo;  Ping tester</title>\n" +
            "\n" +
            "    <link rel=\"pingback\" href=\"http://localhost/~emalethan/wordpress/xmlrpc.php\" />\n" +
            "\n";

    //----------------------------------------------------------------------- Static Methods
    //----------------------------------------------------------------------- Instance Properties

    private TextileLinkLoader linkLoader;

    //----------------------------------------------------------------------- Constructors
    //----------------------------------------------------------------------- Setup & Teardown

    @Before
    public void setUp() {
        linkLoader = new TextileLinkLoader();
    }

    //----------------------------------------------------------------------- Tests

    @Test
    public void gettingTitleFromHtmlShouldWorkForSingleLine() {
        String html = "Blah\n" +
                "<title>Some great title</title>\n" +
                "Some more blah";

        assertEquals("Some great title", linkLoader.getTitleFromHtml(html));
    }

    @Test
    public void gettingTitleFromHtmlShouldWorkMultiline() {
        String html = "Blah\n" +
                "<title>Some great\n\ntitle</title>\n" +
                "Some more blah";

        assertEquals("Some great title", linkLoader.getTitleFromHtml(html));
    }

    @Test
    public void gettingTitleFromHtmlShouldReturnNullIfAbsent() {
        String html = "Blah\n" +
                "Some more blah";

        assertEquals(null, linkLoader.getTitleFromHtml(html));
    }

    @Test
    public void gettingTitleFromHtmlShouldReturnWithoutEntities() {
        String html = "Blah\n" +
                "<title>Some great &amp; &laquo; &bull; &quot; title</title>\n" +
                "Some more blah";
        assertEquals("Some great title",
                linkLoader.getTitleFromHtml(html));
    }

    @Test
    public void gettingPingbackUrlFromHtmlShouldAdhereToStandard() {
        String[] validPatterns = new String[] {
                "<link rel=\"pingback\" href=\"http://xml.rpc.srv/do_something\">",
                "<link rel=\"pingback\" href=\"http://xml.rpc.srv/do_something\"/>",
                "<link rel=\"pingback\" href=\"http://xml.rpc.srv/do_something\" />"
        };

        for (String validPattern : validPatterns) {
            assertEquals("http://xml.rpc.srv/do_something", linkLoader.getPingbackUrlFromHtml(validPattern));
        }
    }

    @Test
    public void testLocalWordpressDefault() {
        String textileAndHtml= "" +
                "This is some text with a \"great link in it\":http://localhost/~emalethan/wordpress/?p=3  in it.\n" +
                "\n" +
                "<p>This is some text with a <a href=\"http://localhost/~emalethan/wordpress/?p=4\">great link in it</a>  in it.</p>\n" +
                "\n" +
                "Hooray";

        List<String> links = linkLoader.findLinkAddresses(textileAndHtml);
        assertEquals(2, links.size());
        assertEquals("http://localhost/~emalethan/wordpress/?p=3", links.get(0));
        assertEquals("http://localhost/~emalethan/wordpress/?p=4", links.get(1));
    }

    @Test
    public void testRemote() {
        String textileAndHtml= "" +
                "This is some text with a \"great link in it\":http://wordpress.org/development/2009/02/change-the-web-challenge/  in it.\n" +
                "\n" +
                "<p>This is some text with a <a href=\"http://wordpress.org/development/2009/02/change-the-web-challengexx/\">great link in it</a>  in it.</p>\n" +
                "\n" +
                "Hooray";

        List<String> links = linkLoader.findLinkAddresses(textileAndHtml);
        assertEquals(2, links.size());
        assertEquals("http://wordpress.org/development/2009/02/change-the-web-challenge/", links.get(0));
        assertEquals("http://wordpress.org/development/2009/02/change-the-web-challengexx/", links.get(1));

    }

    @Test
    public void testStopsReadingAPageAtTheEndOfHead() throws IOException {
        String htmlPage = "" +
                TOP_OF_HTML_PAGE +
                "   <!-- hello -->\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"page\">\n" +
                "\n" +
                "\n" +
                "    <div id=\"header\">";

        String captured = linkLoader.readHeadSectionOfPage(getReaderForString(htmlPage));
        System.out.println(captured);
        assertTrue("Have got this '<!-- hello -->' in", captured.indexOf("<!-- hello -->") > -1);
        assertFalse("should not have got as far as the end-head tag", captured.indexOf("</head>") > -1);
        assertFalse("should not have got as far as the body tag", captured.indexOf("<body>") > -1);
    }

    public void testStopsReadingAPageAtTheStartOfBody() throws IOException {
        String htmlPage = "" +
                TOP_OF_HTML_PAGE +
                "   <!-- hello -->\n" +
                "<body>\n" +
                "    <div id=\"page\">\n" +
                "\n" +
                "\n" +
                "    <div id=\"header\">";

        String captured = linkLoader.readHeadSectionOfPage(getReaderForString(htmlPage));
        System.out.println(captured);
        assertTrue("Have got this '<!-- hello -->' in", captured.indexOf("<!-- hello -->") > -1);
        assertFalse("should not have got as far as the body tag", captured.indexOf("<body>") > -1);
    }

    public void testStopsReadingAPageAtTheStartOfBodyWithAttributes() throws IOException {
        String htmlPage = "" +
                TOP_OF_HTML_PAGE +
                "   <!-- hello -->\n" +
                "<body class=\"wide\">\n" +
                "    <div id=\"page\">\n" +
                "\n" +
                "\n" +
                "    <div id=\"header\">";

        String captured = linkLoader.readHeadSectionOfPage(getReaderForString(htmlPage));
        System.out.println(captured);
        assertTrue("Have got this '<!-- hello -->' in", captured.indexOf("<!-- hello -->") > -1);
        assertFalse("should not have got as far as the body tag", captured.indexOf("<body>") > -1);
    }

    //----------------------------------------------------------------------- Getters and Setters
    //----------------------------------------------------------------------- Instance Methods

    private BufferedReader getReaderForString(String htmlPage) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(htmlPage.getBytes())));
    }

}
