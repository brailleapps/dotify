/**
 * <p>
 * Provides input implementations.
 * </p>
 * <h3>English conversions of DTBook</h3>
 * <ul>
 * <li><a href=
 * "../../../../../../xsltdoc/org.daisy.dotify.impl.input.common.xslt-files.dtbook2obfl_base.html"
 * >dtbook2flow.xsl</a></li>
 * <li><a href=
 * "../../../../../../xsltdoc/org.daisy.dotify.impl.input.common.xslt-files.xml2flow.html"
 * >xml2flow.xsl</a></li>
 * </ul>
 * <h3>Swedish conversion of DTBook</h3>
 * <ul>
 * <li><a href=
 * "../../../../../../xsltdoc/org.daisy.dotify.impl.input.sv_SE.xslt-files.dtbook2flow_sv_SE.html"
 * >dtbook2flow_sv_SE.xsl</a></li>
 * <li><a href=
 * "../../../../../../xsltdoc/org.daisy.dotify.impl.input.sv_SE.xslt-files.dtbook2flow_sv_SE_braille.html"
 * >dtbook2flow_sv_SE_braille.xsl</a></li>
 * <li><a href=
 * "../../../../../../xsltdoc/org.daisy.dotify.impl.input.sv_SE.xslt-files.dtbook2flow_sv_SE_text.html"
 * >dtbook2flow_sv_SE_text.xsl</a></li>
 * </ul>
 * <h3>Adding an input format conversion</h3>
 * <p>
 * Detecting an XML input format is easy, thanks to the XMLInputManager. It is
 * specifically designed to inject the correct validation rules and XSLT
 * stylesheet for any XML-format and locale combination into the task chain.
 * </p>
 * <p>
 * Adding a new format involves the following:
 * </p>
 * <ol>
 * <li>Modify the input_format_catalog.xml</li>
 * <li>Add a selector file in the folder hierarchy</li>
 * <li>Add the desired validation rules and style sheets in folder hierarchy</li>
 * </ol>
 * 
 * <p>
 * 1. Modifying the input format catalog involves adding an entry for the new
 * format. The key must be the root element of the format followed by '@' and
 * the namespace of the root element. The value for this key should be a short
 * but descriptive filename for the format. Typically, the root element followed
 * by '.properties' will suffice as filename. However, the filename must be
 * unique throughout the file, so another name may have to be chosen.
 * </p>
 * 
 * <p>
 * 2. Add a selector file into the folder hierarchy in the appropriate location
 * of the folder hierarchy. The location of the selector file is subject to
 * change, and is therefore not documented here. The selector file should
 * contain two entries: a path to the validation file and a path to the
 * transformation file (relative to the root folder of the locale).
 * </p>
 * 
 * <p>
 * 3. Add the desired validation and stylesheets files to the folder hierarchy
 * as indicated by the selector file. If there already exists an XSLT for the 
 * input format for another locale, the stylesheet could be copied into the desired
 * locale's directory and then modified. It is not recommended to extend another
 * locale's stylesheets.</p>
 * 
 * <p><b>IMPORTANT: This package contains implementations that should only be 
 * accessed using the Java Services API. Additional classes in this package 
 * should only be used by these implementations. This package is not part of the 
 * public API.</b>
 * </p>
 * @author Joel Håkansson
 */
package org.daisy.dotify.impl.input;