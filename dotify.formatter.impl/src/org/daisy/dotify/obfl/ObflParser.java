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
import org.daisy.dotify.api.formatter.DynamicSequenceBuilder;
import org.daisy.dotify.api.formatter.Field;
import org.daisy.dotify.api.formatter.FieldList;
import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.formatter.FormatterSequence;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.ItemSequenceProperties;
import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.MarginRegion;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.MarkerIndicatorRegion;
import org.daisy.dotify.api.formatter.MarkerReferenceField;
import org.daisy.dotify.api.formatter.MarkerReferenceField.MarkerSearchDirection;
import org.daisy.dotify.api.formatter.MarkerReferenceField.MarkerSearchScope;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.PageAreaBuilder;
import org.daisy.dotify.api.formatter.PageAreaProperties;
import org.daisy.dotify.api.formatter.PageTemplateBuilder;
import org.daisy.dotify.api.formatter.Position;
import org.daisy.dotify.api.formatter.ReferenceListBuilder;
import org.daisy.dotify.api.formatter.RenameFallbackRule;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.StringField;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.TableProperties;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.api.formatter.VolumeContentBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.Border;
import org.daisy.dotify.api.translator.DefaultTextAttribute;
import org.daisy.dotify.api.translator.MarkerProcessor;
import org.daisy.dotify.api.translator.TextAttribute;
import org.daisy.dotify.api.translator.TextBorderConfigurationException;
import org.daisy.dotify.api.translator.TextBorderFactory;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.api.writer.MetaDataItem;
import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.common.text.FilterLocale;

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
	private final boolean hyphGlobal;
	private final FormatterFactory formatterFactory;
	private final MarkerProcessor mp;
	private final TextBorderFactoryMakerService maker;
	private final ExpressionFactory ef;
	private final Logger logger;

	public ObflParser(String locale, String mode, MarkerProcessor mp, FormatterFactory formatterFactory, TextBorderFactoryMakerService maker, ExpressionFactory ef) {
		this.locale = FilterLocale.parse(locale);
		this.mode = mode;
		//TODO: add this to input parameters
		this.hyphGlobal = true;
		this.formatterFactory = formatterFactory;
		this.mp = mp;
		this.maker = maker;
		this.ef = ef;
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
	}
	
	public void parse(XMLEventReader input) throws XMLStreamException, OBFLParserException {
		this.formatter = formatterFactory.newFormatter(locale.toString(), mode);
		//this.masters = new HashMap<String, LayoutMaster>();
		this.meta = new ArrayList<>();
		formatter.open();
		XMLEvent event;
		TextProperties tp = new TextProperties.Builder(this.locale.toString()).translationMode(mode).hyphenate(hyphGlobal).build();
		
		while (input.hasNext()) {
			event = input.nextEvent();
			if (equalsStart(event, ObflQName.OBFL)) {
				String loc = getAttr(event, ObflQName.ATTR_XML_LANG);
				if (loc==null) {
					throw new OBFLParserException("Missing xml:lang on root element");
				}
				tp = getTextProperties(event, tp);
			} else if (equalsStart(event, ObflQName.META)) {
				parseMeta(event, input);
			} else if (equalsStart(event, ObflQName.LAYOUT_MASTER)) {
				parseLayoutMaster(event, input);
			} else if (equalsStart(event, ObflQName.SEQUENCE)) {
				parseSequence(event, input, tp);
			} else if (equalsStart(event, ObflQName.TABLE_OF_CONTENTS)) {
				parseTableOfContents(event, input, tp);
			} else if (equalsStart(event, ObflQName.VOLUME_TEMPLATE)) {
				parseVolumeTemplate(event, input, tp);
			} else if (equalsStart(event, ObflQName.COLLECTION)) {
				parseCollection(event, input, tp);
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
		HashMap<String, Object> border = new HashMap<>();
		while (i.hasNext()) {
			Attribute atts = i.next();
			String name = atts.getName().getLocalPart();
			String value = atts.getValue();
			if ("inner-margin".equals(name)) {
				masterConfig.innerMargin(Integer.parseInt(value));
			} else if ("outer-margin".equals(name)) {
				masterConfig.outerMargin(Integer.parseInt(value));
			} else if ("row-spacing".equals(name)) {
				masterConfig.rowSpacing(Float.parseFloat(value));
			} else if ("duplex".equals(name)) {
				masterConfig.duplex("true".equals(value));
			}  else if (name.startsWith("border")) {
				border.put(name, value);
			}
		}
		if (!border.isEmpty()) {
			border.put(TextBorderFactory.FEATURE_MODE, mode);
			try {
				masterConfig.border(maker.newTextBorderStyle(border));
			} catch (TextBorderConfigurationException e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Failed to add border to block properties: " + border, e);
			}
		}
		LayoutMasterBuilder master = formatter.newLayoutMaster(masterName, masterConfig.build());
		Integer w = null;
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.TEMPLATE, ObflQName.DEFAULT_TEMPLATE)) {
				int wa = parseTemplate(master, event, input);
				if (w!=null && w!=wa) {
					throw new XMLStreamException("Due to a limitation in the implementation, "
							+ "the total width of all margin-regions must be the same in all templates. "
							+ "For more information, "
							+ "see https://github.com/joeha480/dotify/issues/148", event.getLocation());
				} else {
					w = wa;
				}
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
			if ("align".equals(name)) {
				config.align(PageAreaProperties.Alignment.valueOf(value.toUpperCase()));
			}
		}
		PageAreaBuilder builder = null;
		// Use global values here, because they are not inherited from anywhere
		TextProperties tp = new TextProperties.Builder(locale.toString()).translationMode(mode).hyphenate(hyphGlobal).build();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.FALLBACK)) {
				parseFallback(event, input, config);
			} else if (equalsStart(event, ObflQName.BEFORE)) {
				if (builder==null) { builder = master.setPageArea(config.build()); }
				parseBeforeAfter(event, input, builder.getBeforeArea(), tp);
			} else if (equalsStart(event, ObflQName.AFTER)) {
				if (builder==null) { builder = master.setPageArea(config.build()); }
				parseBeforeAfter(event, input, builder.getAfterArea(), tp);
			} else if (equalsEnd(event, ObflQName.PAGE_AREA)) {
				if (builder==null) { builder = master.setPageArea(config.build()); }
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseFallback(XMLEvent event, XMLEventReader input, PageAreaProperties.Builder pap) throws XMLStreamException {
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.RENAME)) {
				parseRename(event, input, pap);
			}
			else if (equalsEnd(event, ObflQName.FALLBACK)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseRename(XMLEvent event, XMLEventReader input, PageAreaProperties.Builder pap) throws XMLStreamException {
		String from = getAttr(event, "collection");
		String to = getAttr(event, "to");
		pap.addFallback(new RenameFallbackRule(from, to));
		scanEmptyElement(input, ObflQName.RENAME);
	}

	private void parseBeforeAfter(XMLEvent event, XMLEventReader input, FormatterCore fc, TextProperties tp) throws XMLStreamException {
		tp = getTextProperties(event, tp);
		fc.startBlock(blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				fc.addChars(event.asCharacters().getData(), tp);
			} else if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, fc, tp);
			} else if (processAsBlockContents(fc, event, input, tp)) {
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
	
	private int parseTemplate(LayoutMasterBuilder master, XMLEvent event, XMLEventReader input) throws XMLStreamException {
		PageTemplateBuilder template;
		if (equalsStart(event, ObflQName.TEMPLATE)) {
			template = master.newTemplate(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, false));
		} else {
			template = master.newTemplate(null);
		}
		int width = 0;
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
			} else if (equalsStart(event, ObflQName.MARGIN_REGION)) {
				String align = getAttr(event, ObflQName.ATTR_ALIGN);
				MarginRegion region = parseMarginRegion(event, input);
				width += region.getWidth();
				if ("right".equals(align.toLowerCase())) {
					template.addToRightMargin(region);
				} else {
					template.addToLeftMargin(region);
				}
			}
			else if (equalsEnd(event, ObflQName.TEMPLATE) || equalsEnd(event, ObflQName.DEFAULT_TEMPLATE)) {
				break;
			} else {
				report(event);
			}
		}
		return width;
	}
	
	private FieldList parseHeaderFooter(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		@SuppressWarnings("unchecked")
		Iterator<Attribute> i = event.asStartElement().getAttributes();
		Float rowSpacing = null;
		while (i.hasNext()) {
			Attribute atts = i.next();
			String name = atts.getName().getLocalPart();
			String value = atts.getValue();
			if ("row-spacing".equals(name)) {
				rowSpacing = Float.parseFloat(value);
			}
		}
		ArrayList<Field> fields = new ArrayList<>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.FIELD)) {
				String textStyle = getAttr(event, ObflQName.ATTR_TEXT_STYLE);
				ArrayList<Field> compound = parseField(event, input);
				if (compound.size()==1) {
					fields.add(compound.get(0));
				} else {
					CompoundField f = new CompoundField(textStyle);
					f.addAll(compound);
					fields.add(f);
				}
			} else if (equalsEnd(event, ObflQName.HEADER) || equalsEnd(event, ObflQName.FOOTER)) {
				break;
			} else {
				report(event);
			}
		}
		if (!fields.isEmpty()) {
			return new FieldList.Builder(fields).rowSpacing(rowSpacing).build();
		} else {
			return null;
		}
	}
	
	private ArrayList<Field> parseField(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		ArrayList<Field> compound = new ArrayList<>();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.STRING)) {
				compound.add(new StringField(getAttr(event, "value"), getAttr(event, ObflQName.ATTR_TEXT_STYLE)));
			} else if (equalsStart(event, ObflQName.EVALUATE)) {
				//FIXME: add variables...
				compound.add(new StringField(ef.newExpression().evaluate(getAttr(event, "expression")), getAttr(event, ObflQName.ATTR_TEXT_STYLE)));
			} else if (equalsStart(event, ObflQName.CURRENT_PAGE)) {
				compound.add(new CurrentPageField(getNumeralStyle(event), getAttr(event, ObflQName.ATTR_TEXT_STYLE)));
			} else if (equalsStart(event, ObflQName.MARKER_REFERENCE)) {
				compound.add(
					new MarkerReferenceField(
							getAttr(event, ObflQName.ATTR_MARKER), 
							MarkerSearchDirection.valueOf(getAttr(event, "direction").toUpperCase()),
							MarkerSearchScope.valueOf(getAttr(event, "scope").replace('-', '_').toUpperCase()),
							getAttr(event, ObflQName.ATTR_TEXT_STYLE),
							toInt(getAttr(event, ObflQName.ATTR_START_OFFSET), 0)
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
	
	private MarginRegion parseMarginRegion(XMLEvent event, XMLEventReader input) throws XMLStreamException {
		int width = 1;
		String value = getAttr(event, ObflQName.ATTR_WIDTH);
		try {
			if (value!=null) {
				width = Integer.parseInt(value);
			}
		} catch (NumberFormatException e) {
			warning(event, "Failed to parse integer: " + value);
		}
		MarginRegion ret = null;
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.INDICATORS)) {
				//this element is optional, it can only occur once
				ret = parseIndicatorRegion(event, input, width);
			} else if (equalsEnd(event, ObflQName.MARGIN_REGION)) {
				break;
			} else {
				report(event);
			}
		}
		return ret;
	}
	
	private MarginRegion parseIndicatorRegion(XMLEvent event, XMLEventReader input, int width) throws XMLStreamException {
		MarkerIndicatorRegion.Builder builder = MarkerIndicatorRegion.ofWidth(width);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.MARKER_INDICATOR)) {
				String markers = getAttr(event, "markers");
				String indicator = getAttr(event, "indicator");
				if (markers==null||"".equals(markers)) {
					warning(event, "@markers missing / has no value");
				} else {
					if (indicator==null||"".equals(indicator)) {
						warning(event, "@indicator missing / has no value");
					} else {
						String[] names = markers.split("\\s+");
						for (String name : names) {
							if (!"".equals(name)) {
								builder.addIndicator(name, indicator);
							}
						}
					}
				}
				scanEmptyElement(input, ObflQName.MARKER_INDICATOR);
			} else if (equalsEnd(event, ObflQName.INDICATORS)) {
				break;
			} else {
				report(event);
			}
		}
		return builder.build();
	}
	
	private int toInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	

	private void parseSequence(XMLEvent event, XMLEventReader input, TextProperties tp) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		tp = getTextProperties(event, tp);
		SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName);
		String initialPageNumber = getAttr(event, ObflQName.ATTR_INITIAL_PAGE_NUMBER);
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}
		FormatterSequence seq = formatter.newSequence(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, seq, tp);
			}/* else if (equalsStart(event, LEADER)) {
				parseLeader(event, input);
			}*/
			else if (equalsStart(event, ObflQName.TABLE)) {
				parseTable(event, input, seq, tp);
			}
			else if (equalsEnd(event, ObflQName.SEQUENCE)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseBlock(XMLEvent event, XMLEventReader input, FormatterCore fc, TextProperties tp) throws XMLStreamException {
		tp = getTextProperties(event, tp);
		fc.startBlock(blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				fc.addChars(event.asCharacters().getData(), tp);
			} else if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, fc, tp);
			} else if (processAsBlockContents(fc, event, input, tp)) {
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
	
	private void parseSpan(XMLEvent event, XMLEventReader input, FormatterCore fc, TextProperties tp) throws XMLStreamException {
		tp = getTextProperties(event, tp);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				fc.addChars(event.asCharacters().getData(), tp);
			} else if (equalsStart(event, ObflQName.STYLE)) {
				parseStyle(event, input, fc, tp);
			} else if (equalsStart(event, ObflQName.LEADER)) {
				parseLeader(fc, event, input);
			} else if (equalsStart(event, ObflQName.MARKER)) {
				parseMarker(fc, event);
			} else if (equalsStart(event, ObflQName.BR)) {
				fc.newLine();
				scanEmptyElement(input, ObflQName.BR);
			} else if (equalsStart(event, ObflQName.ANCHOR)) {
				fc.insertAnchor(parseAnchor(event));
			}
			else if (equalsEnd(event, ObflQName.SPAN)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseStyle(XMLEvent event, XMLEventReader input, FormatterCore fc, TextProperties tp) throws XMLStreamException {
		parseStyleEvent(fc, event, input, tp);
	}

	private void parseStyleEvent(FormatterCore fc, XMLEvent ev, XMLEventReader input, TextProperties tp) throws XMLStreamException {
		//Buffer events and extract text
		List<XMLEvent> events = new ArrayList<>();
		List<String> chunks = new ArrayList<>();
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
				if (level == 0) {
					break;
				}
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
			} else if (equalsStart(event, ObflQName.ANCHOR)) {
				fc.insertAnchor(parseAnchor(event));
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
			} else if (equalsStart(ev, ObflQName.MARKER, ObflQName.BR, ObflQName.ANCHOR)) {
				// ignore
			} else if (equalsEnd(ev, ObflQName.MARKER, ObflQName.BR, ObflQName.ANCHOR)) {
				// ignore
			} else {
				name = "";
				if (ev.isEndElement()) {
					name = ev.asEndElement().getName().getLocalPart();
				} else if (ev.isStartElement()) {
					name = ev.asStartElement().getName().getLocalPart();
				}
				throw new UnsupportedOperationException("Unknown element: " + ev +" "+ name);
			}
		}
		return ret.build(len);
	}

	private BlockProperties blockBuilder(Iterator<?> atts) {
		BlockProperties.Builder builder = new BlockProperties.Builder();
		HashMap<String, Object> border = new HashMap<>();
		while (atts.hasNext()) {
			Attribute att = (Attribute)atts.next();
			String name = att.getName().getLocalPart();
			if ("margin-left".equals(name)) {
				builder.leftMargin(Integer.parseInt(att.getValue()));
			} else if ("margin-right".equals(name)) {
				builder.rightMargin(Integer.parseInt(att.getValue()));
			} else if ("margin-top".equals(name)) {
				builder.topMargin(Integer.parseInt(att.getValue()));
			} else if ("margin-bottom".equals(name)) {
				builder.bottomMargin(Integer.parseInt(att.getValue()));
			} else if ("padding-left".equals(name)) {
				builder.leftPadding(Integer.parseInt(att.getValue()));
			} else if ("padding-right".equals(name)) {
				builder.rightPadding(Integer.parseInt(att.getValue()));
			} else if ("padding-top".equals(name)) {
				builder.topPadding(Integer.parseInt(att.getValue()));
			} else if ("padding-bottom".equals(name)) {
				builder.bottomPadding(Integer.parseInt(att.getValue()));
			} else if ("text-indent".equals(name)) {
				builder.textIndent(Integer.parseInt(att.getValue()));
			} else if ("first-line-indent".equals(name)) {
				builder.firstLineIndent(Integer.parseInt(att.getValue()));
			} else if ("list-type".equals(name)) {
				builder.listType(FormattingTypes.ListStyle.valueOf(att.getValue().toUpperCase()));
			} else if ("break-before".equals(name)) {
				builder.breakBefore(FormattingTypes.BreakBefore.valueOf(att.getValue().toUpperCase()));
			} else if ("keep".equals(name)) {
				builder.keep(FormattingTypes.Keep.valueOf(att.getValue().toUpperCase()));
			} else if ("orphans".equals(name)) {
				builder.orphans(Integer.parseInt(att.getValue()));
			} else if ("widows".equals(name)) {
				builder.widows(Integer.parseInt(att.getValue()));
			} else if ("keep-with-next".equals(name)) {
				builder.keepWithNext(Integer.parseInt(att.getValue()));
			} else if ("keep-with-previous-sheets".equals(name)) {
				builder.keepWithPreviousSheets(Integer.parseInt(att.getValue()));
			} else if ("keep-with-next-sheets".equals(name)) {
				builder.keepWithNextSheets(Integer.parseInt(att.getValue()));
			} else if ("block-indent".equals(name)) {
				builder.blockIndent(Integer.parseInt(att.getValue()));
			} else if ("id".equals(name)) {
				builder.identifier(att.getValue());
			} else if ("align".equals(name)) {
				builder.align(FormattingTypes.Alignment.valueOf(att.getValue().toUpperCase()));
			} else if ("vertical-position".equals(name)) {
				builder.verticalPosition(Position.parsePosition(att.getValue()));
			} else if ("vertical-align".equals(name)) {
				builder.verticalAlignment(VerticalAlignment.valueOf(att.getValue().toUpperCase()));
			} else if ("row-spacing".equals(name)) {
				builder.rowSpacing(Float.parseFloat(att.getValue()));
			} else if (name.startsWith("border")) {
				border.put(name, att.getValue());
			}
		}
		if (!border.isEmpty()) {
			border.put(TextBorderFactory.FEATURE_MODE, mode);
			try {
				builder.textBorderStyle(maker.newTextBorderStyle(border));
			} catch (TextBorderConfigurationException e) {
				logger.log(Level.WARNING, "Failed to add border to block properties: " + border, e);
			}
		}
		return builder.build();
	}
	
	private Border borderBuilder(Iterator<?> atts) {
		BorderBuilder builder = new BorderBuilder();
		while (atts.hasNext()) {
			Attribute att = (Attribute)atts.next();
			String name = att.getName().getLocalPart();
			if (name.startsWith("border")) {
				builder.put(name, att.getValue());
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
			if ("align".equals(name)) {
				builder.align(Leader.Alignment.valueOf(att.getValue().toUpperCase()));
			} else if ("position".equals(name)) {
				builder.position(Position.parsePosition(att.getValue()));
			} else if ("pattern".equals(name)) {
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
	
	private void parseTable(XMLEvent event, XMLEventReader input, FormatterSequence fc, TextProperties tp) throws XMLStreamException {
		int tableColSpacing = toInt(getAttr(event, ObflQName.ATTR_TABLE_COL_SPACING), 0);
		int tableRowSpacing = toInt(getAttr(event, ObflQName.ATTR_TABLE_ROW_SPACING), 0);
		BlockProperties bp = blockBuilder(event.asStartElement().getAttributes());
		Border b = borderBuilder(event.asStartElement().getAttributes());
		TableProperties.Builder tableProps = new TableProperties.Builder()
				.tableColSpacing(tableColSpacing)
				.tableRowSpacing(tableRowSpacing)
				.margin(bp.getMargin())
				.padding(bp.getPadding())
				.border(b);
		fc.startTable(tableProps.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.THEAD)) {
				parseTHeadTBody(event, input, fc, tp);
			} else if (equalsStart(event, ObflQName.TBODY)) {
				parseTHeadTBody(event, input, fc, tp);
			} else if (equalsStart(event, ObflQName.TR)) {
				parseTR(event, input, fc, tp);
			}
			else if (equalsEnd(event, ObflQName.TABLE)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseTHeadTBody(XMLEvent event, XMLEventReader input, FormatterSequence fc, TextProperties tp) throws XMLStreamException {
		if (equalsStart(event, ObflQName.THEAD)) {
			fc.beginsTableHeader();
		} else {
			fc.beginsTableBody();
		}
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.TR)) {
				parseTR(event, input, fc, tp);
			} else if (equalsEnd(event, ObflQName.THEAD, ObflQName.TBODY)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseTR(XMLEvent event, XMLEventReader input, FormatterSequence fc, TextProperties tp) throws XMLStreamException {
		fc.beginsTableRow();
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.TD)) {
				parseTD(event, input, fc, tp);
			}
			else if (equalsEnd(event, ObflQName.TR)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseTD(XMLEvent event, XMLEventReader input, FormatterSequence fs, TextProperties tp) throws XMLStreamException {
		tp = getTextProperties(event, tp);
		int colSpan = toInt(getAttr(event, ObflQName.ATTR_COL_SPAN), 1);
		int rowSpan = toInt(getAttr(event, ObflQName.ATTR_ROW_SPAN), 1);
		BlockProperties bp = blockBuilder(event.asStartElement().getAttributes());
		Border b = borderBuilder(event.asStartElement().getAttributes());
		TableCellProperties tcp = new TableCellProperties.Builder()
				.colSpan(colSpan)
				.rowSpan(rowSpan)
				.padding(bp.getPadding())
				.textBlockProperties(bp.getTextBlockProperties())
				.border(b)
				.build();
		FormatterCore fc = fs.beginsTableCell(tcp);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				fc.addChars(event.asCharacters().getData(), tp);
			} else if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, fc, tp);
			} else if (processAsBlockContents(fc, event, input, tp)) {
				//done
			}
			else if (equalsEnd(event, ObflQName.TD)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseTableOfContents(XMLEvent event, XMLEventReader input, TextProperties tp) throws XMLStreamException {
		String tocName = getAttr(event, ObflQName.ATTR_NAME);
		tp = getTextProperties(event, tp);
		TableOfContents toc = formatter.newToc(tocName);
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.TOC_ENTRY)) {
				parseTocEntry(event, input, toc, tp);
			} else if (equalsEnd(event, ObflQName.TABLE_OF_CONTENTS)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseCollection(XMLEvent event, XMLEventReader input, TextProperties tp) throws XMLStreamException {
		String id = getAttr(event, ObflQName.ATTR_NAME);
		ContentCollection coll = formatter.newCollection(id); 
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.ITEM)) {
				parseCollectionItem(event, input, coll, tp);
			} else if (equalsEnd(event, ObflQName.COLLECTION)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseTocEntry(XMLEvent event, XMLEventReader input, TableOfContents toc, TextProperties tp) throws XMLStreamException {
		String refId = getAttr(event, "ref-id");
		tp = getTextProperties(event, tp);
		toc.startEntry(refId, blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				toc.addChars(event.asCharacters().getData(), tp);
			} else if (equalsStart(event, ObflQName.TOC_ENTRY)) {
				parseTocEntry(event, input, toc, tp);
			} else if (processAsBlockContents(toc, event, input, tp)) {
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

	private void parseCollectionItem(XMLEvent event, XMLEventReader input, ContentCollection coll, TextProperties tp) throws XMLStreamException {
		tp = getTextProperties(event, tp);
		coll.startItem(blockBuilder(event.asStartElement().getAttributes()));
		while (input.hasNext()) {
			event=input.nextEvent();
			if (event.isCharacters()) {
				coll.addChars(event.asCharacters().getData(), tp);
			} else if (equalsStart(event, ObflQName.ITEM)) {
				parseCollectionItem(event, input, coll, tp);
				Logger.getLogger(this.getClass().getCanonicalName()).warning("Nested collection items.");
			} else if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, coll, tp);
			} else if (processAsBlockContents(coll, event, input, tp)) {
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

	private boolean processAsBlockContents(FormatterCore fc, XMLEvent event, XMLEventReader input, TextProperties tp) throws XMLStreamException {
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
			parseEvaluate(fc, event, input, tp);
			return true;
		} else if (equalsStart(event, ObflQName.STYLE)) {
			parseStyle(event, input, fc, tp);
			return true;
		} else if (equalsStart(event, ObflQName.SPAN)) {
			parseSpan(event, input, fc, tp);
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
	
	private NumeralStyle getNumeralStyle(XMLEvent event) {
		NumeralStyle style = NumeralStyle.DEFAULT;
		String styleStr = getAttr(event, "style");
		if (styleStr!=null) {
			logger.warning("@style has been deprecated. Use @number-format instead." + toLocation(event));
		} else {
			styleStr = getAttr(event, "number-format");
		}
		try {
			style = NumeralStyle.valueOf(styleStr.replace('-', '_').toUpperCase());
		} catch (Exception e) { 
			if (styleStr!=null) {
				logger.warning("Unsupported value '" + styleStr + "'" + toLocation(event));
			}
		}

		return style;
	}
	
	private String toLocation(XMLEvent event) {
		Location l = event.getLocation();
		StringBuilder sb = new StringBuilder();
		if (l.getLineNumber()>-1) {
			sb.append("line: ").append(l.getLineNumber());
		}
		if (l.getColumnNumber()>-1) {
			if (sb.length()>0) {
				sb.append(", ");
			}
			sb.append("column: ").append(l.getColumnNumber());
		}
		return (sb.length()>0?" (at "+sb.toString()+")":"");
	}
	
	private void parsePageNumber(FormatterCore fc, XMLEvent event, XMLEventReader input) throws XMLStreamException {
		String refId = getAttr(event, "ref-id");
		NumeralStyle style = getNumeralStyle(event);
		scanEmptyElement(input, ObflQName.PAGE_NUMBER);
		fc.insertReference(refId, style);
	}
	
	private void parseEvaluate(FormatterCore fc, XMLEvent event, XMLEventReader input, TextProperties tp) throws XMLStreamException {
		String expr = getAttr(event, "expression");
		scanEmptyElement(input, ObflQName.EVALUATE);
		OBFLDynamicContent dynamic = new OBFLDynamicContent(expr, ef, true);
		fc.insertEvaluate(dynamic, tp);
	}
	
	private void parseVolumeTemplate(XMLEvent event, XMLEventReader input, TextProperties tp) throws XMLStreamException {
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
				parsePreVolumeContent(event, input, template.getPreVolumeContentBuilder(), tp);
			} else if (equalsStart(event, ObflQName.POST_CONTENT)) {
				parsePostVolumeContent(event, input, template.getPostVolumeContentBuilder(), tp);
			} else if (equalsEnd(event, ObflQName.VOLUME_TEMPLATE)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parsePreVolumeContent(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, TextProperties tp) throws XMLStreamException {
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.SEQUENCE)) {
				parseVolumeSequence(event, input, template, tp);
			} else if (equalsStart(event, ObflQName.TOC_SEQUENCE)) {
				parseTocSequence(event, input, template, tp);
			} else if (equalsStart(event, ObflQName.DYNAMIC_SEQUENCE)) {
				parseItemSequence(event, input, template, tp);
			} else if (equalsEnd(event, ObflQName.PRE_CONTENT)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parsePostVolumeContent(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, TextProperties tp) throws XMLStreamException {
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.SEQUENCE)) {
				parseVolumeSequence(event, input, template, tp);
			} else if (equalsStart(event, ObflQName.DYNAMIC_SEQUENCE)) {
				parseItemSequence(event, input, template, tp);
			} else if (equalsEnd(event, ObflQName.POST_CONTENT)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseVolumeSequence(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, TextProperties tp) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		tp = getTextProperties(event, tp);
		SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName);
		String initialPageNumber = getAttr(event, ObflQName.ATTR_INITIAL_PAGE_NUMBER);
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}
		template.newSequence(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, template, tp);
			} else if (equalsEnd(event, ObflQName.SEQUENCE)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseTocSequence(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, TextProperties tp) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		String tocName = getAttr(event, "toc");
		tp = getTextProperties(event, tp);
		TocProperties.TocRange range = TocProperties.TocRange.valueOf(getAttr(event, "range").toUpperCase());
		TocProperties.Builder builder = new TocProperties.Builder(masterName, tocName, range);
		String initialPageNumber = getAttr(event, ObflQName.ATTR_INITIAL_PAGE_NUMBER);
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}

		template.newTocSequence(builder.build());
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.ON_TOC_START)) {
				template.newOnTocStart(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, true));
				parseOnEvent(event, input, template, ObflQName.ON_TOC_START, tp);
			} else if (equalsStart(event, ObflQName.ON_VOLUME_START)) {
				template.newOnVolumeStart(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, true));
				parseOnEvent(event, input, template, ObflQName.ON_VOLUME_START, tp);
			} else if (equalsStart(event, ObflQName.ON_VOLUME_END)) {
				template.newOnVolumeEnd(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, true));
				parseOnEvent(event, input, template, ObflQName.ON_VOLUME_END, tp);
			} else if (equalsStart(event, ObflQName.ON_TOC_END)) {
				template.newOnTocEnd(new OBFLCondition(getAttr(event, ObflQName.ATTR_USE_WHEN), ef, true));
				parseOnEvent(event, input, template, ObflQName.ON_TOC_END, tp);
			}
			else if (equalsEnd(event, ObflQName.TOC_SEQUENCE)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseItemSequence(XMLEvent event, XMLEventReader input, VolumeContentBuilder template, TextProperties tp) throws XMLStreamException {
		String masterName = getAttr(event, "master");
		tp = getTextProperties(event, tp);
		SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName);
		String initialPageNumber = getAttr(event, ObflQName.ATTR_INITIAL_PAGE_NUMBER);
		if (initialPageNumber!=null) {
			builder.initialPageNumber(Integer.parseInt(initialPageNumber));
		}

		DynamicSequenceBuilder dsb = template.newDynamicSequence(builder.build());
		FormatterCore context = null;
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.INSERT_REFS_LIST)) {
				parseRefsList(event, input, dsb, tp);
				context = null;
			} else if (equalsStart(event, ObflQName.BLOCK)) {
				if (context == null) {
					 context = dsb.newStaticContext();
				}
				parseBlock(event, input, context, tp);
			}
			else if (equalsEnd(event, ObflQName.DYNAMIC_SEQUENCE)) {
				break;
			} else {
				report(event);
			}
		}
	}
	
	private void parseRefsList(XMLEvent event, XMLEventReader input, DynamicSequenceBuilder dsb, TextProperties tp) throws XMLStreamException {
		String collection = getAttr(event, "collection");
		tp = getTextProperties(event, tp);
		ItemSequenceProperties.Range range = ItemSequenceProperties.Range.valueOf(getAttr(event, "range").toUpperCase());
		ItemSequenceProperties.Builder builder = new ItemSequenceProperties.Builder(collection, range);
		
		ReferenceListBuilder rlb = dsb.newReferencesListContext(builder.build());

		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.ON_PAGE_START)) {
				parseOnEvent(event, input, rlb.newOnPageStart(), ObflQName.ON_PAGE_START, tp);
			} else if (equalsStart(event, ObflQName.ON_PAGE_END)) {
				parseOnEvent(event, input, rlb.newOnPageEnd(), ObflQName.ON_PAGE_END, tp);
			} else if (equalsStart(event, ObflQName.ON_COLLECTION_START)) {
				parseOnEvent(event, input, rlb.newOnCollectionStart(), ObflQName.ON_COLLECTION_START, tp);
			} else if (equalsStart(event, ObflQName.ON_COLLECTION_END)) {
				parseOnEvent(event, input, rlb.newOnCollectionEnd(), ObflQName.ON_COLLECTION_END, tp);
			}
			else if (equalsEnd(event, ObflQName.INSERT_REFS_LIST)) {
				break;
			} else {
				report(event);
			}
		}
	}

	private void parseOnEvent(XMLEvent event, XMLEventReader input, FormatterCore fc, QName end, TextProperties tp) throws XMLStreamException {
		while (input.hasNext()) {
			event=input.nextEvent();
			if (equalsStart(event, ObflQName.BLOCK)) {
				parseBlock(event, input, fc, tp);
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
	
	private TextProperties getTextProperties(XMLEvent event, TextProperties defaults) {
		String loc = getLang(event, defaults.getLocale());
		boolean hyph = getHyphenate(event, defaults.isHyphenating());
		String trans = getTranslate(event, defaults.getTranslationMode());
		return new TextProperties.Builder(loc.toString()).translationMode(trans).hyphenate(hyph).build();
	}
	
	private String getLang(XMLEvent event, String locale) {
		String lang = getAttr(event, ObflQName.ATTR_XML_LANG);
		if (lang!=null) {
			if ("".equals(lang)) {
				return null;
			} else {
				//we're doing the parsing only to get the validation
				return FilterLocale.parse(lang).toString();
			}
		}
		return locale;
	}

	private boolean getHyphenate(XMLEvent event, boolean hyphenate) {
		String hyph = getAttr(event, ObflQName.ATTR_HYPHENATE);
		if (hyph!=null) {
			if ("".equals(hyph)) {
				return hyphGlobal;
			} else {
				return "true".equals(hyph);
			}
		}
		return hyphenate;
	}

	private String getTranslate(XMLEvent event, String translate) {
		String tr = getAttr(event, ObflQName.ATTR_TRANSLATE);
		if (tr!=null) {
			if ("".equals(tr)) {
				return mode;
			} else {
				return tr;
			}
		}
		return translate;
	}
	
	public void writeResult(PagedMediaWriter writer) throws IOException {
		formatter.write(writer);
	}

	public List<MetaDataItem> getMetaData() {
		return meta;
	}

}
