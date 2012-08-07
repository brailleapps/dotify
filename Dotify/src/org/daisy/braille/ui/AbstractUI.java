/*
 * Braille Utils (C) 2010-2011 Daisy Consortium 
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
package org.daisy.braille.ui;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Provides an abstract base for command line UI's. NOTE:
 * This class is copied from Braille Utils as a temporary method for adding this 
 * functionality. However, the official braille utils jar-file should instead
 * be used, and this file removed. At this time (August 2012) there exists some
 * compatibility issues between braille utils 1.2 and 1.1.0 and Daisy Pipeline. 
 * @author Joel Håkansson
 */
public abstract class AbstractUI {
	/**
	 * Provides exit codes to be used by implementing classes.
	 */
	public enum ExitCode {
		/**
		 * Normal application termination
		 */
		OK,
		/**
		 * Missing a required argument
		 */
		MISSING_ARGUMENT,
		/**
		 * Argument is unknown to the application
		 */
		UNKNOWN_ARGUMENT,
		/**
		 * 
		 */
		FAILED_TO_READ,
		/**
		 * 
		 */
		MISSING_RESOURCE,
		/**
		 * Argument value is illegal
		 */
		ILLEGAL_ARGUMENT_VALUE
	};
	/**
	 * Prefix used for required arguments in the arguments map
	 */
	public final static String ARG_PREFIX = "required-";
	private String delimiter;
	private String optionalArgumentPrefix;

	/**
	 * Provides a definition, that is a name and a description
	 * @author Joel Håkansson
	 */
	public static class Definition {
		private final String name;
		private final String desc;
		
		/**
		 * Creates a new Definition.
		 * @param name the name of the definition
		 * @param desc the description of the definition
		 */
		public Definition(String name, String desc) {
			this.name = name;
			this.desc = desc;
		}

		/**
		 * Gets the name of the definition
		 * @return returns the name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Gets the description of the definition
		 * @return returns the description
		 */
		public String getDescription() {
			return desc;
		}
	}

	/**
	 * Provides the information needed by an application argument.
	 * @author Joel Håkansson
	 */
	public static class Argument extends Definition {
		private final List<Definition> values;
		
		/**
		 * Creates a new Argument.
		 * @param name the name of the argument
		 * @param desc the description of the argument
		 */
		public Argument(String name, String desc) {
			this(name, desc, null);
		}
		
		/**
		 * Creates a new Argument with a finite list of acceptable values.
		 * @param name the name of the argument
		 * @param desc the description of the argument
		 * @param values the list of acceptable values
		 */
		public Argument(String name, String desc, List<Definition> values) {
			super(name, desc);
			this.values = values;
		}

		/**
		 * Returns true if this argument has a finite list of acceptable values.
		 * @return returns true if a finite list of acceptable values exist, false otherwise
		 */
		public boolean hasValues() {
			return values!=null && values.size()>0;
		}
		
		/**
		 * Gets the list of acceptable values.
		 * @return returns the list of acceptable values, or null if the list of possible values 
		 * is infinite
		 */
		public List<Definition> getValues() {
			return values;
		}
	}

	/**
	 * Provides the information needed by an optional argument.
	 * @author Joel Håkansson
	 */
	public static class OptionalArgument extends Argument {
		private final String defaultValue;
		
		/**
		 * Creates a new optional argument
		 * @param name the name of the argument
		 * @param description the description of the argument
		 * @param defaultValue the default value for the argument
		 */
		public OptionalArgument(String name, String description, String defaultValue) {
			super(name, description);
			this.defaultValue = defaultValue;
		}
		
		/**
		 * Creates a new optional argument with a finite list of acceptable values.
		 * @param name the name of the argument
		 * @param description the description of the argument
		 * @param values the list of acceptable values
		 * @param defaultValue the default value for the argument
		 */
		public OptionalArgument(String name, String description, List<Definition> values, String defaultValue) {
			super(name, description, values);
			this.defaultValue = defaultValue;
		}
		
		/**
		 * Gets the default value.
		 * @return returns the default value for the argument
		 */
		public String getDefault() {
			return defaultValue;
		}

	}
	
	/**
	 * Creates a new AbstractUI using the default key/value delimiter '=' and
	 * the default optional argument prefix '-'
	 */
	public AbstractUI() {
		setKeyValueDelimiter("=");
		setOptionalArgumentPrefix("-");
	}

	/**
	 * Sets the delimiter to use between key and value in the argument
	 * strings passed to the UI.
	 * @param value the delimiter to use
	 */
	public void setKeyValueDelimiter(String value) {
		delimiter = value;
	}
	
	/**
	 * Sets the optional argument prefix to use in argument strings passed to
	 * the UI.
	 * @param value the prefix to use
	 */
	public void setOptionalArgumentPrefix(String value) {
		if (ARG_PREFIX.equals(value)) {
			throw new IllegalArgumentException("Prefix is reserved: " + ARG_PREFIX);
		}
		optionalArgumentPrefix = value;
	}
	
	/**
	 * Gets the name for the UI
	 * @return returns the UI name
	 */
	public abstract String getName();
	
	/**
	 * Gets required arguments
	 * @return returns a list of required arguments that can be
	 * passed to the UI on startup
	 */
	public abstract List<Argument> getRequiredArguments();
	
	/**
	 * Gets optional arguments
	 * @return returns a list of optional arguments that can be
	 * passed to the UI on startup
	 */
	public abstract List<OptionalArgument> getOptionalArguments();

	/**
	 * Converts a string based definition of UI arguments, typically 
	 * passed by the main method, into a key-value map as described
	 * by displayHelp
	 * @param args the arguments passed to the application
	 * @return returns a map of application arguments
	 */
	public Map<String, String> toMap(String[] args) {
		Hashtable<String, String> p = new Hashtable<String, String>();
		int i = 0;
		String[] t;
		for (String s : args) {
			s = s.trim();
			t = s.split(delimiter, 2);
			if (s.startsWith(optionalArgumentPrefix) && t.length==2) {
				p.put(t[0].substring(1), t[1]);
			} else {
				p.put(ARG_PREFIX+i, s);
			}
			i++;
		}
		return p;
	}
	
	/**
	 * Displays a help text for the UI based on the implementation of 
	 * the methods getName, getOptionalArguments and getRequiredArguments. 
	 * @param ps The print stream to use, typically System.out
	 */
	public void displayHelp(PrintStream ps) {
		ps.print(getName());
		if (getRequiredArguments()!=null && getRequiredArguments().size()>0) {
			for (Argument a : getRequiredArguments()) {
				ps.print(" ");
				ps.print(a.getName());
			}
		}
		if (getOptionalArguments()!=null && getOptionalArguments().size()>0) {
			ps.print(" [options ... ]");
		}
		ps.println();
		ps.println();
		if (getRequiredArguments()!=null && getRequiredArguments().size()>0) {
			for (Argument a : getRequiredArguments()) {
				ps.println("Required argument:");
				ps.println("\t" + a.getName()+ " - " + a.getDescription());
				ps.println();
				if (a.hasValues()) {
					ps.println("\tValues:");
					for (Definition value : a.getValues()) {
						ps.println("\t\t'"+value.getName() + "' - " + value.getDescription());
					}
					ps.println();
				}
			}
		}
		if (getOptionalArguments()!=null && getOptionalArguments().size()>0) {
			ps.println("Optional arguments:");
			for (OptionalArgument a : getOptionalArguments()) {
				ps.print("\t" + optionalArgumentPrefix + a.getName() + delimiter + "[value]");
				if (!a.hasValues()) {
					ps.print(" (default '"  + a.getDefault() + "')");
				}
				ps.println();
				ps.println("\t\t" + a.getDescription());
				if (a.hasValues()) {
					ps.println("\t\tValues:");
					for (Definition value : a.getValues()) {
						ps.print("\t\t\t'"+value.getName() + "' - " + value.getDescription());
						if (value.getName().equals(a.getDefault())) {
							ps.println(" (default)");
						} else {
							ps.println();
						}
					}
				}
			}
			ps.println();
		}
	}
}
