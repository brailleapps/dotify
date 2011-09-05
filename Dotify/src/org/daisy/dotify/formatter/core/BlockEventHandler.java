package org.daisy.dotify.formatter.core;

import java.io.IOException;
import java.util.Map;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.dom.BlockEvent;
import org.daisy.dotify.formatter.dom.BlockStruct;
import org.daisy.dotify.formatter.dom.CrossReferences;
import org.daisy.dotify.formatter.dom.EventContents;
import org.daisy.dotify.formatter.dom.EventContents.ContentType;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Leader;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.SequenceEvent;
import org.daisy.dotify.formatter.dom.TocEvent;
import org.daisy.dotify.formatter.utils.Expression;

/**
 * Provides a method to send events to a formatter.
 * 
 * @author Joel Håkansson
 */
public class BlockEventHandler {
	private final CrossReferences refs;
	private final Formatter formatter;
	private boolean isDirty;
	
	public BlockEventHandler(FormatterFactory factory, Map<String, LayoutMaster> masters) {
		this(factory, masters, null);
	}

	public BlockEventHandler(FormatterFactory factory, Map<String, LayoutMaster> masters, CrossReferences refs) {
		this.refs = refs;
		this.formatter = factory.newFormatter();
		this.formatter.open();
		for (String name : masters.keySet()) {
			this.formatter.addLayoutMaster(name, masters.get(name));
		}
		isDirty = false;
	}
	
	private void runBlockContents(BlockEvent b) {
		for (EventContents bc : b) {
			switch (bc.getContentType()) {
				case PCDATA: {
					formatter.addChars(((TextContents)bc).getText());
					break; }
				case LEADER: {
					formatter.insertLeader(((Leader)bc));
					break; }
				case PAGE_NUMBER: {
					if (refs==null) {
						throw new RuntimeException("No cross references supplied.");
					}
					String refid = ((PageNumberReference)bc).getRefId();
					Page page = refs.getPage(refid);
					if (page==null) {
						isDirty = true;
						formatter.addChars("??");
					} else {
						int p = page.getPageIndex()+1;
						switch (((PageNumberReference)bc).getNumeralStyle()) {
							case ROMAN:
								formatter.addChars(""+RomanNumeral.int2roman(p));
								break;
							case DEFAULT:default:
								formatter.addChars(""+p);
						}
					}
					break; }
				case BLOCK: {
					BlockEvent ev = (BlockEvent)bc;
					formatter.startBlock(ev.getProperties());
					runBlockContents(ev);
					formatter.endBlock();
					break; }
				case TOC_ENTRY: {
					TocEvent ev = (TocEvent)bc;
					formatter.startBlock(ev.getProperties(), ev.getTocId());
					runBlockContents(ev);
					formatter.endBlock();
					break; }
				case BR: {
					formatter.newLine();
					break; }
				case EVALUATE: {
					Evaluate e = ((Evaluate)bc);
					formatter.addChars((new Expression().evaluate(e.getExpression(), e.getVariables())).toString());
					break; }
				default:
					throw new RuntimeException("Unknown contents: " + bc.getContentType());
			}
		}
	}
	
	public void formatSequences(Iterable<SequenceEvent> sequences) {
		for (SequenceEvent events : sequences) {
			formatSequence(events);
		}
	}
	
	public void formatSequence(SequenceEvent events) {
		formatter.newSequence(events.getSequenceProperties());
		for (BlockEvent e : events) {
			if (e.getContentType()==ContentType.TOC_ENTRY) {
				formatter.startBlock(e.getProperties(), ((TocEvent)e).getTocId());
			} else if (e.getContentType()==ContentType.BLOCK) {
				formatter.startBlock(e.getProperties());
			} else {
				throw new RuntimeException("Coding error");
			}
			runBlockContents(e);
			formatter.endBlock();
		}
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	public BlockStruct close() throws IOException {
		formatter.close();
		return formatter.getFlowStruct();
	}

}
