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


/**
 * <p>Abstract base class for internal tasks. This class is only
 * intended to be extended by classes in this package. Refer to the 
 * direct subclasses of this class for possible extension points.</p>
 * 
 * @author Joel Håkansson
 */
public abstract class InternalTask {
	protected String name = null;

	/**
	 * Creates a new internal task with the specfied name. The constructor
	 * is intended only for package use.
	 * @param name a descriptive name for the task
	 */
	InternalTask(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the internal task
	 * @return returns the name of this internal task
	 */
	public String getName() {
		return name;
	}

}
