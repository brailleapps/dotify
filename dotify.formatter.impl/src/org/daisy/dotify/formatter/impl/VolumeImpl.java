package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.common.collection.CompoundIterable;

/**
 * Provides a container for a physical volume of braille
 * @author Joel Håkansson
 */
class VolumeImpl implements Volume {
	private CompoundIterable<PageSequence> ret;
	private Iterable<PageSequence> body;
	private Iterable<PageSequence> preVolData;
	private Iterable<PageSequence> postVolData;
	private int preVolSize;
	private int postVolSize;
	private int targetVolSize;
	
	VolumeImpl() {
		ret = null;
		this.preVolSize = 0;
		this.postVolSize = 0;
		this.targetVolSize = 0;
	}

	public void setBody(List<Sheet> body) {
		ret = null;
		this.body = sequencesFromSheets(body);
	}
	
	private static Iterable<PageSequence> sequencesFromSheets(List<Sheet> sheets) {
		PageStruct ret = new PageStruct();
		PageSequence currentSeq = null;
		for (Sheet s : sheets) {
			for (PageImpl p : s.getPages()) {
				if (ret.empty() || currentSeq!=p.getSequenceParent()) {
					currentSeq = p.getSequenceParent();
					ret.add(new PageSequence(ret, currentSeq.getLayoutMaster(), currentSeq.getPageNumberOffset()));
				}
				((PageSequence)ret.peek()).addPage(p);
			}
		}
		return ret;
	}

	public void setPreVolData(Iterable<PageSequence> preVolData) {
		ret = null;
		//use the highest value to avoid oscillation
		preVolSize = Math.max(preVolSize, PageStruct.countSheets(preVolData));
		this.preVolData = preVolData;
	}

	public void setPostVolData(Iterable<PageSequence> postVolData) {
		ret = null;
		//use the highest value to avoid oscillation
		postVolSize = Math.max(postVolSize, PageStruct.countSheets(postVolData));
		this.postVolData = postVolData;
	}
	
	public int getOverhead() {
		return preVolSize + postVolSize;
	}

	public int getTargetSize() {
		return targetVolSize;
	}

	public void setTargetVolSize(int targetVolSize) {
		this.targetVolSize = targetVolSize;
	}

	@Override
	public Iterable<? extends Section> getSections() {
		if (ret==null) {
			List<Iterable<PageSequence>> contents = new ArrayList<>();
			contents.add(preVolData);
			contents.add(body);
			contents.add(postVolData);
			ret = new CompoundIterable<>(contents);
		}
		return ret;
	}

}