<?xml version="1.0" encoding="UTF-8"?>
<!--
	Braille finalizer

	Performs post rendering character replacing. The purpose is to 
	replace remaining non braille characters that are needed in the 
	layout process, such as space, no-break space, hyphen and soft
	hyphen.

	For each row, the braille-finalizer replaces occurrences of characters
	in the finalizer-input string with the character at the corresponding
	position in the finalizer-output string.

	Parameters
		finalizer-input
		finalizer-output

	Format (input -> output)
		PEF -> PEF

	Author: Joel Håkansson, TPB
	Version: 2009-10-26
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pef="http://www.daisy.org/ns/2008/pef" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:param name="finalizer-input" select="''"/> <!-- ' &#x00a0;-&#x00ad;' -->
	<xsl:param name="finalizer-output" select="''"/> <!-- '&#x2800;&#x2800;&#x2824;&#x2824;' -->

	<xsl:output method="xml" media-type="application/x-pef+xml" encoding="utf-8" indent="no"/>
	
	<xsl:template match="/">
		<xsl:message terminate="no">Braille Finalizer has been deprecated.</xsl:message>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="text()[parent::pef:row]">
		<xsl:value-of select="translate(., $finalizer-input, $finalizer-output)"/>
	</xsl:template>

	<xsl:template match="*|comment()|processing-instruction()">
		<xsl:call-template name="copy"/>
	</xsl:template>

	<xsl:template name="copy">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
