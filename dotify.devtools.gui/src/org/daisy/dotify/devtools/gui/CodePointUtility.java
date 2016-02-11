package org.daisy.dotify.devtools.gui;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.daisy.dotify.devtools.gui.views.BrailleConvertPanel;
import org.daisy.dotify.devtools.gui.views.TextConvertPanel;


public class CodePointUtility extends JPanel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3237476094594239863L;

	public CodePointUtility() {
		initComponents();
	}

    private void initComponents() {

        TextConvertPanel textPanel = new TextConvertPanel();
        JComponent braillePanel = new BrailleConvertPanel(textPanel);

		javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(this);
		setLayout(mainLayout);
		//setBackground(Color.BLACK);
        mainLayout.setAutoCreateGaps(true);
        mainLayout.setAutoCreateContainerGaps(true);
		JLabel textLabel = new JLabel("Input");
        //textLabel.setForeground(Color.GRAY);
        JLabel brailleLabel = new JLabel("Braille Input");
        //brailleLabel.setForeground(Color.GRAY);
        GroupLayout.SequentialGroup h2Group = mainLayout.createSequentialGroup();
        h2Group.addGroup(mainLayout.createParallelGroup()
        		.addComponent(brailleLabel)
        		.addComponent(braillePanel)
        		.addComponent(textLabel)
        		.addComponent(textPanel)

        		);
        mainLayout.setHorizontalGroup(h2Group);
        
        GroupLayout.SequentialGroup v2Group = mainLayout.createSequentialGroup();
        v2Group
        .addComponent(brailleLabel)
    	.addComponent(braillePanel)
        	.addComponent(textLabel)
        	.addComponent(textPanel)
        	;
        mainLayout.setVerticalGroup(v2Group);
    }

}
