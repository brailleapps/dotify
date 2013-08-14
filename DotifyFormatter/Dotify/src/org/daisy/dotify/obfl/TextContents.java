package org.daisy.dotify.obfl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.formatter.TextProperties;

/**
 * Provides a text event object.
 * @author Joel Håkansson
 *
 */
class TextContents implements EventContents {
	private String text;
	private final TextProperties p;
	
	public TextContents(String text, TextProperties p) {
		this.text = text;
		this.p = p;
	}

	public ContentType getContentType() {
		return ContentType.PCDATA;
	}

	public String getText() {
		return text;
	}

	public TextProperties getSpanProperties() {
		return p;
	}

	public boolean canContainEventObjects() {
		return false;
	}

	public static List<String> getTextSegments(IterableEventContents ev) {
		List<String> chunks = new ArrayList<String>();
		for (EventContents c : ev) {
			switch (c.getContentType()) {
				case PCDATA:
					chunks.add(((TextContents) c).getText());
					break;
				default:
					if (c instanceof IterableEventContents) {
						chunks.addAll(getTextSegments((IterableEventContents) c));
					}
			}
		}
		return chunks;
	}

	public static void updateTextContents(IterableEventContents ev, String[] chunks) {
		updateTextContents(chunks, ev, 0);
	}

	private static int updateTextContents(String[] chunks, IterableEventContents ev, int i) {
		for (EventContents c : ev) {
			switch (c.getContentType()) {
				case PCDATA:
					((TextContents) c).text = chunks[i];
					i++;
					break;
				default:
					if (c instanceof IterableEventContents) {
						i = updateTextContents(chunks, (IterableEventContents) c, i);
					}
			}
		}
		return i;
	}
}
