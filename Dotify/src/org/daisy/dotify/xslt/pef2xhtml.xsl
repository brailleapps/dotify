<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:dc="http://purl.org/dc/elements/1.1/"
xmlns:pef="http://www.daisy.org/ns/2008/pef"
exclude-result-prefixes="dc pef">
<!-- <xsl:output method="xml" encoding="windows-1252" indent="yes" omit-xml-declaration="yes"/> -->
<xsl:output method="xml" encoding="utf-8" indent="yes" omit-xml-declaration="no"/>

<xsl:template match="pef:pef">
	<html>
		<xsl:apply-templates/>
	</html>
</xsl:template>

<xsl:template match="pef:section">
	<div class="section">
		<xsl:apply-templates/>
	</div>
</xsl:template>

<xsl:template match="pef:volume">
	<div class="volume">
		<xsl:apply-templates/>
	</div>
</xsl:template>

<xsl:template match="pef:label">
	<div class="label"> <!--  style="height: {(ancestor-or-self::*[@rows][1]/@rows) * 30}" -->
		<xsl:apply-templates/>
	</div>
</xsl:template>

<xsl:template match="pef:page">
<!--
	<p>Page (dimension <xsl:value-of select="concat(ancestor::*[@cols][1]/@cols * 2,' x ', ancestor::*[@rows][1]/@rows * (4 + count(ancestor::*[@gap][1][@gap='false'])) + (1 - count(ancestor::*[@gap][1][@gap='false']) * 2) * count(descendant::row[@gap!=ancestor::*/@gap]))"/>):</p>
-->
	<div class="page">	 <!-- style="height: {(ancestor::*[@rows][1]/@rows) * 30}"-->
		<xsl:apply-templates/>
	</div>
</xsl:template>

<xsl:template name="insertBlank">
	<xsl:param name="i">0</xsl:param>
	<xsl:if test="$i&gt;0">&#x2800;<xsl:call-template name="insertBlank">
			<xsl:with-param name="i" select="$i - 1"/>
		</xsl:call-template></xsl:if>
</xsl:template>

<xsl:template match="pef:row">
	<p class="braille">
		<xsl:attribute name="title"><xsl:value-of select="@text"/></xsl:attribute>
		<xsl:variable name="gap"><xsl:choose>
			<xsl:when test="@gap"><xsl:value-of select="@gap"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="ancestor::*[@rowgap][1]/@rowgap"/></xsl:otherwise>
		</xsl:choose></xsl:variable>
		<xsl:if test="$gap&gt;0"><xsl:attribute name="style">margin-bottom:<xsl:value-of select="2 + $gap * 6"/>px;</xsl:attribute></xsl:if>
		<xsl:value-of select="."/>
		<xsl:variable name="strLen" select="string-length(.)"/>
		<xsl:if test="$strLen&lt;ancestor::*[@cols][1]/@cols">
			<xsl:call-template name="insertBlank">
				<xsl:with-param name="i" select="ancestor::*[@cols][1]/@cols - $strLen"/>
			</xsl:call-template>
		</xsl:if>
	</p>
</xsl:template>


<xsl:template match="pef:head">
	<head>
		<xsl:copy-of select="@*"/>
<!--			<meta http-equiv="Content-Type" content="text/xml; charset=x-user-defined" /> -->
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
			<meta http-equiv="Content-Style-Type" content="text/css" />
			<xsl:apply-templates/>
			<style type="text/css">
			p.braille {
				font-family: code2000, courier;
				font-size: 30px;
				margin: 0px;
				margin-bottom: 2px;
				padding: 0px;
			}
			div.page {
				border: solid;
				border-bottom: solid;
				border-width: 1px;
				padding: 5px;
				margin: 5px;
				width:600px;
			}
			div.section {
				border: dashed;
				border-width: 1px;
				padding: 5px;
				margin: 5px;
				border-color: aqua;
			}
			div.volume {
				border: dashed;
				border-width: 1px;
				padding: 5px;
				margin: 5px;
				border-color: red;
			}
			div.label {
				border: solid;
				border-width: 2px;
				padding: 5px;
				margin: 5px;
				border-color: black;
				background-color: silver;
				width:0px;
			}
			</style>
	</head>
</xsl:template>

<xsl:template match="pef:body">
	<body>
		<xsl:copy-of select="@*"/>
		<p><xsl:apply-templates select="//meta/*"/></p>
		<xsl:apply-templates/>
	</body>
</xsl:template>

<xsl:template match="pef:meta"/>

<xsl:template match="dc:*">
	<xsl:value-of select="concat(name(), ': ', .)"/><br />
</xsl:template>

</xsl:stylesheet>
