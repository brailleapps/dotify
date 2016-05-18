<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xs obfl"
	xmlns:obfl="http://www.daisy.org/ns/2011/obfl"
	xmlns="http://www.daisy.org/ns/2011/obfl">

	<xsl:output method="xml" encoding="utf-8" indent="no"/>
	<xsl:param name="page-width" select="10" as="xs:integer"/>
	<xsl:param name="page-height" select="10" as="xs:integer"/>
	<xsl:param name="inner-margin" select="0" as="xs:integer"/>
	<xsl:param name="outer-margin" select="0" as="xs:integer"/>
	<xsl:param name="row-spacing" select="1" as="xs:decimal"/>
	<xsl:param name="duplex" select="true()" as="xs:boolean"/>
	
	<xsl:param name="splitterMax" select="10"/>
	<xsl:param name="volume-toc" as="xs:boolean" select="true()"/>
	<xsl:param name="show-braille-page-numbers" as="xs:boolean" select="true()"/>
	<xsl:param name="show-print-page-numbers" as="xs:boolean" select="true()"/>
	<xsl:param name="show-print-page-breaks" as="xs:boolean" select="false()"/>
	<xsl:param name="matrix-table-columns-max" select="10"/>
	<xsl:param name="staircase-table-columns-max" select="10"/>

	<xsl:param name="l10nLang" select="'en'"/>
	<xsl:param name="l10nTocHeadline" select="'Table Of Contents'"/>
	<xsl:param name="l10nTocDescription" select="''"/>
	<xsl:param name="l10nTocVolumeStart" select="'Volume {0}'"/>
	<xsl:param name="l10nTocVolumeHeading" select="'Contents of Volume {0}'"/>
	<xsl:param name="l10nTocVolumeXofY" select="'Volume {0} of {1}'"/>
	<xsl:param name="l10nTocOneVolume" select="'One Volume'"/>
	<xsl:param name="l10nEndnotesHeading" select="'Footnotes'"/>
	<xsl:param name="l10nEndnotesPageStart" select="'Page {0}'"/>
	<xsl:param name="l10nEndnotesPageHeader" select="'Footnotes'"/>
	<xsl:param name="l10ntablepart" select="'Table part'"/>
		
	<xsl:function name="obfl:insertLayoutMaster">
		<xsl:param name="footnotesInFrontMatter" as="xs:integer"/> 
		<!-- count(//dtb:note[key('noterefs', @id)[ancestor::dtb:frontmatter]]) -->
		<!-- count(//dtb:note[key('noterefs', @id)[ancestor::dtb:frontmatter]]) -->
		<xsl:param name="footnotesNotInFrontMatter" as="xs:integer"/>
		<!-- count(//dtb:note[key('noterefs', @id)[not(ancestor::dtb:frontmatter)]]) -->
		<!-- count(//dtb:note[key('noterefs', @id)[not(ancestor::dtb:frontmatter)]]) -->
		<layout-master name="front" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<template use-when="(= (% $page 2) 0)">
				<xsl:if test="$row-spacing=2 and not($show-braille-page-numbers)">
					<header>
						<xsl:attribute name="row-spacing">1</xsl:attribute>
						<field><string value=""/></field>
					</header>
				</xsl:if>
				<header>
					<xsl:if test="$show-braille-page-numbers">
						<field><string value="&#xA0;&#xA0;"/><current-page number-format="roman"/></field>
					</xsl:if>
				</header>
				<footer></footer>
				<xsl:call-template name="margin-region"/>
			</template>
			<default-template>
				<header>
					<xsl:if test="$show-braille-page-numbers">
						<!-- This looks weird, but it is correct. If row-spacing is double, then offset the header
						 of every front page as to avoid embossing on the same row on front and back -->
						<xsl:if test="$row-spacing=2">
							<xsl:attribute name="row-spacing">1</xsl:attribute>
						</xsl:if>
						<field><string value=""/></field>
						<field><current-page number-format="roman"/></field>
					</xsl:if>
				</header>
				<footer></footer>
				<xsl:call-template name="margin-region"/>
			</default-template>
			<xsl:if test="$footnotesInFrontMatter>0">
				<page-area align="bottom" max-height="10" collection="footnotes-front">
					<fallback>
						<rename collection="footnotes-front" to="endnotes-front"/>
						<xsl:if test="$footnotesNotInFrontMatter>0">
							<rename collection="footnotes" to="endnotesB"/>
						</xsl:if>
					</fallback>
					<before><leader position="100%" pattern="."/></before>
					<after></after>
				</page-area>
			</xsl:if>
		</layout-master>
		<layout-master name="main" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<template use-when="(= (% $page 2) 0)">
				<xsl:if test="$row-spacing=2 and not($show-braille-page-numbers or $show-print-page-numbers)">
					<header>
						<xsl:attribute name="row-spacing">1</xsl:attribute>
						<field><string value=""/></field>
					</header>
				</xsl:if>
				<header>
					<xsl:if test="$show-braille-page-numbers or $show-print-page-numbers">
						<field><string value="&#xA0;&#xA0;"/>
							<xsl:if test="$show-braille-page-numbers">
								<current-page number-format="default"/>
							</xsl:if>
						</field>
						<field>
							<xsl:choose>
								<xsl:when test="$show-print-page-numbers">
									<marker-reference marker="pagenum-turn" direction="forward" scope="page-content"/>
									<marker-reference marker="pagenum" direction="backward" scope="sequence"/>
								</xsl:when>
								<xsl:otherwise>
									<string value=""/>
								</xsl:otherwise>
							</xsl:choose>
						</field>
					</xsl:if>
				</header>
				<footer></footer>
				<xsl:call-template name="margin-region"/>
			</template>
			<default-template>
				<header>
					<xsl:if test="$show-braille-page-numbers or $show-print-page-numbers">
						<!-- This looks weird, but it is correct. If row-spacing is double, then offset the header
						 of every front page as to avoid embossing on the same row on front and back -->
						<xsl:if test="$row-spacing=2">
							<xsl:attribute name="row-spacing">1</xsl:attribute>
						</xsl:if>
						<field><string value="&#xA0;&#xA0;"/>
							<xsl:if test="$show-print-page-numbers">
								<marker-reference marker="pagenum-turn" direction="forward" scope="page-content"/>
								<marker-reference marker="pagenum" direction="backward" scope="sequence"/>
							</xsl:if>
						</field>
						<field>
							<xsl:choose>
								<xsl:when test="$show-braille-page-numbers"><current-page number-format="default"/></xsl:when>
								<xsl:otherwise><string value=""/></xsl:otherwise>
							</xsl:choose>
						</field>
					</xsl:if>
				</header>
				<footer></footer>
				<xsl:call-template name="margin-region"/>
			</default-template>
			<xsl:if test="$footnotesNotInFrontMatter>0">
				<page-area align="bottom" max-height="10" collection="footnotes">
					<fallback>
						<rename collection="footnotes" to="endnotes"/>
						<xsl:if test="$footnotesInFrontMatter>0">
							<rename collection="footnotes-front" to="endnotes-frontB"/>
						</xsl:if>
					</fallback>
					<before><leader position="100%" pattern="."/></before>
					<after></after>
				</page-area>
			</xsl:if>
		</layout-master>
		<layout-master name="plain" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<template use-when="(= (% $page 2) 0)">
				<header><field><string value=""/></field></header>
				<footer></footer>
				<xsl:call-template name="margin-region"/>
			</template>
			<default-template>
				<header>
					<!-- This looks weird, but it is correct. If row-spacing is double, then offset the header
					 of every front page as to avoid embossing on the same row on front and back -->
					<xsl:if test="$row-spacing=2">
						<xsl:attribute name="row-spacing">1</xsl:attribute>
					</xsl:if>
					<field><string value=""/></field>
				</header>
				<footer></footer>
				<xsl:call-template name="margin-region"/>
			</default-template>
		</layout-master>
		<layout-master name="notes" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<template use-when="(= (% $page 2) 0)">
				<xsl:if test="$row-spacing=2 and not($show-braille-page-numbers)">
					<header>
						<xsl:attribute name="row-spacing">1</xsl:attribute>
						<field><string value=""/></field>
					</header>
				</xsl:if>
				<header>
					<xsl:if test="$show-braille-page-numbers">
						<field><string value="&#xA0;&#xA0;"/><string value="{$l10nEndnotesPageHeader} "/><current-page number-format="default"/></field>
					</xsl:if>
				</header>
				<footer></footer>
				<xsl:call-template name="margin-region"/>
			</template>
			<default-template>
				<header>
					<xsl:if test="$show-braille-page-numbers">
						<!-- This looks weird, but it is correct. If row-spacing is double, then offset the header
						 of every front page as to avoid embossing on the same row on front and back -->
						<xsl:if test="$row-spacing=2">
							<xsl:attribute name="row-spacing">1</xsl:attribute>
						</xsl:if>
						<field><string value=""/></field>
						<field><string value="{$l10nEndnotesPageHeader} "/><current-page number-format="default"/></field>
					</xsl:if>
				</header>
				<footer></footer>
				<xsl:call-template name="margin-region"/>
			</default-template>
		</layout-master>
		<layout-master name="cover" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="1" duplex="{$duplex}" border-style="solid" border-width="1" border-align="outer">
			<default-template>
				<header></header>
				<footer></footer>
			</default-template>
		</layout-master>
	</xsl:function>
	
	<xsl:template name="margin-region">
		<xsl:if test="$show-print-page-breaks">
			<margin-region align="left" width="1">
				<indicators>
					<marker-indicator markers="pagenum" indicator="&#x2022;"/>
				</indicators>
			</margin-region>
		</xsl:if>
	</xsl:template>
	
	<xsl:function name="obfl:insertVolumeTemplate">
		<xsl:param name="title"/>
		<xsl:param name="authors"/>
		<xsl:param name="footnotesInFrontMatter" as="xs:integer"/> 
		<xsl:param name="footnotesNotInFrontMatter" as="xs:integer"/>
		<xsl:param name="insertToc" as="xs:boolean"/>
		<xsl:param name="additionalPreContent"/>
		<xsl:choose>
			<xsl:when test="$insertToc">
				<volume-template volume-number-variable="volume" volume-count-variable="volumes" use-when="(= $volume 1)" sheets-in-volume-max="{$splitterMax}">
					<pre-content>
						<xsl:copy-of select="obfl:insertCoverPage($title, $authors)"/>
						<toc-sequence master="front" toc="full-toc" range="document" initial-page-number="1">
							<on-toc-start>
								<block padding-bottom="1"><xsl:value-of select="$l10nTocHeadline"/></block>
								<xsl:if test="$l10nTocDescription!=''">
									<block padding-bottom="1"><xsl:value-of select="$l10nTocDescription"/></block>
								</xsl:if>
							</on-toc-start>
							<on-volume-start use-when="(&amp; (> $volumes 1) (= $started-volume-number 1))">
								<block keep="all" keep-with-next="1" padding-bottom="0"><evaluate expression="(format &quot;{$l10nTocVolumeStart}&quot; $started-volume-number)"/></block>
							</on-volume-start>
							<on-volume-start use-when="(&amp; (> $volumes 1) (> $started-volume-number 1))">
								<block keep="all" keep-with-next="1" padding-top="1" padding-bottom="0"><evaluate expression="(format &quot;{$l10nTocVolumeStart}&quot; $started-volume-number)"/></block>
							</on-volume-start>
						</toc-sequence>
						<xsl:copy-of select="$additionalPreContent"/>
					</pre-content>
					<post-content>
						<xsl:copy-of select="obfl:insertPostContentNotes($footnotesInFrontMatter, $footnotesNotInFrontMatter)"/>
					</post-content>
				</volume-template>
				<volume-template volume-number-variable="volume" volume-count-variable="volumes" use-when="(> $volume 1)" sheets-in-volume-max="{$splitterMax}">
					<pre-content>
						<xsl:copy-of select="obfl:insertCoverPage($title, $authors)"/>
						<xsl:if test="$volume-toc">
							<toc-sequence master="front" toc="full-toc" range="volume" initial-page-number="1">
								<on-toc-start>
									<block padding-bottom="1"><evaluate expression="(format &quot;{$l10nTocVolumeHeading}&quot; $volume)"/></block>
								</on-toc-start>
							</toc-sequence>
						</xsl:if>
					</pre-content>
					<post-content>
						<xsl:copy-of select="obfl:insertPostContentNotes($footnotesInFrontMatter, $footnotesNotInFrontMatter)"/>
					</post-content>
				</volume-template>
			</xsl:when>
			<xsl:otherwise>
				<volume-template sheets-in-volume-max="{$splitterMax}">
					<pre-content>
						<xsl:copy-of select="obfl:insertCoverPage($title, $authors)"/>
					</pre-content>
					<post-content>
						<xsl:copy-of select="obfl:insertPostContentNotes($footnotesInFrontMatter, $footnotesNotInFrontMatter)"/>
					</post-content>
				</volume-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="obfl:insertPostContentNotes">
		<xsl:param name="footnotesInFrontMatter" as="xs:integer"/> 
		<!-- count(//dtb:note[key('noterefs', @id)[ancestor::dtb:frontmatter]]) -->
		<!-- count(//dtb:note[key('noterefs', @id)[ancestor::dtb:frontmatter]]) -->
		<xsl:param name="footnotesNotInFrontMatter" as="xs:integer"/>
		<!-- count(//dtb:note[key('noterefs', @id)[not(ancestor::dtb:frontmatter)]]) -->
		<!-- count(//dtb:note[key('noterefs', @id)[not(ancestor::dtb:frontmatter)]]) -->
		<xsl:if test="$footnotesInFrontMatter+$footnotesNotInFrontMatter>0"> <!-- count(//dtb:note)>0 -->
			<dynamic-sequence master="notes">
				<block padding-top="3"><xsl:value-of select="$l10nEndnotesHeading"/></block>
				<xsl:if test="$footnotesInFrontMatter>0">
					<list-of-references collection="endnotes-front" range="volume">
						<on-page-start>
							<block padding-top="1" keep="all" keep-with-next="1"><evaluate expression="(format &quot;{$l10nEndnotesPageStart}&quot; (numeral-format roman $started-page-number))"/></block>
						</on-page-start>
					</list-of-references>
					<xsl:if test="$footnotesNotInFrontMatter>0">
						<list-of-references collection="endnotes-frontB" range="volume">
							<on-page-start>
								<block padding-top="1" keep="all" keep-with-next="1"><evaluate expression="(format &quot;{$l10nEndnotesPageStart}&quot; (numeral-format roman $started-page-number))"/></block>
							</on-page-start>
						</list-of-references>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$footnotesNotInFrontMatter>0">
					<list-of-references collection="endnotes" range="volume">
						<on-page-start>
							<block padding-top="1" keep="all" keep-with-next="1"><evaluate expression="(format &quot;{$l10nEndnotesPageStart}&quot; $started-page-number)"/></block>
						</on-page-start>
					</list-of-references>
					<xsl:if test="$footnotesInFrontMatter>0">
						<list-of-references collection="endnotesB" range="volume">
							<on-page-start>
								<block padding-top="1" keep="all" keep-with-next="1"><evaluate expression="(format &quot;{$l10nEndnotesPageStart}&quot; $started-page-number)"/></block>
							</on-page-start>
						</list-of-references>
					</xsl:if>
				</xsl:if>
			</dynamic-sequence>
		</xsl:if>
	</xsl:function>
	
	<xsl:function name="obfl:insertCoverPage">
		<xsl:param name="title"/>
		<!-- /dtb:dtbook/dtb:book/dtb:frontmatter/dtb:doctitle -->
		<!-- /dtb:dtbook/dtb:book/dtb:frontmatter/dtb:doctitle -->
		<xsl:param name="authors"/>
		<!-- /dtb:dtbook/dtb:book/dtb:frontmatter/dtb:docauthor -->
		<!-- /dtb:dtbook/dtb:book/dtb:frontmatter/dtb:docauthor -->
		<!-- /dtb:dtbook/dtb:book/dtb:frontmatter/dtb:docauthor -->
		<sequence master="cover">
			<xsl:choose>
				<xsl:when test="$title">
					<block align="center" padding-top="3" padding-bottom="1" margin-left="2" margin-right="2"><xsl:value-of select="$title"/></block>
				</xsl:when>
				<xsl:otherwise>
					<block align="center" padding-top="3"  margin-left="2" margin-right="2">&#x00a0;</block>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="count($authors)>3">
					<block align="center"  margin-left="2" margin-right="2"><xsl:value-of select="$authors[0]"/></block>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="$authors">
						<block align="center" margin-left="2" margin-right="2"><xsl:value-of select="."/></block>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
			<block align="center" margin-left="2" margin-right="2" vertical-align="before" vertical-position="100%" hyphenate="false"><evaluate expression="
				(if (&gt; $volumes 1) 
				(format &quot;{$l10nTocVolumeXofY}&quot; (int2text (round $volume) {$l10nLang}) (int2text (round $volumes) {$l10nLang}))
				&quot;{$l10nTocOneVolume}&quot;)"/></block>
		</sequence>
	</xsl:function>
	
	<xsl:function name="obfl:insertProcessorRenderer">
		<file-reference uri="dtbook_table_grid.xsl">
			<xsl:copy-of select="document('dtbook_table_grid.xsl')"/>
		</file-reference>
		<xml-processor name="staircase">
			<xsl:copy-of select="document('staircase-table.xsl')"/>
		</xml-processor>
		<xml-processor name="matrix">
			<xsl:copy-of select="document('matrix-table.xsl')"/>
		</xml-processor>
		<renderer name="table-renderer">
			<rendering-scenario processor="matrix" cost="(+ (* 100 $forced-break-count) $total-height (/ (- {$page-width} $min-block-width) {$page-width}))">
				<parameter name="table-split-columns" value="{$matrix-table-columns-max}"/>
				<parameter name="l10ntablepart" value="{$l10ntablepart}"/>
			</rendering-scenario>
			<rendering-scenario processor="staircase" cost="(+ (* 100 $forced-break-count) $total-height (/ (- {$page-width} $min-block-width) {$page-width}))">
				<parameter name="table-split-columns" value="{$staircase-table-columns-max}"/>
				<parameter name="l10ntablepart" value="{$l10ntablepart}"/>
			</rendering-scenario>
		</renderer>
	</xsl:function>

</xsl:stylesheet>