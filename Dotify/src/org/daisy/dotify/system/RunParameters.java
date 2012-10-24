package org.daisy.dotify.system;

import java.util.Map;
import java.util.Properties;

public class RunParameters {
	private final Properties p;
	private final int flowWidth;
	private final int innerMargin;
	private final int outerMargin;
	private final float rowgap;
	
	private RunParameters(Properties p) {
		this.p = p;
		this.flowWidth = Integer.parseInt(p.getProperty("cols", "28"));
		int pageHeight = Integer.parseInt(p.getProperty("rows", "29"));
		this.innerMargin = Integer.parseInt(p.getProperty("inner-margin", "5"));
		this.outerMargin = Integer.parseInt(p.getProperty("outer-margin", "2"));
		this.rowgap = Float.parseFloat(p.getProperty("rowgap", "0"));

		this.p.put("page-height", pageHeight);
		this.p.put("page-width", flowWidth+innerMargin+outerMargin);
		this.p.put("row-spacing", (rowgap/4)+1);
	}
	
	public static RunParameters load(Properties p0, Map<String, String> guiParams) {
		Properties p = new Properties();
		p.putAll(p0);

		// GUI parameters should take precedence
		p.putAll(guiParams);

		return new RunParameters(p);
	}

	public String getProperty(Object key) {
		return p.get(key).toString();
	}
	
	public Iterable<Object> getKeys() {
		return p.keySet();
	}

	/**
	 * @return the flowWidth
	 */
	public int getFlowWidth() {
		return flowWidth;
	}

	/**
	 * @return the pageHeight
	 *//*
	public int getPageHeight() {
		return pageHeight;
	}*/

	/**
	 * @return the innerMargin
	 */
	public int getInnerMargin() {
		return innerMargin;
	}

	/**
	 * @return the outerMargin
	 */
	public int getOuterMargin() {
		return outerMargin;
	}

	/**
	 * @return the rowgap
	 */
	public float getRowgap() {
		return rowgap;
	}

	@Override
	public String toString() {
		return p.toString();
	}
}
