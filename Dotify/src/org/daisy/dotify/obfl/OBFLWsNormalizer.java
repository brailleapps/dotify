package org.daisy.dotify.obfl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;

public class OBFLWsNormalizer {
	private final XMLEventReader input;
	private final OutputStream out;
	private final XMLEventFactory eventFactory;
	private XMLEventWriter writer;
	private final Pattern beginWS;
	private final Pattern endWS;

	public OBFLWsNormalizer(InputStream stream, OutputStream out) throws XMLStreamException {
		XMLInputFactory inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
		inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
		inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		this.input = inFactory.createXMLEventReader(stream);
		this.writer = null;
		this.out = out;
		this.eventFactory = XMLEventFactory.newInstance();
		beginWS = Pattern.compile("\\A\\s+");
		endWS = Pattern.compile("\\s+\\z");
	}

	public void parse() {
		XMLEvent event;
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		while (input.hasNext()) {
			try {
				event = input.nextEvent();
				if (event.getEventType() == XMLStreamConstants.START_DOCUMENT) {
					StartDocument sd = (StartDocument) event;
					if (sd.encodingSet()) {
						writer = outputFactory.createXMLEventWriter(out, sd.getCharacterEncodingScheme());
						writer.add(event);
					} else {
						writer = outputFactory.createXMLEventWriter(out, "utf-8");
						writer.add(eventFactory.createStartDocument("utf-8", "1.0"));
					}
				} else if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
					writer.add(eventFactory.createCharacters(normalizeSpace(event.asCharacters().getData())));
				} else if (equalsStart(event, ObflQName.BLOCK, ObflQName.TOC_ENTRY)) {
					parseBlock(event);
				} else {
					writer.add(event);
				}
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		try {
			input.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	private void parseBlock(XMLEvent event) throws XMLStreamException {
		QName end = event.asStartElement().getName();
		List<XMLEvent> events = new ArrayList<XMLEvent>();
		events.add(event);
		while (input.hasNext()) {
			event = input.nextEvent();
			if (equalsStart(event, ObflQName.BLOCK, ObflQName.TOC_ENTRY)) {
				processList(events);
				events.clear();
				parseBlock(event);
			} else if (equalsEnd(event, end)) {
				events.add(event);
				processList(events);
				break;
			} else {
				events.add(event);
			}
		}
	}

	private void processList(List<XMLEvent> events) throws XMLStreamException {
		// System.out.println(events.size());
		List<XMLEvent> modified = new ArrayList<XMLEvent>();
		// process
		for (int i = 0; i < events.size(); i++) {
			XMLEvent event = events.get(i);

			if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
				String data = event.asCharacters().getData();

				String pre = "";
				String post = "";

				if (normalizeSpace(data).equals("") && ((i == events.size() - 2 && equalsEnd(events.get(i + 1), ObflQName.BLOCK, ObflQName.TOC_ENTRY)) || i == events.size() - 1)) {
					// this is the last element in the block, ignore
				} else if (i > 0) {
					XMLEvent preceedingEvent = events.get(i - 1);
					if (preceedingEvent.isEndElement() && beginWS.matcher(data).find() && isPreserveElement(preceedingEvent.asEndElement().getName())) {
						pre = " ";
					} else if (equalsEnd(preceedingEvent, ObflQName.SPAN, ObflQName.STYLE) && beginWS.matcher(data).find()) {
						pre = " ";
					} else if (equalsEnd(preceedingEvent, ObflQName.MARKER, ObflQName.ANCHOR)) {
						if (beginWS.matcher(data).find()) {
							pre = " ";
						} else {
							int j = untilEventIsNotBackward(events, i - 1, ObflQName.MARKER, ObflQName.ANCHOR);
							if (j > -1) {
								XMLEvent upstream = events.get(j);
								if (upstream.isCharacters() && endWS.matcher(upstream.asCharacters().getData()).find()) {
									pre = " ";
								}
							}
						}
					} else if (preceedingEvent.isEndElement()) {
						int j = untilEventIsNotBackward(events, i - 1, XMLStreamConstants.END_ELEMENT);
						if (j > -1) {
							XMLEvent upstream = events.get(j);
							if (upstream.isCharacters() && endWS.matcher(upstream.asCharacters().getData()).find()) {
								pre = " ";
							}
						}
					}

				}
				if (i < events.size() - 1) {
					XMLEvent followingEvent = events.get(i + 1);
					if (normalizeSpace(data).equals("")) {
						// don't output post
						if (equalsStart(followingEvent, ObflQName.MARKER)) {
							pre = "";
						}
					} else if (followingEvent.isStartElement() && endWS.matcher(data).find() && isPreserveElement(followingEvent.asStartElement().getName())) {
						post = " ";
					} else if ((equalsStart(followingEvent, ObflQName.SPAN, ObflQName.STYLE)) && endWS.matcher(data).find()) {
						post = " ";
					} else if (followingEvent.isStartElement()) {
						int j = untilEventIsNotForward(events, i + 1, XMLStreamConstants.START_ELEMENT);
						if (j > -1) {
							XMLEvent downstream = events.get(j);
							if (downstream.isCharacters() && beginWS.matcher(downstream.asCharacters().getData()).find()) {
								post = " ";
							}
						}
					}
				}
				// System.out.println("'" + pre + "'" + normalizeSpace(data) +
				// "'" + post + "'");
				modified.add(eventFactory.createCharacters(pre + normalizeSpace(data) + post));
			} else if (equalsStart(event, ObflQName.SPAN, ObflQName.STYLE)) {

				if (i > 0) {
					int j = untilEventIsNotBackward(events, i - 1, ObflQName.MARKER, ObflQName.ANCHOR);
					if (!(j > -1 && j < i - 1)) {
						j = untilEventIsNotBackward(events, i - 1, XMLStreamConstants.END_ELEMENT);
					}
					if (j > -1 && j < i - 1) {
						XMLEvent upstream = events.get(j);
						if (upstream.isCharacters() && endWS.matcher(upstream.asCharacters().getData()).find()) {
							modified.add(eventFactory.createCharacters(" "));
						}
					}
				}

				modified.add(event);

			} else if (equalsEnd(event, ObflQName.SPAN, ObflQName.STYLE)) {
				modified.add(event);
				if (i < events.size() - 1) {
					int j = untilEventIsNotForward(events, i + 1, XMLStreamConstants.START_ELEMENT);
					if (j > -1 && j > i + 1) {
						XMLEvent downstream = events.get(j);
						if (downstream.isCharacters() && beginWS.matcher(downstream.asCharacters().getData()).find()) {
							modified.add(eventFactory.createCharacters(" "));
						}
					}
				}
			} else {
				modified.add(event);
			}
		}

		// write result
		for (XMLEvent event : modified) {
			writer.add(event);
			// System.out.print(event);
		}
		// System.out.println();
	}

	private boolean isPreserveElement(QName name) {
		return name.equals(ObflQName.PAGE_NUMBER) || name.equals(ObflQName.LEADER) || name.equals(ObflQName.EVALUATE);
	}

	private int untilEventIsNotForward(List<XMLEvent> events, final int i, final int eventType) {
		for (int j = i; j < events.size(); j++) {
			if (events.get(j).getEventType() != eventType) {
				return j;
			}
		}
		return -1;
	}

	private int untilEventIsNotBackward(List<XMLEvent> events, final int i, final int eventType) {
		for (int j = 0; j < i; j++) {
			if (events.get(i - j).getEventType() != eventType) {
				return i - j;
			}
		}
		return -1;
	}

	private int untilEventIsNotBackward(List<XMLEvent> events, final int i, QName... name) {
		for (int j = 0; j < i; j++) {
			XMLEvent event = events.get(i - j);
			boolean found = false;
			for (QName n : name) {
				if (equalsStart(event, n) || equalsEnd(event, n)) {
					found = true;
					break;
				}
			}
			if (found) {
				// continue
			} else {
				return i - j;
			}
		}
		return -1;
	}

	private String normalizeSpace(String input) {
		return input.replaceAll("\\s+", " ").trim();
	}

	private boolean equalsStart(XMLEvent event, QName... element) {
		for (QName n : element) {
			if (event.getEventType() == XMLStreamConstants.START_ELEMENT && event.asStartElement().getName().equals(n)) {
				return true;
			}
		}
		return false;
	}

	private boolean equalsEnd(XMLEvent event, QName... element) {
		for (QName n : element) {
			if (event.getEventType() == XMLStreamConstants.END_ELEMENT && event.asEndElement().getName().equals(n)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			OBFLWsNormalizer p = new OBFLWsNormalizer(new FileInputStream("ws-test-input.xml"), new FileOutputStream("out.xml"));
			p.parse();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
