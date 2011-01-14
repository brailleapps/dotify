<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>
	
	<xsl:param name="filename"/>

	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
				<title><xsl:value-of select="$filename"/></title>
			</head>
			<body>
				<h1>Description for <xsl:value-of select="$filename"/></h1>
				<xsl:choose>
					<xsl:when test="processing-instruction('build-xslt-doc')[.='disable-output-escaping']">
						<xsl:value-of select="comment()[1]" disable-output-escaping="yes"/>
					</xsl:when>
					<xsl:otherwise>
						<pre><xsl:value-of select="comment()[1]"/></pre>
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>
