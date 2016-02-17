package org.daisy.dotify.devtools.gui;


import javax.swing.UIManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class GuiActivator implements BundleActivator {
	private MainFrame frame;
	private OsgiFactoryContext factoryContext;

	@Override
	public void start(final BundleContext context) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		factoryContext = new OsgiFactoryContext();
		frame = new MainFrame("Dotify/Braille Utils live OSGi test GUI", factoryContext);
		factoryContext.openTracking(context);
		frame.setVisible(true);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		frame.setVisible(false);
		factoryContext.closeTracking();
	}
}
