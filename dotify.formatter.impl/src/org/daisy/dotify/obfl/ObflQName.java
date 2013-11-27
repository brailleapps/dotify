package org.daisy.dotify.obfl;

import javax.xml.namespace.QName;

interface ObflQName {
	final static String OBFL_NS = "http://www.daisy.org/ns/2011/obfl";
	 final static QName OBFL = new QName(OBFL_NS, "obfl");
	final static QName META = new QName(OBFL_NS, "meta");
	 final static QName LAYOUT_MASTER = new QName(OBFL_NS, "layout-master");
	 final static QName TEMPLATE = new QName(OBFL_NS, "template");
	 final static QName DEFAULT_TEMPLATE = new QName(OBFL_NS, "default-template");
	 final static QName HEADER = new QName(OBFL_NS, "header");
	 final static QName FOOTER = new QName(OBFL_NS, "footer");
	 final static QName FIELD = new QName(OBFL_NS, "field");
	 final static QName STRING = new QName(OBFL_NS, "string");
	 final static QName EVALUATE = new QName(OBFL_NS, "evaluate");
	 final static QName CURRENT_PAGE = new QName(OBFL_NS, "current-page");
	 final static QName MARKER_REFERENCE = new QName(OBFL_NS, "marker-reference");
	 final static QName BLOCK = new QName(OBFL_NS, "block");
	 final static QName SPAN = new QName(OBFL_NS, "span");
	final static QName STYLE = new QName(OBFL_NS, "style");
	 final static QName TOC_ENTRY = new QName(OBFL_NS, "toc-entry");
	 final static QName LEADER = new QName(OBFL_NS, "leader");
	 final static QName MARKER = new QName(OBFL_NS, "marker");
	 final static QName ANCHOR = new QName(OBFL_NS, "anchor");
	 final static QName BR = new QName(OBFL_NS, "br");
	 final static QName PAGE_NUMBER = new QName(OBFL_NS, "page-number");
	
	 final static QName SEQUENCE = new QName(OBFL_NS, "sequence");
	 final static QName VOLUME_TEMPLATE = new QName(OBFL_NS, "volume-template");
	 final static QName PRE_CONTENT = new QName(OBFL_NS, "pre-content");
	 final static QName POST_CONTENT = new QName(OBFL_NS, "post-content");
	 final static QName TOC_SEQUENCE = new QName(OBFL_NS, "toc-sequence");
	 final static QName ON_TOC_START = new QName(OBFL_NS, "on-toc-start");
	 final static QName ON_VOLUME_START = new QName(OBFL_NS, "on-volume-start");
	 final static QName ON_VOLUME_END = new QName(OBFL_NS, "on-volume-end");
	 final static QName ON_TOC_END = new QName(OBFL_NS, "on-toc-end");
	
	 final static QName TABLE_OF_CONTENTS = new QName(OBFL_NS, "table-of-contents");
	
	 final static QName ATTR_XML_LANG = new QName("http://www.w3.org/XML/1998/namespace", "lang", "xml");
	 final static QName ATTR_HYPHENATE = new QName("hyphenate");
	 final static QName ATTR_PAGE_WIDTH = new QName("page-width");
	 final static QName ATTR_PAGE_HEIGHT = new QName("page-height");
	 final static QName ATTR_NAME = new QName("name");
	 final static QName ATTR_USE_WHEN = new QName("use-when");
}