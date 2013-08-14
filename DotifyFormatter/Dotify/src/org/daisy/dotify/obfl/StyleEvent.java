package org.daisy.dotify.obfl;

import java.util.Stack;

public class StyleEvent extends Stack<EventContents> implements IterableEventContents {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7263147868028801228L;

	private final String name;

	public StyleEvent(String name) {
		this.name = name;
	}

	public ContentType getContentType() {
		return ContentType.STYLE;
	}

	public String getName() {
		return name;
	}

	public boolean canContainEventObjects() {
		return true;
	}

}
