package org.daisy.dotify.obfl;

import java.io.IOException;
import java.util.Map;

import org.daisy.dotify.api.formatter.BlockStruct;
import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.obfl.EventContents.ContentType;
import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a method to send events to a formatter.
 * 
 * @author Joel Håkansson
 */
class BlockEventHandler {
	private final Formatter formatter;
	private final ExpressionFactory ef;

	public BlockEventHandler(FilterLocale locale, String mode, Map<String, LayoutMaster> masters, FormatterFactory ff, ExpressionFactory ef) {
		this.formatter = ff.newFormatter(locale.toString(), mode);
		this.formatter.open();
		for (String name : masters.keySet()) {
			this.formatter.addLayoutMaster(name, masters.get(name));
		}
		this.ef = ef;
	}
	
	public BlockEventHandler(Formatter formatter, ExpressionFactory ef) {
		this.formatter = formatter;
		this.ef = ef;
	}

	public void insertEventContents(IterableEventContents b) {
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
					insertEventContents(ev);
					formatter.endBlock();
					break; }
				case TOC_ENTRY: {
					TocBlockEvent ev = (TocBlockEvent)bc;
					formatter.startBlock(ev.getProperties(), ev.getTocId());
					insertEventContents(ev);
					formatter.endBlock();
					break; }
				case BR: {
					formatter.newLine();
					break; }
				case EVALUATE: {
					Evaluate e = ((Evaluate)bc);
					formatter.addChars((ef.newExpression().evaluate(e.getExpression(), e.getVariables())).toString(), e.getTextProperties());
					break; }
				case MARKER: {
					Marker m = ((Marker)bc);
					formatter.insertMarker(m);
					break;
				}
				case STYLE: {
					StyleEvent ev = (StyleEvent) bc;
					insertEventContents(ev);
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
			insertEventContents(e);
			formatter.endBlock();
		}
	}
	
	public BlockStruct close() throws IOException {
		formatter.close();
		return formatter.getFlowStruct();
	}

}
