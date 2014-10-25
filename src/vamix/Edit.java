/**
 * @author ywu591
 */
package vamix;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import editPanes.Extract;
import editPanes.OverLay;
import editPanes.Replace;
import editPanes.Subtitles;
import editPanes.SubtitlesSave;
import functionality.*;
import functionality.audio.ExtractWorker;
import functionality.audio.OverLayWorker;
import functionality.audio.ReplaceWorker;
import functionality.helpers.CheckAudioTrack;
import functionality.helpers.FileChecker;
import functionality.subtitles.SubtitlesWriter;
import functionality.video.FadeWorker;
import functionality.video.FlipWorker;
import functionality.video.RotateWorker;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.text.DefaultFormatter;

/**
 * Edit Panel
 */
public class Edit extends JPanel {
	private static Edit instance;

	private JPanel videoPanel;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;

	private JButton play;
	private JButton pause;
	private JButton stop;
	private JButton back;
	private JButton forward;
	private JButton mute;
	private String outputFile;

	private JButton extractAudio;
	private JButton replaceAudio;
	private JButton overlayAudio;

	protected JLabel filenameLabel;
	private JButton chooser;

	private JProgressBar progressBar;

	private ExtractWorker extractWorker;
	private ReplaceWorker replaceWorker;
	private OverLayWorker overlayWorker;
	private RotateWorker rotateWorker;
	private FlipWorker flipWorker;
	private FadeWorker fadeWorker;

	private boolean hasAudioTrack;

	private JLabel rotateLabel;
	private JButton rotate;
	private JComboBox<String> rotateSelector;

	private JLabel flipLabel;
	private JButton flip;
	private JComboBox<String> flipSelector;

	private JLabel fadeStartLabel;
	private JLabel fadeinsec;
	private JLabel fadeEndLabel;
	private JLabel fadeoutsec;
	private SpinnerModel spinnerInLimit;
	private SpinnerModel spinnerOutLimit;
	private JSpinner fadein;
	private JSpinner fadeout;
	private JButton fade;
	
	private JButton trim;
	private JButton subtitles;

	public static Edit getInstance() {
		if (instance == null) {
			instance = new Edit();
		}
		return instance;
	}

	private Edit() {
		// Allows us to resize using absolute positioning
		addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			public void ancestorResized(HierarchyEvent e) {
				resize();
			}
		});
		setSize(900, 473);
		setLayout(null);

		addEditButtons();
		addChoosers();
		addVideoComponents();

		addProgressBar();

		addRotate();
		addFlip();
		addFade();
		addSubtitles();
	}

	// Adds in assignment 3 functionality.
	private void addEditButtons() {
		extractAudio = new JButton("Extract Audio");
		extractAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Warns user if it does not have an audio track.
				if (!hasAudioTrack) {
					JOptionPane.showMessageDialog(null,
							"This File does not have an Audio Track",
							"Warning!", JOptionPane.WARNING_MESSAGE);
				}

				pressStopButton();

				// Brings up the extract pop up
				String[] options = { "Extract", "Cancel" };
				JPanel panel = Extract.getInstance();
				panel.setPreferredSize(new Dimension(420, 180));
				boolean status = false;
				// Checks if user gives valid input or not.
				while (!status) {
					int n = JOptionPane.showOptionDialog(null, panel,
							"Extract Audio", JOptionPane.YES_NO_OPTION,
							JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0) {
						status = Extract.getInstance().startProcess();
					} else {
						break;
					}
					// If valid input, execute extract.
					if (status) {
						enableEditButtons(false);
						String fullname = Extract.getInstance().getOutputFile();
						String start = Extract.getInstance().getStartTime();
						String to = Extract.getInstance().getToTime();
						boolean b = Extract.getInstance()
								.checkSelectedWholeFile();
						extractWorker = new ExtractWorker(fullname, b, start,
								to);
						extractWorker.execute();
					}
				}
			}
		});
		extractAudio.setEnabled(false);
		extractAudio.setBounds(462,387,135, 25);
		add(extractAudio);

		replaceAudio = new JButton("Replace Audio");
		replaceAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// warns user if there is not audio track
				if (!hasAudioTrack) {
					JOptionPane.showMessageDialog(null,
							"This File does not have an Audio Track",
							"Warning!", JOptionPane.WARNING_MESSAGE);
				}

				pressStopButton();

				// pop up for replace audio
				String[] options = { "Replace Audio", "Cancel" };
				JPanel panel = Replace.getInstance();
				panel.setPreferredSize(new Dimension(370, 180));
				boolean status = false;
				// Checks if user gives valid input or not
				while (!status) {
					int n = JOptionPane.showOptionDialog(null, panel,
							"Replace Audio", JOptionPane.YES_NO_OPTION,
							JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0) {
						status = Replace.getInstance().startProcess();
					} else {
						break;
					}
					// If valid input then replace Audio!
					if (status) {
						enableEditButtons(false);
						String input = Replace.getInstance().getReplaceFile();
						String output = Replace.getInstance().getOutputFile();
						replaceWorker = new ReplaceWorker(input, output);
						replaceWorker.execute();
					}

				}
			}
		});
		replaceAudio.setEnabled(false);
		replaceAudio.setBounds(609,387,135, 25);
		add(replaceAudio);

		overlayAudio = new JButton("Overlay Audio");
		overlayAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Warns user if the file does not have an audio track
				if (!hasAudioTrack) {
					JOptionPane.showMessageDialog(null,
							"This File does not have an Audio Track",
							"Warning!", JOptionPane.WARNING_MESSAGE);
				}

				pressStopButton();

				// brings up the overlay pop up
				String[] options = { "Overlay Audio", "Cancel" };
				JPanel panel = OverLay.getInstance();
				panel.setPreferredSize(new Dimension(370, 180));
				boolean status = false;
				// Checks for valid input
				while (!status) {
					int n = JOptionPane.showOptionDialog(null, panel,
							"Overlay Audio", JOptionPane.YES_NO_OPTION,
							JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0) {
						status = OverLay.getInstance().startProcess();
					} else {
						break;
					}
					// if valid input then execute the command
					if (status) {
						enableEditButtons(false);
						String input = OverLay.getInstance().getOverlayFile();
						String output = OverLay.getInstance().getOutputFile();
						overlayWorker = new OverLayWorker(input, output);
						overlayWorker.execute();
					}
				}
			}
		});
		overlayAudio.setEnabled(false);
		overlayAudio.setBounds(756,387,135, 25);
		add(overlayAudio);
	}

	// Adds the Jfilechooser and the label that tells user what file was
	// selected
	private void addChoosers() {
		chooser = new JButton("Choose File");
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Opens JFileChooser when button pressed
				JFileChooser jfile = new JFileChooser();

				File f = Main.getInstance().original;
				if (f != null) {
					jfile.setCurrentDirectory(new File(f.getParent()));
				}

				// Allows the user to open a file and play the video/audio.
				int response = jfile.showOpenDialog(null);
				if (response == JFileChooser.APPROVE_OPTION) {
					// Checks if the user picks a video or audio file
					if (FileChecker.checkFile(jfile.getSelectedFile()
							.toString())) {
						Main.getInstance().original = jfile.getSelectedFile();
						filenameLabel.setText("Selected file: "
								+ Main.getInstance().original.getName());
						filenameLabel.setFont(new Font(Font.SANS_SERIF, 0, 10));
						filenameLabel.setVisible(true);
						// get path of file (excluding file name)
						Playback.getInstance().enablePlay();
						enableEditButtons(true);
						checkForAudioTrack();
					} else {
						JOptionPane.showMessageDialog(null,
								"Please select a Video or Audio file",
								"Error!", JOptionPane.ERROR_MESSAGE);
					}
				}
				jfile.setVisible(true);
			}
		});
		chooser.setSize(135, 32);
		add(chooser);

		filenameLabel = new JLabel("");
		filenameLabel.setSize(429, 25);
		add(filenameLabel);
	}

	// Adds the video components that allows the user to play the video they
	// have just made.
	private void addVideoComponents() {
		videoPanel = new JPanel(new BorderLayout());
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();
		videoPanel.add(mediaPlayerComponent, BorderLayout.CENTER);

		videoPanel.setLocation(12, 12);
		add(videoPanel);

		play = new JButton();
		setIcon(play, Main.PLAYPIC);
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (video.isPlayable()) {
					video.pause();
					pause.setVisible(true);
					play.setVisible(false);
				} else {
					video.playMedia(outputFile);
					pause.setVisible(true);
					play.setVisible(false);
					toggleStopButtons(true);
				}
			}
		});
		play.setEnabled(false);
		play.setSize(32, 32);
		add(play);

		pause = new JButton();
		setIcon(pause, Main.PAUSEPIC);
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.pause();
				pause.setVisible(false);
				play.setVisible(true);
			}
		});
		pause.setVisible(false);
		pause.setSize(32, 32);
		add(pause);

		stop = new JButton();
		setIcon(stop, Main.STOPPIC);
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressStopButton();
			}
		});
		stop.setEnabled(false);
		stop.setSize(32, 32);
		add(stop);

		forward = new JButton();
		setIcon(forward, Main.FORWARDPIC);
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.skip(10000);
			}
		});
		forward.setEnabled(false);
		forward.setSize(32, 32);
		add(forward);

		back = new JButton();
		setIcon(back, Main.BACKPIC);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.skip(-10000);
			}
		});
		back.setEnabled(false);
		back.setSize(32, 32);
		add(back);

		mute = new JButton();
		setIcon(mute, Main.MUTEPIC);
		mute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (video.isMute()) {
					video.mute(false);
					setIcon(mute, Main.MUTEPIC);
				} else {
					video.mute(true);
					setIcon(mute, Main.LOWSOUNDPIC);
				}
			}
		});
		mute.setEnabled(false);
		mute.setSize(32, 32);
		add(mute);
	}

	// Adds progress bar
	private void addProgressBar() {
		progressBar = new JProgressBar();
		progressBar.setBounds(462, 424, 429, 32);
		add(progressBar);

	}

	// Adds the JComponents needed for the rotate functionality.
	private void addRotate() {
		// Adds the Label
		rotateLabel = new JLabel("Rotate Clockwise:");
		rotateLabel.setBounds(462, 49, 135, 25);
		add(rotateLabel);

		// Adds the comboBox for selection
		String[] options = { "90\u00b0", "180\u00b0", "270\u00b0",
				"90\u00b0 + vertical flip", "270\u00b0 + vertical flip" };
		rotateSelector = new JComboBox<String>();
		for (String s : options) {
			rotateSelector.addItem(s);
		}
		rotateSelector.setBounds(609, 49, 135, 25);
		add(rotateSelector);

		// Button that executes the command.
		rotate = new JButton("Rotate");
		rotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressStopButton();
				enableEditButtons(false);
				String filename = Main.getInstance().original.getName();
				String[] find = filename.split("\\.");
				String name = Main.getInstance().original.getParent() + "/"
						+ find[0] + "_" + rotateSelector.getSelectedItem()
						+ ".mp4";

				rotateWorker = new RotateWorker(name, rotateSelector
						.getSelectedIndex());
				rotateWorker.execute();
			}
		});
		rotate.setBounds(756, 49, 135, 25);
		rotate.setEnabled(false);
		add(rotate);

	}

	// Adds the JComponents for the flip functionality
	private void addFlip() {
		// Adds the Label
		flipLabel = new JLabel("Filp:");
		flipLabel.setBounds(462, 86, 135, 25);
		add(flipLabel);

		// Adds the selection
		String[] options = { "Vertical Flip", "Horizontal Flip" };
		flipSelector = new JComboBox<String>();
		for (String s : options) {
			flipSelector.addItem(s);
		}
		flipSelector.setBounds(609, 86, 135, 25);
		add(flipSelector);

		// Adds the button that executes the commands
		flip = new JButton("Flip");
		flip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressStopButton();
				enableEditButtons(false);
				String filename = Main.getInstance().original.getName();
				String[] find = filename.split("\\.");
				String name = Main.getInstance().original.getParent() + "/"
						+ find[0] + "_" + flipSelector.getSelectedItem()
						+ ".mp4";

				flipWorker = new FlipWorker(name, flipSelector
						.getSelectedIndex());
				flipWorker.execute();
			}
		});
		flip.setBounds(756, 86, 135, 25);
		flip.setEnabled(false);
		add(flip);

	}

	// Adds the JComponents for the fade functionality
	private void addFade() {
		// Limits the spinner selections
		spinnerInLimit = new SpinnerNumberModel(0, 0, 59, 1);
		spinnerOutLimit = new SpinnerNumberModel(0, 0, 59, 1);

		// Adds the label
		fadeStartLabel = new JLabel("Fade in from the start:");
		fadeStartLabel.setBounds(462, 123, 167, 25);
		add(fadeStartLabel);

		// Adds fade in spinner
		fadein = new JSpinner(spinnerInLimit);
		// reference:
		// http://www.coderanch.com/t/343526/GUI/java/prevent-inputs-JSpinner
		((DefaultFormatter) ((JSpinner.DefaultEditor) fadein.getEditor())
				.getTextField().getFormatter()).setAllowsInvalid(false);
		fadein.setBounds(647, 123, 55, 25);
		add(fadein);

		// More labels
		fadeinsec = new JLabel("sec");
		fadeinsec.setBounds(720, 123, 24, 25);
		add(fadeinsec);

		// Even more labels
		fadeEndLabel = new JLabel("Fade out to the end:");
		fadeEndLabel.setBounds(462, 160, 167, 25);
		add(fadeEndLabel);

		// Adds fade out spinner
		fadeout = new JSpinner(spinnerOutLimit);
		// reference:
		// http://www.coderanch.com/t/343526/GUI/java/prevent-inputs-JSpinner
		((DefaultFormatter) ((JSpinner.DefaultEditor) fadeout.getEditor())
				.getTextField().getFormatter()).setAllowsInvalid(false);
		fadeout.setBounds(647, 160, 55, 25);
		add(fadeout);

		// And more Labels
		fadeoutsec = new JLabel("sec");
		fadeoutsec.setBounds(720, 160, 24, 25);
		add(fadeoutsec);

		// Button that does the fade functionality
		fade = new JButton("Fade");
		fade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressStopButton();
				enableEditButtons(false);
				// Makes sure the user actually enters values for fade in and
				// out
				if (fadein.getValue().equals(0) && fadeout.getValue().equals(0)) {
					JOptionPane
							.showMessageDialog(
									null,
									"Both fade in and out values are 0 seconds.\nThere is no point in doing that command");
					enableEditButtons(true);
				} else {
					int start = (Integer) fadein.getValue();
					int end = (Integer) fadeout.getValue();
					fadeWorker = new FadeWorker(start, end);
					fadeWorker.execute();
				}
			}
		});
		fade.setEnabled(false);
		fade.setBounds(756, 160, 135, 25);
		add(fade);
		

	}
	
	
	//TODO subtitles! =D
	private void addSubtitles(){
		subtitles = new JButton("Add Subtitles");
		subtitles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressStopButton();
				//TODO like check the file and like get the subtitles 
				
				//Sets up the joptionpane for adding subtitles
				String[] options = { "Save Subtitles ", "Add Stream"};
				JPanel panel = Subtitles.getInstance();
				panel.setPreferredSize(new Dimension(600, 300));
				boolean status = false;
				boolean video = false;
				
				// Checks for valid input
				while (!status) {
					int n = JOptionPane.showOptionDialog(null, panel,
							"Subtitles", JOptionPane.YES_NO_OPTION,
							JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0) {
						status = Subtitles.getInstance().hasSubtitles();
						if(!status){
							break;
						}
					} else if (n == 1) {
						//Sets up the joptionpane for adding subtitle stream
						JPanel newpanel = SubtitlesSave.getInstance();
						newpanel.setPreferredSize(new Dimension(400,100));
						String[] saveOptions = {"Ok","Cancel"};
						boolean check = false;
						while (!check){
							int o = JOptionPane.showOptionDialog(null, newpanel,
									"Saving", JOptionPane.YES_NO_OPTION,
									JOptionPane.NO_OPTION, null, saveOptions, saveOptions[0]);
							if (o == 0){
								check = SubtitlesSave.getInstance().doProcess();
							} else {
								break;
							}
						}
					} else {
						break;
					}
					// if valid input then execute the command
					if (status) {
						enableEditButtons(false);
						SubtitlesWriter.writeSubtitles();
						//TODO If they want to get the video! =DDDDDDDDDD
						if(video){
							
						}
					}
				}
				
			}
		});
		subtitles.setBounds(756, 313, 135, 25);
		subtitles.setEnabled(false);
		add(subtitles);
	}

	/**
	 * This methods changes the icon of a JButton
	 */
	private void setIcon(JButton button, String location) {
		try {
			Image img = ImageIO.read(getClass().getResource(location));
			button.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder());
	}

	/**
	 * This method gets called when we want to stop the video!
	 */
	private void pressStopButton() {
		video.stop();
		pause.setVisible(false);
		play.setVisible(true);
		toggleStopButtons(false);
	}

	/**
	 * This method toggle buttons when the video has been stopped.
	 */
	private void toggleStopButtons(boolean b) {
		stop.setEnabled(b);
		back.setEnabled(b);
		forward.setEnabled(b);
		mute.setEnabled(b);
	}

	/**
	 * This method toggles on the video buttons when one of the edit
	 * functionally works.
	 */
	public void enableVideoButtons() {
		play.setEnabled(true);
		play.setVisible(false);
		pause.setVisible(true);
		stop.setEnabled(true);
		back.setEnabled(true);
		forward.setEnabled(true);
		mute.setEnabled(true);
	}

	/**
	 * This function enables the edit buttons once an input file has been given
	 */
	public void enableEditButtons(Boolean b) {
		extractAudio.setEnabled(b);
		replaceAudio.setEnabled(b);
		overlayAudio.setEnabled(b);
		rotate.setEnabled(b);
		flip.setEnabled(b);
		fade.setEnabled(b);
		subtitles.setEnabled(b);
	}

	/**
	 * Pauses the video.
	 */
	public void pauseVideo() {
		if (video.isPlaying()) {
			video.pause();
			play.setVisible(true);
			pause.setVisible(false);
		}
	}

	// Getter and setters for when functionality is successful
	// That allows the user see what they have produced.
	public void checkForAudioTrack() {
		hasAudioTrack = CheckAudioTrack
				.checkAudioTrack(Main.getInstance().original.getAbsolutePath());
	}

	public void updateProgressBar(boolean b,String s) {
		progressBar.setIndeterminate(b);
		progressBar.setStringPainted(b);
		progressBar.setString(s);
	}

	public EmbeddedMediaPlayer getVideo() {
		return video;
	}

	public void setOutputFile(String out) {
		outputFile = out;
	}

	public String getOutputFile() {
		return outputFile;
	}

	// More math to resize the screen! Cause Absolute positioning is too good
	private void resize() {
		int x = Main.getInstance().getWidth();
		int y = Main.getInstance().getHeight();

		extractAudio.setLocation(x - 438, y - 113);
		replaceAudio.setLocation(x - 291, y - 113);
		overlayAudio.setLocation(x - 144, y - 113);
		chooser.setLocation(x - 623, y - 76);
		filenameLabel.setLocation(x - 438, 12);
		videoPanel.setSize(x - 500, y - 100);
		play.setLocation(12, y - 76);
		pause.setLocation(12, y - 76);
		stop.setLocation(56, y - 76);
		back.setLocation(100, y - 76);
		forward.setLocation(144, y - 76);
		mute.setLocation(188, y - 76);
		progressBar.setLocation(x - 438, y - 76);
		rotateLabel.setLocation(x - 438, 49);
		rotateSelector.setLocation(x - 291, 49);
		rotate.setLocation(x - 144, 49);
		flipLabel.setLocation(x - 438, 86);
		flipSelector.setLocation(x - 291, 86);
		flip.setLocation(x - 144, 86);
		fadeStartLabel.setLocation(x - 438, 123);
		fadein.setLocation(x - 253, 123);
		fadeinsec.setLocation(x - 180, 123);
		fadeEndLabel.setLocation(x - 438, 160);
		fadeout.setLocation(x - 253, 160);
		fadeoutsec.setLocation(x - 180, 160);
		fade.setLocation(x - 144, 160);
	}
}
