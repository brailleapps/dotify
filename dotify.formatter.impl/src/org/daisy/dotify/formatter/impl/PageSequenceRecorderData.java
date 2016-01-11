package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

class PageSequenceRecorderData {
	List<RowGroupSequence> dataGroups = new ArrayList<>();
	List<RowGroup> data = new ArrayList<>();
	int keepWithNext = 0;

	PageSequenceRecorderData() {
		dataGroups = new ArrayList<>();
		data = new ArrayList<>();
		keepWithNext = 0;
	}
	
	PageSequenceRecorderData copy() {
		PageSequenceRecorderData r = new PageSequenceRecorderData();
		r.dataGroups = new ArrayList<>(dataGroups);
		r.data = new ArrayList<>(data);
		r.keepWithNext = keepWithNext;
		return r;
	}
	
	float calcSize() {
		float size = 0;
		for (RowGroupSequence rgs : dataGroups) {
			size += rgs.calcSequenceSize();
		}
		return size;
	}

}
