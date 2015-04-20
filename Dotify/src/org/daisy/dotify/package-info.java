/**
 * <p>
 * Provides a braille translation system. The system can convert XML documents
 * into braille or other text-oriented display formats.
 * </p>
 * 
 * <p>
 * This package contains the application runnable (the command line ui), a
 * component for UI embedding of Dotify and system keys and properties.
 * </p>
 * <p>
 * The key packages for extending locale support in the application are:
 * </p>
 * <ul>
 * <li>org.daisy.dotify.config</li>
 * <li>org.daisy.dotify.setups</li>
 * <li>org.daisy.dotify.translator</li>
 * <li>org.daisy.dotify.hyphenator</li>
 * </ul>
 * <h3>Extending Dotify</h3>
 * <p>The entry point to extending Dotify is to implement a <i>task system</i>
 *  and add to it the tasks that your locale needs. The task system is 
 *  responsible for the entire conversion chain - from the input format 
 *  to the output format. A TaskSystem implementation should contain at least
 *  two steps in order to fulfill the general contract of Dotify:
 * </p>
 * <ul>
 * <li>An input format detection and conversion to OBFL
 * <p>
 * Since the Dotify formatter only understands OBFL markup, the first step in a task system
 * is typically a translation between the grammar in the input document and OBFL. 
 * In many cases, this can be achieved by applying an XSLT tailored for the task.
 * Page layout, such as headers and footers, as well as the interpretation of all
 * elements in the input document should be handled by this XSLT. See
 * http://code.google.com/p/obfl/ for a description of this format.</p>
 * </li>
 * <li>An OBFL format to a PagedMedia conversion.
 * <p>The LayoutEngineTask is specifically designed for handling OBFL input and
 * converting it into braille. For example:
 * <code>setup.add(new LayoutEngineTask("OBFL to PEF converter", flow,
    paginator, paged));</code>
 * </p>
 * </li>
 * </ul>
 * <p>
 * Note that the translator and hyphenator packages are located in a separate
 * project, see the repository layout for more information.
 * </p>
 * @author Joel Håkansson
 */
package org.daisy.dotify;