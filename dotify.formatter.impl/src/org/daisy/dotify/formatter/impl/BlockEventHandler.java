package org.daisy.dotify.formatter.impl;

import java.io.IOException;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.formatter.impl.EventContents.ContentType;

/**
 * Provides a method to send events to a formatter.
 * 
 * @author Joel Håkansson
 */
class BlockEventHandler extends BlockEventHandlerCore {
	private final BlockStructImpl formatter;

	public BlockEventHandler(FormatterContext context) {
		super(new BlockStructImpl(context));
		this.formatter = (BlockStructImpl)getFormatterCore();
		this.formatter.open();
	}
	
	public BlockEventHandler(BlockStructImpl formatter) {
		super(formatter);
		this.formatter = formatter;
	}
	
	public void formatSequences(Iterable<SequenceEvent> sequences, Context vars) {
		for (SequenceEvent events : sequences) {
			formatSequence(events, vars);
		}
	}

	public void newSequence(SequenceProperties props, Context vars) {
		formatter.newSequence(props);
	}
	
	public void formatSequence(SequenceEvent events, Context vars) {
		newSequence(events.getSequenceProperties(), vars);
		formatBlock(events, vars);
	}
	
	public void formatBlock(Iterable<BlockEvent> events, Context vars) {
		for (BlockEvent e : events) {
			if (e.getContentType()==ContentType.TOC_ENTRY) {
				formatter.startBlock(e.getProperties(), ((TocBlockEvent)e).getTocId());
			} else if (e.getContentType()==ContentType.BLOCK) {
				formatter.startBlock(e.getProperties(), e.getBlockId());
			} else {
				throw new RuntimeException("Coding error");
			}
			insertEventContents(e, vars);
			formatter.endBlock();
		}
	}
	
	public BlockStruct close() throws IOException {
		formatter.close();
		return formatter.getFlowStruct();
	}

}
