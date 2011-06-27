package org.daisy.dotify.formatter.impl;

import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.formatter.BlockContents;
import org.daisy.dotify.formatter.BlockEvent;
import org.daisy.dotify.formatter.BlockProperties;
import org.daisy.dotify.formatter.Evaluate;
import org.daisy.dotify.formatter.EventContents;

public class BlockEventImpl extends Stack<EventContents> implements BlockEvent {
	private final BlockProperties props;

	public BlockEventImpl(BlockProperties props) {
		this.props = props;
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

	public void setEvaluateContext(Map<String, String> vars) {
		for (int i=0; i<this.size(); i++) {
			EventContents e = this.get(i);
			if (e.canContainEventObjects()) {
				((BlockContents)e).setEvaluateContext(vars);
			} else if (e.getContentType()==ContentType.EVALUATE) {
				Evaluate x = ((Evaluate)e);
				this.set(i, new Evaluate(x.getExpression(), vars));
			}
		}
	}

	public boolean canContainEventObjects() {
		return true;
	}


}
