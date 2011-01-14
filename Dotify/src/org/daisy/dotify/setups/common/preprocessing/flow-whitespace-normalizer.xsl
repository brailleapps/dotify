<?xml version="1.0" encoding="UTF-8"?>
<!--
	Flow whitespace normalizer

	Description
	Removes undesired whitespace that will effect the layout 
	process. Whitespace is often injected by mistake into the 
	input file, e.g. by "Pretty printing".

	Notes
		1.	This implementation does not support inline containers
		2.	This implementation does not support xml:space="preserve"

	Parameters
		None

	Format (input -> output)
		Flow -> Flow

	Nodes
		text()
    
	Author: Joel Håkansson, TPB
	Version: 2009-09-14
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="no"/>

    <xsl:template match="block">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:variable name="size" select="count(node())"/>
			<xsl:for-each select="node()">
				<xsl:choose>
					<!-- this will have to change if inline text containers, such as "span", are implemented -->
					<xsl:when test="self::text() and string-length(normalize-space(.))=0"></xsl:when>
					<xsl:when test="self::text()">
						<xsl:choose>
							<xsl:when test="$size=1">
								<xsl:value-of select="normalize-space(.)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="position()>1 and matches(substring(., 1, 1), '\s+') and not((preceding-sibling::node()[1])[self::block or self::br])">
									<xsl:text> </xsl:text>
								</xsl:if>
								<xsl:value-of select="normalize-space(.)"/>
								<xsl:if test="position()&lt;$size and matches(substring(., string-length(.), 1), '\s+') and not((following-sibling::node()[1])[self::block or self::br])">
									<xsl:text> </xsl:text>
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise><xsl:apply-templates select="."/></xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:copy>
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
