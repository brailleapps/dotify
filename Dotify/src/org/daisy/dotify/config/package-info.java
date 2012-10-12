/**
 * <p>
 * Provides means for adding configuration catalogs as well as a default
 * catalog.
 * </p>
 * 
 * <h2>Adding a catalog</h2>
 * 
 * <p>
 * A catalog can be added by creating a new java project, implementing
 * <code>ConfigurationsProvider</code> and adding the file
 * <code>org.daisy.dotify.config.ConfigurationsProvider</code> to the
 * META-INF/services folder. This file should contain a qualified reference to
 * the new implementation. Package the result as a jar-file and included in the
 * Dotify classpath.
 * </p>
 * 
 * <h2>Default configurations</h2>
 * 
 * <h4>A4_w32</h4>
 * 
 * <p>
 * This setup will create braille documents intended for A4 while maintaining
 * compatibility with existing software. Specifically, the paper size in the
 * output will be 32 braille cells wide. Other properties of this setup are:
 * </p>
 * <ul>
 * <li>Page height: 29 characters</li>
 * <li>Inner margin: 2 characters</li>
 * <li>Outer margin: 2 characters</li>
 * <li>Row gap: 0</li>
 * <li>Volume size: about 50 sheets</li>
 * </ul>
 * 
 * <h4>FA44_w42</h4>
 * 
 * <p>
 * This setup will create braille documents intended for FA44 while maintaining
 * compatibility with existing software. Specifically, the paper size in the
 * output will be 42 braille cells wide. Other properties of this setup are:
 * </p>
 * <ul>
 * <li>Page height: 29 characters</li>
 * <li>Inner margin: 3 characters</li>
 * <li>Outer margin: 3 characters</li>
 * <li>Row gap: 0</li>
 * <li>Volume size: about 50 sheets</li>
 * </ul>
 * 
 * <h4>w50</h4>
 * <p>
 * This setup will create text documents 50 characters wide. Other properties of
 * this setup are:
 * </p>
 * <ul>
 * <li>Page height: 40 characters</li>
 * <li>Inner margin: 0 characters</li>
 * <li>Outer margin: 0 characters</li>
 * <li>Row gap: 0</li>
 * </ul>
 * @author Joel Håkansson
 */
package org.daisy.dotify.config;