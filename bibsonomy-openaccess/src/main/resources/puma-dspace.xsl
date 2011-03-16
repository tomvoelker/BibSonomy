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
    <xsl:template match="puma:PumaPostType">
    	<dim:dim>
    		<xsl:apply-templates/>
	    <!-- dc.identifier.isbn -->
		<xsl:if test="./@ISBN">
	    	<dim:field mdschema="dc" element="identifier" qualifier="isbn">
	    		<xsl:value-of select="@ISBN"/>
	    	</dim:field>
		</xsl:if>
	
	    <!-- dc.identifier.issn -->
		<xsl:if test="./@ISSN">
	    	<dim:field mdschema="dc" element="identifier" qualifier="issn">
	    		<xsl:value-of select="@ISSN"/>
	    	</dim:field>
		</xsl:if>
    	</dim:dim>
    </xsl:template>
    
    <xsl:template match="bibtex">

	    <!-- dc.title -->
		<xsl:if test="./@title">
	    	<dim:field mdschema="dc" element="title">
	    		<xsl:value-of select="@title"/>
	    	</dim:field>
		</xsl:if>
	
		<xsl:if test="./@entrytype">
	    	<dim:field mdschema="dc" element="type">
	    		<xsl:value-of select="@entrytype"/>
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
			
				<xsl:if test="./@month">-<xsl:value-of select="@month"/></xsl:if>
			
				<xsl:if test="./@day">-<xsl:value-of select="@day"/></xsl:if>

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

	</xsl:template>

    <!-- dc.subject -->
    <xsl:template match="tag">
    	<dim:field mdschema="dc" element="subject">
    		<xsl:value-of select="@name"/>
    	</dim:field>
    </xsl:template>

    <!-- dc.contributor.corporatename -->
    <xsl:template match="puma:examinstitution">
    	<dim:field mdschema="dc" element="contributor" qualifier="corporatename">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.contributor.referee -->
    <xsl:template match="puma:examreferee">
    	<dim:field mdschema="dc" element="contributor" qualifier="referee">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.date.examination -->
    <xsl:template match="puma:phdoralexam">
    	<dim:field mdschema="dc" element="date" qualifier="examination">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.description.sponsorship -->
    <xsl:template match="puma:sponsors">
    	<dim:field mdschema="dc" element="description" qualifier="sponsorship">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.title.alternative -->
    <xsl:template match="puma:additionaltitle">
    	<dim:field mdschema="dc" element="title" qualifier="alternative">
    		<xsl:value-of select="."/>
    	</dim:field>
    </xsl:template>

    <!-- dc.classification -->
<!--    <xsl:template match="puma:classification">-->
<!--    	<dim:field mdschema="dc" element="subject" qualifier="{@name}">-->
<!--    		<xsl:value-of select="@value"/>-->
<!--    	</dim:field>-->
<!--    </xsl:template>-->
    
<!--dc.subject.ccs-->
<!--dc.subject.classification-->
<!--dc.subject.ddb-->
<!--dc.subject.ddc-->
<!--dc.subject.jel-->
<!--dc.subject.lcc-->
<!--dc.subject.lcsh-->
<!--dc.subject.mesh-->
<!--dc.subject.msc-->
<!--dc.subject.other-->
<!--dc.subject.pacs-->
<!--dc.subject.swd-->

    <xsl:template match="puma:classification">
    
      <xsl:choose>
  
	    <!-- jel -->
		<xsl:when test="./@name='acm'">
	    	<dim:field mdschema="dc" element="subject" qualifier="{@name}">
	    		<xsl:value-of select="@value"/>
	    	</dim:field>
		</xsl:when>

	    <!-- jel -->
		<xsl:when test="./@name='jel'">
	    	<dim:field mdschema="dc" element="subject" qualifier="{@name}">
	    		<xsl:value-of select="@value"/>
	    	</dim:field>
		</xsl:when>

	    <!-- ddc -->
		<xsl:when test="./@name='ddc'">
	    	<dim:field mdschema="dc" element="subject" qualifier="{@name}">
	    		<xsl:value-of select="@value"/>
	    	</dim:field>
		</xsl:when>

	    <!-- other -->
        <xsl:otherwise>
          <xsl:if test="./@name">
	    	<dim:field mdschema="dc" element="subject" qualifier="other">
	    		<xsl:value-of select="@name"/>
	    		<xsl:text> </xsl:text>
	    		<xsl:value-of select="@value"/>
	    	</dim:field>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>

    </xsl:template>
    
    
    

</xsl:stylesheet>
