<?xml version="1.0" encoding="utf-8"?>
<?xslt-doc-file doc-files/dtb2obfl_braille.xml?>
<!--
	TODO:
		- komplexa sub, sup
		- länkar, e-postadresser
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:import href="dtbook2flow_sv_SE.xsl" />
	<xsl:output method="xml" encoding="utf-8" indent="no"/>
	<xsl:param name="toc-indent-multiplier" select="1"/>
	<xsl:param name="splitterMax" select="10"/>

	<xsl:template match="/">
		<!--Note! This works with Saxon (which is used in Dotify), but not with XMLSpy's engine -->
		<xsl:variable name="text-processing"><xsl:apply-templates mode="dtbook"/></xsl:variable>
		<xsl:apply-templates select="$text-processing" mode="obfl"/>
	</xsl:template>
	
	<xsl:template match="/" mode="obfl">
		<obfl version="2011-1">
			<xsl:attribute name="xml:lang"><xsl:value-of select="/dtb:dtbook/@xml:lang"/></xsl:attribute>
			<xsl:call-template name="insertLayoutMaster"/>
			<xsl:apply-templates/>
		</obfl>
	</xsl:template>
	
	<xsl:template name="insertLayoutMaster">
		<layout-master name="front" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<template use-when="(= (% $page 2) 0)">
				<header>
					<field><string value="&#x2800;&#x2800;"/><current-page style="roman"/></field>
				</header>
				<footer></footer>
			</template>
			<default-template>
				<header>
					<field><string value=""/></field>
					<field><current-page style="roman"/></field>
				</header>
				<footer></footer>
			</default-template>
		</layout-master>
		<layout-master name="main" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<template use-when="(= (% $page 2) 0)">
				<header>
					<field><string value="&#x2800;&#x2800;"/><current-page style="default"/></field>
					<field>
						<marker-reference marker="pagenum-turn" direction="forward" scope="page_content"/>
						<marker-reference marker="pagenum" direction="backward" scope="sequence"/>
					</field>
				</header>
				<footer></footer>
			</template>
			<default-template>
				<header>
					<field><string value="&#x2800;&#x2800;"/>
						<marker-reference marker="pagenum-turn" direction="forward" scope="page_content"/>
						<marker-reference marker="pagenum" direction="backward" scope="sequence"/>
					</field>
					<field><current-page style="default"/></field>
				</header>
				<footer></footer>
			</default-template>
		</layout-master>
		<layout-master name="plain" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<default-template>
				<header><field><string value=""/></field></header>
				<footer></footer>
			</default-template>
		</layout-master>
		<xsl:choose>
			<xsl:when test="//dtb:level1[@class='toc'] or //dtb:level1[dtb:list[@class='toc']]">
			<table-of-contents name="full-toc">
				<xsl:apply-templates select="//dtb:level1" mode="toc"/>
			</table-of-contents>
			<volume-template volume-number-variable="volume" volume-count-variable="volumes" use-when="(= $volume 1)" sheets-in-volume-max="{$splitterMax}">
				<pre-content>
					<toc-sequence master="front" toc="full-toc" range="document" use-when="(= $volume 1)" initial-page-number="1">
						<on-toc-start>
							<block margin-bottom="1">Innehåll</block>
							<block margin-bottom="1">Sid­hän­vis­ning­ar till svart­skrifts­bo­ken står in­om pa­ren­tes.</block>
						</on-toc-start>
						<on-volume-start use-when="(&amp; (> $volumes 1) (= $started-volume-number 1))">
							<block keep="all" keep-with-next="1" margin-bottom="0">Volym <evaluate expression="(round $started-volume-number)"/></block>
						</on-volume-start>
						<on-volume-start use-when="(&amp; (> $volumes 1) (> $started-volume-number 1))">
							<block keep="all" keep-with-next="1" margin-top="1" margin-bottom="0">Volym <evaluate expression="(round $started-volume-number)"/></block>
						</on-volume-start>
					</toc-sequence>
					<xsl:apply-templates select="//dtb:frontmatter" mode="pre-volume-mode"/>
				</pre-content>
				<post-content/>
			</volume-template>
			<volume-template volume-number-variable="volume" volume-count-variable="volumes" use-when="(> $volume 1)" sheets-in-volume-max="{$splitterMax}">
				<pre-content>
					<toc-sequence master="front" toc="full-toc" range="volume" use-when="(> $volume 1)" initial-page-number="1">
						<on-toc-start>
							<block margin-bottom="1">Innehåll volym <evaluate expression="(round $volume)"/></block>
						</on-toc-start>
					</toc-sequence>
				</pre-content>
				<post-content/>
			</volume-template>
			</xsl:when>
			<xsl:otherwise>
				<volume-template sheets-in-volume-max="{$splitterMax}">
					<pre-content/>
					<post-content/>
				</volume-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Don't output a sequence if there is nothing left when doctitle, docauthor and level1@class='backCoverText', level1@class='rearjacketcopy' and level1@class='colophon' has been moved -->
	<xsl:template match="dtb:frontmatter" mode="sequence-mode">
		<xsl:if test="(*[not(self::dtb:doctitle or self::dtb:docauthor or self::dtb:level1[@class='backCoverText' or @class='rearjacketcopy' or @class='colophon' or @class='toc' or dtb:list[@class='toc']])])
						and not(//dtb:level1[@class='toc'] or //dtb:level1[dtb:list[@class='toc']])"><!--  -->
			<sequence>
				<xsl:apply-templates select="." mode="apply-sequence-attributes"/>
				<xsl:apply-templates/>
			</sequence>
		</xsl:if>
	</xsl:template>
	
		<!-- Don't output a sequence if there is nothing left when level1@class='backCoverText', level1@class='rearjacketcopy' and level1@class='colophon' has been moved -->
	<xsl:template match="dtb:rearmatter" mode="sequence-mode">
		<xsl:if test="*[not(self::dtb:level1[@class='backCoverText' or @class='rearjacketcopy' or @class='colophon' or @class='toc' or dtb:list[@class='toc']])]"><!--  -->
			<sequence>
				<xsl:apply-templates select="." mode="apply-sequence-attributes"/>
				<xsl:apply-templates/>
			</sequence>
		</xsl:if>
	</xsl:template>
	
		<!-- Don't output a sequence if there is nothing left when doctitle, docauthor and level1@class='backCoverText', level1@class='rearjacketcopy' and level1@class='colophon' has been moved -->
	<xsl:template match="dtb:frontmatter" mode="pre-volume-mode">
		<xsl:if test="*[not(self::dtb:doctitle or self::dtb:docauthor or self::dtb:level1[@class='backCoverText' or @class='rearjacketcopy' or @class='colophon' or @class='toc' or dtb:list[@class='toc']])]">
			<sequence master="front">
				<block break-before="page">
					<xsl:apply-templates/>
					<!-- 
					<xsl:variable name="tree">
						<xsl:apply-templates/>
					</xsl:variable>
					<xsl:apply-templates select="$tree" mode="strip-id"/> -->
				</block>
			</sequence>
		</xsl:if>
	</xsl:template>

	<xsl:template match="*|comment()|processing-instruction()" mode="strip-id">
		<xsl:call-template name="copy-without-id"/>
	</xsl:template>
	
	<xsl:template name="copy-without-id">
		<xsl:copy>
			<xsl:copy-of select="@*[name()!='id']"/>
			<xsl:apply-templates mode="strip-id"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="dtb:level1[@class='toc']"></xsl:template>
	<xsl:template match="dtb:level1[dtb:list[@class='toc']]"></xsl:template>
	<xsl:template match="dtb:level1[@class='toc']" mode="toc"></xsl:template>
	<xsl:template match="dtb:level1[dtb:list[@class='toc']]" mode="toc"></xsl:template>

	<xsl:template match="dtb:level1[(@class='backCoverText' or @class='rearjacketcopy' or @class='colophon') and (parent::dtb:frontmatter or parent::dtb:rearmatter)]" mode="toc"></xsl:template>
	
	<xsl:template match="dtb:level1|dtb:level2" mode="toc">
		<xsl:if test="dtb:h1|dtb:h2">
<!--
			<xsl:choose>
				<xsl:when test="self::dtb:level1 and @class='part'"><toc-entry ref-id="{generate-id(.)}" margin-bottom="1" keep="all"><xsl:apply-templates select="dtb:h1" mode="toc-hd"/></toc-entry><xsl:apply-templates mode="toc"/></xsl:when>
				<xsl:otherwise>--><toc-entry ref-id="{generate-id(dtb:h1|dtb:h2)}" block-indent="{$toc-indent-multiplier}" text-indent="{2*$toc-indent-multiplier}" keep="all"><xsl:apply-templates select="dtb:h1|dtb:h2" mode="toc-hd"/><xsl:apply-templates mode="toc"/></toc-entry><!--</xsl:otherwise>
			</xsl:choose>-->
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dtb:level3|dtb:level4|dtb:level5|dtb:level6" mode="toc">
		<xsl:if test="dtb:h3|dtb:h4|dtb:h5|dtb:h6">
			<toc-entry ref-id="{generate-id(dtb:h3|dtb:h4|dtb:h5|dtb:h6)}" block-indent="{$toc-indent-multiplier}" text-indent="{$toc-indent-multiplier}" keep="all"><xsl:apply-templates select="dtb:h3|dtb:h4|dtb:h5|dtb:h6" mode="toc-hd"/>
			<xsl:if test="dtb:level3 and ancestor::dtb:level1[@class='part']"><xsl:apply-templates mode="toc"/></xsl:if>
			</toc-entry>
			<xsl:if test="not(dtb:level3 and ancestor::dtb:level1[@class='part'])"><xsl:apply-templates mode="toc"/></xsl:if>
		</xsl:if>
	</xsl:template>
	<!--
	<xsl:template name="addBottomMarginIfPart">
	
		<xsl:if test="(following::*[self::dtb:level1|self::dtb:level2|self::dtb:level3|self::dtb:level4|self::dtb:level5|self::dtb:level6][1])[self::dtb:level1[@class='part']]">
			<xsl:attribute name="margin-bottom">1</xsl:attribute>
		</xsl:if>
	</xsl:template>
-->
	
	<xsl:template match="dtb:h1|dtb:h2|dtb:h3|dtb:h4|dtb:h5|dtb:h6" mode="toc-hd">
<!--		<xsl:value-of select="descendant::text()"/>-->
	<xsl:apply-templates mode="toc-text"/>
	<!-- <xsl:if test="not(self::dtb:h1 and ancestor::dtb:level1[@class='part'])"> -->
		<xsl:text> (</xsl:text><xsl:value-of select="preceding::dtb:pagenum[1]/text()"/><xsl:text>) </xsl:text><leader position="100%" align="right" pattern="."/><page-number ref-id="{generate-id(.)}"><xsl:if test="ancestor::dtb:frontmatter"><xsl:attribute name="style">roman</xsl:attribute></xsl:if></page-number>
		<!--  </xsl:if>  -->
	</xsl:template>

	<xsl:template match="*" mode="toc-text">
		<xsl:apply-templates mode="toc-text"/>
	</xsl:template>
	<xsl:template match="text()" mode="toc-text">
		<!-- Hack to remove braille markers from toc entries. This will do for now, since the long term goal
			 is for all braille markers to be added in the translator. -->
		<xsl:value-of select="translate(., '&#x2820;&#x2804;&#x2823;&#x2824;&#x2828;&#x282c;&#x2831;', '')"/>
	</xsl:template>
	<xsl:template match="dtb:br" mode="toc-text">
		<xsl:text> </xsl:text>
	</xsl:template>
	
	<xsl:template match="node()" mode="toc"/>
	
	<!-- Redigering och avskrivning, page 148 -->
	<xsl:template match="dtb:dd" mode="block-mode">
		<block>
			<xsl:apply-templates select="." mode="apply-block-attributes"/>
			<xsl:text>&#x2820;&#x2804; </xsl:text><xsl:apply-templates/>
		</block>
	</xsl:template>
	
	<!-- Svenska skrivregler för punktskrift 2009, page 34 -->
	<xsl:template match="dtb:em[not(ancestor::dtb:list[@class='toc'])]" mode="dtbook">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2820;&#x2804;'"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2820;&#x2824;'"/>
			<xsl:with-param name="postfix-multi-word" select="'&#x2831;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 34 -->
	<xsl:template match="dtb:strong[not(ancestor::dtb:list[@class='toc'])]" mode="dtbook">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2828;'"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2828;&#x2828;'"/>
			<xsl:with-param name="postfix-multi-word" select="'&#x2831;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 32 -->
	<xsl:template match="dtb:sup" mode="dtbook">
		<xsl:call-template name="addMarkersAlfaNum">
			<xsl:with-param name="prefix" select="'&#x282c;'"/>
			<xsl:with-param name="postfix" select="''"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 32 -->
	<xsl:template match="dtb:sub" mode="dtbook">
		<xsl:call-template name="addMarkersAlfaNum">
			<xsl:with-param name="prefix" select="'&#x2823;'"/>
			<xsl:with-param name="postfix" select="''"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="*|comment()|processing-instruction()" mode="dtbook">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates mode="dtbook"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="text()" mode="dtbook">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template name="addMarkersAlfaNum">
		<xsl:param name="prefix" select="''"/>
		<xsl:param name="postfix" select="''"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:choose>
				<!-- text contains a single alfa/numerical string -->
				<xsl:when test="count(node())=1 and text() and matches(text(),'^[a-zA-Z0-9]*$')">
					<xsl:value-of select="$prefix"/>
					<xsl:apply-templates mode="dtbook"/>
					<xsl:value-of select="$postfix"/>
				</xsl:when>
				<!-- Otherwise -->
				<xsl:otherwise>
					<xsl:message terminate="no">Error: sub/sub contains a complex expression for which there is no specified formatting.</xsl:message>
					<xsl:apply-templates mode="dtbook"/>
				</xsl:otherwise>		
			</xsl:choose>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template name="addMarkers">
		<xsl:param name="prefix-single-word" select="''"/>
		<xsl:param name="postfix-single-word" select="''"/>
		<xsl:param name="prefix-multi-word" select="''"/>
		<xsl:param name="postfix-multi-word" select="''"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:choose>
				<xsl:when test="count(node())=0"><xsl:text> </xsl:text></xsl:when>
				<!-- if text contains one word only -->
				<xsl:when test="count(text())=1 and translate(text(), ' ', '')=text()">
					<xsl:value-of select="$prefix-single-word"/>
					<xsl:apply-templates mode="dtbook"/>
					<xsl:value-of select="$postfix-single-word"/>
				</xsl:when>
				<!-- text contains several words -->
				<xsl:otherwise>
					<xsl:value-of select="$prefix-multi-word"/>
					<xsl:apply-templates mode="dtbook"/>
					<xsl:value-of select="$postfix-multi-word"/>
				</xsl:otherwise>		
			</xsl:choose>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
