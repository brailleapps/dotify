package org.daisy.dotify.devtools.gui;

import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class DeveloperUtility extends javax.swing.JFrame {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3237476094594239863L;

	public DeveloperUtility() {
		initComponents();
	}

    private void initComponents() {
        //Create and set up the window.
		setTitle("Dotify Developer Utilities");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JTabbedPane tabbedPane = new JTabbedPane();

		JComponent panel1 = new CodePointUtility();
		tabbedPane.addTab("Code Points", null, panel1, "Convert characters to codepoints");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = new TranslatorDemo(new SpiFactoryContext());
		tabbedPane.addTab("Translator", null, panel2, "Translate into braille");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		add(tabbedPane);
        //Display the window.
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                new DeveloperUtility().setVisible(true);
            }
        });
    }
    
}
