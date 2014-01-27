package org.daisy.dotify.obfl;

import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;



public class BlockEventImpl extends Stack<EventContents> implements BlockEvent {
	private final BlockProperties props;
	private final String blockId;

	public BlockEventImpl(BlockProperties props) {
		this(props, null);
	}
	
	public BlockEventImpl(BlockProperties props, String blockId) {
		this.props = props;
		this.blockId = blockId;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 9098524584205247145L;

	public ContentType getContentType() {
		return ContentType.BLOCK;
	}
	
	public BlockProperties getProperties() {
		return props;
	}

	public String getBlockId() {
		return blockId;
	}

	public void setEvaluateContext(Map<String, String> vars) {
		for (int i=0; i<this.size(); i++) {
			EventContents e = this.get(i);
			if (e instanceof BlockContents) {
				((BlockContents)e).setEvaluateContext(vars);
			} else if (e.getContentType()==ContentType.EVALUATE) {
				Evaluate x = ((Evaluate)e);
				this.set(i, new Evaluate(x.getExpression(), vars, x.getTextProperties()));
			}
		}
	}

	public boolean canContainEventObjects() {
		return true;
	}


}
