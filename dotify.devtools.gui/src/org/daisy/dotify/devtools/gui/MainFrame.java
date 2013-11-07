package org.daisy.dotify.devtools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.osgi.framework.BundleContext;

public class MainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5599508953697168226L;
	private JTextField loc;
	private ArrayList<MyPanel> panels;
	
	public MainFrame() {
		//getContentPane().set
		super("Dotify OSGi bundle test GUI");
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 400));
		Int2TextPanel panel1;
		HyphPanel panel2;
		TranslatorPanel panel3;
		panels = new ArrayList<MyPanel>();
		
		panel1 = new Int2TextPanel();
		panel2 = new HyphPanel();
		panel3 = new TranslatorPanel();
		
		loc = new JTextField();
		loc.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				updateLocale();
			}

		});
		JTabbedPane pane = new JTabbedPane();
		addPanel("Numbers", pane, panel1);
		addPanel("Hyphenation", pane, panel2);
		addPanel("Translation", pane, panel3);
		
		JPanel north = new JPanel(new BorderLayout());
		north.add(new JLabel("Locale:"), BorderLayout.WEST);
		north.add(loc, BorderLayout.CENTER);
		add(north, BorderLayout.NORTH);
		add(pane, BorderLayout.CENTER);
	}
	
	private void addPanel(String desc, JComponent panel, MyPanel p) {
		panel.add(p, desc);
		panels.add(p);
	}
	
	protected void openTracking(BundleContext context) {
		for (MyPanel p : panels) {
			p.openTracking(context);
		}
	}

	protected void closeTracking() {
		for (MyPanel p : panels) {
			p.closeTracking();
		}
	}
	
	private void updateLocale() {
		for (MyPanel p : panels) {
			p.setTargetLocale(loc.getText());
		}
	}

}
