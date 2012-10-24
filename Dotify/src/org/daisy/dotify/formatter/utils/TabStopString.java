package org.daisy.dotify.formatter.utils;


/**
 * Provides a tab stop string. A tab stop string is a data object
 * containg a string, a position, an alignment and a fill pattern.
 * This information can be used to place the string at the appropriate 
 * position on a row with the specified formatting. 
 * 
 * @author Joel Håkansson
 */
public class TabStopString implements Comparable<TabStopString> {
	/**
	 * Provides alignment options for a tab stop
	 */
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
	private int position;
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
		return position;
	}
	
	public void setPosition(int stop) {
		this.position = stop;
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

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((align == null) ? 0 : align.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + position;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabStopString other = (TabStopString) obj;
		if (align != other.align)
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (position != other.position)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
}
