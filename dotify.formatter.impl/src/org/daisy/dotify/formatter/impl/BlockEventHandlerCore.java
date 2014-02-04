package org.daisy.dotify.formatter.impl;

import java.util.Map;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.obfl.ExpressionFactory;

class BlockEventHandlerCore {
	private final ExpressionFactory ef;
	private final FormatterCore formatter;

	public BlockEventHandlerCore(FormatterCore formatter, ExpressionFactory ef) {
		this.ef = ef;
		this.formatter = formatter;
	}
	
	protected FormatterCore getFormatterCore() {
		return formatter;
	}
	
	public void insertEventContents(IterableEventContents b, Map<String, String> vars) {
		for (EventContents bc : b) {
			switch (bc.getContentType()) {
				case PCDATA: {
					TextSegment tc = (TextSegment)bc;
					formatter.addChars(tc.getText(), tc.getTextProperties());
					break; }
				case LEADER: {
					formatter.insertLeader(((Leader)bc));
					break; }
				case PAGE_NUMBER: {
					formatter.insertReference(((PageNumberReference)bc).getRefId(), ((PageNumberReference)bc).getNumeralStyle());
					break; }
				case BLOCK: {
					BlockEvent ev = (BlockEvent)bc;
					formatter.startBlock(ev.getProperties(), ev.getBlockId());
					insertEventContents(ev, vars);
					formatter.endBlock();
					break; }
				case TOC_ENTRY: {
					TocBlockEvent ev = (TocBlockEvent)bc;
					formatter.startBlock(ev.getProperties(), ev.getTocId());
					insertEventContents(ev, vars);
					formatter.endBlock();
					break; }
				case BR: {
					formatter.newLine();
					break; }
				case EVALUATE: {
					Evaluate e = ((Evaluate)bc);
					formatter.addChars((ef.newExpression().evaluate(e.getExpression(), vars)).toString(), e.getTextProperties());
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

}
