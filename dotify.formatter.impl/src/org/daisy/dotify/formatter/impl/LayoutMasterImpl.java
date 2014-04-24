package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.formatter.FieldList;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.PageArea;
import org.daisy.dotify.api.formatter.PageTemplate;
import org.daisy.dotify.api.formatter.SectionProperties;
import org.daisy.dotify.api.translator.TextBorderStyle;

/**
 * 
 * @author Joel Håkansson
 */
class LayoutMasterImpl implements LayoutMaster, LayoutMasterBuilder {
	private final SectionProperties props; 
	private final ArrayList<PageTemplate> templates;
	private final DefaultPageTemplate defaultPageTemplate;

	public LayoutMasterImpl(SectionProperties props) {
		this.templates = new ArrayList<PageTemplate>();
		this.props = props;
		this.defaultPageTemplate = new DefaultPageTemplate();
	}
	
	public void addTemplate(PageTemplate value) {
		templates.add(value);
	}

	public PageTemplate getTemplate(int pagenum) {
		for (PageTemplate t : templates) {
			if (t.appliesTo(pagenum)) { return t; }
		}
		return defaultPageTemplate;
	}

	public PageArea getPageArea() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPageWidth() {
		return props.getPageWidth();
	}

	public int getPageHeight() {
		return props.getPageHeight();
	}

	public float getRowSpacing() {
		return props.getRowSpacing();
	}

	public boolean duplex() {
		return props.duplex();
	}

	public int getFlowWidth() {
		return props.getFlowWidth();
	}

	public TextBorderStyle getBorder() {
		return props.getBorder();
	}

	public int getInnerMargin() {
		return props.getInnerMargin();
	}

	public int getOuterMargin() {
		return props.getOuterMargin();
	}

	private static class DefaultPageTemplate implements PageTemplate {
		private final List<FieldList> empty = new ArrayList<FieldList>();

		public List<FieldList> getHeader() {
			return empty;
		}

		public List<FieldList> getFooter() {
			return empty;
		}

		public boolean appliesTo(int pagenum) {
			return false;
		}
		
	}
}
