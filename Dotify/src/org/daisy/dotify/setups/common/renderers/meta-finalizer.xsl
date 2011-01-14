<?xml version="1.0" encoding="UTF-8"?>
<!--
	Meta finalizer
		
	Description
	The meta finalizer inserts meta data from the supplied dtbook (input-uri):
		- dc:Title
		- dc:Creator
		- dc:Language
		- dc:Description
		- dc:Publisher
		- dtb:uid

	The following dc elements are kept from the input PEF-file:
		- dc:format
		- dc:identifier
		- dc:date

	Parameters
		input-uri

	Format (input -> output)
		PEF -> PEF
		
	Author: Joel Håkansson, TPB
	Version: 2009-06-26
 -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pef="http://www.daisy.org/ns/2008/pef" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
exclude-result-prefixes="dtb">
	<xsl:param name="input-uri"/>

	<xsl:output method="xml" media-type="application/x-pef+xml" encoding="utf-8" indent="no"/>
	<xsl:variable name="dtbook" select="document($input-uri)"/>
	
	<xsl:template match="pef:meta">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="dc:format|dc:identifier|dc:date"/>
			<xsl:call-template name="addMetaElement">
				<xsl:with-param name="inName" select="'dc:Title'"/>
				<xsl:with-param name="outName" select="'dc:title'"/>
			</xsl:call-template>
			<xsl:call-template name="addMetaElement">
				<xsl:with-param name="inName" select="'dc:Creator'"/>
				<xsl:with-param name="outName" select="'dc:creator'"/>
			</xsl:call-template>
			<xsl:call-template name="addMetaElement">
				<xsl:with-param name="inName" select="'dc:Language'"/>
				<xsl:with-param name="outName" select="'dc:language'"/>
			</xsl:call-template>
			<xsl:call-template name="addMetaElement">
				<xsl:with-param name="inName" select="'dc:Description'"/>
				<xsl:with-param name="outName" select="'dc:description'"/>
			</xsl:call-template>
			<xsl:call-template name="addMetaElement">
				<xsl:with-param name="inName" select="'dc:Publisher'"/>
				<xsl:with-param name="outName" select="'dc:publisher'"/>
			</xsl:call-template>
			<xsl:call-template name="addMetaElement">
				<xsl:with-param name="inName" select="'dtb:uid'"/>
				<xsl:with-param name="outName" select="'dc:source'"/>
			</xsl:call-template>
			<xsl:apply-templates select="*[not(self::dc:*)]"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template name="addMetaElement">
		<xsl:param name="inName"/>
		<xsl:param name="outName"/>
		<xsl:for-each select="$dtbook/dtb:dtbook/dtb:head/dtb:meta[@name=$inName]">
			<xsl:element name="{$outName}" namespace="http://purl.org/dc/elements/1.1/">
				<xsl:value-of select="@content"/>
			</xsl:element>
		</xsl:for-each>
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
