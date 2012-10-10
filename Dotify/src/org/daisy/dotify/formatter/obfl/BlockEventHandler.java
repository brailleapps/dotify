package org.daisy.dotify.formatter.obfl;

import java.io.IOException;
import java.util.Map;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.Leader;
import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.TextProperties;
import org.daisy.dotify.formatter.obfl.EventContents.ContentType;
import org.daisy.dotify.paginator.BlockStruct;

/**
 * Provides a method to send events to a formatter.
 * 
 * @author Joel Håkansson
 */
class BlockEventHandler {
	private final Formatter formatter;

	public BlockEventHandler(FormatterFactory factory, Map<String, LayoutMaster> masters) {
		this.formatter = factory.newFormatter();
		this.formatter.open();
		for (String name : masters.keySet()) {
			this.formatter.addLayoutMaster(name, masters.get(name));
		}
	}
	
	private void runBlockContents(BlockEvent b) {
		for (EventContents bc : b) {
			switch (bc.getContentType()) {
				case PCDATA: {
					TextContents tc = (TextContents)bc;
					formatter.addChars(tc.getText(), tc.getSpanProperties());
					break; }
				case LEADER: {
					formatter.insertLeader(((Leader)bc));
					break; }
				case PAGE_NUMBER: {
					formatter.insertReference(((PageNumberReference)bc).getRefId(), ((PageNumberReference)bc).getNumeralStyle());
					break; }
				case BLOCK: {
					BlockEvent ev = (BlockEvent)bc;
					formatter.startBlock(ev.getProperties());
					runBlockContents(ev);
					formatter.endBlock();
					break; }
				case TOC_ENTRY: {
					TocBlockEvent ev = (TocBlockEvent)bc;
					formatter.startBlock(ev.getProperties(), ev.getTocId());
					runBlockContents(ev);
					formatter.endBlock();
					break; }
				case BR: {
					formatter.newLine();
					break; }
				case EVALUATE: {
					Evaluate e = ((Evaluate)bc);
					formatter.addChars((new Expression().evaluate(e.getExpression(), e.getVariables())).toString(), new TextProperties.Builder(null).build());
					break; }
				case MARKER: {
					Marker m = ((Marker)bc);
					formatter.insertMarker(m);
					break;
				}
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
				formatter.startBlock(e.getProperties(), ((TocBlockEvent)e).getTocId());
			} else if (e.getContentType()==ContentType.BLOCK) {
				formatter.startBlock(e.getProperties());
			} else {
				throw new RuntimeException("Coding error");
			}
			runBlockContents(e);
			formatter.endBlock();
		}
	}
	
	public BlockStruct close() throws IOException {
		formatter.close();
		return formatter.getFlowStruct();
	}

}
