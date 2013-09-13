package org.daisy.dotify.api.hyphenator;

/**
 * Provides a catalog of common hyphenation features. These can be used when getting or
 * setting features of hyphenation factories.
 * 
 * @author Joel Håkansson
 *
 */
public interface HyphenationFeatures {
	
	/**
	 * <p>Defines hyphenation accuracy on a scale from 1 to 5 (integer):</p>
	 * <ul><li>5 is high accuracy, low performance</li>
	 * <li>3 is medium accuracy, medium performance</li>
	 * <li>1 is low accuracy, high performance</li></ul>
	 * 
	 * <p>Not all values must be implemented to support this feature.
	 * It is recommended, but not strictly required, that an implementation
	 * sends a log message when this feature is set to an unsupported value.</p>
	 */
	public String HYPHENATION_ACCURACY = "hyphenation-accuracy";

}
