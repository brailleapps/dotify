package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.ItemSequenceProperties;
import org.daisy.dotify.api.formatter.SequenceProperties;

class ItemSequenceEventImpl implements VolumeSequence {
	private final SequenceProperties props;
	private final String collectionID;
	private final ItemSequenceProperties.Range range;

	private final FormatterCoreImpl collectionStartEvents;
	private final FormatterCoreImpl pageStartEvents;
	private final FormatterCoreImpl pageEndEvents;
	private final FormatterCoreImpl collectionEndEvents;
	
	public ItemSequenceEventImpl(SequenceProperties props, ItemSequenceProperties.Range range, String collectionID) {
		this.props = props;
		this.collectionID = collectionID;
		this.range = range;
		this.collectionStartEvents = new FormatterCoreImpl();
		this.pageStartEvents = new FormatterCoreImpl();
		this.pageEndEvents = new FormatterCoreImpl();
		this.collectionEndEvents = new FormatterCoreImpl();
	}

	FormatterCore addCollectionStart() {
		return collectionStartEvents;
	}

	FormatterCore addPageStartEvents() {
		return pageStartEvents;
	}
	
	FormatterCore addPageEndEvents() {
		return pageEndEvents;
	}
	
	FormatterCore addCollectionEnd() {
		return collectionEndEvents;
	}

	public SequenceProperties getSequenceProperties() {
		return props;
	}

	public BlockSequence getBlockSequence(FormatterContext context, DefaultContext vars, CrossReferences crh) {
		ContentCollectionImpl c = context.getCollections().get(collectionID);
		if (c==null) {
			return null;
		}
		
		BlockSequenceManipulator fsm = new BlockSequenceManipulator(
				context.getMasters().get(getSequenceProperties().getMasterName()), 
				getSequenceProperties().getInitialPageNumber());
		fsm.appendGroup(collectionStartEvents);
		boolean hasContents = false;
		for (PageSequence s : crh.getContents()) {
			for (PageImpl p : s.getPages()) {
				ArrayList<String> refs = new ArrayList<String>();
				for (String a : p.getAnchors()) {
					if (c.containsItemID(a) && !refs.contains(a)) {
						refs.add(a);
					}
				}
				if (refs.size()>0 && (range == ItemSequenceProperties.Range.DOCUMENT || crh.getVolumeNumber(p)==vars.getCurrentVolume())) {
					hasContents = true;
					{
						ArrayList<Block> b = new ArrayList<Block>();
						for (Block blk : pageStartEvents) {
							Block bl = (Block)blk.clone();
							bl.setMetaPage(p.getPageIndex()+1);
							b.add(bl);
						}
						fsm.appendGroup(b);
					}
					for (String key : refs) {
						fsm.appendGroup(c.getBlocks(key));
					}
					{
						ArrayList<Block> b = new ArrayList<Block>();
						for (Block blk : pageEndEvents) {
							Block bl = (Block)blk.clone();
							bl.setMetaPage(p.getPageIndex()+1);
							b.add(bl);
						}
						fsm.appendGroup(b);
					}
				}
			}
		}
		fsm.appendGroup(collectionEndEvents);
		if (hasContents) {
			//only add a section if there are notes in it.
			return fsm.newSequence();
		}
		return null;
	}
}
