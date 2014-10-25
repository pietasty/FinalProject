package editPanes;

import java.awt.Font;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import vamix.Main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;


/**
 * This class is the GUI for when the user wants to add a subtitle stream to the video 
 * @author ywu591
 *
 */
public class SubtitlesSave extends JPanel {
	private static SubtitlesSave instance;
	
	private JTextField output;
	private JButton save;
	
	private String defaultlocation;
	private String fullname;
	
	public static SubtitlesSave getInstance(){
		if (instance == null){
			instance = new SubtitlesSave();
		}
		return instance;
	}
	
	public SubtitlesSave(){
		setSize(400,100);
		setLayout(null);
		
		String file = Main.getInstance().original.getAbsolutePath();
		defaultlocation = file.substring(0, file.lastIndexOf('/') + 1);
		
		JLabel lblName = new JLabel("Output File: (do not give an extension)");
		lblName.setBounds(12, 12, 300, 15);
		lblName.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(lblName);
		
		output = new JTextField();
		output.setBounds(12, 39, 239, 30);
		add(output);
		
		save = new JButton("Save to");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfile = new JFileChooser();
				jfile.setCurrentDirectory(new File(defaultlocation));

				int response = jfile.showSaveDialog(null);
				if (response == JFileChooser.APPROVE_OPTION) {
					// records the fullname
					fullname = jfile.getSelectedFile().toString();
					String basename = fullname.substring(
							fullname.lastIndexOf('/') + 1, fullname.length());
					output.setText(basename);
				}

				jfile.setVisible(true);
			}
		});
		save.setBounds(263, 39, 125, 30);
		add(save);
	}
	
	
	private boolean checkOverrideOriginal() {
		if (Main.getInstance().original.getAbsolutePath().equals(fullname)) {
			return true;
		}
		return false;
	}
	
	
	public boolean doProcess() {
		// If a name was given but did not use fileChooser then file is
		// stored in default location
		if (fullname == null) {
			fullname = defaultlocation + output.getText();
		} else {
			String basename = fullname.substring(fullname.lastIndexOf('/') + 1,
					fullname.length());
			String location = fullname.substring(0,
					fullname.lastIndexOf('/') + 1);
			if (!basename.equals(output.getText())
					&& location.equals(defaultlocation)) {
				fullname = defaultlocation + output.getText();
			}
		}
		
		//appends the correct file type for subtitles to work!
		fullname = fullname + ".mkv";
		
		// Needs to give an output filename.
		if (output.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null,
					"Please give a output filename!", "Error!",
					JOptionPane.ERROR_MESSAGE);
			return false;
			// Checks the user does not override the original file
		}  else if (checkOverrideOriginal()) {
			JOptionPane.showMessageDialog(null,
					"You cannot override the original file!", "Error!",
					JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			// Checks if the user wants to override
			File f = new File(fullname);
			if (f.exists()) {
				String basename = fullname.substring(
						fullname.lastIndexOf('/') + 1, fullname.length());
				int exists = JOptionPane.showConfirmDialog(null, basename
						+ " already exists \nDo you want to override?",
						"Override?", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (exists != JOptionPane.OK_OPTION) {
					return false;
				}
			}

			return true;
		}
	}

}
