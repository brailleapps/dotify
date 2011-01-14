/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.dotify.system;

import java.io.File;

/**
 * <p>Abstract base class for internal tasks.</p>
 * 
 * <p>InternalTask is an interface designed for a transformer internal 
 * conversion pipeline. Tasks are chained by file exchange.</p>
 * 
 * <p>The design is based on se_tpb_dtbookFix.Executor by Markus Gylling</p>
 * @author Joel Håkansson
 */
public abstract class InternalTask {
	protected String name = null;

	/**
	 * Constructor.
	 * @param name a descriptive name for the task
	 */
	protected InternalTask(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the internal task
	 * @return returns the name of this internal task
	 */
	public String getName() {
		return name;
	}

	/**
	 * Apply the task to <code>input</code> and place the result in <code>output</code>.
	 * @param input input file
	 * @param output output file
	 * @throws InternalTaskException throws InternalTaskException if something goes wrong.
	 */
	public abstract void execute(File input, File output) throws InternalTaskException;

}
