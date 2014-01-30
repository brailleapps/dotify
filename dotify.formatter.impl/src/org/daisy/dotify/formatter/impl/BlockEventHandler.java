package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.Map;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.formatter.impl.EventContents.ContentType;

/**
 * Provides a method to send events to a formatter.
 * 
 * @author Joel Håkansson
 */
class BlockEventHandler extends BlockEventHandlerCore {
	private final Formatter formatter;

	public BlockEventHandler(Map<String, LayoutMaster> masters, BrailleTranslator bt, ExpressionFactory ef) {
		super(new FormatterImpl(bt, ef), ef);
		this.formatter = (Formatter)getFormatterCore();
		this.formatter.open();
		for (String name : masters.keySet()) {
			this.formatter.addLayoutMaster(name, masters.get(name));
		}
	}
	
	public BlockEventHandler(Formatter formatter, ExpressionFactory ef) {
		super(formatter, ef);
		this.formatter = formatter;
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
				formatter.startBlock(e.getProperties(), e.getBlockId());
			} else {
				throw new RuntimeException("Coding error");
			}
			insertEventContents(e);
			formatter.endBlock();
		}
	}
	
	public BlockStruct close() throws IOException {
		formatter.close();
		//FIXME: this is a temporary solution in order to remove getFlowStruct from the API, In the long term, this should be solved in another way
		return ((FormatterImpl)formatter).getFlowStruct();
	}

}
