package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.SequenceProperties;

class ItemSequenceEventImpl implements VolumeSequence {
	private final SequenceProperties props;
	private final Map<String, ContentCollectionImpl> collections;
	private final String collectionID;


	private final FormatterCoreImpl collectionStartEvents;
	private final FormatterCoreImpl pageStartEvents;
	private final FormatterCoreImpl pageEndEvents;
	private final FormatterCoreImpl collectionEndEvents;
	
	public ItemSequenceEventImpl(SequenceProperties props, String collectionID, Map<String, ContentCollectionImpl> collections) {
		this.props = props;
		this.collections = collections;
		this.collectionID = collectionID;
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

	public List<BlockSequence> getBlockSequence(FormatterContext context, DefaultContext vars, CrossReferences crh) {
		ArrayList<BlockSequence> ret = new ArrayList<BlockSequence>();
		ContentCollectionImpl c = collections.get(collectionID);
		if (c==null) {
			return ret;
		}
		
		BlockSequenceManipulator fsm = new BlockSequenceManipulator(
				context.getMasters().get(getSequenceProperties().getMasterName()), 
				getSequenceProperties().getInitialPageNumber());
		fsm.appendGroup(collectionStartEvents);
		for (PageSequence s : crh.getContents()) {
			for (PageImpl p : s.getPages()) {
			
				ArrayList<String> refs = new ArrayList<String>();
				for (String a : p.getAnchors()) {
					if (c.containsItemID(a) && !refs.contains(a)) {
						refs.add(a);
					}
				}
				if (refs.size()>0) {
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
		ret.add(fsm.newSequence());
		return ret;
	}
}
