package org.daisy.dotify.formatter.impl;

import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.Page;
import org.daisy.dotify.formatter.PageSequence;

public class PageSequenceCloneImpl implements PageSequence {
	private final Stack<Page> pages;
	private final LayoutMaster master;
	private final int pageOffset;
	private final FormatterFactory formatterFactory;
	private Formatter formatter;
	
	public PageSequenceCloneImpl(LayoutMaster master, int pageOffset, FormatterFactory formatterFactory) {
		this.pages = new Stack<Page>();
		this.master = master;
		this.pageOffset = pageOffset;
		this.formatterFactory = formatterFactory;
		this.formatter = null;
	}
	
	public void addPage(Page p) {
		pages.add(new PageCloneImpl(p, this));
	}

	public Iterator<Page> iterator() {
		return pages.iterator();
	}

	public LayoutMaster getLayoutMaster() {
		return master;
	}

	public int getPageCount() {
		return pages.size();
	}

	public Page getPage(int index) {
		return pages.get(index);
	}

	public int getPageNumberOffset() {
		return pageOffset;
	}

	public FormatterFactory getFormatterFactory() {
		return formatterFactory;
	}

	public Formatter getFormatter() {
		if (formatter == null) {
			formatter = formatterFactory.newFormatter();
		}
		return formatter;
	}

}
