package org.daisy.dotify.devtools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.osgi.framework.BundleContext;

public class MainFrame extends JFrame {
	private final Logger logger = Logger.getLogger(MainFrame.class.getCanonicalName());
	/**
	 * 
	 */
	private static final long serialVersionUID = -5599508953697168226L;
	private JTextField loc;
	private ArrayList<MyPanel> panels;
	
	final JTabbedPane pane;
	final TranslatorPanel panel3;
	final EmbossPanel panel6;
	
	public MainFrame() {
		//getContentPane().set
		super("Dotify/Braille Utils live OSGi test GUI");
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(600, 400));

		panels = new ArrayList<MyPanel>();
		
		Int2TextPanel panel1 = new Int2TextPanel();
		HyphPanel panel2 = new HyphPanel();
		panel3 = new TranslatorPanel();
		FormatterPanel panel4 = new FormatterPanel();
		ValidatorPanel panel5 = new ValidatorPanel();
		panel6 = new EmbossPanel();
		
		loc = new JTextField();
		loc.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateLocale();
			}

		});
		pane = new JTabbedPane();
		addPanel("Numbers", pane, panel1);
		addPanel("Hyphenation", pane, panel2);
		addPanel("Translation", pane, panel3);
		addPanel("Formatter", pane, panel4);
		addPanel("Validator", pane, panel5);
		addPanel("Emboss", pane, panel6);
		
		pane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateSelectedPane();
			}
		});
		addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent arg0) {
				logger.finer("Window focus lost.");
				
			}
			
			@Override
			public void windowGainedFocus(WindowEvent arg0) {
				logger.finer("Window focus gained.");
				updateSelectedPane();
				
			}
		});
		
		JPanel north = new JPanel(new BorderLayout());
		north.add(new JLabel("Locale:"), BorderLayout.WEST);
		north.add(loc, BorderLayout.CENTER);
		add(north, BorderLayout.NORTH);
		add(pane, BorderLayout.CENTER);
	}
	
	private void updateSelectedPane() {
		if (pane.getSelectedComponent()==panel6) {
			panel6.updateLists();
		} else if (pane.getSelectedComponent()==panel3) {
			panel3.updateTableList();
		}
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
