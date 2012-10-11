/**
 * <p>Provides braille transformation classes.</p>
 * <p>Note: the following information is subject to change.</p>
 * <p>To add rules for another language:</p>
 * <ul>
 * 	<li>Create a table that follows the rules of UCharReplacer (for more
 *  information, see the int_daisy_unicodeTranscoder documentation in Daisy Pipeline), 
 *  and place it in a new sub package to this package, e.g "de_DE/de_DE.xml".<br />
 *  <em>If your locale has special requirements (such as contractions), this
 *  method cannot be used. In this case, another implementation must be
 *  created. If your class implements the StringFilter interface, the next step
 *  can still be applied.</em></li>
 *  <li>Add your locale to the system, by adding the name of your implementation
 *	to the org.daisy.dotify.translator.BrailleFilter file in META-INF/services.
 * </li>
 * </ul>
 * @author Joel Håkansson
 */
package org.daisy.dotify.translator;