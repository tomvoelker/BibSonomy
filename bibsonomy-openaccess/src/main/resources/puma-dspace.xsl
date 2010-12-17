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
        xmlns:puma="http://puma.uni-kassel.de/2010/11/PUMA-SWORD"
        xmlns:mets="http://www.loc.gov/METS/"
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
    <xsl:template match="puma:DerPost">
    	<dim:dim>
    		<xsl:apply-templates/>
    	</dim:dim>
    </xsl:template>
    
    <!-- dc.subject -->
    <xsl:template match="puma:tag">
    	<dim:field mdschema="dc" element="subject">
    		<xsl:value-of select="@name"/>
    	</dim:field>
    </xsl:template>


    <xsl:template match="puma:bibtex">

		<xsl:if test="./@entrytype">
	    	<dim:field mdschema="dc" element="type">
	    		<xsl:value-of select="@entrytype"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.title -->
		<xsl:if test="./@title">
	    	<dim:field mdschema="dc" element="title">
	    		<xsl:value-of select="@title"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.contributor.author -->
		<xsl:if test="./@author">
	    	<dim:field mdschema="dc" element="contributor" qualifier="author">
	    		<xsl:value-of select="@author"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.contributor.editor -->
		<xsl:if test="./@editor">
	    	<dim:field mdschema="dc" element="contributor" qualifier="editor">
	    		<xsl:value-of select="@editor"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.publisher -->
		<xsl:if test="./@publisher">
	    	<dim:field mdschema="dc" element="publisher">
	    		<xsl:value-of select="@publisher"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.date -->
		<xsl:if test="./@year">
	    	<dim:field mdschema="dc" element="date">
	    		<xsl:value-of select="@year"/>
			
				<xsl:if test="./@month">
					-<xsl:value-of select="@month"/>
				</xsl:if>
			
				<xsl:if test="./@day">
					-<xsl:value-of select="@day"/>
				</xsl:if>

	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.identifier.uri -->
		<xsl:if test="./@href">
	    	<dim:field mdschema="dc" element="identifier" qualifier="uri">
	    		<xsl:value-of select="@href"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.relation.ispartof .... journal oder booktitle, je nach publikationstyp? ...exklusiv oder? -->
		<xsl:if test="./@journal">
	    	<dim:field mdschema="dc" element="relation" qualifier="ispartof">
	    		<xsl:value-of select="@journal"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.relation.ispartof .... journal oder booktitle, je nach publikationstyp? ...exklusiv oder? -->
		<xsl:if test="./@booktitle">
	    	<dim:field mdschema="dc" element="relation" qualifier="ispartof">
	    		<xsl:value-of select="@booktitle"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.description.abstract -->
		<xsl:if test="./@bibtexAbstract">
	    	<dim:field mdschema="dc" element="description" qualifier="abstract">
	    		<xsl:value-of select="@bibtexAbstract"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.description.everything -->
		<xsl:if test="./@description">
	    	<dim:field mdschema="dc" element="description" qualifier="everything">
	    		<xsl:value-of select="@description"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.identifier.isbn -->
		<xsl:if test="./@xISBN">
	    	<dim:field mdschema="dc" element="identifier" qualifier="isbn">
	    		<xsl:value-of select="@entrytype"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.identifier.issn -->
		<xsl:if test="./@xISSN">
	    	<dim:field mdschema="dc" element="identifier" qualifier="issn">
	    		<xsl:value-of select="@entrytype"/>
	    	</dim:field>
		</xsl:if>

	</xsl:template>


</xsl:stylesheet>
