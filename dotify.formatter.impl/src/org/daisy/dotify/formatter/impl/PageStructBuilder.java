package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
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
			return buildSplitPoints();
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
	
	List<Sheet> buildSplitPoints() {
		List<Sheet.Builder> ret = new ArrayList<>();
		boolean volBreakAllowed = true;
		int size = 0;
		for (PageSequence seq : struct) {
			LayoutMaster lm = seq.getLayoutMaster();
			int pageIndex = 0;
			Sheet.Builder s = null;
			for (PageImpl p : seq.getPages()) {
				if (!lm.duplex() || pageIndex % 2 == 0) {
					volBreakAllowed = true;
					s = new Sheet.Builder();
					ret.add(s);
				}
				setPreviousSheet(ret, p);
				volBreakAllowed &= p.allowsVolumeBreak();
				if (!lm.duplex() || pageIndex % 2 == 1) {
					if (volBreakAllowed) {
						s.breakable(true);
					}
				}
				s.add(p);
				pageIndex++;
			}
			// if this sequence was not empty
			if (size < ret.size()) {
				size += ret.size();
				// set breakable to true
				ret.get(ret.size() - 1).breakable(true);
			}
		}
		return buildAll(ret);
	}


	private List<Sheet> buildAll(List<Sheet.Builder> builders) {
		List<Sheet> ret = new ArrayList<>();
		for (Sheet.Builder b : builders) {
			ret.add(b.build());
		}
		return ret;
	}

	private void setPreviousSheet(List<Sheet.Builder> sb, PageImpl p) {
		int i = 0;
		for (int x = sb.size() - 1; i < p.keepPreviousSheets() && x > 0; x--) {
			sb.get(x - 1).breakable(false);
			i++;
		}
	}

}