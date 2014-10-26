/**
 * @author lwwe379 & ywu591
 */
package vamix;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

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
	public static final String PLAYPIC = "/resource/play.png";
	public static final String PAUSEPIC = "/resource/pause.png";
	public static final String STOPPIC = "/resource/stop.png";
	public static final String BACKPIC = "/resource/back.png";
	public static final String FORWARDPIC = "/resource/forward.png";
	public static final String MUTEPIC = "/resource/mute.png";
	public static final String HIGHSOUNDPIC = "/resource/highsound.png";
	public static final String LOWSOUNDPIC = "/resource/lowsound.png";

	// Singleton
	public static Main getInstance() {
		if (instance == null) {
			instance = new Main();
		}
		return instance;
	}

	// define frames
	JTabbedPane vamixTabs;

	private Main() {
		// set up screen
		this.setTitle("VAMIX - Video Audio Mixer");
		this.setMinimumSize(new Dimension(900, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLookAndFeel();
		vamixTabs = VamixTabs.getInstance();
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
	
	// Sets the look and feel of the whole GUI
	private void setLookAndFeel(){
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
	}
}
