package org.daisy.dotify.devtools.gui;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class GuiActivator implements BundleActivator {
	private MainFrame frame;

	@Override
	public void start(final BundleContext context) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		frame = new MainFrame();
		frame.pack();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					//context.getBundle().stop();
					System.exit(0);
				} catch (Exception e1) {//BundleException e1) {
					// Ignore
				}
			}
		});
		frame.openTracking(context);
		frame.setVisible(true);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		frame.setVisible(false);
		frame.closeTracking();
	}
}
