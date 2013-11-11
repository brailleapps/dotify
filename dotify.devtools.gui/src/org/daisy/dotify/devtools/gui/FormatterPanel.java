package org.daisy.dotify.devtools.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.daisy.dotify.api.engine.FormatterEngineFactoryMakerService;
import org.osgi.framework.BundleContext;

public class FormatterPanel extends MyPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051107255963928066L;
	private final JTextField textField;
	private final JTextArea outputField;

	private FormatterTracker tracker;
	
	public FormatterPanel() {
		super(new GridLayout(2, 1));

		Font f = new Font(null, Font.BOLD, 26);

		outputField = new JTextArea();
		outputField.setFont(f);
		outputField.setEditable(false);

		textField = new JTextField();
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

				FormatterEngineFactoryMakerService t = tracker.get();
				if (t == null) {
					outputField.setText("No conversion");
				} else {
					if (textField.getText().equals("")) {
						outputField.setText("");
					} else {
							//try {
								outputField.setText("not implemented");
							//} catch (Integer2TextConfigurationException e1) {
								outputField.setText("Locale not supported: " + getTargetLocale() + "\n");
								/*
								outputField.append("Supported values:\n");
								for (String s : t.listLocales()) {
									outputField.append(s+"\n");
								}*/
							//}
					}
				}

		}
	}

	public void openTracking(BundleContext context) {
		tracker = new FormatterTracker(context);
		tracker.open();
	}

	public void closeTracking() {
		tracker.close();
	}

}
