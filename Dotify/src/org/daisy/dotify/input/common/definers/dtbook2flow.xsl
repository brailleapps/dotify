<?xml version="1.0" encoding="utf-8"?>
<?build-xslt-doc disable-output-escaping?>
<!-- 
<pre>
	DTBook to Flow

	Description
	Provides a base stylesheet for DTBook. The stylesheet must be extended. 
	The minimum requirement is to implement a template with the name 
	insertLayoutMaster. This template should add layout definition elements,
	see flow.dtd. The extending stylesheet can also benefit from the 
	processing modes to refine layout. See below.
	
	Parameters
		None

	Format (input -> output)
		DTBook -> Flow
    
	Author: Joel Håkansson, TPB
</pre>

<h2>Processing modes</h2>
<h3>Sequence modes</h3>
<p>Use "sequence-mode" to override default sequence element processing and "apply-sequence-attributes" to override default sequence attributes</p>

<h3>Block modes</h3>
<p>Use "block-mode" to override default block element processing and "apply-block-attributes" to override default element attributes</p>

<h3>Inline modes</h3>
<p>Use "inline-mode" to override default inline processing</p>

<h2>Element groups</h2>
<p>This stylesheet categorizes DTBook elements into five groups: sequence, block, inline, special and no-op. The first three (sequence, block and
inline) can easily be customized using the processing modes.</p>

<h3>sequence</h3> 
<p>bodymatter, frontmatter, rearmatter</p>

<h3>block</h3>
<dl>
	<dt>no text</dt>
		<dd>level, level1, level2, level3, level4, level5, level6</dd>
	<dt>can be surrounded by text, does not contain text</dt>
		<dd>list, blockquote, linegroup, poem, div, annotation, dl, imggroup</dd>
	<dt>can contain text, but cannot be surrounded by text</dt>
		<dd>bridgehead, caption, covertitle, docauthor, doctitle, li, h1, h2, h3, h4, h5, h6</dd>
	<dt>can be surrounded by text and contain text</dt>
		<dd>author, address, hd, line, p, sidebar, byline, dateline, epigraph, prodnote</dd>
<dl>
<h3>inline/block</h3>
<p>The following elemnts may be treated as inline or block depending on context:</p>
<p>a, cite, kbd, samp</p>

<h3>inline</h3>
<p>code, bdo, em, strong, span, sub, sup, abbr, acronym, 
dfn, q, noteref, annoref, sent, w, linenum, lic, dd, dt</p>

<h3>special</h3>
<p>br, pagenum</p>

<h3>no-op</h3>
<p>book, dtbook, head, meta, link, img</p>

<h2>Unhandled elements</h2>
<p>note, table, col, colgroup, tbody, td, tfoot, th, thead, title, tr</p>
<h2>Summary</h2>
		<table border="1px">
			<tr>
				<th>Sequence</th>
				<th>Block (type 1)</th>
				<th>Block (type 2)</th>
				<th>Block (type 3)</th>
				<th>Block (type 4)</th>
				<th>Block or Inline</th>
				<th>Inline</th>
				<th>Special</th>
				<th>No-op</th>
				<th>Not supported</th>
			</tr>
			<tr>
				<td>bodymatter</td>
				<td>level</td>
				<td>annotation</td>
				<td>bridgehead</td>
				<td>address</td>
				<td>a</td>
				<td>abbr</td>
				<td>br</td>
				<td>book</td>
				<td>col</td>
			</tr>
			<tr>
				<td>frontmatter</td>
				<td>level1</td>
				<td>blockquote</td>
				<td>caption</td>
				<td>author</td>
				<td>cite</td>
				<td>acronym</td>
				<td>pagenum</td>
				<td>dtbook</td>
				<td>colgroup</td>
			</tr>
			<tr>
				<td>rearmatter</td>
				<td>level2</td>
				<td>div</td>
				<td>covertitle</td>
				<td>byline</td>
				<td>kbd</td>
				<td>annoref</td>
				<td/>
				<td>head</td>
				<td>note</td>
			</tr>
			<tr>
				<td/>
				<td>level3</td>
				<td>dl</td>
				<td>docauthor</td>
				<td>dateline</td>
				<td>samp</td>
				<td>bdo</td>
				<td/>
				<td>img</td>
				<td>table</td>
			</tr>
			<tr>
				<td/>
				<td>level4</td>
				<td>imggroup</td>
				<td>doctitle</td>
				<td>epigraph</td>
				<td/>
				<td>code</td>
				<td/>
				<td>link</td>
				<td>tbody</td>
			</tr>
			<tr>
				<td/>
				<td>level5</td>
				<td>linegroup</td>
				<td>h1</td>
				<td>hd</td>
				<td/>
				<td>dd</td>
				<td/>
				<td>meta</td>
				<td>td</td>
			</tr>
			<tr>
				<td/>
				<td>level6</td>
				<td>list</td>
				<td>h2</td>
				<td>line</td>
				<td/>
				<td>dfn</td>
				<td/>
				<td/>
				<td>tfoot</td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td>poem</td>
				<td>h3</td>
				<td>p</td>
				<td/>
				<td>dt</td>
				<td/>
				<td/>
				<td>th</td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td>h4</td>
				<td>prodnote</td>
				<td/>
				<td>em</td>
				<td/>
				<td/>
				<td>thead</td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td>h5</td>
				<td>sidebar</td>
				<td/>
				<td>lic</td>
				<td/>
				<td/>
				<td>title</td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td>h6</td>
				<td/>
				<td/>
				<td>linenum</td>
				<td/>
				<td/>
				<td>tr</td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td>li</td>
				<td/>
				<td/>
				<td>noteref</td>
				<td/>
				<td/>
				<td></td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td>q</td>
				<td/>
				<td/>
				<td></td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td>sent</td>
				<td/>
				<td/>
				<td></td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td>span</td>
				<td/>
				<td/>
				<td></td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td>strong</td>
				<td/>
				<td/>
				<td></td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td>sub</td>
				<td/>
				<td/>
				<td></td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td>sup</td>
				<td/>
				<td/>
				<td></td>
			</tr>
			<tr>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
				<td>w</td>
				<td/>
				<td/>
				<td></td>
			</tr>
		</table>
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:output method="xml" encoding="utf-8" indent="no"/>

	<xsl:template match="/"><root version="2011-1"><xsl:call-template name="insertLayoutMaster"/><xsl:apply-templates/></root></xsl:template>
	<xsl:template match="dtb:dtbook | dtb:book"><xsl:apply-templates/></xsl:template>
	<xsl:template match="dtb:head | dtb:meta | dtb:link"></xsl:template>
	
<!-- sequence elements / -->
	<xsl:template match="dtb:frontmatter | dtb:bodymatter | dtb:rearmatter">
		<xsl:apply-templates select="." mode="sequence-mode"/>
	</xsl:template>
<!-- / sequence elements -->

<!-- block elements / -->
	<!-- No text elements -->
	<xsl:template match="dtb:level1 | dtb:level2 | dtb:level3 | dtb:level4 | dtb:level5 | dtb:level6 | dtb:level">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
	<!-- Can be surrounded by text, but does not contain text -->
	<xsl:template match="dtb:list | dtb:blockquote | dtb:linegroup | dtb:poem |
								dtb:div | dtb:annotation | dtb:dl | dtb:imggroup">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
	<!-- Can contain text, but isn't surrounded by text -->
	<xsl:template match="dtb:caption | dtb:h1 | dtb:h2 | dtb:h3 | dtb:h4 | dtb:h5 | dtb:h6 | dtb:li |
								dtb:bridgehead | dtb:covertitle | dtb:docauthor | dtb:doctitle">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
	<!-- Can be surrounded by text and contain text -->
	<xsl:template match="dtb:address | dtb:prodnote | dtb:hd | dtb:p | dtb:author | dtb:line | 
								dtb:epigraph | dtb:sidebar | dtb:byline | dtb:dateline">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
<!-- / block elements -->

<!-- inlines that may alternatively be in block elements / -->
	<xsl:template match="dtb:a | dtb:cite | dtb:samp | dtb:kbd">
		<xsl:choose>
			<xsl:when test="parent::dtb:level1 or parent::dtb:level2 or parent::dtb:level3 or parent::dtb:level4 or parent::dtb:level5 or parent::dtb:level6 or parent::dtb:level or parent::dtb:div or parent::dtb:annotation">
			<xsl:message><xsl:apply-templates select="." mode="block-mode"/></xsl:message>
			</xsl:when>
			<xsl:otherwise><xsl:apply-templates select="." mode="inline-mode"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
<!-- / inlines that may alternatively be in block elements -->

<!-- inline elements / -->
	<xsl:template match="dtb:bdo | dtb:code | dtb:em | dtb:strong | dtb:sup | dtb:sub | dtb:w |  
								dtb:sent | dtb:span | dtb:acronym | dtb:abbr | dtb:q | dtb:dfn |
								dtb:annoref | dtb:noteref | dtb:linenum | dtb:lic | dtb:dt | dtb:dd">
		<xsl:apply-templates select="." mode="inline-mode"/>
	</xsl:template>
<!-- / inline elements -->

<!-- special / -->
	<xsl:template match="dtb:pagenum">
		<marker class="pagenum" value="{text()}"/>
		<xsl:variable name="preceding-pagenum" select="preceding::dtb:pagenum[1]"/>
		<!-- @page='normal' or $preceding-pagenum/@page='normal' -->
		<!-- This should be true for all normal pages, but false for a sequence of "unnumbered page" or similar. -->
		<xsl:if test="text()!=$preceding-pagenum/text()">
			<xsl:variable name="preceding-marker">
				<xsl:if test="not($preceding-pagenum) or generate-id($preceding-pagenum/ancestor::dtb:level1/parent::*)=
							generate-id(ancestor::dtb:level1/parent::*)">
					<xsl:value-of select="$preceding-pagenum"/><xsl:text>&#x2013;</xsl:text>
				</xsl:if>
			</xsl:variable>
			<marker class="pagenum-turn" value="{$preceding-marker}"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="dtb:br"><br/></xsl:template>

	<xsl:template match="dtb:img"></xsl:template>
<!-- / special -->

<!-- disallowed elements / -->
	<xsl:template match="dtb:col | dtb:colgroup | dtb:table | dtb:tbody | dtb:thead | dtb:tfoot | dtb:tr | dtb:th | dtb:td">
		<xsl:message terminate="yes">Tables are not supported.</xsl:message>
	</xsl:template>
	<xsl:template match="dtb:note">
		<xsl:message terminate="yes">Notes are not supported.</xsl:message>
	</xsl:template>
<!-- / disallowed elements -->

<!-- default mode templates / -->
	<xsl:template match="*" mode="sequence-mode">
		<sequence>
			<xsl:apply-templates select="." mode="apply-sequence-attributes"/>
			<xsl:apply-templates/>
		</sequence>
	</xsl:template>

	<xsl:template match="*" mode="block-mode">
		<block>
			<xsl:apply-templates select="." mode="apply-block-attributes"/>
			<xsl:apply-templates/>
		</block>
	</xsl:template>

	<xsl:template match="*" mode="inline-mode">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="*" mode="apply-sequence-attributes"/>
	<xsl:template match="*" mode="apply-block-attributes"/>
<!-- / default mode templates -->

</xsl:stylesheet>
