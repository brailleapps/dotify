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

public class HyphPanel extends MyPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051107255963928066L;
	private final JTextArea textField;
	private final JTextArea outputField;
	private final FactoryContext context;
	
	public HyphPanel(FactoryContext context) {
		setLayout(new GridLayout(2, 1));
		this.context = context;

		Font f = new Font(null, Font.BOLD, 26);

		outputField = new JTextArea();
		outputField.setFont(f);
		outputField.setEditable(false);

		textField = new JTextArea();
		textField.setFont(f);

		textField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateResult();
			}

		});

		add(textField);
		add(new JScrollPane(outputField));

		setPreferredSize(new Dimension(500, 400));
	}
	
	@Override
	protected void updateResult() {
		if (getTargetLocale()==null || getTargetLocale().equals("")) {
			outputField.setText("No locale selected");
		} else {
			HyphenatorFactoryMakerService t = context.getHyphenatorFactoryMakerService();
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

}
