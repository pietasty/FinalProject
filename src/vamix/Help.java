package vamix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;

/**
 * This is the GUI for the Help tab.
 * This displays a text file that tells you how to use Vamix.
 * @author ywu591
 *
 */
public class Help extends JPanel {
	private static Help instance;
	
	private JTextArea editor;
	private JScrollPane panel;
	private JButton button;
	private JDialog popup;
	
	public static Help getInstance(){
		if (instance == null){
			instance = new Help();
		}
		return instance;
	}
	
	private Help(){
		setSize(900,473);
		setLayout(null);
		
		addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			public void ancestorResized(HierarchyEvent e) {
				resize();
			}
		});
		
		//Sets up the JTextArea and ScrollPane
		editor = new JTextArea();
		editor.setEditable(false);
		panel = new JScrollPane(editor);
		panel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		readHelpFile(editor);
		
		panel.setBounds(12, 12, 876, 376);
		add(panel);
		
		//Button that allows the user to pop out the help panel
		button = new JButton("Popout");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (popup == null) {
					popup = new Popout();
				} else {
					popup.setVisible(true);
				}
			}
		});
		button.setBounds(391, 400, 117, 25);
		add(button);
	}
	
	// This method reads the help file and appends the text to a JTextArea
	private void readHelpFile(JTextArea jta) {
		// Reads from the file
		try {
			InputStream in = getClass().getResourceAsStream(
					"/resource/help.txt");

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;

			while ((line = reader.readLine()) != null) {
				jta.append(line + System.lineSeparator());
			}
			jta.setCaretPosition(0);
		} catch (IOException e) {
		}
	}
	
	private void resize(){
		int x = Main.getInstance().getWidth();
		int y = Main.getInstance().getHeight();
		
		panel.setSize(x -12, y -124);
		button.setLocation(x/2 -59, y-100);
	}
	
	//Class that allows the help panel to pop out;
	class Popout extends JDialog{
		private Popout(){
			setSize(900,400);
			JTextArea text = new JTextArea();
			text.setEditable(false);
			JScrollPane newpanel = new JScrollPane(text);
			readHelpFile(text);
			setContentPane(newpanel);
			setVisible(true);
		}
	}
}
