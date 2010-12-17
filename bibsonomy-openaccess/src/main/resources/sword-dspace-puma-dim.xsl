<?xml version="1.0" encoding="utf-8"?>
<!-- sword-mets-bibtex-dim-ingest.xsl
 * 
 * Copyright (c) 2010, University Library of Kassel
 * Dipl.-Ing. Sven Stefani, stefani@bibliothek.uni-kassel.de
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 -->

<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
        xmlns:bibtex="http://puma.uni-kassel.de"
        version="1.0">

<!-- NOTE: This stylesheet is a work in progress, and does not
     cover all aspects of bibtex schema.
     BTW: who knows a bibtex schema?
-->

	<!-- nicer output -->
	<xsl:output indent="yes"/>


	<!-- Catch all.  This template will ensure that nothing
	     other than explicitly what we want to xwalk will be dealt
	     with -->
	<xsl:template match="text()"></xsl:template> 


<!-- This stylesheet converts incoming DC metadata in a SWAP
     profile into the DSpace Interal Metadata format (DIM) -->

    <!-- match the top level bibtex-entry element and kick off the
         template matching process -->
    <xsl:template match="bibtex:entry">
    	<dim:dim>
    		<xsl:apply-templates/>
    	</dim:dim>
    </xsl:template>
    
    <!-- generate dc.type -->
    <xsl:template match="bibtex:entry/node()">
	<xsl:if test="local-name()!=''">
    	<dim:field mdschema="dc" element="type">
    		<xsl:value-of select="local-name()"/>
    	</dim:field>
	</xsl:if>
    	<xsl:apply-templates/>
    </xsl:template>
    
    <!-- dc.title -->
    <xsl:template match="bibtex:title">
    	<dim:field mdschema="dc" element="title">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>
    
    <!-- dc.contributor.author -->
    <xsl:template match="bibtex:author">
    	<dim:field mdschema="dc" element="contributor" qualifier="author">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.contributor.editor -->
    <xsl:template match="bibtex:editor">
    	<dim:field mdschema="dc" element="contributor" qualifier="editor">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.publisher -->
    <xsl:template match="bibtex:author">
    	<dim:field mdschema="dc" element="contributor" qualifier="author">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.date -->
    <xsl:template match="bibtex:date">
    	<dim:field mdschema="dc" element="date">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.identifier.uri -->
    <xsl:template match="bibtex:url">
    	<dim:field mdschema="dc" element="identifier" qualifier="uri">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.relation.ispartof .... journal oder booktitle, je nach publikationstyp? ...exklusiv oder? -->
    <xsl:template match="bibtex:journal">  
    	<dim:field mdschema="dc" element="contributor" qualifier="author">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.relation.ispartof .... journal oder booktitle, je nach publikationstyp? ...exklusiv oder? -->
    <xsl:template match="bibtex:booktitle">       
        <dim:field mdschema="dc" element="contributor" qualifier="author">
                <xsl:value-of select="."/>
        </dim:field>
    </xsl:template>

    <!-- dc.description.abstract -->
    <xsl:template match="bibtex:abstract">
    	<dim:field mdschema="dc" element="description" qualifier="abstract">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.description.everything -->
    <xsl:template match="bibtex:description">
    	<dim:field mdschema="dc" element="description" qualifier="everything">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.subject -->
    <xsl:template match="bibtex:keyword">
    	<dim:field mdschema="dc" element="subject">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.identifier.isbn -->
    <xsl:template match="bibtex:isbn">
    	<dim:field mdschema="dc" element="identifier" qualifier="isbn">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.identifier.issn -->
    <xsl:template match="bibtex:issn">
    	<dim:field mdschema="dc" element="identifier" qualifier="issn">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>


</xsl:stylesheet>
