package org.daisy.dotify.formatter.utils;

/**
 * Breaks a paragraph of text into rows. It is assumed that all 
 * preferred break points are supplied with the input String.
 * As a consequence, non standard hyphenation is not supported. 
 * @author Joel Håkansson, TPB
 *
 */
public class BreakPointHandler {
	private String charsStr;
	
	/**
	 * Create a new BreakPointHandler. All preferred break points 
	 * must be in supplied with the input String, represented by 
	 * hyphen 0x2d, soft hyphen 0xad or space 0x20.
	 * @param str the paragraph to break into rows. 
	 */
	public BreakPointHandler(String str) {
		if (str==null) {
			throw new NullPointerException("Input string cannot be null.");
		}
		this.charsStr = str;
	}

	/**
	 * Get the next row from this BreakPointHandler
	 * @param breakPoint the desired breakpoint for this row
	 * @return returns the next BreakPoint
	 */
	public BreakPoint nextRow(int breakPoint) {
		if (charsStr.length()==0) {
			// pretty simple...
			return new BreakPoint("", "", false);
		}
		String head;
		String tail;
		boolean hard = false;
		assert charsStr.length()==charsStr.codePointCount(0, charsStr.length());
		if (charsStr.length()<=breakPoint) {
			head = charsStr;
			tail = "";
		} else if (breakPoint<=0) {
			head = "";
			tail = charsStr;
		} else {
			int strPos = -1;
			int len = 0;
			for (char c : charsStr.toCharArray()) {
				strPos++;
				switch (c) {
					case '\u00ad': 
						break;
					default:
						len++;
				}
				if (len>=breakPoint) {
					break;
				}
			}
			assert strPos<charsStr.length();
			
			int tailStart;
			
			/*if (strPos>=charsStr.length()-1) {
				head = charsStr.substring(0, strPos);
				System.out.println(head);
				tailStart = strPos;
			} else */
			// check next character to see if it can be removed.
			if (strPos==charsStr.length()-1) {
				head = charsStr.substring(0, strPos+1);
				tailStart = strPos+1;
			} else if (charsStr.charAt(strPos+1)==' ') {
				head = charsStr.substring(0, strPos+2); // strPos+1
				tailStart = strPos+2;
			} else { // back up
				int i=strPos;
whileLoop:		while (i>=0) {
					switch (charsStr.charAt(i)) {
						case ' ' : case '-' : case '\u00ad' : 
							break whileLoop;
					}
					i--;
				}
				if (i<0) { // no breakpoint found, break hard 
					hard = true;
					head = charsStr.substring(0, strPos+1);
					tailStart = strPos+1;
				} else if (charsStr.charAt(i)==' ') { // don't ignore space at breakpoint
					head = charsStr.substring(0, i+1); //i
					tailStart = i+1;
				} else if (charsStr.charAt(i)=='\u00ad'){ // convert soft hyphen to hard hyphen 
					head = charsStr.substring(0, i) + '-';
					tailStart = i+1;
				} else if (charsStr.charAt(i)=='-' && charsStr.length()>1 && charsStr.charAt(i-1)==' ') {
					// if hyphen is preceded by space, back up one more
					head = charsStr.substring(0, i);
					tailStart = i;
				} else {
					head = charsStr.substring(0, i+1);
					tailStart = i+1;
				}
			}
			if (charsStr.length()>tailStart) {
				tail = charsStr.substring(tailStart);
			} else {
				tail = "";
			}
		}
		assert (tail.length()<charsStr.length());
		charsStr = tail;
		head = head.replaceAll("\u00ad", "");
		return new BreakPoint(head, tail, hard);
	}

	/**
	 * Does this BreakPointHandler has any text left to break into rows 
	 * @return returns true if this BreakPointHandler has any text left to break into rows
	 */
	public boolean hasNext() {
		return (charsStr!=null && charsStr.length()>0);
	}

}
