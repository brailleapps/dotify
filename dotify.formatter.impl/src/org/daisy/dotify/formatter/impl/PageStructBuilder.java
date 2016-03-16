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
		PageSequence ps = new PageSequence(struct, seq.getLayoutMaster(), seq.getInitialPageNumber()!=null?seq.getInitialPageNumber() - 1:offset);
		PageSequenceBuilder2 psb = new PageSequenceBuilder2(ps, crh, seq, this.pageReferences, context, refs, rcontext);
		struct.add(ps);
		return psb.paginate();
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

}