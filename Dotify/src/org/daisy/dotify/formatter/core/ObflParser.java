package org.daisy.dotify.formatter.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterException;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.core.MarkerReferenceField.MarkerSearchDirection;
import org.daisy.dotify.formatter.core.MarkerReferenceField.MarkerSearchScope;
import org.daisy.dotify.formatter.core.NumeralField.NumeralStyle;
import org.daisy.dotify.formatter.dom.BlockEvent;
import org.daisy.dotify.formatter.dom.BlockEventImpl;
import org.daisy.dotify.formatter.dom.BlockProperties;
import org.daisy.dotify.formatter.dom.BlockStruct;
import org.daisy.dotify.formatter.dom.FormattingTypes;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Leader;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.PageTemplate;
import org.daisy.dotify.formatter.dom.SequenceProperties;
import org.daisy.dotify.formatter.dom.StaticSequenceEvent;
import org.daisy.dotify.formatter.dom.TextProperties;
import org.daisy.dotify.formatter.dom.TocBlockEventImpl;
import org.daisy.dotify.formatter.dom.TocSequenceEvent;
import org.daisy.dotify.formatter.dom.TocSequenceEvent.TocRange;
import org.daisy.dotify.formatter.dom.VolumeSequenceEvent;
import org.daisy.dotify.formatter.dom.VolumeTemplate;
import org.daisy.dotify.formatter.utils.Expression;
import org.daisy.dotify.formatter.utils.Position;
import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a parser for OBFL. The parser accepts OBFL input
 * and returns a VolumeStruct.
 *
 * @author Joel Håkansson
 *
 */
public class ObflParser {
	private final static QName OBFL = new QName("obfl");
	private final static QName LAYOUT_MASTER = new QName("layout-master");
	private final static QName TEMPLATE = new QName("template");
	private final static QName DEFAULT_TEMPLATE = new QName("default-template");
	private final static QName HEADER = new QName("header");
	private final static QName FOOTER = new QName("footer");
	private final static QName FIELD = new QName("field");
	private final static QName STRING = new QName("string");
	private final static QName EVALUATE = new QName("evaluate");
	private final static QName CURRENT_PAGE = new QName("current-page");
	private final static QName MARKER_REFERENCE = new QName("marker-reference");
	private final static QName BLOCK = new QName("block");
	private final static QName SPAN = new QName("span");
	private final static QName TOC_ENTRY = new QName("toc-entry");
	private final static QName LEADER = new QName("leader");
	private final static QName MARKER = new QName("marker");
	private final static QName ANCHOR = new QName("anchor");
	private final static QName BR = new QName("br");
	private final static QName PAGE_NUMBER = new QName("page-number");
	
	private final static QName SEQUENCE = new QName("sequence");
	private final static QName VOLUME_TEMPLATE = new QName("volume-template");
	private final static QName PRE_CONTENT = new QName("pre-content");
	private final static QName POST_CONTENT = new QName("post-content");
	private final static QName TOC_SEQUENCE = new QName("toc-sequence");
	private final static QName ON_TOC_START = new QName("on-toc-start");
	private final static QName ON_VOLUME_START = new QName("on-volume-start");
	private final static QName ON_VOLUME_END = new QName("on-volume-end");
	private final static QName ON_TOC_END = new QName("on-toc-end");
	
	private final static QName TABLE_OF_CONTENTS = new QName("table-of-contents");
	
	private final static QName ATTR_XML_LANG = new QName("http://www.w3.org/XML/1998/namespace", "lang", "xml");
	private final static QName ATTR_HYPHENATE = new QName("hyphenate");
	private final static QName ATTR_PAGE_WIDTH = new QName("page-width");
	private final static QName ATTR_PAGE_HEIGHT = new QName("page-height");
	private final static QName ATTR_NAME = new QName("name");

	private HashMap<String, TableOfContents> tocs;
	private HashMap<String, LayoutMaster> masters;
	private Stack<VolumeTemplate> volumeTemplates;

	private FormatterFactory formatterFactory;
	//private PaginatorFactory paginatorFactory;
	//private VolumeSplitterFactory splitterFactory;
	private Formatter formatter;
	
	public ObflParser() {
		this(FormatterFactory.newInstance());
	}
	
	public ObflParser(FormatterFactory formatterFactory) {
		this.formatterFactory = formatterFactory;
		//this.paginatorFactory = PaginatorFactory.newInstance();
		//this.splitterFactory = VolumeSplitterFactory.newInstance();
	}
	
	public void setFormatterFactory(FormatterFactory formatterFactory) {
		this.formatterFactory = formatterFactory;
	}
	
	/*
	public void setPaginatorFactory(PaginatorFactory paginatorFactory) {
		this.paginatorFactory = paginatorFactory;
	}
	
	public void setVolumeSplitterFactory(VolumeSplitterFactory splitterFactory) {
		this.splitterFactory = splitterFactory;
	}*/
	
	public void parse(InputStream stream) throws XMLStreamException, FormatterException {
        XMLInputFactory inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        XMLEventReader input = inFactory.createXMLEventReader(stream);
		this.formatter = formatterFactory.newFormatter();
		this.tocs = new HashMap<String, TableOfContents>();
		this.masters = new HashMap<String, LayoutMaster>();
		this.volumeTemplates = new Stack<VolumeTemplate>();
		formatter.open();
		XMLEvent event;
		FilterLocale locale = null;
		boolean hyphenate = true;
		while (input.hasNext()) {
			event = input.nextEvent();
			if (equalsStart(event, OBFL)) {
				String loc = getAttr(event, ATTR_XML_LANG);
				if (loc==null) {
					throw new FormatterException("Missing xml:lang on root element");
				} else {
					locale = FilterLocale.parse(loc);
				}
				hyphenate = getHyphenate(event, hyphenate);
			} else if (equalsStart(event, LAYOUT_MASTER)) {
				parseLayoutMaster(event, input);
			} else if (equalsStart(event, SEQUENCE)) {
				parseSequence(event, input, locale, hyphenate);
			} else if (equalsStart(event, TABLE_OF_CONTENTS)) {
				parseTableOfContents(event, input, locale, hyphenate);
			} else if (equalsStart(event, VOLUME_TEMPLATE)) {
				parseVolumeTemplate(event, input, locale, hyphenate);
			}
		}
		try {
			input.close();
			formatter.close();
			
		} catch (IOException e) {
			throw new FormatterException(e);
		}
	}

	//TODO: parse page-number-variable
	private void parseLayoutMaster(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		@SuppressWarnings("unchecked")
		Iterator<Attribute> i = event.asStartElement().getAttributes();
		int width = Integer.parseInt(getAttr(event, ATTR_PAGE_WIDTH));
		int height = Integer.parseInt(getAttr(event, ATTR_PAGE_HEIGHT));
		String masterName = getAttr(event, ATTR_NAME);
		LayoutMasterImpl.Builder masterConfig = new LayoutMasterImpl.Builder(width, height);
		while (i.hasNext()) {
			Attribute atts = i.next();
			String name = atts.getName().getLocalPart();
			String value = atts.getValue();
			if (name.equals("inner-margin")) {
				masterConfig.innerMargin(Integer.parseInt(value));
			} else if (name.equals("outer-margin")) {
				masterConfig.outerMargin(Integer.parseInt(value));
			} else if (name.equals("row-spacing")) {
				masterConfig.rowSpacing(Float.parseFloat(value));
			} else if (name.equals("duplex")) {
				masterConfig.duplex(value.equals("true"));
			}
		}
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, TEMPLATE)) {
				masterConfig.addTemplate(parseTemplate(event, input));
			} else if (equalsStart(event, DEFAULT_TEMPLATE)) {
				masterConfig.addTemplate(parseTemplate(event, input));
			} else if (equalsEnd(event, LAYOUT_MASTER)) {
				break;
			}
		}
		formatter.addLayoutMaster(masterName, masterConfig.build());
		masters.put(masterName, masterConfig.build());
	}
	
	private PageTemplate parseTemplate(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		PageTemplateImpl template;
		if (equalsStart(event, TEMPLATE)) {
			template = new PageTemplateImpl(getAttr(event, "use-when"));
		} else {
			template = new PageTemplateImpl();
		}
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, HEADER)) {
				ArrayList<Object> fields = parseHeaderFooter(event, input);
				if (fields.size()>0) {
					template.addToHeader(fields);
				}
			} else if (equalsStart(event, FOOTER)) {
				ArrayList<Object> fields = parseHeaderFooter(event, input);
				if (fields.size()>0) {
					template.addToFooter(fields);
				}
			} else if (equalsEnd(event, TEMPLATE) || equalsEnd(event, DEFAULT_TEMPLATE)) {
				break;
			}
		}
		return template;
	}
	
	private ArrayList<Object> parseHeaderFooter(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		ArrayList<Object> fields = new ArrayList<Object>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, FIELD)) {
				ArrayList<Object> compound = parseField(event, input);
				if (compound.size()==1) {
					fields.add(compound.get(0));
				} else {
					CompoundField f = new CompoundField();
					f.addAll(compound);
					fields.add(f);
				}
			} else if (equalsEnd(event, HEADER) || equalsEnd(event, FOOTER)) {
				break;
			}
		}
		return fields;
	}
	
	private ArrayList<Object> parseField(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		ArrayList<Object> compound = new ArrayList<Object>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, STRING)) {
				compound.add(getAttr(event, "value"));
			} else if (equalsStart(event, EVALUATE)) {
				//FIXME: add variables...
				compound.add(new Expression().evaluate(getAttr(event, "expression")));
			} else if (equalsStart(event, CURRENT_PAGE)) {
				compound.add(new CurrentPageField(NumeralStyle.valueOf(getAttr(event, "style").toUpperCase())));
			} else if (equalsStart(event, MARKER_REFERENCE)) {
				compound.add(
					new MarkerReferenceField(
							getAttr(event, "marker"), 
							MarkerSearchDirection.valueOf(getAttr(event, "direction").toUpperCase()),
							MarkerSearchScope.valueOf(getAttr(event, "scope").toUpperCase())
					)
				);
			} else if (equalsEnd(event, FIELD)) {
				break;
			}
		}
		return compound;
	}
	
	private void parseSequence(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName);
		String initialPageNumber = getAttr(event, "initial-page-number");
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}
		formatter.newSequence(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, BLOCK)) {
				parseBlock(event, input, locale, hyph);
			}/* else if (equalsStart(event, LEADER)) {
				parseLeader(event, input);
			}*/
			else if (equalsEnd(event, SEQUENCE)) {
				break;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseBlock(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		formatter.startBlock(blockBuilder(event.asStartElement().getAttributes()));
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				formatter.addChars(event.asCharacters().getData(), new TextProperties.Builder(locale).hyphenate(hyph).build());
			} else if (equalsStart(event, BLOCK)) {
				parseBlock(event, input, locale, hyph);
			} else if (equalsStart(event, SPAN)) {
				parseSpan(event, input, locale, hyph);
			} else if (equalsStart(event, LEADER)) {
				formatter.insertLeader(parseLeader(event, input));
			} else if (equalsStart(event, MARKER)) {
				formatter.insertMarker(parseMarker(event, input));
			} else if (equalsStart(event, BR)) {
				formatter.newLine();
				scanEmptyElement(input, BR);
			}
			else if (equalsEnd(event, BLOCK)) {
				break;
			}
		}
		formatter.endBlock();
	}
	
	private void parseSpan(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				formatter.addChars(event.asCharacters().getData(), new TextProperties.Builder(locale).hyphenate(hyph).build());
			} else if (equalsStart(event, LEADER)) {
				formatter.insertLeader(parseLeader(event, input));
			} else if (equalsStart(event, MARKER)) {
				formatter.insertMarker(parseMarker(event, input));
			} else if (equalsStart(event, BR)) {
				formatter.newLine();
				scanEmptyElement(input, BR);
			}
			else if (equalsEnd(event, SPAN)) {
				break;
			}
		}
	}
	
	private BlockProperties blockBuilder(Iterator<Attribute> atts) {
		BlockProperties.Builder builder = new BlockProperties.Builder();
		while (atts.hasNext()) {
			Attribute att = atts.next();
			String name = att.getName().getLocalPart();
			if (name.equals("margin-left")) {
				builder.leftMargin(Integer.parseInt(att.getValue()));
			} else if (name.equals("margin-right")) {
				builder.rightMargin(Integer.parseInt(att.getValue()));
			} else if (name.equals("margin-top")) {
				builder.topMargin(Integer.parseInt(att.getValue()));
			} else if (name.equals("margin-bottom")) {
				builder.bottomMargin(Integer.parseInt(att.getValue()));
			} else if (name.equals("text-indent")) {
				builder.textIndent(Integer.parseInt(att.getValue()));
			} else if (name.equals("first-line-indent")) {
				builder.firstLineIndent(Integer.parseInt(att.getValue()));
			} else if (name.equals("list-type")) {
				builder.listType(FormattingTypes.ListStyle.valueOf(att.getValue().toUpperCase()));
			} else if (name.equals("break-before")) {
				builder.breakBefore(FormattingTypes.BreakBefore.valueOf(att.getValue().toUpperCase()));
			} else if (name.equals("keep")) {
				builder.keep(FormattingTypes.Keep.valueOf(att.getValue().toUpperCase()));
			} else if (name.equals("keep-with-next")) {
				builder.keepWithNext(Integer.parseInt(att.getValue()));
			} else if (name.equals("keep-with-previous-sheets")) {
				builder.keepWithPreviousSheets(Integer.parseInt(att.getValue()));
			} else if (name.equals("keep-with-next-sheets")) {
				builder.keepWithNextSheets(Integer.parseInt(att.getValue()));
			} else if (name.equals("block-indent")) {
				builder.blockIndent(Integer.parseInt(att.getValue()));
			} else if (name.equals("id")) {
				builder.identifier(att.getValue());
			}
		}
		return builder.build();
	}
	
	private Leader parseLeader(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		Leader.Builder builder = new Leader.Builder();
		@SuppressWarnings("unchecked")
		Iterator<Attribute> atts = event.asStartElement().getAttributes();
		while (atts.hasNext()) {
			Attribute att = atts.next();
			String name = att.getName().getLocalPart();
			if (name.equals("align")) {
				builder.align(Leader.Alignment.valueOf(att.getValue().toUpperCase()));
			} else if (name.equals("position")) {
				builder.position(Position.parsePosition(att.getValue()));
			} else if (name.equals("pattern")) {
				builder.pattern(att.getValue());
			}
		}
		scanEmptyElement(input, LEADER);
		return builder.build();
	}

	private Marker parseMarker(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		String markerName = getAttr(event, "class");
		String markerValue = getAttr(event, "value");
		return new Marker(markerName, markerValue);
	}
	
	private void parseTableOfContents(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String tocName = getAttr(event, "name");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		TableOfContents toc = new TableOfContents();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, TOC_ENTRY)) {
				toc.add(parseTocEntry(event, input, toc, locale, hyph));
			} else if (equalsEnd(event, TABLE_OF_CONTENTS)) {
				break;
			}
		}
		tocs.put(tocName, toc);
	}

	@SuppressWarnings("unchecked")
	private BlockEvent parseTocEntry(XMLEvent event, XMLEventReader input, TableOfContents toc, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String refId = getAttr(event, "ref-id");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		String tocId;
		do {
			tocId = ""+((int)Math.round((99999999*Math.random())));
		} while (toc.containsTocID(tocId));
		TocBlockEventImpl ret = new TocBlockEventImpl(refId, tocId, blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				ret.add(new TextContents(event.asCharacters().getData(), new TextProperties.Builder(locale).hyphenate(hyph).build()));
			} else if (equalsStart(event, TOC_ENTRY)) {
				ret.add(parseTocEntry(event, input, toc, locale, hyph));
			} else if (equalsStart(event, LEADER)) {
				ret.add(parseLeader(event, input));
			} else if (equalsStart(event, MARKER)) {
				ret.add(parseMarker(event, input));
			} else if (equalsStart(event, BR)) {
				ret.add(new LineBreak());
				scanEmptyElement(input, BR);
			} else if (equalsStart(event, PAGE_NUMBER)) {
				ret.add(parsePageNumber(event, input));
			} else if (equalsStart(event, ANCHOR)) {
				//TODO: implement
				throw new UnsupportedOperationException("Not implemented");
			} else if (equalsStart(event, EVALUATE)) {
				ret.add(parseEvaluate(event, input));
			}
			else if (equalsEnd(event, TOC_ENTRY)) {
				break;
			}
		}
		return ret;
	}
	
	private PageNumberReference parsePageNumber(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		String refId = getAttr(event, "ref-id");
		NumeralStyle style = NumeralStyle.DEFAULT;
		String styleStr = getAttr(event, "style");
		if (styleStr!=null) {
			try {
				style = NumeralStyle.valueOf(styleStr.toUpperCase());
			} catch (Exception e) { }
		}
		scanEmptyElement(input, PAGE_NUMBER);
		return new PageNumberReference(refId, style);
	}
	
	private Evaluate parseEvaluate(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		String expr = getAttr(event, "expression");
		scanEmptyElement(input, EVALUATE);
		return new Evaluate(expr);
	}
	
	private void parseVolumeTemplate(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String volumeVar = getAttr(event, "volume-number-variable");
		String volumeCountVar = getAttr(event, "volume-count-variable");
		String useWhen = getAttr(event, "use-when");
		String splitterMax = getAttr(event, "sheets-in-volume-max");
		VolumeTemplateImpl template = new VolumeTemplateImpl(volumeVar, volumeCountVar, useWhen, Integer.parseInt(splitterMax));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, PRE_CONTENT)) {
				template.setPreVolumeContent(parsePreVolumeContent(event, input, template, locale, hyph));
			} else if (equalsStart(event, POST_CONTENT)) {
				template.setPostVolumeContent(parsePostVolumeContent(event, input, locale, hyph));
			} else if (equalsEnd(event, VOLUME_TEMPLATE)) {
				break;
			}
		}
		volumeTemplates.push(template);
	}
	
	private Iterable<VolumeSequenceEvent> parsePreVolumeContent(XMLEvent event, XMLEventReader input, VolumeTemplate template, FilterLocale locale, boolean hyph) throws XMLStreamException {
		ArrayList<VolumeSequenceEvent> ret = new ArrayList<VolumeSequenceEvent>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, SEQUENCE)) {
				ret.add(parseVolumeSequence(event, input, locale, hyph));
			} else if (equalsStart(event, TOC_SEQUENCE)) {
				ret.add(parseTocSequence(event, input, template, locale, hyph));
			} else if (equalsEnd(event, PRE_CONTENT)) {
				break;
			}
		}
		return ret;
	}
	
	private Iterable<VolumeSequenceEvent> parsePostVolumeContent(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		ArrayList<VolumeSequenceEvent> ret = new ArrayList<VolumeSequenceEvent>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, SEQUENCE)) {
				ret.add(parseVolumeSequence(event, input, locale, hyph));
			} else if (equalsEnd(event, POST_CONTENT)) {
				break;
			}
		}
		return ret;
	}

	private VolumeSequenceEvent parseVolumeSequence(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName);
		String initialPageNumber = getAttr(event, "initial-page-number");
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}
		StaticSequenceEvent volSeq = new StaticSequenceEvent(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, BLOCK)) {
				volSeq.add(parseBlockEvent(event, input, locale, hyph));
			} else if (equalsEnd(event, SEQUENCE)) {
				break;
			}
		}
		return volSeq;
	}

	private VolumeSequenceEvent parseTocSequence(XMLEvent event, XMLEventReader input, VolumeTemplate template, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		String tocName = getAttr(event, "toc");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName);
		String initialPageNumber = getAttr(event, "initial-page-number");
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}
		TocRange range = TocRange.valueOf(getAttr(event, "range").toUpperCase());
		String condition = getAttr(event, "use-when");
		String volEventVar = getAttr(event, "toc-event-volume-number-variable");
		TocSequenceEvent tocSequence = new TocSequenceEvent(builder.build(), tocName, range, condition, volEventVar, template);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ON_TOC_START)) {
				String tmp = getAttr(event, "use-when");
				tocSequence.addTocStartEvents(parseOnEvent(event, input, ON_TOC_START, locale, hyph), tmp);
			} else if (equalsStart(event, ON_VOLUME_START)) {
				String tmp = getAttr(event, "use-when");
				tocSequence.addVolumeStartEvents(parseOnEvent(event, input, ON_VOLUME_START, locale, hyph), tmp);
			} else if (equalsStart(event, ON_VOLUME_END)) {
				String tmp = getAttr(event, "use-when");
				tocSequence.addVolumeEndEvents(parseOnEvent(event, input, ON_VOLUME_END, locale, hyph), tmp);
			} else if (equalsStart(event, ON_TOC_END)) {
				String tmp = getAttr(event, "use-when");
				tocSequence.addTocEndEvents(parseOnEvent(event, input, ON_TOC_END, locale, hyph), tmp);
			}
			else if (equalsEnd(event, TOC_SEQUENCE)) {
				break;
			}
		}
		return tocSequence;
	}

	private Iterable<BlockEvent> parseOnEvent(XMLEvent event, XMLEventReader input, QName end, FilterLocale locale, boolean hyph) throws XMLStreamException {
		ArrayList<BlockEvent> ret = new ArrayList<BlockEvent>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, BLOCK)) {
				ret.add(parseBlockEvent(event, input, locale, hyph));
			} else if (equalsEnd(event, end)) {
				break;
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private BlockEvent parseBlockEvent(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		BlockEventImpl ret = new BlockEventImpl(blockBuilder(event.asStartElement().getAttributes()));
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				ret.add(new TextContents(event.asCharacters().getData(), new TextProperties.Builder(locale).hyphenate(hyph).build()));
			} else if (equalsStart(event, BLOCK)) {
				ret.add(parseBlockEvent(event, input, locale, hyph));
			} else if (equalsStart(event, LEADER)) {
				ret.add(parseLeader(event, input));
			} else if (equalsStart(event, MARKER)) {
				ret.add(parseMarker(event, input));
			} else if (equalsStart(event, BR)) {
				ret.add(new LineBreak());
				scanEmptyElement(input, BR);
			} else if (equalsStart(event, EVALUATE)) {
				ret.add(parseEvaluate(event, input));
			}
			else if (equalsEnd(event, BLOCK)) {
				break;
			}
		}
		return ret;
	}

	private void scanEmptyElement(XMLEventReader input, QName element) throws XMLStreamException {
		XMLEvent event;
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.getEventType()!=XMLStreamConstants.END_ELEMENT) {
				throw new RuntimeException("Unexpected input");
			} else if (equalsEnd(event, element)) {
				break;
			}
		}
	}

	private String getAttr(XMLEvent event, String attr) {
		return getAttr(event, new QName(attr));
	}
	
	private String getAttr(XMLEvent event, QName attr) {
		Attribute ret = event.asStartElement().getAttributeByName(attr);
		if (ret==null) {
			return null;
		} else {
			return ret.getValue();
		}
	}
	
	private FilterLocale getLang(XMLEvent event, FilterLocale locale) {
		String lang = getAttr(event, ATTR_XML_LANG);
		if (lang!=null) {
			if (lang.equals("")) {
				return null;
			} else {
				return FilterLocale.parse(lang);
			}
		}
		return locale;
	}
	
	private boolean getHyphenate(XMLEvent event, boolean hyphenate) {
		String hyph = getAttr(event, ATTR_HYPHENATE);
		if (hyph!=null) {
			return hyph.equals("true");
		}
		return hyphenate;
	}
	
	private boolean equalsStart(XMLEvent event, QName element) {
		return 	event.getEventType()==XMLStreamConstants.START_ELEMENT
				&& event.asStartElement().getName().equals(element);
	}
	
	private boolean equalsEnd(XMLEvent event, QName element) {
		return 	event.getEventType()==XMLStreamConstants.END_ELEMENT 
				&& event.asEndElement().getName().equals(element);
	}
	
	public Map<String, TableOfContents> getTocs() {
		return tocs;
	}
	
	public Iterable<VolumeTemplate> getVolumeTemplates() {
		return volumeTemplates;
	}
	
	public Map<String, LayoutMaster> getMasters() {
		return masters;
	}
	
	public BlockStruct getBlockStruct() {
		return formatter.getFlowStruct();
	}

}
