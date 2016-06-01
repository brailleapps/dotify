package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

class PageStructBuilder {

	private final FormatterContext context;
	private final Iterable<BlockSequence> fs;
	private final CrossReferenceHandler crh;
	private PageStruct struct;
	

	public PageStructBuilder(FormatterContext context, Iterable<BlockSequence> fs, CrossReferenceHandler crh) {
		this.context = context;
		this.fs = fs;
		this.crh = crh;
	}

	List<Sheet> paginate(DefaultContext rcontext) throws PaginatorException {
		restart:while (true) {
			crh.resetUniqueChecks();
			struct = new PageStruct();
			List<Sheet.Builder> ret = new ArrayList<>();
			boolean volBreakAllowed = true;
			for (BlockSequence bs : fs) {
				try {
					PageSequence seq = newSequence(bs, rcontext);
					LayoutMaster lm = seq.getLayoutMaster();
					Sheet.Builder s = null;
					List<PageImpl> pages = seq.getPages();
					for (int pageIndex = 0; pageIndex<pages.size(); pageIndex++) {
						PageImpl p = pages.get(pageIndex);
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
						if (pageIndex==pages.size()-1) {
							ret.get(ret.size() - 1).breakable(true);
						}
					}
				} catch (RestartPaginationException e) {
					continue restart;
				}
			}
			return buildAll(ret);
		}
	}

	private PageSequence newSequence(BlockSequence seq, DefaultContext rcontext) throws PaginatorException, RestartPaginationException {
		int offset = getCurrentPageOffset();
		PageSequence ps = new PageSequence(struct, seq.getLayoutMaster(), seq.getInitialPageNumber()!=null?seq.getInitialPageNumber() - 1:offset);
		PageSequenceBuilder2 psb = new PageSequenceBuilder2(ps.getLayoutMaster(), ps.getPageNumberOffset(), crh, seq, context, rcontext);
		struct.add(ps);
		while (psb.hasNext()) {
			PageImpl p = psb.nextPage();
			p.setSequenceParent(ps);
			//This is for pre/post volume contents, where the volume number is known
			if (rcontext.getCurrentVolume()!=null) {
				for (String id : p.getIdentifiers()) {
					crh.setVolumeNumber(id, rcontext.getCurrentVolume());
				}
			}
			ps.addPage(p);
		}
		return ps;
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