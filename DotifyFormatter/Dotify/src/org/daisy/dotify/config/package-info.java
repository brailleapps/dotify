/**
 * <p>
 * Provides means for adding configuration catalogs.
 * </p>
 * <p>
 * A catalog can be added by creating a new java project, implementing
 * <code>ConfigurationsProvider</code> and adding the file
 * <code>org.daisy.dotify.config.ConfigurationsProvider</code> to the
 * META-INF/services folder. This file should contain a qualified reference to
 * the new implementation. Package the result as a jar-file and included in the
 * Dotify classpath.
 * </p>
 * @author Joel Håkansson
 */
package org.daisy.dotify.config;