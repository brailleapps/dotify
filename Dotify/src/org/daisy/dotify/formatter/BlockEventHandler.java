package org.daisy.dotify.formatter;

import java.io.IOException;
import java.util.Map;

import org.daisy.dotify.formatter.utils.Expression;

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
		for (BlockContents bc : b) {
			switch (bc.getContentType()) {
				case PCDATA:
					formatter.addChars(((TextContents)bc).getText());
					break;
				case LEADER:
					formatter.insertLeader(((Leader)bc));
					break;
				case PAGE_NUMBER:
					if (refs==null) {
						throw new RuntimeException("No cross references supplied.");
					}
					String refid = ((PageNumberReference)bc).getRefId();
					Integer page = refs.getPageNumber(refid);
					if (page==null) {
						isDirty = true;
						formatter.addChars("??");
					} else {
						formatter.addChars(""+page);
					}
					break;
				case BLOCK:
					BlockEvent ev = (BlockEvent)bc;
					formatter.startBlock(ev.getProperties());
					runBlockContents(ev);
					formatter.endBlock();
					break;
				case BR:
					formatter.newLine();
					break;
				case EVALUATE:
					//FIXME: inject variables...
					formatter.addChars((new Expression().evaluate(((Evaluate)bc).getExpression())).toString());
					break;
				default:
					throw new RuntimeException("Unknown contents: " + bc.getContentType());
			}
		}
	}
	
	public void format(Iterable<SequenceEvent> sequences) {
		for (SequenceEvent events : sequences) {
			format(events);
		}
	}
	
	public void format(SequenceEvent events) {
		formatter.newSequence(events.getSequenceProperties());
		for (BlockEvent e : events) {
			formatter.startBlock(e.getProperties());
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
