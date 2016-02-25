package org.daisy.dotify.devtools.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
        		try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e2) {
					e2.printStackTrace();
				}
        		MainFrame frame = new MainFrame("Dotify/Braille Utils SPI test GUI", new SpiFactoryContext());
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
        		frame.setVisible(true);
            }
        });
	}

}
