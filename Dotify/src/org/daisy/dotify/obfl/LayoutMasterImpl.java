package org.daisy.dotify.obfl;

import java.util.ArrayList;

import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.PageTemplate;

/**
 * ConfigurableLayoutMaster will ensure that the LayoutMaster measurements adds up.
 * @author Joel Håkansson, TPB
 */
class LayoutMasterImpl implements LayoutMaster {
	protected final int headerHeight;
	protected final int footerHeight;
	protected final int flowWidth;
	//protected final int flowHeight;
	protected final int pageWidth;
	protected final int pageHeight;
	protected final int innerMargin;
	protected final int outerMargin;
	protected final float rowSpacing;
	protected final boolean duplex;
	protected final ArrayList<PageTemplate> templates;
	
	/**
	 * Configuration class for a ConfigurableLayoutMaster
	 * @author Joel Håkansson, TPB
	 *
	 */
	public static class Builder {
		int pageWidth;
		int pageHeight;
		// optional
		int headerHeight = 0; 
		int footerHeight = 0;
		int innerMargin = 0;
		int outerMargin = 0;
		float rowSpacing = 1;
		boolean duplex = true;
		ArrayList<PageTemplate> templates;

		public Builder(int pageWidth, int pageHeight) {
			this.pageWidth = pageWidth;
			this.pageHeight = pageHeight;
			this.templates = new ArrayList<PageTemplate>();
		}
		
		public Builder headerHeight(int value) {
			this.headerHeight = value;
			return this;
		}

		public Builder footerHeight(int value) {
			this.footerHeight = value;
			return this;
		}

		public Builder innerMargin(int value) {
			this.innerMargin = value;
			return this;
		}
		
		public Builder outerMargin(int value) {
			this.outerMargin = value;
			return this;
		}
		
		public Builder rowSpacing(float value) {
			this.rowSpacing = value;
			return this;
		}
		
		public Builder duplex(boolean value) {
			this.duplex = value;
			return this;
		}
		
		public Builder addTemplate(PageTemplate value) {
			this.templates.add(value);
			return this;
		}
		
		public LayoutMasterImpl build() {
			return new LayoutMasterImpl(this);
		}
	}

	private LayoutMasterImpl(Builder config) {
		// int flowWidth, int flowHeight, int headerHeight, int footerHeight, int innerMargin, int outerMargin, float rowSpacing
		this.headerHeight = config.headerHeight;
		this.footerHeight = config.footerHeight;
		this.flowWidth = config.pageWidth-config.innerMargin-config.outerMargin;
		//this.flowHeight = config.pageHeight-config.headerHeight-config.footerHeight;
		this.pageWidth = config.pageWidth;
		this.pageHeight = config.pageHeight;
		this.innerMargin = config.innerMargin;
		this.outerMargin = config.outerMargin;
		this.rowSpacing = config.rowSpacing;
		this.duplex = config.duplex;
		this.templates = config.templates;
	}
	
	public int getPageWidth() {
		return pageWidth;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public int getFlowWidth() {
		return flowWidth;
	}
/*
	public int getFlowHeight() {
		return flowHeight;
	}*/

	public int getHeaderHeight() {
		return headerHeight;
	}

	public int getFooterHeight() {
		return footerHeight;
	}

	public int getInnerMargin() {
		return innerMargin;
	}

	public int getOuterMargin() {
		return outerMargin;
	}
	
	public float getRowSpacing() {
		return rowSpacing;
	}
	
	public boolean duplex() {
		return duplex;
	}
	
	public PageTemplate getTemplate(int pagenum) {
		for (PageTemplate t : templates) {
			if (t.appliesTo(pagenum)) { return t; }
		}
		// if no template applies, an empty template should be returned
		// since adding templates is optional in Builder
		return new PageTemplateImpl();
	}

}
