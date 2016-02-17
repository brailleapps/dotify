package org.daisy.dotify.devtools.gui;

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
        		frame.setVisible(true);
            }
        });
	}

}
