package org.daisy.dotify.system.tasks.layout.utils;

import java.util.TreeSet;

public class TabStopString implements Comparable<TabStopString> {
	public enum Alignment {
		/**
		 * Text run to the right of the specified position
		 */
		LEFT,
		/**
		 * Text is centered around the specified position
		 */
		CENTER,
		/**
		 * Text run to the left of the specified position
		 */
		RIGHT
	};
	private String text;
	private int stop;
	private int length;
	private Alignment align;
	private String pattern;

	public TabStopString(String text) {
		init(text, 0, Alignment.LEFT, " ");
	}
	
	public TabStopString(String text, int stop) {
		init(text, stop, Alignment.LEFT, " ");
	}
	
	public TabStopString(String text, int stop, Alignment align) {
		init(text, stop, align, " ");
	}
	
	public TabStopString(String text, int stop, Alignment align, String pattern) {
		init(text, stop, align, pattern);
	}
	
	private void init(String text, int stop, Alignment align, String pattern) {
		setText(text);
		setPosition(stop);
		setAlignment(align);
		setPattern(pattern);
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
		this.length = text.codePointCount(0, text.length());
	}
	
	public int getPosition() {
		return stop;
	}
	
	public void setPosition(int stop) {
		this.stop = stop;
	}
	
	public Alignment getAlignment() {
		return align;
	}
	
	public void setAlignment(Alignment align) {
		this.align = align;
	}
	
	public String getPattern() {
		return this.pattern;
	}
	
	public void setPattern(String pattern) {
		if (pattern.length()==0) {
			throw new IllegalArgumentException("Pattern cannot be empty string");
		}
		this.pattern = pattern;
	}

	public int length() {
		return length; 
	}

	public String toString() {
		return "{\"" + getText() + "\", " + getPosition() + ", " + getAlignment() + ", \"" + getPattern() + "\"}";
	}

	public int compareTo(TabStopString o) {
		if (getPosition()<o.getPosition()) {
			return -1;
		} else if (getPosition()>o.getPosition()) {
			return 1;
		} else {
			if (getText().equals(o.getText())) {
				if (getAlignment().equals(o.getAlignment())) {
					return getPattern().compareTo(o.getPattern());
				} else {
					return getAlignment().compareTo(o.getAlignment());
				}
			} else {
				return getText().compareTo(o.getText());
			}
		}
	}

    public boolean equals(Object obj) {
    	if (this == obj) {
    	    return true;
    	}
    	if (obj instanceof TabStopString) {
    		TabStopString o = (TabStopString)obj;
    		if (getPosition() != o.getPosition()) {
    			return false;
    		} else if (!getText().equals(o.getText())) {
    			return false;
    		} else if (!getAlignment().equals(o.getAlignment())) {
    			return false;
    		} else if (!getPattern().equals(o.getPattern())) {
    			return false;
    		}
    		return true;
    	}
    	return false;
    }

    public static void main(String args[]) {
    	TreeSet<TabStopString> ts = new TreeSet<TabStopString>();
    	ts.add(new TabStopString("Text1", 15));
    	ts.add(new TabStopString("Text2", 13));
    	ts.add(new TabStopString("Text3", 3));
    	ts.add(new TabStopString("Text4", 27));
    	ts.add(new TabStopString("Text", 11, Alignment.CENTER));
    	ts.add(new TabStopString("Text", 11));
    	ts.add(new TabStopString("Text", 11,  Alignment.LEFT, "1"));
    	for (TabStopString tss : ts) {
    		System.out.println(tss.toString());
    	}
    	System.out.println(new TabStopString("Text", 1).equals(new TabStopString("Text", 1)));
    }
	
}
