package org.daisy.dotify.devtools.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.osgi.framework.BundleContext;

public class HyphPanel extends MyPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051107255963928066L;
	private final JTextArea textField;
	private final JTextArea outputField;

	private HyphTracker tracker;

	
	public HyphPanel() {
		setLayout(new GridLayout(2, 1));

		Font f = new Font(null, Font.BOLD, 26);

		outputField = new JTextArea();
		outputField.setFont(f);
		outputField.setEditable(false);

		textField = new JTextArea();
		textField.setFont(f);

		textField.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				updateResult();
			}

		});

		add(textField);
		add(new JScrollPane(outputField));

		setPreferredSize(new Dimension(500, 400));
	}
	
	protected void updateResult() {
		if (getTargetLocale()==null || getTargetLocale().equals("")) {
			outputField.setText("No locale selected");
		} else {
			HyphenatorFactoryMakerService t = tracker.get();
			if (t == null) {
				outputField.setText("No conversion");
			} else {
				if (textField.getText().equals("")) {
					outputField.setText("");
				} else {
					try {
						outputField.setText(t.newHyphenator(getTargetLocale()).hyphenate(textField.getText()));
					} catch (HyphenatorConfigurationException e1) {
						outputField.setText("Locale not supported: " + getTargetLocale() + "\n");
						ArrayList<String> locs = new ArrayList<String>();
						locs.addAll(t.listLocales());
						Collections.sort(locs);
						outputField.append("Supported values ("+locs.size()+"): \n");
						for (String s : locs) {
							outputField.append(s+"\n");
						}
					}
				}
			}
		}
	}

	public void openTracking(BundleContext context) {
		tracker = new HyphTracker(context);
		tracker.open();
	}

	public void closeTracking() {
		tracker.close();
	}

}
