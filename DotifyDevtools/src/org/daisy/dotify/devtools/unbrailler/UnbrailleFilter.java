package org.daisy.dotify.devtools.unbrailler;

import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.daisy.braille.api.table.BrailleConverter;

class UnbrailleFilter extends StaxFilter2 {
	private final static String PEF_NS = "http://www.daisy.org/ns/2008/pef";
	private final static QName row = new QName(PEF_NS, "row");
	private final BrailleConverter t;
	private boolean translate = false;
	private boolean filtering = false;
	
	public UnbrailleFilter(XMLEventReader xer, OutputStream outStream, BrailleConverter t)
			throws XMLStreamException {
		super(xer, outStream);
		this.t = t;
	}
	
    protected StartElement startElement(StartElement event) {
    	if (event.getName().equals(row)) {
    		translate = true;
    	}
        return event;
    }

	@Override
	protected Characters characters(Characters event) {
		if (translate) {
			return getEventFactory().createCharacters(t.toText(filtering ? filter(event.getData()) : event.getData()));
		}
		return super.characters(event);
	}

	public boolean isFiltering() {
		return filtering;
	}

	public void setFiltering(boolean filtering) {
		this.filtering = filtering;
	}

	private static String filter(String input) {
		return input.replaceAll("[^\u2800-\u28FF]", "\u2800");
	}

	@Override
	protected EndElement endElement(EndElement event) {
		if (event.getName().equals(row)) {
    		translate = false;
    	}
		return super.endElement(event);
	}


}
