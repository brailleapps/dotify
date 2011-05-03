<?xml version="1.0" encoding="utf-8"?>
<!-- Rules generated on: 2009-11-10 14:48:28 -->
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">
	<sch:title>DTBook 2005-3 Schematron basic tests</sch:title>
	<sch:ns prefix="dtb" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
	<!-- Rule 1: Disallowed element: note -->
	<sch:pattern name="no_note" id="no_note">
		<sch:rule context="dtb:note">
			<sch:assert test="false">[Rule 1] No 'note'</sch:assert>
		</sch:rule>
	</sch:pattern>
	<!-- Rule 2: Disallowed element: table -->
	<sch:pattern name="no_table" id="no_table">
		<sch:rule context="dtb:table">
			<sch:assert test="false">[Rule 2] No 'table'</sch:assert>
		</sch:rule>
	</sch:pattern>
	<!-- Rule 3: Document contains an unsupported language -->
	<sch:pattern name="xml_lang" id="xml_lang">
		<sch:rule context="*[@xml:lang]">
			<sch:assert test="@xml:lang='sv' or @xml:lang='sv-SE' or @xml:lang='en' or @xml:lang='en-US' or @xml:lang='en-GB' or @xml:lang='no' or @xml:lang='de' or @xml:lang='fr' or @xml:lang='fi'">[Rule 3] Unsupported language./></sch:assert>
		</sch:rule>
	</sch:pattern>
</sch:schema>
