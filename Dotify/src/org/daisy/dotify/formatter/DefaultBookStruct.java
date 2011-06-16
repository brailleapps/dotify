package org.daisy.dotify.formatter;

import java.io.IOException;
import java.util.Map;

import org.daisy.dotify.formatter.impl.SequenceEventImpl;

public class DefaultBookStruct implements BookStruct {
	private final PageStruct ps;
	private final Map<String, LayoutMaster> masters;
	private final Iterable<VolumeTemplate> volumeTemplates;
	private final Map<String, TableOfContents> tocs;
	private final FormatterFactory factory;
	
	public DefaultBookStruct(PageStruct ps, Map<String, LayoutMaster> masters, Iterable<VolumeTemplate> volumeTemplates, Map<String, TableOfContents> tocs,
			FormatterFactory factory) {
		this.ps = ps;
		this.masters = masters;
		this.volumeTemplates = volumeTemplates;
		this.tocs = tocs;
		this.factory = factory;
	}
/*
	public Iterable<PageSequence> getPageSequenceIterable() {
		return ps.getPageSequenceIterable();
	}
*/
	public Iterable<PageSequence> getPreVolumeContents(int volumeNumber, int volumeCount) {
		return getVolumeContents(volumeNumber, volumeCount, true);
	}

	public Iterable<PageSequence> getPostVolumeContents(int volumeNumber, int volumeCount) {
		return getVolumeContents(volumeNumber, volumeCount, false);
	}
	
	private Iterable<PageSequence> getVolumeContents(int volumeNumber, int volumeCount, boolean pre) {
		Paginator paginator2 = PaginatorFactory.newInstance().newPaginator();
		paginator2.open();
		BlockEventHandler beh = new BlockEventHandler(factory, masters, this);
		for (VolumeTemplate t : volumeTemplates) {
			if (t.appliesTo(volumeNumber, volumeCount)) {
				for (VolumeSequence seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
					switch (seq.getType()) {
						case TABLE_OF_CONTENTS:
							TocSequenceEvent toc = (TocSequenceEvent)seq;
							if (toc.appliesTo(volumeNumber, volumeCount)) {
								TableOfContents data = tocs.get(toc.getTocName());
								TocEvents events = toc.getTocEvents(volumeNumber, volumeCount);
								SequenceEventImpl evs = new SequenceEventImpl(toc.getSequenceProperties());
								for (BlockEvent e : events.getTocStartEvents()) {
									evs.push(e);
								}
								for (BlockEvent e : data) {
									evs.push(e);
								}
								for (BlockEvent e : events.getTocEndEvents()) {
									evs.push(e);
								}
								beh.format(evs);
								//FIXME: not complete
							}
							break;
						case STATIC:
							beh.format(((SequenceEvent)seq));
							break;
						default:
							throw new RuntimeException("Unexpected error");
					}
				}
				break;
			}
		}
		try {
			PaginatorHandler.paginate(beh.close(), paginator2);
			paginator2.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return paginator2.getPageStruct().getContents();
	}

	public Integer getVolumeNumber(String refid) {
		throw new RuntimeException("Not implemented");
	}

	public PageStruct getPageStruct() {
		return ps;
	}

	public Integer getPageNumber(String refid) {
		return ps.getPageNumber(refid);
	}

}
