package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.Map;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.formatter.impl.EventContents.ContentType;

/**
 * Provides a method to send events to a formatter.
 * 
 * @author Joel Håkansson
 */
class BlockEventHandler extends BlockEventHandlerCore {
	private final Formatter formatter;

	public BlockEventHandler(FormatterContext context, ExpressionFactory ef) {
		super(new FormatterImpl(context, ef), ef);
		this.formatter = (Formatter)getFormatterCore();
		this.formatter.open();
	}
	
	public BlockEventHandler(Formatter formatter, ExpressionFactory ef) {
		super(formatter, ef);
		this.formatter = formatter;
	}
	
	public void formatSequences(Iterable<SequenceEvent> sequences, Map<String, String> vars) {
		for (SequenceEvent events : sequences) {
			formatSequence(events, vars);
		}
	}
	
	
	public void newSequence(SequenceProperties props, Map<String, String> vars) {
		formatter.newSequence(props);
	}
	
	public void formatSequence(SequenceEvent events, Map<String, String> vars) {
		newSequence(events.getSequenceProperties(), vars);
		formatBlock(events, vars);
	}
	
	public void formatBlock(Iterable<BlockEvent> events, Map<String, String> vars) {
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
		//FIXME: this is a temporary solution in order to remove getFlowStruct from the API, In the long term, this should be solved in another way
		return ((FormatterImpl)formatter).getFlowStruct();
	}

}
