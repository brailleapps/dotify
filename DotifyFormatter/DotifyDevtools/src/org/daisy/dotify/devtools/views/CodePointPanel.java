package org.daisy.dotify.devtools.views;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.daisy.dotify.devtools.converters.CodePointHelper;
import org.daisy.dotify.devtools.converters.CodePointHelper.Mode;
import org.daisy.dotify.devtools.converters.CodePointHelper.Style;

public class CodePointPanel extends JPanel implements StringFormatterResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2155201906265896180L;
	private final static String xmlStr = "XML entity";
	private final static String commaStr = "Comma separated";
	private final static String hexStr = "Hex";
	private final static String decimalStr = "Decimal";

	JRadioButton s1, s2, m1, m2;
	
	JTextField codeTextField;
	// Create the listener list
    protected javax.swing.event.EventListenerList listenerList =
        new javax.swing.event.EventListenerList();
	
	public CodePointPanel() {
        JPanel style = new JPanel();
        JPanel mode = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
		ButtonGroup mButtonGroup = new ButtonGroup();
		ActionListener updateStyle = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				styleChanged();
			}
		};
		
		ActionListener updateMode = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modeChanged();
			}
		};

		s1 = new JRadioButton(xmlStr);
		s2 = new JRadioButton(commaStr);
		s1.addActionListener(updateStyle);
		s2.addActionListener(updateStyle);

		m1 = new JRadioButton(hexStr);
		m2 = new JRadioButton(decimalStr);
		m1.addActionListener(updateMode);
		m2.addActionListener(updateMode);
		

		buttonGroup.add(s1);
		buttonGroup.add(s2);
		
		mButtonGroup.add(m1);
		mButtonGroup.add(m2);

		s2.setSelected(true);
		m1.setSelected(true);
		
		style.setLayout(new GridLayout(2, 1));
		style.add(s1);
		style.add(s2);
		
		mode.setLayout(new GridLayout(2, 1));
		mode.add(m1);
		mode.add(m2);
		
        codeTextField = new JTextField(30);
        codeTextField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==10) {
					fireTextChangedEvent(new TextChangedEvent(this));
				}
			}
			});
        
        setLayout(new GridLayout(2, 1));
        add(codeTextField);
        
        JPanel options = new JPanel(new GridLayout(1, 2));
        options.add(style);
        options.add(mode);
        add(options);
	}
	
	public void addTextChangedListener(TextChangedListener listener) {
		listenerList.add(TextChangedListener.class, listener);
	}
	
	public void removeTextChangedListener(TextChangedListener listener) {
		listenerList.remove(TextChangedListener.class, listener);
	}
	
	public void setResult(String text) {
		codeTextField.setText(CodePointHelper.format(text, getSelectedStyle(), getSelectedMode()));
	}
	
	protected void fireTextChangedEvent(TextChangedEvent evt) {
		Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
		for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==TextChangedListener.class) {
                ((TextChangedListener)listeners[i+1]).textChanged(evt);
            }
		}
	}

	public String getResultAsText() {
		return CodePointHelper.parse(codeTextField.getText(), getSelectedMode());
	}
	
	private void styleChanged() {
		switch (getSelectedStyle()) {
			case XML:
				setResult(CodePointHelper.parse(codeTextField.getText(), getSelectedMode()));
				break;
			case COMMA:
				setResult(CodePointHelper.parse(codeTextField.getText(), getSelectedMode()));
				break;
		}
	}
	private void modeChanged() {
		switch (getSelectedMode()) {
			case HEX:
				setResult(CodePointHelper.parse(codeTextField.getText(), Mode.DECIMAL));
				break;
			case DECIMAL:
				setResult(CodePointHelper.parse(codeTextField.getText(), Mode.HEX));
				break;
		}
		
	}
	private Style getSelectedStyle() {
		if (s1.isSelected()) {
			return Style.XML;
		} else if (s2.isSelected()) {
			return Style.COMMA;
		}
		return Style.COMMA;
	}
	private Mode getSelectedMode() {
		if (m1.isSelected()) {
			return Mode.HEX;
		} else if (m2.isSelected()) {
			return Mode.DECIMAL;
		}
		return Mode.HEX;
	}
	
 
	


}
