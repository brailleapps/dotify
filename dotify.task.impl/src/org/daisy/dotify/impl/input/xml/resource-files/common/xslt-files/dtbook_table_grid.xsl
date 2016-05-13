<?xml version="1.0" encoding="utf-8"?>
<!--
	Supports splitting tables with the tag names table, tr, th and td 
	and the attributes colspan and rowspan. The assumption is that
	the table follows html table sematics for these elements.
	
	The main entry points are:
	
	<xsl:template match="*:table" mode="splitTable">
	<xsl:template match="*:table" mode="makeGrid">

	The names of the attributes (colspan and rowspan) can be configured via
	the parameters rowspanName and colspanName.
	
	Tables in the html, dtbook and obfl namespaces have been tested.
-->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:dotify="http://brailleapps.github.io/ns/dotify"
	xmlns:aobfl="http://www.daisy.org/ns/2011/obfl"
	xmlns:tmp="http://brailleapps.github.io/ns/dotify/result"
	exclude-result-prefixes="xs dotify">

	<xsl:output method="xml" encoding="utf-8" indent="no"/>
	<xsl:param name="debug" select="false()" as="xs:boolean"/>
	<!-- Override these params for obfl tables -->
	<xsl:param name="rowspanName" select="'rowspan'" as="xs:string"/>
	<xsl:param name="colspanName" select="'colspan'" as="xs:string"/>
	
	<!-- 
		Splits a table into several parts, depending on the number of columns.
		It is safe to use for smaller tables (which will be left untouched). 
	-->
	<xsl:template match="*:table" mode="splitTable">
		<xsl:param name="maxColumns" select="10" as="xs:integer"/>
		<xsl:variable name="grid">
			<xsl:apply-templates select="." mode="makeGrid"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$grid/tmp:summary/@grid-width>$maxColumns">
				<xsl:variable name="sections" select="ceiling($grid/tmp:summary/@grid-width div $maxColumns)"/>
				<xsl:variable name="size" select="ceiling($grid/tmp:summary/@grid-width div $sections)"/>
				<xsl:apply-templates select="." mode="tableSplitIterator">
					<xsl:with-param name="grid" select="$grid"/>
					<xsl:with-param name="start" select="1"/>
					<xsl:with-param name="size" select="$size"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise><xsl:copy-of select="."/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="*:table" mode="tableSplitIterator">
		<xsl:param name="grid" required="yes"/>
		<xsl:param name="start" select="1"/>
		<xsl:param name="size" select="1"/>
		<xsl:variable name="end" select="$start+$size"/>
		<xsl:if test="$start&lt;=$grid/tmp:summary/@grid-width">
			<xsl:variable name="result">
				<xsl:apply-templates select="." mode="makeSplitTable">
					<xsl:with-param name="grid" select="$grid"/>
					<xsl:with-param name="start" select="$start"/>
					<xsl:with-param name="end" select="$end"/>
				</xsl:apply-templates>
			</xsl:variable>
			<xsl:variable name="actualSize" select="if ($end>$grid/tmp:summary/@grid-width) then $grid/tmp:summary/@grid-width - $start else $size"/>
			<xsl:apply-templates select="$result" mode="cleanTable">
				<xsl:with-param name="grid-width" select="$actualSize"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="." mode="tableSplitIterator">
				<xsl:with-param name="grid" select="$grid"/>
				<xsl:with-param name="start" select="$end"/>
				<xsl:with-param name="size" select="$size"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*[self::*:td or self::*:th]" mode="makeSplitTable">
		<xsl:param name="grid" required="yes"/>
		<xsl:param name="start" required="yes"/>
		<xsl:param name="end" required="yes"/>
		<xsl:variable name="td-id" select="generate-id(.)"/>
		<xsl:if test="$grid/tmp:cell[@id=$td-id and @col>=$start and @col&lt;$end]">
			<xsl:variable name="cellStart" select="$grid/tmp:cell[@id=$td-id and @col=$start][1]"/> <!-- [1] if rowspan > 1 -->
			<xsl:variable name="cellEnd" select="$grid/tmp:cell[@id=$td-id and @col=$end - 1][1]"/> <!-- [1] if rowspan > 1 -->
			<xsl:copy>
				<xsl:copy-of select="@*[not(name()=$colspanName)]"/>
				<xsl:variable name="colspan">
					<xsl:choose>
						<xsl:when test="$cellStart and $cellEnd">
							<xsl:value-of select="$end - $start"/>
						</xsl:when>
						<xsl:when test="$cellStart">
							<xsl:value-of select="(if (@*[name()=$colspanName]) then @*[name()=$colspanName] else 1) - $cellStart/@col-offset"/>
						</xsl:when>
						<xsl:when test="$cellEnd">
							<xsl:value-of select="$cellEnd/@col-offset+1"/>
						</xsl:when>
						<xsl:when test="@*[name()=$colspanName]">
							<xsl:value-of select="@*[name()=$colspanName]"/>
						</xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:if test="@*[name()=$colspanName] or $colspan>1"> 
					<xsl:attribute name="{$colspanName}" select="$colspan"/>
				</xsl:if>
				<xsl:apply-templates select="node()" mode="makeSplitTable">
					<xsl:with-param name="grid" select="$grid"/>
					<xsl:with-param name="start" select="$start"/>
					<xsl:with-param name="end" select="$end"/>
				</xsl:apply-templates>
			</xsl:copy>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*|processing-instruction()|comment()" mode="makeSplitTable">
		<xsl:param name="grid" required="yes"/>
		<xsl:param name="start" required="yes"/>
		<xsl:param name="end" required="yes"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node()" mode="makeSplitTable">
				<xsl:with-param name="grid" select="$grid"/>
				<xsl:with-param name="start" select="$start"/>
				<xsl:with-param name="end" select="$end"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	
	<!-- Add dummy cells in rows that have been emptied in splitting. -->
	<xsl:template match="*:tr[not(descendant::node()[not(self::text() and normalize-space(.)='')])]" mode="cleanTable">
		<xsl:param name="grid-width" select="1"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="ancestor::*:table/descendant::*[self::*:td or self::*:th][1]" mode="insertEmptyCell">
				<xsl:with-param name="it" select="$grid-width"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="*:td | *:th" mode="insertEmptyCell">
		<xsl:param name="it" required="yes"/>
		<xsl:copy> 
			<aobfl:style name="table-cell-continued"/>
		</xsl:copy>
		<xsl:if test="$it>1">
			<xsl:apply-templates select="." mode="insertEmptyCell">
				<xsl:with-param name="it" select="$it - 1"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<!-- Reduce rowspan for rows with cells that all have a minimum rowspan > 1 to account for empty cells added above -->
	<xsl:template match="*:tr[min(descendant::*[self::*:td or self::*:th]/(if (@*[name()=$rowspanName]) then @*[name()=$rowspanName] else 1))>1]" mode="cleanTable">
		<xsl:param name="grid-width"/>
		<xsl:variable name="minRowSpan" select="min(descendant::*[self::*:td or self::*:th]/(if (@*[name()=$rowspanName]) then @*[name()=$rowspanName] else 1))"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates mode="cleanTable">
				<xsl:with-param name="reduceRowSpan" select="$minRowSpan - 1"/>
				<xsl:with-param name="grid-width" select="$grid-width"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="*:td|*:th" mode="cleanTable">
		<xsl:param name="grid-width"/>
		<xsl:param name="reduceRowSpan" select="0"/>
		<xsl:copy>
			<xsl:copy-of select="@*[not(name()=$rowspanName)]"/>
			<xsl:if test="@*[name()=$rowspanName]">
				<xsl:attribute name="{$rowspanName}" select="@*[name()=$rowspanName]-$reduceRowSpan"/>
			</xsl:if>
			<xsl:apply-templates mode="cleanTable">
				<xsl:with-param name="grid-width" select="$grid-width"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="*|processing-instruction()|comment()" mode="cleanTable">
		<xsl:param name="reduceRowSpan" select="0"/>
		<xsl:param name="grid-width"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates mode="cleanTable">
				<xsl:with-param name="reduceRowSpan" select="$reduceRowSpan"/>
				<xsl:with-param name="grid-width" select="$grid-width"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>

	<!-- 
		This returns a grid view of the table where each coordinate has an element.
	-->
	<xsl:template match="*:table" mode="makeGrid">
		<!-- Don't allow tables where a row's cells have a uniform rowspan > 1 -->
		<!-- FIXME: This doesn't work, beccause it may give false positives 
		<xsl:for-each select="descendant::dtb:tr">
			<xsl:variable name="maxRowSpan">
				<xsl:choose>
					<xsl:when test="dtb:td/@rowspan"><xsl:value-of select="max(dtb:td/@rowspan)"/></xsl:when>
					<xsl:otherwise>1</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:if test="$maxRowSpan>1 and count(dtb:td)=count(dtb:td[@rowspan=$maxRowSpan])">
				<xsl:message terminate="yes">Table has a row with uniform rowspan > 1</xsl:message>
			</xsl:if>
		</xsl:for-each>-->
		<xsl:variable name="result">
			<xsl:apply-templates select="descendant::*[self::*:td or self::*:th][1]" mode="gridBuilderStart">
				<xsl:with-param name="table-id" select="generate-id()"/>
			</xsl:apply-templates>
		</xsl:variable>
		<!-- TODO: Check that count (@row-@col distinct values) = count(*) -->
		<xsl:copy-of select="$result"/>
		<tmp:summary>
			<xsl:attribute name="grid-width" select="dotify:calculateGridWidth($result)"/>
			<xsl:copy-of select="dotify:getValidSplitPoints($result)"/>
		</tmp:summary>
	</xsl:template>
	
	<!-- Optimized version using tail recursion -->
	<xsl:template match="*[self::*:td or self::*:th]" mode="gridBuilderStart">
		<xsl:param name="table-id" required="yes"/>
		<xsl:param name="gridInput" select="/.."/> <!-- Defaults to an empty node -->
		<xsl:variable name="gy" select="ancestor::*:tr/count(preceding::*:tr[ancestor::*:table[generate-id()=$table-id]])+1"/>
		<xsl:variable name="gx">
			<xsl:call-template name="findGridX">
				<xsl:with-param name="grid" select="$gridInput"/>
				<xsl:with-param name="gy" select="$gy"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="result">
			<xsl:apply-templates select="." mode="gridBuilderInner">
				<xsl:with-param name="gx" select="$gx"/>
				<xsl:with-param name="gy" select="$gy"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:variable name="grid">
			<xsl:copy-of select="$gridInput"/>
			<xsl:copy-of select="$result"/>
		</xsl:variable>
		<xsl:copy-of select="$result"/>
		<xsl:apply-templates select="following::*[self::*:td or self::*:th][1][ancestor::*:table[generate-id()=$table-id]]" mode="gridBuilderStart">
			<xsl:with-param name="table-id" select="$table-id"/>
			<xsl:with-param name="gridInput" select="$grid"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="*[self::*:td or self::*:th]" mode="gridBuilderInner">
		<xsl:param name="i" select="1"/> <!-- current_column + (current_row-1) * column_count -->
		<xsl:param name="gx" select="1"/>
		<xsl:param name="gy" select="1"/>
		<xsl:variable name="rowspan">
			<xsl:choose><xsl:when test="@*[name()=$rowspanName]"><xsl:value-of select="@*[name()=$rowspanName]"/></xsl:when><xsl:otherwise>1</xsl:otherwise></xsl:choose>
		</xsl:variable>
		<xsl:variable name="colspan">
			<xsl:choose><xsl:when test="@*[name()=$colspanName]"><xsl:value-of select="@*[name()=$colspanName]"/></xsl:when><xsl:otherwise>1</xsl:otherwise></xsl:choose>
		</xsl:variable>
		<xsl:if test="$i&lt;=$rowspan*$colspan">
			<!-- unpack i -->
			<xsl:variable name="cr" select="floor(($i - 1) div $colspan)"/>
			<xsl:variable name="cc" select="(($i - 1) mod $colspan)"/>
			<tmp:cell row="{$gy+$cr}" col="{$gx+$cc}" row-offset="{$cr}" col-offset="{$cc}" id="{generate-id()}" rowspan="{$rowspan}" colspan="{$colspan}" heading="{count(self::*:th)=1}">
				<xsl:if test="$debug">
					<xsl:attribute name="text" select="text()"/>
				</xsl:if>
			</tmp:cell> 
			<xsl:apply-templates select="." mode="gridBuilderInner">
				<xsl:with-param name="i" select="$i+1"/>
				<xsl:with-param name="gx" select="$gx"/>
				<xsl:with-param name="gy" select="$gy"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<xsl:template name="findGridX">
		<xsl:param name="gx" select="1"/>
		<xsl:param name="gy" select="1"/>
		<xsl:param name="grid"/>
		<xsl:choose>
			<xsl:when test="$grid/tmp:cell[@row=$gy and @col=$gx]">
				<xsl:call-template name="findGridX">
					<xsl:with-param name="gx" select="$gx+1"/>
					<xsl:with-param name="gy" select="$gy"/>
					<xsl:with-param name="grid" select="$grid"/> 
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="$gx"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Returns true if a column is a valid split point -->
	<xsl:function name="dotify:isValidSplitPoint">
		<xsl:param name="grid"/>
		<xsl:param name="split"/>
		<xsl:choose>
			<xsl:when test="count($grid/tmp:cell[@col=$split])=count($grid/tmp:cell[@col=$split and @col-offset=0])">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<!-- Function wrapper for getValidSplitPoints template -->
	<xsl:function name="dotify:getValidSplitPoints">
		<xsl:param name="grid"/>
		<xsl:call-template name="getValidSplitPoints">
			<xsl:with-param name="grid" select="$grid"/>
		</xsl:call-template>
	</xsl:function>
	
	<!-- Returns all valid split points-->
	<xsl:template name="getValidSplitPoints">
		<xsl:param name="grid" required="yes"/>
		<xsl:param name="split" select="1"/>
		<xsl:choose>
			<xsl:when test="count($grid/tmp:cell[@col=$split])=count($grid/tmp:cell[@col=$split and @col-offset=0])"><tmp:split-point index="{$split}"/></xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:if test="count($grid/tmp:cell[@col=$split+1])>0">
			<xsl:call-template name="getValidSplitPoints">
				<xsl:with-param name="grid" select="$grid"/>
				<xsl:with-param name="split" select="$split+1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- Returns the grid width -->
	<xsl:function name="dotify:calculateGridWidth">
		<xsl:param name="grid"/>
		<xsl:value-of select="count($grid/tmp:cell[@row=1])"/>
	</xsl:function>
	
	<!-- Returns a print out of the grid information -->
	<xsl:function name="dotify:gridAsString">
		<xsl:param name="grid"/>
		<xsl:for-each select="$grid/tmp:cell"><xsl:for-each select="@*"><xsl:value-of select="name()"/>=<xsl:value-of select="."/>
				<xsl:text>&#x09;</xsl:text>
			</xsl:for-each>
			<xsl:text>&#x0a;</xsl:text>
		</xsl:for-each>
	</xsl:function>

</xsl:stylesheet>