/**
 * @author lwwe379 & ywu591
 */
package vamix;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;

/**
 * Main class
 * 
 * @author ywu591
 * 
 */
public class Main extends JFrame {
	private static Main instance;

	// Keeps track of the Main file being edited.
	public File original;

	// Keeps track the location of where resources are
	public static final String playpic = "/resource/play.png";
	public static final String pausepic = "/resource/pause.png";
	public static final String stoppic = "/resource/stop.png";
	public static final String backpic = "/resource/back.png";
	public static final String forwardpic = "/resource/forward.png";
	public static final String mutepic = "/resource/mute.png";
	public static final String highsoundpic = "/resource/highsound.png";
	public static final String lowsoundpic = "/resource/lowsound.png";

	// Singleton
	public static Main getInstance() {
		if (instance == null) {
			instance = new Main();
		}
		return instance;
	}

	// define frames
	JTabbedPane vamixTabs = new JTabbedPane();

	private Main() {
		// set up screen
		this.setTitle("VAMIX - Video Audio Mixer");
		this.setMinimumSize(new Dimension(900, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		vamixTabs.add("Playback", Playback.getInstance());
		vamixTabs.add("Editor", Edit.getInstance());
		vamixTabs.add("Text", Text.getInstance());
		vamixTabs.add("Download", Download.getInstance());
		vamixTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// Pause the playback video when switching to a new tab
				if (!(vamixTabs.getSelectedComponent().equals(Playback
						.getInstance()))) {
					Playback.getInstance().pauseVideo();
				}
				// Pause the text video when switching to a new tab
				if (!(vamixTabs.getSelectedComponent().equals(Text
						.getInstance()))) {
					Text.getInstance().pauseVideo();
				}
				// Pause the edit video when switching to a new tab
				if (!(vamixTabs.getSelectedComponent().equals(Edit
						.getInstance()))) {
					Edit.getInstance().pauseVideo();
				}
				// Checks the for audio track of a video when switching to edit
				// tab
				if (vamixTabs.getSelectedComponent().equals(Edit.getInstance())
						&& original != null) {
					Edit.getInstance().checkForAudioTrack();
				}
				// updated the selected file label when switching to a different
				// tab
				if (!vamixTabs.getSelectedComponent()
						.equals(Main.getInstance()) && original != null) {
					Text.getInstance().filenameLabel.setText("Selected file: "
							+ original.getName());
					Text.getInstance().filenameLabel.setVisible(true);
					Text.getInstance().filenameLabel.setFont(new Font(
							Font.SANS_SERIF, 0, 10));
					Edit.getInstance().filenameLabel.setText("Selected file: "
							+ original.getName());
					Edit.getInstance().filenameLabel.setVisible(true);
					Edit.getInstance().filenameLabel.setFont(new Font(
							Font.SANS_SERIF, 0, 10));
					Edit.getInstance().enableEditButtons(true);
				}
			}
		});
		this.getContentPane().add(vamixTabs);
	}
	
	/**
	 * Main Method
	 */
	public static void main(String[] args) {
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main play = Main.getInstance();
					play.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
