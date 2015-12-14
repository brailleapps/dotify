package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Map;

class PageStructBuilder {

	private final FormatterContext context;
	private final Iterable<BlockSequence> fs;
	private PageStruct struct;
	private Map<String, PageImpl> pageReferences;

	public PageStructBuilder(FormatterContext context, Iterable<BlockSequence> fs) {
		this.pageReferences = new HashMap<>();
		this.context = context;
		this.fs = fs;
	}

	PageStruct paginate(CrossReferenceHandler crh, CrossReferences refs, DefaultContext rcontext) throws PaginatorException {
		restart:while (true) {
			pageReferences = new HashMap<>();
			struct = new PageStruct();
			for (BlockSequence seq : fs) {
				if (!newSequence(crh, seq, refs, rcontext)) {
					continue restart;
				}
			}
			return struct;
		}
	}

	private boolean newSequence(CrossReferenceHandler crh, BlockSequence seq, CrossReferences refs, DefaultContext rcontext) throws PaginatorException {
		int offset = getCurrentPageOffset();
		PageSequenceBuilder2 psb = new PageSequenceBuilder2(crh, seq, this.pageReferences, context, offset, refs, rcontext);
		struct.add(psb);
		return psb.paginate();
	}
	
	private int getCurrentPageOffset() {
		int offset = 0;
		if (struct.size()>0) {
			PageSequenceBuilder2 prv = (PageSequenceBuilder2)struct.peek();
			offset = prv.currentPageNumber();
			if (prv.getLayoutMaster().duplex() && (offset % 2)==1) {
				offset++;
			}
		}
		return offset;
	}

}