package vamix;

import java.awt.Font;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VamixTabs extends JTabbedPane {
	private static VamixTabs instance;
	
	public static VamixTabs getInstance(){
		if(instance == null){
			instance = new VamixTabs();
		}
		return instance;
	}
	
	private VamixTabs(){
		this.add("Playback", Playback.getInstance());
		this.add("Editor", Edit.getInstance());
		this.add("Text", Text.getInstance());
		this.add("Download", Download.getInstance());
		this.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// Pause the playback video when switching to a new tab
				if (!(getSelectedComponent().equals(Playback
						.getInstance()))) {
					Playback.getInstance().pauseVideo();
				}
				// Pause the text video when switching to a new tab
				if (!(getSelectedComponent().equals(Text
						.getInstance()))) {
					Text.getInstance().pauseVideo();
				}
				// Pause the edit video when switching to a new tab
				if (!(getSelectedComponent().equals(Edit
						.getInstance()))) {
					Edit.getInstance().pauseVideo();
				}
				// Checks the for audio track of a video when switching to edit
				// tab
				if (getSelectedComponent().equals(Edit.getInstance())
						&& Main.getInstance().original != null) {
					Edit.getInstance().checkForAudioTrack();
				}
				// updated the selected file label when switching to a different
				// tab
				if (!getSelectedComponent()
						.equals(Main.getInstance()) && Main.getInstance().original != null) {
					Text.getInstance().filenameLabel.setText("Selected file: "
							+ Main.getInstance().original.getName());
					Text.getInstance().filenameLabel.setVisible(true);
					Text.getInstance().filenameLabel.setFont(new Font(
							Font.SANS_SERIF, 0, 10));
					Edit.getInstance().filenameLabel.setText("Selected file: "
							+ Main.getInstance().original.getName());
					Edit.getInstance().filenameLabel.setVisible(true);
					Edit.getInstance().filenameLabel.setFont(new Font(
							Font.SANS_SERIF, 0, 10));
					Edit.getInstance().enableEditButtons(true);
				}
			}
		});
	}
}
