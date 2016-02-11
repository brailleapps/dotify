package org.daisy.dotify.devtools.gui.views;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextConvertPanel extends JPanel implements StringFormatterResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7332544266221207823L;
	CodePointPanel cPanel;
	JLabel codeLabel;
	// JLabel namesLabel;
	JLabel textLabel;
	JTextField textTextField;

	// JList list;

	public TextConvertPanel() {
        textLabel = new JLabel();
        textLabel.setText("Text");
        cPanel = new CodePointPanel();
		textTextField = new JTextField(30);
        
		// namesLabel = new JLabel("Character names");

        codeLabel = new JLabel();
        codeLabel.setText("Code points");
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        
        {
	        JButton button = new JButton();
	        button.setText("To Code Points ↓");
	        button.addActionListener(new java.awt.event.ActionListener() {
	            @Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
	                buttonActionPerformed();
	            }
	        });
	        buttonPanel.add(button);
	        JButton button2 = new JButton();
	        button2.setText("To Characters ↑");
	        button2.addActionListener(new java.awt.event.ActionListener() {
	            @Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
	                buttonActionPerformed2();
	            }
	        });
	        buttonPanel.add(button2);
        }
        textTextField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==10) {
					buttonActionPerformed();
				}
			}
		});
        
        cPanel.addTextChangedListener(new TextChangedListener() {
			
			@Override
			public void textChanged(TextChangedEvent event) {
				buttonActionPerformed2();
			}
		});
        /*
        codeTextField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==10) {
					buttonActionPerformed2();
				}
			}
			});*/

		// list = new JList();
		// JScrollPane listScroll = new JScrollPane();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        //getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
      
        // The sequential group in turn contains two parallel groups.
        // One parallel group contains the labels, the other the text fields.
        // Putting the labels in a parallel group along the horizontal axis
        // positions them at the same x location.
        //
        // Variable indentation is used to reinforce the level of grouping.
        hGroup.addGroup(layout.createParallelGroup().
                 addComponent(textLabel)
		// .addComponent(namesLabel)
                 .addComponent(codeLabel)
                 );
		hGroup.addGroup(layout.createParallelGroup().addComponent(textTextField)
                 .addComponent(buttonPanel)
                 .addComponent(cPanel)
		// .addComponent(listScroll)
                 );
        layout.setHorizontalGroup(hGroup);
        
        // Create a sequential group for the vertical axis.
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
      
        // The sequential group contains two parallel groups that align
        // the contents along the baseline. The first parallel group contains
        // the first label and text field, and the second parallel group contains
        // the second label and text field. By using a sequential group
        // the labels and text fields are positioned vertically after one another.
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
addComponent(textLabel).addComponent(textTextField));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
        		addComponent(buttonPanel));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
		// .addComponent(namesLabel)
		// .addComponent(listScroll)
		);
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
.addComponent(codeLabel).addComponent(cPanel));

        layout.setVerticalGroup(vGroup);
	}

	private void buttonActionPerformed() {
		// updateCharacterNames();
    	cPanel.setResult(textTextField.getText());
    }
    
    private void buttonActionPerformed2() {
    	textTextField.setText(cPanel.getResultAsText());
		// updateCharacterNames();
    }

	@Override
	public void setResult(String text) {
		textTextField.setText(text);
		buttonActionPerformed();
		// cPanel.setResult(text);
	}

	@Override
	public String getResultAsText() {
		return cPanel.getResultAsText();
	}
    

}
