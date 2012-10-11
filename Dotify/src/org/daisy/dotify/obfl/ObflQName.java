package org.daisy.dotify.obfl;

import javax.xml.namespace.QName;

interface ObflQName {
	 final static QName OBFL = new QName("obfl");
	 final static QName LAYOUT_MASTER = new QName("layout-master");
	 final static QName TEMPLATE = new QName("template");
	 final static QName DEFAULT_TEMPLATE = new QName("default-template");
	 final static QName HEADER = new QName("header");
	 final static QName FOOTER = new QName("footer");
	 final static QName FIELD = new QName("field");
	 final static QName STRING = new QName("string");
	 final static QName EVALUATE = new QName("evaluate");
	 final static QName CURRENT_PAGE = new QName("current-page");
	 final static QName MARKER_REFERENCE = new QName("marker-reference");
	 final static QName BLOCK = new QName("block");
	 final static QName SPAN = new QName("span");
	 final static QName TOC_ENTRY = new QName("toc-entry");
	 final static QName LEADER = new QName("leader");
	 final static QName MARKER = new QName("marker");
	 final static QName ANCHOR = new QName("anchor");
	 final static QName BR = new QName("br");
	 final static QName PAGE_NUMBER = new QName("page-number");
	
	 final static QName SEQUENCE = new QName("sequence");
	 final static QName VOLUME_TEMPLATE = new QName("volume-template");
	 final static QName PRE_CONTENT = new QName("pre-content");
	 final static QName POST_CONTENT = new QName("post-content");
	 final static QName TOC_SEQUENCE = new QName("toc-sequence");
	 final static QName ON_TOC_START = new QName("on-toc-start");
	 final static QName ON_VOLUME_START = new QName("on-volume-start");
	 final static QName ON_VOLUME_END = new QName("on-volume-end");
	 final static QName ON_TOC_END = new QName("on-toc-end");
	
	 final static QName TABLE_OF_CONTENTS = new QName("table-of-contents");
	
	 final static QName ATTR_XML_LANG = new QName("http://www.w3.org/XML/1998/namespace", "lang", "xml");
	 final static QName ATTR_HYPHENATE = new QName("hyphenate");
	 final static QName ATTR_PAGE_WIDTH = new QName("page-width");
	 final static QName ATTR_PAGE_HEIGHT = new QName("page-height");
	 final static QName ATTR_NAME = new QName("name");
	 final static QName ATTR_USE_WHEN = new QName("use-when");
}
