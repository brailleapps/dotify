package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.FieldList;
import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.formatter.PageAreaBuilder;
import org.daisy.dotify.api.formatter.PageAreaProperties;
import org.daisy.dotify.api.formatter.PageTemplateBuilder;
import org.daisy.dotify.api.translator.TextBorderStyle;

/**
 * Specifies the layout of a paged media.
 * @author Joel Håkansson
 */
class LayoutMaster implements LayoutMasterBuilder {
	private final LayoutMasterProperties props; 
	private final ArrayList<PageTemplate> templates;
	private final DefaultPageTemplate defaultPageTemplate;
	private PageAreaBuilderImpl pageArea;

	public LayoutMaster(LayoutMasterProperties props) {
		this.templates = new ArrayList<PageTemplate>();
		this.props = props;
		this.defaultPageTemplate = new DefaultPageTemplate();
		this.pageArea = null;
	}
	
	public PageTemplateBuilder newTemplate(Condition c) {
		PageTemplateImpl p = new PageTemplateImpl(c);
		templates.add(p);
		return p;
	}

	/**
	 * Gets the template for the specified page number
	 * @param pagenum the page number to get the template for
	 * @return returns the template
	 */
	public PageTemplate getTemplate(int pagenum) {
		for (PageTemplate t : templates) {
			if (t.appliesTo(pagenum)) { return t; }
		}
		return defaultPageTemplate;
	}

	/**
	 * Gets the page area for all pages using this master.
	 * @return returns the PageArea, or null if no page area is used.
	 */
	public PageAreaProperties getPageArea() {
		return (pageArea!=null?pageArea.getProperties():null);
	}
	
	PageAreaBuilderImpl getPageAreaBuilder() {
		return pageArea;
	}

	/**
	 * Gets the page width.
	 * An implementation must ensure that getPageWidth()=getFlowWidth()+getInnerMargin()+getOuterMargin()
	 * @return returns the page width
	 */
	public int getPageWidth() {
		return props.getPageWidth();
	}

	/**
	 * Gets the page height.
	 * An implementation must ensure that getPageHeight()=getHeaderHeight()+getFlowHeight()+getFooterHeight()
	 * @return returns the page height
	 */
	public int getPageHeight() {
		return props.getPageHeight();
	}

	/**
	 * Gets row spacing, in row heights. For example, use 2.0 for double row spacing and 1.0 for normal row spacing.
	 * @return returns row spacing
	 */
	public float getRowSpacing() {
		return props.getRowSpacing();
	}

	/**
	 * Returns true if output is intended on both sides of the sheets
	 * @return returns true if output is intended on both sides of the sheets
	 */
	public boolean duplex() {
		return props.duplex();
	}

	/**
	 * Gets the flow width
	 * @return returns the flow width
	 */
	public int getFlowWidth() {
		return props.getFlowWidth();
	}

	/**
	 * Gets the border.
	 * @return the border
	 */
	public TextBorderStyle getBorder() {
		return props.getBorder();
	}

	/**
	 * Gets inner margin
	 * @return returns the inner margin
	 */
	public int getInnerMargin() {
		return props.getInnerMargin();
	}

	/**
	 * Gets outer margin
	 * @return returns the outer margin
	 */
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

	public PageAreaBuilder setPageArea(PageAreaProperties properties) {
		pageArea = new PageAreaBuilderImpl(properties);
		return pageArea;
	}
}
