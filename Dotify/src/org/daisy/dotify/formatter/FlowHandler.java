package org.daisy.dotify.formatter;

import java.util.ArrayList;

import org.daisy.dotify.formatter.field.CompoundField;
import org.daisy.dotify.formatter.field.CurrentPageField;
import org.daisy.dotify.formatter.field.MarkerReferenceField;
import org.daisy.dotify.formatter.field.MarkerReferenceField.MarkerSearchDirection;
import org.daisy.dotify.formatter.field.MarkerReferenceField.MarkerSearchScope;
import org.daisy.dotify.formatter.field.NumeralField.NumeralStyle;
import org.daisy.dotify.system.tasks.layout.page.ConfigurableLayoutMaster;
import org.daisy.dotify.system.tasks.layout.page.SimpleTemplate;
import org.daisy.dotify.system.tasks.layout.utils.Expression;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * FlowHandler reads an OBFL-file containing blocks or text within
 * other blocks to an arbitrary depth. The input semantics resembles that of xsl-fo, 
 * but is greatly simplified. The FlowHandler reads the input flow and 
 * breaks it down into rows using the Formatter interface.
 * @author Joel Håkansson, TPB
 *
 */
public class FlowHandler extends DefaultHandler {
	private Formatter flow;
	private StringBuffer sb;
	private ConfigurableLayoutMaster.Builder masterConfig;
	private String masterName;
	private SimpleTemplate template;
	private ArrayList<Object> fields;
	private ArrayList<Object> compound;
	private boolean content;
	
	/**
	 * Create a new FlowReader
	 * @param flow
	 */
	public FlowHandler(Formatter flow) {
		this.flow = flow;
		sb = new StringBuffer();
		content = false;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		flushChars();
		if (localName.equals("layout-master")) {
			int width = Integer.parseInt(atts.getValue("page-width"));
			int height = Integer.parseInt(atts.getValue("page-height"));
			masterConfig = new ConfigurableLayoutMaster.Builder(width, height);
			masterName = atts.getValue("name");
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				String value = atts.getValue(i);
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
		} else if (localName.equals("template")) {
			template = new SimpleTemplate(atts.getValue("use-when"));
		} else if (localName.equals("default-template")) {
			template = new SimpleTemplate();
		} else if (localName.equals("header")) {
			fields = new ArrayList<Object>();
		} else if (localName.equals("footer")) {
			fields = new ArrayList<Object>();
		} else if (localName.equals("string")) {
			compound.add(atts.getValue("value"));
		} else if (localName.equals("evaluate")) {
			compound.add(new Expression().evaluate(atts.getValue("expression")));
		} else if (localName.equals("current-page")) {
			compound.add(new CurrentPageField(NumeralStyle.valueOf(atts.getValue("style").toUpperCase())));
		} else if (localName.equals("marker-reference")) {
			compound.add(
				new MarkerReferenceField(
						atts.getValue("marker"), 
						MarkerSearchDirection.valueOf(atts.getValue("direction").toUpperCase()),
						MarkerSearchScope.valueOf(atts.getValue("scope").toUpperCase())
				)
			);
		} else if (localName.equals("field")) {
			compound = new ArrayList<Object>();
		}
		else if (localName.equals("sequence")) {
			content = true;
			String masterName = atts.getValue("master");
			SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName); 
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				if (name.equals("initial-page-number")) {
					builder.initialPageNumber(Integer.parseInt(atts.getValue(i)));
				}
			}
			flow.newSequence(builder.build());
		} else if (localName.equals("block")) {
			BlockProperties.Builder builder = new BlockProperties.Builder();
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				if (name.equals("margin-left")) {
					builder.leftMargin(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("margin-right")) {
					builder.rightMargin(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("margin-top")) {
					builder.topMargin(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("margin-bottom")) {
					builder.bottomMargin(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("text-indent")) {
					builder.textIndent(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("first-line-indent")) {
					builder.firstLineIndent(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("list-type")) {
					builder.listType(FormattingTypes.ListStyle.valueOf(atts.getValue(i).toUpperCase()));
				} else if (name.equals("break-before")) {
					builder.breakBefore(FormattingTypes.BreakBefore.valueOf(atts.getValue(i).toUpperCase()));
				} else if (name.equals("keep")) {
					builder.keep(FormattingTypes.Keep.valueOf(atts.getValue(i).toUpperCase()));
				} else if (name.equals("keep-with-next")) {
					builder.keepWithNext(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("block-indent")) {
					builder.blockIndent(Integer.parseInt(atts.getValue(i)));
				}
			}
			flow.startBlock(builder.build());
		} else if (localName.equals("float-item")) {
			flow.startFloat(atts.getValue("name"));
		} else if (localName.equals("marker")) {
			String markerName = "";
			String markerValue = "";
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				if (name.equals("class")) {
					markerName = atts.getValue(i);
				} else if (name.equals("value")) {
					markerValue = atts.getValue(i);
				}
			}
			flow.insertMarker(new Marker(markerName, markerValue));
		} else if (localName.equals("anchor")) {
			flow.insertAnchor(atts.getValue("float-item"));
		} else if (localName.equals("br")) {
			flow.newLine();
		} else if (localName.equals("leader")) {
			Leader.Builder builder = new Leader.Builder();
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				if (name.equals("align")) {
					builder.align(Leader.Alignment.valueOf(atts.getValue(i).toUpperCase()));
				} else if (name.equals("position")) {
					builder.position(Position.parsePosition(atts.getValue(i)));
				} else if (name.equals("pattern")) {
					builder.pattern(atts.getValue(i));
				}
			}
			flow.insertLeader(builder.build());
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		flushChars();
		if (localName.equals("block")) {
			flow.endBlock();
		} else if (localName.equals("float-item")) {
			flow.endFloat();
		} else if (localName.equals("layout-master")) {
			flow.addLayoutMaster(masterName, masterConfig.build());
		} else if (localName.equals("template") || localName.equals("default-template")) {
			masterConfig.addTemplate(template);
		} else if (localName.equals("field")) {
			if (compound.size()==1) {
				fields.add(compound.get(0));
			} else {
				CompoundField f = new CompoundField();
				f.addAll(compound);
				fields.add(f);
			}
		}
		else if (localName.equals("header")) {
			if (fields.size()>0) {
				template.addToHeader(fields);
			}
			/*for (Object obj : fields) {
				
			}*/
		} else if (localName.equals("footer")) {
			if (fields.size()>0) {
				template.addToFooter(fields);
			}
			/*for (Object obj : fields) {
				
			}	*/		
		}
		 else if (localName.equals("sequence")) {
			content = false;
		}
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		if (content) {
			sb.append(new String(ch, start, length));
		}
	}
	
	// Coalescing feature
	private void flushChars() {
		if (sb.length()>0) {
			flow.addChars(sb);
			sb = new StringBuffer();
		}
	}

}
