package org.daisy.dotify.system.tasks.cover;

import java.io.OutputStream;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.daisy.dotify.formatter.dom.Row;
import org.daisy.dotify.tools.StaxFilter2;


public class VolumeCoverPageFilter extends StaxFilter2 {
	private final static String PEF_NS = "http://www.daisy.org/ns/2008/pef";
	private final QName volume;
	private final QName section;
	private final VolumeCoverPage cover;
	private final VolumeCoverPage rearCover;
	private final int vols;
	private boolean firstSection;
	private int volumeNo;

	public VolumeCoverPageFilter(XMLEventReader xer, OutputStream outStream, VolumeCoverPage cover, int vols)
	throws XMLStreamException {
		this(xer, outStream, cover, null, vols);
	}
	
	public VolumeCoverPageFilter(XMLEventReader xer, OutputStream outStream, VolumeCoverPage cover, VolumeCoverPage rearCover, int vols)
			throws XMLStreamException {
		super(xer, outStream);
		this.volume = new QName(PEF_NS, "volume");
		this.section = new QName(PEF_NS, "section");
		this.cover = cover;
		this.rearCover = rearCover;
		this.vols = vols;
		firstSection = true;
		volumeNo = 0;
	}

    protected StartElement startElement(StartElement event) {
    	if (event.getName().equals(section) && firstSection) {
    		writeSection(cover.buildPage(volumeNo, vols));
			firstSection = false;
    	} else if (event.getName().equals(volume)) {
    		volumeNo++;
    		firstSection = true;
    		//rows = Integer.parseInt(event.getAttributeByName(new QName("rows")).getValue());
    		//cols = Integer.parseInt(event.getAttributeByName(new QName("cols")).getValue());
    	}
        return event;
    }
    
    protected EndElement endElement(EndElement event) {
    	if (rearCover != null && event.getName().equals(volume)) {
    		writeSection(rearCover.buildPage(volumeNo, vols));
    	}
        return event;
    }

    private void writeSection(List<Row> rows) {
    	try {
			getEventWriter().add(getEventFactory().createStartElement("", PEF_NS, "section"));
			getEventWriter().add(getEventFactory().createStartElement("", PEF_NS, "page"));
			for (Row r : rows) {
				getEventWriter().add(getEventFactory().createStartElement("", PEF_NS, "row"));
				getEventWriter().add(getEventFactory().createCharacters(r.getChars().toString()));
				getEventWriter().add(getEventFactory().createEndElement("", PEF_NS, "row"));
			}
			getEventWriter().add(getEventFactory().createEndElement("", PEF_NS, "page"));
			getEventWriter().add(getEventFactory().createEndElement("", PEF_NS, "section"));
    	} catch (XMLStreamException e) {
			e.printStackTrace();
		}
    }

}