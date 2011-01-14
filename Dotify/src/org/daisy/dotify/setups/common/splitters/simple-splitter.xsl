<?xml version="1.0" encoding="UTF-8"?>
<!--
	Simple splitter

	Splits a PEF file with a single volume into several volumes.
	The splitter will distribute pages into volumes equally with a
	maximum of "splitterMax" pages per volume.

	Note:	Simple splitter does not preserve or respect the duplex attribute
			anywhere in the input file. The duplex attribute will be overwritten 
			using the value of the duplex parameter supplied when calling the xslt.

	Future improvements: Support existing duplex attributes in input.

	Parameters
		splitterMax

	Format (input -> output)
		PEF -> PEF

	Author: Joel Håkansson, TPB
	Version: 2010-02-02
 -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pef="http://www.daisy.org/ns/2008/pef" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="xml" media-type="application/x-pef+xml" encoding="utf-8" indent="no"/>

	<!-- min: deprecated -->
	<xsl:param name="min" select="-1" as="xs:integer"/> 
	<!-- target: deprecated -->
	<xsl:param name="target" select="-1" as="xs:integer"/>
	
	<!-- max: the maximum number of sheets in a volume -->
	<xsl:param name="splitterMax" select="49" as="xs:integer"/>
	<!-- duplex: two pages per sheet -->
	<xsl:param name="duplex" select="true()" as="xs:boolean"/>
	<!-- Test implementation on a set of book sizes -->
	<xsl:param name="debug" select="false()" as="xs:boolean"/>

	<xsl:variable name="pages" select="if ($duplex) then 
				count(descendant::pef:page)+count(descendant::pef:section[(count(pef:page) mod 2)=1])
				else count(descendant::pef:page)"/>

	<!-- Duplex multiplier -->
	<xsl:variable name="k" select="if ($duplex) then 2 else 1"/>

	<!-- Breakpoint -->
	<xsl:variable name="breakpoint">
		<xsl:call-template name="calcBreakPoint">
			<xsl:with-param name="pages" select="$pages" as="xs:integer"/>
		</xsl:call-template>
	</xsl:variable>
	
	<!--	The number of volumes that should contain breakpoint pages. If this
			value is smaller than the total number of volumes than the following 
			volumes will contain breakpoint - 1 pages. -->
	<xsl:variable name="volsWithBP">
		<xsl:call-template name="calcVolsWithBP">
			<xsl:with-param name="pages" select="$pages" as="xs:integer"/>
		</xsl:call-template>
	</xsl:variable>

	<!-- Calculate the breakpoint in pages -->
	<xsl:template name="calcBreakPoint">
		<xsl:param name="pages" select="0"/>
		
		<xsl:variable name="sheets" select="ceiling($pages div $k)"/>
		<xsl:variable name="volumes" select="ceiling($sheets div $splitterMax)"/>
		<xsl:variable name="spv" select="ceiling($sheets div $volumes)"/> <!-- Sheets per volume -->
		<xsl:variable name="breakpoint" select="$spv * $k"/> <!-- Breakpoint in pages -->

		<!-- Return breakpoint in pages -->
		<xsl:value-of select="$breakpoint"/>
	</xsl:template>

	<!--	The number of volumes that should contain max sheets is returned. -->
	<xsl:template name="calcVolsWithBP">
		<xsl:param name="pages" select="0"/>
		
		<xsl:variable name="sheets" select="ceiling($pages div $k)"/>
		<xsl:variable name="volumes" select="ceiling($sheets div $splitterMax)"/>
		<xsl:variable name="spv" select="ceiling($sheets div $volumes)"/> <!-- Sheets per volume -->
		<!-- Sheets in last volume, assuming the same breakpoint is used throughout -->
		<xsl:variable name="slv" select="$sheets - ($spv * ($volumes - 1))"/> 

		<xsl:value-of select="$volumes - ($spv - $slv)"/>
	</xsl:template>
	
	<!--	Tests if the supplied pageIndex is a breakpoint.
			Returns true if it is a breakpoint, false otherwise. -->
	<xsl:template name="breakIndex">
		<xsl:param name="pageIndex" select="-1"/>
		<!-- Optional, used in debugging -->
		<xsl:param name="breakpoint" select="$breakpoint"/> <!-- 'Normal' breakpoint -->
		<xsl:param name="volsWithBP" select="$volsWithBP"/> <!-- Number of volumes to use max value on -->
		
		<xsl:if test="$pageIndex&lt;0">
			<xsl:message terminate="yes">Missing required parameter: "pageIndex"</xsl:message>
		</xsl:if>

		<xsl:variable name="countBPpages" select="$breakpoint * $volsWithBP"/>
		<xsl:choose>
			<!-- Have we done enough volumes using max? -->
			<xsl:when test="$pageIndex &lt; $countBPpages">
				<xsl:value-of select="($pageIndex mod $breakpoint) = 0"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- Offset the break calculation for the full volumes and use breakpoint - $k for the rest -->
				<xsl:value-of select="(($pageIndex - $countBPpages) mod ($breakpoint - $k)) = 0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Main debugging template. -->
	<xsl:template name="debug">
		<xsl:param name="n" select="100"/>
		<xsl:param name="end" select="200"/>
		<xsl:choose>
			<xsl:when test="$n>=$end"></xsl:when>
			<xsl:otherwise>
				<xsl:variable name="breakpoint">
					<xsl:call-template name="calcBreakPoint">
						<xsl:with-param name="pages" select="$n"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="fullVolsNeeded">
					<xsl:call-template name="calcVolsWithBP">
						<xsl:with-param name="pages" select="$n"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="breaks">
					<xsl:call-template name="volBreaks">
						<xsl:with-param name="bp" select="$breakpoint"/>
						<xsl:with-param name="fullVols" select="$fullVolsNeeded"/>
						<xsl:with-param name="end" select="$n"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="vols" select="ceiling(ceiling($n div $k) div $splitterMax)"/>
				<!-- Pages, Breakpoint, Volumes total, Full volumes, Pages in last volume, All breakpoints -->
				<xsl:value-of select="concat($n, ' ', $breakpoint, ' ', $vols, ' ', $fullVolsNeeded, ' ', $n - number(tokenize($breaks,' ')[last()]), $breaks)"/>
				<xsl:text>
</xsl:text>
				<xsl:call-template name="debug">
					<xsl:with-param name="n" select="$n+1"/>
					<xsl:with-param name="end" select="$end"/>
				</xsl:call-template>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>

	<!--	This template is used when debugging. -->
	<xsl:template name="volBreaks">
		<xsl:param name="bp" select="100"/>
		<xsl:param name="fullVols" select="0"/>
		<xsl:param name="end" select="300"/>
		<!-- Counter used in template recursion -->
		<xsl:param name="page" select="1"/>
		<xsl:choose>
			<xsl:when test="$page>=$end"></xsl:when>
			<xsl:otherwise>
				<xsl:variable name="break" as="xs:boolean">
					<xsl:call-template name="breakIndex">
						<xsl:with-param name="pageIndex" select="$page"/>
						<xsl:with-param name="breakpoint" select="$bp"/>
						<xsl:with-param name="volsWithBP" select="$fullVols"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:if test="$break"><xsl:value-of select="concat(' ', $page)"/></xsl:if>
				<xsl:call-template name="volBreaks">
					<xsl:with-param name="bp" select="$bp"/>
					<xsl:with-param name="page" select="$page+1"/>
					<xsl:with-param name="end" select="$end"/>
					<xsl:with-param name="fullVols" select="$fullVols"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="/">
		<!-- Check input file -->
		<xsl:if test="count(//pef:volume)>1">
			<xsl:message terminate="yes">Input file problem: The file is already broken into several volumes.</xsl:message>
		</xsl:if>
		<!-- Check input parameters -->
		<xsl:if test="$min>-1">
			<xsl:message terminate="yes">Configuration problem: Parameter "min" has been deprecated.</xsl:message>
		</xsl:if>
		<xsl:if test="$target>-1">
			<xsl:message terminate="yes">Configuration problem: Parameter "target" has been deprecated.</xsl:message>
		</xsl:if>
		<xsl:if test="2>$splitterMax">
			<xsl:message terminate="yes">Configuration problem: Parameter "max" must be larger than 1.</xsl:message>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="$debug=true()">
				<xsl:call-template name="debug">
					<xsl:with-param name="n" select="200"/>
					<xsl:with-param name="end" select="500"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pef:volume">
		<xsl:copy>
			<xsl:copy-of select="@*[not(name()='duplex')]"/>
			<xsl:attribute name="duplex" select="$duplex"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="pef:section">
		<xsl:copy>
			<xsl:copy-of select="@*[not(name()='duplex')]"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="pef:page">
		<xsl:variable name="pageIndex" select="if ($duplex) then 
				count(preceding::pef:page) + count(preceding::pef:section[(count(pef:page) mod 2)=1])
				else count(preceding::pef:page)
				"/>
		<xsl:comment>Page: <xsl:value-of select="$pageIndex"/></xsl:comment>
		<xsl:variable name="breakHere" as="xs:boolean">
			<xsl:call-template name="breakIndex">
				<xsl:with-param name="pageIndex" select="$pageIndex"/>
				<!-- Use global values for breakpoint and volsWithBP -->
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$breakHere">
			<xsl:if test="preceding::pef:page">
				<xsl:text disable-output-escaping="yes">&lt;/section>&lt;/volume>&lt;volume</xsl:text>
				<xsl:for-each select="ancestor::pef:volume/attribute()[not(name()='duplex')]">
					<xsl:value-of select="concat(' ', name(),'=&quot;', ., '&quot;')"/>
				</xsl:for-each>
				<xsl:value-of select="concat(' duplex=&quot;', $duplex, '&quot;')"/>
				<xsl:text disable-output-escaping="yes">>&lt;section</xsl:text>
				<xsl:for-each select="parent::pef:section/attribute()[not(name()='duplex')]">
					<xsl:value-of select="concat(' ', name(),'=&quot;', ., '&quot;')"/>
				</xsl:for-each>
				<xsl:text disable-output-escaping="yes">></xsl:text>
			</xsl:if>
		</xsl:if>
		<xsl:call-template name="copy"/>
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