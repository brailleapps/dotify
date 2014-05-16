package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.ContentCollection;


/**
 * Provides a content collection to be used when placing e.g. footnotes.
 * 
 * @author Joel Håkansson
 */
class ContentCollectionImpl extends FormatterCoreImpl implements ContentCollection  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2198713822437968076L;
	private final Map<String, Item> items;
	private final Stack<Item> open;
	
	public ContentCollectionImpl() {
		this.items = new HashMap<String, Item>();
		this.open = new Stack<Item>();
	}
	
	public boolean containsItemID(String id) {
		return items.containsKey(id);
	}
	
	public List<Block> getBlocks(String id) {
		Item i = items.get(id);
		if (i!=null) {
			return subList(i.from, i.to);
		} else {
			return new ArrayList<Block>();
		}
	}

	public void startItem(BlockProperties props) {
		String id = props.getIdentifier();
		if (id!=null) {
			open.push(new Item(id, size()));
			if (items.put(id, open.peek())!=null) {
				throw new RuntimeException("Identifier is not unique: " + id);
			}
		}
		startBlock(props);
	}
	
	public void endItem() {
		endBlock();
		Item i = items.get(open.pop().id);
		i.to = size();
	}
	
	private static class Item {
		private final String id;
		/**
		 * fromIndex, inclusive
		 */
		private final int from;
		/**
		 * toIndex, exclusive
		 */
		private int to;
		
		Item(String id, int start) {
			this.id = id;
			this.from = start;
		}
	}

}
