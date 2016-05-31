package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PageStructBuilder {

	private final FormatterContext context;
	private final Iterable<BlockSequence> fs;
	private final CrossReferenceHandler crh;
	private final DefaultContext rcontext;
	private PageStruct struct;
	private Map<String, PageImpl> pageReferences;
	

	public PageStructBuilder(FormatterContext context, Iterable<BlockSequence> fs, CrossReferenceHandler crh, DefaultContext rcontext) {
		this.pageReferences = new HashMap<>();
		this.context = context;
		this.fs = fs;
		this.crh = crh;
		this.rcontext = rcontext;
	}

	List<Sheet> paginate() throws PaginatorException {
		restart:while (true) {
			pageReferences = new HashMap<>();
			struct = new PageStruct();
			for (BlockSequence seq : fs) {
				if (!newSequence(seq)) {
					continue restart;
				}
			}
			struct.updateSheetCount();
			return struct.buildSplitPoints();
		}
	}

	private boolean newSequence(BlockSequence seq) throws PaginatorException {
		int offset = getCurrentPageOffset();
		PageSequence ps = new PageSequence(struct, seq.getLayoutMaster(), seq.getInitialPageNumber()!=null?seq.getInitialPageNumber() - 1:offset);
		PageSequenceBuilder2 psb = new PageSequenceBuilder2(ps.getLayoutMaster(), ps.getPageNumberOffset(), crh, seq, this.pageReferences, context, rcontext);
		struct.add(ps);
		while (psb.hasNext()) {
			try {
				PageImpl p = psb.nextPage();
				p.setSequenceParent(ps);
				ps.addPage(p);
			} catch (RestartPaginationException e) {
				return false;
			}
		}
		return true;
	}
	
	private int getCurrentPageOffset() {
		if (struct.size()>0) {
			PageSequence prv = (PageSequence)struct.peek();
			if (prv.getLayoutMaster().duplex() && (prv.getPageCount() % 2)==1) {
				return prv.getPageNumberOffset() + prv.getPageCount() + 1;
			} else {
				return prv.getPageNumberOffset() + prv.getPageCount();
			}
		} else {
			return 0;
		}
	}
	
	void setVolumeScope(int volumeNumber, int fromIndex, int toIndex) {
		struct.setVolumeScope(volumeNumber, fromIndex, toIndex);
	}

}