package org.daisy.dotify.translator.attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DefaultMarkerProcessor implements MarkerProcessor {
	private final Map<String, MarkerDictionary> specs;

	public static class Builder {
		private final Map<String, MarkerDictionary> specs;

		public Builder() {
			specs = new HashMap<String, MarkerDictionary>();
		}

		public Builder addDictionary(String identifier, MarkerDictionary def) {
			specs.put(identifier, def);
			return this;
		}

		public DefaultMarkerProcessor build() {
			return new DefaultMarkerProcessor(this);
		}
	}

	private DefaultMarkerProcessor(Builder builder) {
		this.specs = builder.specs;
	}

	public String process(String text, TextAttribute atts) {
		if (atts == null) {
			return text;
		} else {
			if (atts.getWidth() != text.length()) {
				throw new IllegalArgumentException("Text attribute width (" + atts.getWidth() + ") does not match text length (" + text.length() + ").");
			}
			StringBuilder sb = new StringBuilder();

			Marker m = getMarker(atts.getDictionaryIdentifier(), text);

			if (m != null) {
				sb.append(m.getPrefix());
			}
			int startInx = 0;
			if (atts.hasAttributes()) {
				for (TextAttribute d : atts) {
					sb.append(process(text.substring(startInx, startInx + d.getWidth()), d));
					startInx += d.getWidth();
				}
			} else {
				sb.append(text.substring(0, atts.getWidth()));
			}
			if (m != null) {
				sb.append(m.getPostfix());
			}
			return sb.toString();
		}
	}

	private Marker getMarker(String specKey, String text) {
		if (specKey != null) {
			MarkerDictionary def = specs.get(specKey);
			if (def != null) {
				try {
					return def.getMarkersFor(text);
				} catch (MarkerNotFoundException e) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, specKey + " has no marker information for " + text, e);
				}
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).warning("Undefined attribute: " + specKey);
			}
		}
		return null;
	}

}