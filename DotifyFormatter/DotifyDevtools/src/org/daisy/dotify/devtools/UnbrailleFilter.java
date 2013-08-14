package org.daisy.dotify.devtools;

import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.daisy.braille.table.BrailleConverter;
import org.daisy.dotify.tools.StaxFilter2;

public class UnbrailleFilter extends StaxFilter2 {
	private final static String PEF_NS = "http://www.daisy.org/ns/2008/pef";
	private final static QName row = new QName(PEF_NS, "row");
	private final BrailleConverter t;
	private boolean translate = false;
	
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
			return getEventFactory().createCharacters( t.toText(event.getData()));
		}
		return super.characters(event);
	}

	@Override
	protected EndElement endElement(EndElement event) {
		if (event.getName().equals(row)) {
    		translate = false;
    	}
		return super.endElement(event);
	}


}
