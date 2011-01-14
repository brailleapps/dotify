package org.daisy.dotify.system.tasks.layout.page;


/**
 * Specifies the layout of a paged media.
 * @author Joel Håkansson, TPB
 */
public interface LayoutMaster extends SectionProperties {

	/**
	 * Gets the template for the specified page number
	 * @param pagenum the page number to get the template for
	 * @return returns the template
	 */
	public Template getTemplate(int pagenum);

}
