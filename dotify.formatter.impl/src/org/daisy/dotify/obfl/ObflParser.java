package org.daisy.dotify.obfl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.daisy.dotify.api.formatter.BlockPosition.VerticalAlignment;
import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.CompoundField;
import org.daisy.dotify.api.formatter.ContentCollection;
import org.daisy.dotify.api.formatter.CurrentPageField;
import org.daisy.dotify.api.formatter.Field;
import org.daisy.dotify.api.formatter.FieldList;
import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.ItemSequenceProperties;
import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.MarkerReferenceField;
import org.daisy.dotify.api.formatter.MarkerReferenceField.MarkerSearchDirection;
import org.daisy.dotify.api.formatter.MarkerReferenceField.MarkerSearchScope;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.PageAreaBuilder;
import org.daisy.dotify.api.formatter.PageAreaProperties;
import org.daisy.dotify.api.formatter.PageTemplateBuilder;
import org.daisy.dotify.api.formatter.Position;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.StringField;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.api.formatter.VolumeContentBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.MarkerProcessor;
import org.daisy.dotify.api.translator.TextAttribute;
import org.daisy.dotify.api.translator.TextBorderConfigurationException;
import org.daisy.dotify.api.translator.TextBorderFactory;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.api.writer.MetaDataItem;
import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.DefaultTextAttribute;

/**
 * Provides a parser for OBFL. The parser accepts OBFL input, either
 * as an InputStream or as an XMLEventReader.
 *
 * @author Joel Håkansson
 *
 */
public class ObflParser extends XMLParserBase {

	
	//private HashMap<String, LayoutMaster> masters;
	private List<MetaDataItem> meta;

	private Formatter formatter;
	private final FilterLocale locale;
	private final String mode;
	private final FormatterFactory formatterFactory;
	private final MarkerProcessor mp;
	private final TextBorderFactoryMakerService maker;
	private final ExpressionFactory ef;

	public ObflParser(String locale, String mode, MarkerProcessor mp, FormatterFactory formatterFactory, TextBorderFactoryMakerService maker, ExpressionFactory ef) {
		this.locale = FilterLocale.parse(locale);
		this.mode = mode;
		this.formatterFactory = formatterFactory;
		this.mp = mp;
		this.maker = maker;
		this.ef = ef;
	}
	
	public void parse(XMLEventReader input) throws XMLStreamException, OBFLParserException {
		this.formatter = formatterFactory.newFormatter(locale.toString(), mode);
		//this.masters = new HashMap<String, LayoutMaster>();
		this.meta = new ArrayList<MetaDataItem>();
		formatter.open();
		XMLEvent event;
		FilterLocale locale = null;
		boolean hyphenate = true;
		while (input.hasNext()) {
			event = input.nextEvent();
			if (equalsStart(event, ObflQName.OBFL)) {
				String loc = getAttr(event, ObflQName.ATTR_XML_LANG);
				if (loc==null) {
					throw new OBFLParserException("Missing xml:lang on root element");
				} else {
					locale = FilterLocale.parse(loc);
				}
				hyphenate = getHyphenate(event, hyphenate);
			} else if (equalsStart(event, ObflQName.META)) {
				parseMeta(event, input);
			} else if (equalsStart(event, ObflQName.LAYOUT_MASTER)) {
				parseLayoutMaster(event, input);
			} else if (equalsStart(event, ObflQName.SEQUENCE)) {
				parseSequence(event, input, locale, hyphenate);
			} else if (equalsStart(event, ObflQName.TABLE_OF_CONTENTS)) {
				parseTableOfContents(event, input, locale, hyphenate);
			} else if (equalsStart(event, ObflQName.VOLUME_TEMPLATE)) {
				parseVolumeTemplate(event, input, locale, hyphenate);
			} else if (equalsStart(event, ObflQName.COLLECTION)) {
				parseCollection(event, input, locale, hyphenate);
			} 
			else {
				report(event);
			}
		}
		try {
			input.close();
			formatter.close();
		} catch (IOException e) {
			throw new OBFLParserException(e);
		}
	}

	private void parseMeta(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		int level = 0;
		while (input.hasNext()) {
			event = input.nextEvent();
			if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
				level++;
				if (level == 1) {
					StringBuilder sb = new StringBuilder();
					QName name = event.asStartElement().getName();
					while (input.hasNext()) {
						event = input.nextEvent();
						if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
							level++;
							warning(event, "Nested meta data not supported.");
						} else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
							level--;
						} else if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
							sb.append(event.asCharacters().getData());
						} else {
							report(event);
						}
						if (level < 2) {
							break;
						}
					}
					meta.add(new MetaDataItem(name, sb.toString()));
				} else {
					warning(event, "Nested meta data not supported.");
				}
			} else if (equalsEnd(event, ObflQName.META)) {
				break;
			} else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
				level--;
			} else {
				report(event);
			}
		}
	}

	private void report(XMLEvent event) {
		if (event.isEndElement()) {
			// ok
		} else if (event.isStartElement()) {
			String msg = "Unsupported context for element: " + event.asStartElement().getName() + buildLocationMsg(event.getLocation());
			//throw new UnsupportedOperationException(msg);
			Logger.getLogger(this.getClass().getCanonicalName()).warning(msg);
		} else if (event.isStartDocument() || event.isEndDocument()) {
			// ok
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).warning(event.toString());
		}
	}

	private void warning(XMLEvent event, String msg) {
		Logger.getLogger(this.getClass().getCanonicalName()).warning(msg + buildLocationMsg(event.getLocation()));
	}

	public String buildLocationMsg(Location location) {
		int line = -1;
		int col = -1;
		if (location != null) {
			line = location.getLineNumber();
			col = location.getColumnNumber();
		}
		return (line > -1 ? " (at line: " + line + (col > -1 ? ", column: " + col : "") + ") " : "");
	}

	//TODO: parse page-number-variable
	private void parseLayoutMaster(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		@SuppressWarnings("unchecked")
		Iterator<Attribute> i = event.asStartElement().getAttributes();
		int width = Integer.parseInt(getAttr(event, ObflQName.ATTR_PAGE_WIDTH));
		int height = Integer.parseInt(getAttr(event, ObflQName.ATTR_PAGE_HEIGHT));
		String masterName = getAttr(event, ObflQName.ATTR_NAME);
		//LayoutMasterImpl.Builder masterConfig = new LayoutMasterImpl.Builder(width, height, ef);
		LayoutMasterProperties.Builder masterConfig = new LayoutMasterProperties.Builder(width, height);
		HashMap<String, Object> border = new HashMap<String, Object>();
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
			}  else if (name.startsWith("border")) {
				border.put(name, value);
			}
		}
		if (border.size()>0) {
			border.put(TextBorderFactory.FEATURE_MODE, mode);
			try {
				masterConfig.border(maker.newTextBorderStyle(border));
			} catch (TextBorderConfigurationException e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Failed to add border to block properties: " + border, e);
			}
		}
		LayoutMasterBuilder master = formatter.newLayoutMaster(masterName, masterConfig.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.TEMPLATE)) {
				parseTemplate(master, event, input);
			} else if (equalsStart(event, ObflQName.DEFAULT_TEMPLATE)) {
				parseTemplate(master, event, input);
			} else if (equalsStart(event, ObflQName.PAGE_AREA)) {
				parsePageArea(master, event, input);
			} else if (equalsEnd(event, ObflQName.LAYOUT_MASTER)) {
				break;
			} else {
				report(event);
			}
		}

		//masters.put(masterName, masterConfig.build());
	}
	
	private void parsePageArea(LayoutMasterBuilder master, XMLEvent event, XMLEventReader input) throws XMLStreamException {
		String collection = getAttr(event, ObflQName.ATTR_COLLECTION);
		int maxHeight = Integer.parseInt(getAttr(event, ObflQName.ATTR_MAX_HEIGHT));
		PageAreaProperties.Builder config = new PageAreaProperties.Builder(collection, maxHeight);
		@SuppressWarnings("unchecked")
		Iterator<Attribute> i = event.asStartElement().getAttributes();
		while (i.hasNext()) {
			Attribute atts = i.next();
			String name = atts.getName().getLocalPart();
			String value = atts.getValue();
			if (name.equals("align")) {
				config.align(PageAreaProperties.Alignment.valueOf(value.toUpperCase()));
			} else if (name.equals("fallback")) {
				config.fallbackId(value);
			} else if (name.equals("fallback-scope")) {
				config.scope(PageAreaProperties.FallbackScope.valueOf(value.toUpperCase()));
			}
		}
		PageAreaBuilder builder = master.setPageArea(config.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.BEFORE)) {
				parseBeforeAfter(event, input, builder.getBeforeArea(), locale, true);
			} else if (equalsStart(event, ObflQName.AFTER)) {
				parseBeforeAfter(event, input, builder.getAfterArea(), locale, true);
			} else if (equalsEnd(event, ObflQName.PAGE_AREA)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseBeforeAfter(XMLEvent event, XMLEventReader input, FormatterCore fc, FilterLocale locale, boolean hyph) throws XMLStreamException {
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		fc.startBlock(blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				fc.addChars(event.asCharacters().getData(), new TextProperties.Builder(locale.toString()).hyphenate(hyph).build());
			} else if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, fc, locale, hyph);
			} else if (processAsBlockContents(fc, event, input, locale, hyph)) {
				//done!
			}
			else if (equalsEnd(event, ObflQName.BEFORE, ObflQName.AFTER)) {
				fc.endBlock();
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseTemplate(LayoutMasterBuilder master, XMLEvent event, XMLEventReader input) throws XMLStreamException {
		PageTemplateBuilder template;
		if (equalsStart(event, ObflQName.TEMPLATE)) {
			template = master.newTemplate(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, false));
		} else {
			template = master.newTemplate(null);
		}
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.HEADER)) {
				FieldList fields = parseHeaderFooter(event, input);
				if (fields!=null) {
					template.addToHeader(fields);
				}
			} else if (equalsStart(event, ObflQName.FOOTER)) {
				FieldList fields = parseHeaderFooter(event, input);
				if (fields!=null) {
					template.addToFooter(fields);
				}
			} else if (equalsEnd(event, ObflQName.TEMPLATE) || equalsEnd(event, ObflQName.DEFAULT_TEMPLATE)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private FieldList parseHeaderFooter(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		@SuppressWarnings("unchecked")
		Iterator<Attribute> i = event.asStartElement().getAttributes();
		Float rowSpacing = null;
		while (i.hasNext()) {
			Attribute atts = i.next();
			String name = atts.getName().getLocalPart();
			String value = atts.getValue();
			if (name.equals("row-spacing")) {
				rowSpacing = Float.parseFloat(value);
			}
		}
		ArrayList<Field> fields = new ArrayList<Field>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.FIELD)) {
				ArrayList<Field> compound = parseField(event, input);
				if (compound.size()==1) {
					fields.add(compound.get(0));
				} else {
					CompoundField f = new CompoundField();
					f.addAll(compound);
					fields.add(f);
				}
			} else if (equalsEnd(event, ObflQName.HEADER) || equalsEnd(event, ObflQName.FOOTER)) {
				break;
			} else {
				report(event);
			}
		}
		if (fields.size()>0) {
			return new FieldList.Builder(fields).rowSpacing(rowSpacing).build();
		} else {
			return null;
		}
	}
	
	private ArrayList<Field> parseField(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		ArrayList<Field> compound = new ArrayList<Field>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.STRING)) {
				compound.add(new StringField(getAttr(event, "value")));
			} else if (equalsStart(event, ObflQName.EVALUATE)) {
				//FIXME: add variables...
				compound.add(new StringField(ef.newExpression().evaluate(getAttr(event, "expression"))));
			} else if (equalsStart(event, ObflQName.CURRENT_PAGE)) {
				compound.add(new CurrentPageField(NumeralStyle.valueOf(getAttr(event, "style").replace('-', '_').toUpperCase())));
			} else if (equalsStart(event, ObflQName.MARKER_REFERENCE)) {
				compound.add(
					new MarkerReferenceField(
							getAttr(event, "marker"), 
							MarkerSearchDirection.valueOf(getAttr(event, "direction").toUpperCase()),
							MarkerSearchScope.valueOf(getAttr(event, "scope").replace('-', '_').toUpperCase())
					)
				);
			} else if (equalsEnd(event, ObflQName.FIELD)) {
				break;
			} else {
				report(event);
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
		FormatterCore seq = formatter.newSequence(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, seq, locale, hyph);
			}/* else if (equalsStart(event, LEADER)) {
				parseLeader(event, input);
			}*/
			else if (equalsEnd(event, ObflQName.SEQUENCE)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseBlock(XMLEvent event, XMLEventReader input, FormatterCore fc, FilterLocale locale, boolean hyph) throws XMLStreamException {
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		fc.startBlock(blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				fc.addChars(event.asCharacters().getData(), new TextProperties.Builder(locale.toString()).hyphenate(hyph).build());
			} else if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, fc, locale, hyph);
			} else if (processAsBlockContents(fc, event, input, locale, hyph)) {
				//done
			}
			else if (equalsEnd(event, ObflQName.BLOCK)) {
				fc.endBlock();
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseSpan(XMLEvent event, XMLEventReader input, FormatterCore fc, FilterLocale locale, boolean hyph) throws XMLStreamException {
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				fc.addChars(event.asCharacters().getData(), new TextProperties.Builder(locale.toString()).hyphenate(hyph).build());
			} else if (equalsStart(event, ObflQName.STYLE)) {
				parseStyle(event, input, fc, locale, hyph);
			} else if (equalsStart(event, ObflQName.LEADER)) {
				parseLeader(fc, event, input);
			} else if (equalsStart(event, ObflQName.MARKER)) {
				parseMarker(fc, event);
			} else if (equalsStart(event, ObflQName.BR)) {
				fc.newLine();
				scanEmptyElement(input, ObflQName.BR);
			}
			else if (equalsEnd(event, ObflQName.SPAN)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseStyle(XMLEvent event, XMLEventReader input, FormatterCore fc, FilterLocale locale, boolean hyph) throws XMLStreamException {
		TextProperties tp = new TextProperties.Builder(locale.toString()).hyphenate(hyph).build();

		parseStyleEvent(fc, event, input, tp);
	}

	private void parseStyleEvent(FormatterCore fc, XMLEvent ev, XMLEventReader input, TextProperties tp) throws XMLStreamException {
		//Buffer events and extract text
		List<XMLEvent> events = new ArrayList<XMLEvent>();
		List<String> chunks = new ArrayList<String>();
		events.add(ev);
		int level = 1;
		while (input.hasNext()) {
			XMLEvent event = input.nextEvent();
			events.add(event);
			if (event.isCharacters()) {
				chunks.add(event.asCharacters().getData());
			} else if (equalsStart(event, ObflQName.STYLE)) {
				level ++;
			} else if (equalsEnd(event, ObflQName.STYLE)) {
				level --;
				if (level == 0) break;
			}
		}
		
		//Build text attributes
		Iterator<XMLEvent> evs = events.iterator();
		TextAttribute t = processTextAttributes(evs.next(), evs);
		
		//Add markers to text
		String[] updated = mp.processAttributesRetain(t, chunks.toArray(new String[chunks.size()]));
		
		//Play back the events
		parseStyleEventInner(fc, events.iterator(), Arrays.asList(updated).iterator(), tp);
	}

	/**
	 * Builds a DOM over the style sub tree.
	 * 
	 * @param event
	 * @param input
	 * @param evr
	 * @param tp
	 * @return
	 * @throws XMLStreamException
	 */
	private void parseStyleEventInner(FormatterCore fc, Iterator<XMLEvent> input, Iterator<String> text, TextProperties tp) throws XMLStreamException {
		//Play back the events
		while (input.hasNext()) {
			XMLEvent event = input.next();
			if (event.isCharacters()) {
				fc.addChars(text.next(), tp);
			} else if (equalsStart(event, ObflQName.STYLE)) {
				parseStyleEventInner(fc, input, text, tp);
			} else if (equalsStart(event, ObflQName.MARKER)) {
				parseMarker(fc, event);
			} else if (equalsStart(event, ObflQName.BR)) {
				fc.newLine();
			} else if (equalsEnd(event, ObflQName.STYLE)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private TextAttribute processTextAttributes(XMLEvent style, Iterator<XMLEvent> events) throws XMLStreamException {
		int len = 0;
		String name = getAttr(style, "name");
		DefaultTextAttribute.Builder ret = new DefaultTextAttribute.Builder(name);
		while (events.hasNext()) {
			XMLEvent ev = events.next();
			if (ev.isCharacters()) {
				String sr = ev.asCharacters().getData();
				ret.add(new DefaultTextAttribute.Builder().build(sr.length()));
				len += sr.length();
			} else if (equalsStart(ev, ObflQName.STYLE)) {
				TextAttribute t = processTextAttributes(ev, events);
				ret.add(t);
				len += t.getWidth();
			} else if (equalsEnd(ev, ObflQName.STYLE)) {
				break;
			} else if (equalsStart(ev, ObflQName.MARKER)||equalsStart(ev, ObflQName.BR)) {
				// ignore
			} else if (equalsEnd(ev, ObflQName.MARKER)||equalsEnd(ev, ObflQName.BR)) {
				// ignore
			} else {
				name = "";
				if (ev.isEndElement()) {
					name = ev.asEndElement().getName().getLocalPart();
				}
				throw new UnsupportedOperationException("Unknown element: " + ev +" "+ name);
			}
		}
		return ret.build(len);
	}

	private BlockProperties blockBuilder(Iterator<Attribute> atts) {
		BlockProperties.Builder builder = new BlockProperties.Builder();
		HashMap<String, Object> border = new HashMap<String, Object>();
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
			} else if (name.equals("align")) {
				builder.align(FormattingTypes.Alignment.valueOf(att.getValue().toUpperCase()));
			} else if (name.equals("vertical-position")) {
				builder.verticalPosition(Position.parsePosition(att.getValue()));
			} else if (name.equals("vertical-align")) {
				builder.verticalAlignment(VerticalAlignment.valueOf(att.getValue().toUpperCase()));
			} else if (name.equals("row-spacing")) {
				builder.rowSpacing(Float.parseFloat(att.getValue()));
			} else if (name.startsWith("border")) {
				border.put(name, att.getValue());
			}
		}
		if (border.size()>0) {
			border.put(TextBorderFactory.FEATURE_MODE, mode);
			try {
				builder.textBorderStyle(maker.newTextBorderStyle(border));
			} catch (TextBorderConfigurationException e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Failed to add border to block properties: " + border, e);
			}
		}
		return builder.build();
	}
	
	private void parseLeader(FormatterCore fc, XMLEvent event, XMLEventReader input) throws XMLStreamException {
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
			} else {
				report(event);
			}
		}
		scanEmptyElement(input, ObflQName.LEADER);
		fc.insertLeader(builder.build());
	}

	private static void parseMarker(FormatterCore fc, XMLEvent event) throws XMLStreamException {
		String markerName = getAttr(event, "class");
		String markerValue = getAttr(event, "value");
		fc.insertMarker(new Marker(markerName, markerValue));
	}
	
	private String parseAnchor(XMLEvent event) {
		return getAttr(event, "item");
	}
	
	private void parseTableOfContents(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String tocName = getAttr(event, ObflQName.ATTR_NAME);
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		TableOfContents toc = formatter.newToc(tocName);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.TOC_ENTRY)) {
				parseTocEntry(event, input, toc, locale, hyph);
			} else if (equalsEnd(event, ObflQName.TABLE_OF_CONTENTS)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseCollection(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String id = getAttr(event, ObflQName.ATTR_NAME);
		ContentCollection coll = formatter.newCollection(id); 
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.ITEM)) {
				parseCollectionItem(event, input, coll, locale, hyph);
			} else if (equalsEnd(event, ObflQName.COLLECTION)) {
				break;
			} else {
				report(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseTocEntry(XMLEvent event, XMLEventReader input, TableOfContents toc, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String refId = getAttr(event, "ref-id");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		toc.startEntry(refId, blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				toc.addChars(event.asCharacters().getData(), new TextProperties.Builder(locale.toString()).hyphenate(hyph).build());
			} else if (equalsStart(event, ObflQName.TOC_ENTRY)) {
				parseTocEntry(event, input, toc, locale, hyph);
			} else if (processAsBlockContents(toc, event, input, locale, hyph)) {
				//done!
			}
			else if (equalsEnd(event, ObflQName.TOC_ENTRY)) {
				toc.endEntry();
				break;
			} else {
				report(event);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseCollectionItem(XMLEvent event, XMLEventReader input, ContentCollection coll, FilterLocale locale, boolean hyph) throws XMLStreamException {
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		coll.startItem(blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				coll.addChars(event.asCharacters().getData(), new TextProperties.Builder(locale.toString()).hyphenate(hyph).build());
			} else if (equalsStart(event, ObflQName.ITEM)) {
				parseCollectionItem(event, input, coll, locale, hyph);
				Logger.getLogger(this.getClass().getCanonicalName()).warning("Nested collection items.");
			} else if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, coll, locale, hyph);
			} else if (processAsBlockContents(coll, event, input, locale, hyph)) {
				//done!
			}
			else if (equalsEnd(event, ObflQName.ITEM)) {
				coll.endItem();
				break;
			} else {
				report(event);
			}
		}
	}

	private boolean processAsBlockContents(FormatterCore fc, XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		if (equalsStart(event, ObflQName.LEADER)) {
			parseLeader(fc, event, input);
			return true;
		} else if (equalsStart(event, ObflQName.MARKER)) {
			parseMarker(fc, event);
			return true;
		} else if (equalsStart(event, ObflQName.BR)) {
			fc.newLine();
			scanEmptyElement(input, ObflQName.BR);
			return true;
		} else if (equalsStart(event, ObflQName.EVALUATE)) {
			parseEvaluate(fc, event, input, locale, hyph);
			return true;
		} else if (equalsStart(event, ObflQName.STYLE)) {
			parseStyle(event, input, fc, locale, hyph);
			return true;
		} else if (equalsStart(event, ObflQName.SPAN)) {
			parseSpan(event, input, fc, locale, hyph);
			return true;
		} else if (equalsStart(event, ObflQName.ANCHOR)) {
			fc.insertAnchor(parseAnchor(event));
			return true;
		} else if (equalsStart(event, ObflQName.PAGE_NUMBER)) {
			parsePageNumber(fc, event, input);
			return true;
		}  else {
			return false;
		}
	}
	
	private void parsePageNumber(FormatterCore fc, XMLEvent event, XMLEventReader input) throws XMLStreamException {
		String refId = getAttr(event, "ref-id");
		NumeralStyle style = NumeralStyle.DEFAULT;
		String styleStr = getAttr(event, "style");
		if (styleStr!=null) {
			try {
				style = NumeralStyle.valueOf(styleStr.replace('-', '_').toUpperCase());
			} catch (Exception e) { }
		}
		scanEmptyElement(input, ObflQName.PAGE_NUMBER);
		fc.insertReference(refId, style);
	}
	
	private void parseEvaluate(FormatterCore fc, XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String expr = getAttr(event, "expression");
		scanEmptyElement(input, ObflQName.EVALUATE);
		OBFLDynamicContent dynamic = new OBFLDynamicContent(expr, ef, true);
		fc.insertEvaluate(dynamic, new TextProperties.Builder(locale.toString()).hyphenate(hyph).build());
	}
	
	private void parseVolumeTemplate(XMLEvent event, XMLEventReader input, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String useWhen = getAttr(event, ObflQName.ATTR_USE_WHEN);
		String splitterMax = getAttr(event, "sheets-in-volume-max");
		OBFLCondition condition = new OBFLCondition(useWhen, ef, false);
		condition.setVolumeCountVariable(getAttr(event, "volume-count-variable"));
		condition.setVolumeNumberVariable(getAttr(event, "volume-number-variable"));
		VolumeTemplateProperties vtp = new VolumeTemplateProperties.Builder(Integer.parseInt(splitterMax))
				.condition(condition)
				.build();
		VolumeTemplateBuilder template = formatter.newVolumeTemplate(vtp);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.PRE_CONTENT)) {
				parsePreVolumeContent(event, input, template.getPreVolumeContentBuilder(), locale, hyph);
			} else if (equalsStart(event, ObflQName.POST_CONTENT)) {
				parsePostVolumeContent(event, input, template.getPostVolumeContentBuilder(), locale, hyph);
			} else if (equalsEnd(event, ObflQName.VOLUME_TEMPLATE)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parsePreVolumeContent(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, FilterLocale locale, boolean hyph) throws XMLStreamException {
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.SEQUENCE)) {
				parseVolumeSequence(event, input, template, locale, hyph);
			} else if (equalsStart(event, ObflQName.TOC_SEQUENCE)) {
				parseTocSequence(event, input, template, locale, hyph);
			} else if (equalsStart(event, ObflQName.ITEM_SEQUENCE)) {
				parseItemSequence(event, input, template, locale, hyph);
			} else if (equalsEnd(event, ObflQName.PRE_CONTENT)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parsePostVolumeContent(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, FilterLocale locale, boolean hyph) throws XMLStreamException {
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.SEQUENCE)) {
				parseVolumeSequence(event, input, template, locale, hyph);
			} else if (equalsStart(event, ObflQName.ITEM_SEQUENCE)) {
				parseItemSequence(event, input, template, locale, hyph);
			} else if (equalsEnd(event, ObflQName.POST_CONTENT)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseVolumeSequence(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName);
		String initialPageNumber = getAttr(event, "initial-page-number");
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}
		template.newSequence(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, template, locale, hyph);
			} else if (equalsEnd(event, ObflQName.SEQUENCE)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseTocSequence(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		String tocName = getAttr(event, "toc");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		TocProperties.TocRange range = TocProperties.TocRange.valueOf(getAttr(event, "range").toUpperCase());
		TocProperties.Builder builder = new TocProperties.Builder(masterName, tocName, range);
		String initialPageNumber = getAttr(event, "initial-page-number");
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}

		template.newTocSequence(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.ON_TOC_START)) {
				template.newOnTocStart(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, true));
				parseOnEvent(event, input, template, ObflQName.ON_TOC_START, locale, hyph);
			} else if (equalsStart(event, ObflQName.ON_VOLUME_START)) {
				template.newOnVolumeStart(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, true));
				parseOnEvent(event, input, template, ObflQName.ON_VOLUME_START, locale, hyph);
			} else if (equalsStart(event, ObflQName.ON_VOLUME_END)) {
				template.newOnVolumeEnd(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, true));
				parseOnEvent(event, input, template, ObflQName.ON_VOLUME_END, locale, hyph);
			} else if (equalsStart(event, ObflQName.ON_TOC_END)) {
				template.newOnTocEnd(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, true));
				parseOnEvent(event, input, template, ObflQName.ON_TOC_END, locale, hyph);
			}
			else if (equalsEnd(event, ObflQName.TOC_SEQUENCE)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseItemSequence(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, FilterLocale locale, boolean hyph) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		String collection = getAttr(event, "collection");
		locale = getLang(event, locale);
		hyph = getHyphenate(event, hyph);
		ItemSequenceProperties.Builder builder = new ItemSequenceProperties.Builder(masterName, collection);
		String initialPageNumber = getAttr(event, "initial-page-number");
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}

		template.newItemSequence(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.ON_COLLECTION_START)) {
				template.newOnCollectionStart();
				parseOnEvent(event, input, template, ObflQName.ON_COLLECTION_START, locale, hyph);
			} else if (equalsStart(event, ObflQName.ON_PAGE_START)) {
				template.newOnPageStart();
				parseOnEvent(event, input, template, ObflQName.ON_PAGE_START, locale, hyph);
			} else if (equalsStart(event, ObflQName.ON_PAGE_END)) {
				template.newOnPageEnd();
				parseOnEvent(event, input, template, ObflQName.ON_PAGE_END, locale, hyph);
			} else if (equalsStart(event, ObflQName.ON_COLLECTION_END)) {
				template.newOnCollectionEnd();
				parseOnEvent(event, input, template, ObflQName.ON_COLLECTION_END, locale, hyph);
			}
			else if (equalsEnd(event, ObflQName.ITEM_SEQUENCE)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseOnEvent(XMLEvent event, XMLEventReader input, FormatterCore fc, QName end, FilterLocale locale, boolean hyph) throws XMLStreamException {
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, fc, locale, hyph);
			} else if (equalsEnd(event, end)) {
				break;
			} else {
				report(event);
			}
		}
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

	private static String getAttr(XMLEvent event, String attr) {
		return getAttr(event, new QName(attr));
	}
	
	private static String getAttr(XMLEvent event, QName attr) {
		Attribute ret = event.asStartElement().getAttributeByName(attr);
		if (ret==null) {
			return null;
		} else {
			return ret.getValue();
		}
	}
	
	private FilterLocale getLang(XMLEvent event, FilterLocale locale) {
		String lang = getAttr(event, ObflQName.ATTR_XML_LANG);
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
		String hyph = getAttr(event, ObflQName.ATTR_HYPHENATE);
		if (hyph!=null) {
			return hyph.equals("true");
		}
		return hyphenate;
	}
	
	public void writeResult(PagedMediaWriter writer) throws IOException {
		formatter.write(writer);
	}

	public List<MetaDataItem> getMetaData() {
		return meta;
	}

}
