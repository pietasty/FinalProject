/**
 * @author lwwe379 & ywu591
 */
package vamix;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import functionality.helpers.FileChecker;
import functionality.video.ScreenShot;
import vamix.Main;

import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * Playback panel
 * 
 * Reference: Jacob Holden for setToolTipText
 */
public class Playback extends JPanel {
	private static Playback instance;

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	private JPanel videoPanel;
	private JSlider videoSlider;
	
	private Timer videoSliderClock;
	private JLabel videoTimer;

	private JButton play;
	private JButton pause;
	private JButton stop;

	private JButton back;
	private JButton forward;
	// records how fast to fast forward or back.
	private int speed;
	private Timer timer;

	private JButton mute;
	private JButton sound;
	private JSlider volume;

	private JButton chooser;

	private JButton camera;
	private ScreenShot screenShot;

	public static Playback getInstance() {
		if (instance == null) {
			instance = new Playback();
		}
		return instance;
	}

	private Playback() {
		// Allows the panel is resize when absolute positioning is used
		addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			public void ancestorResized(HierarchyEvent e) {
				resize();
			}
		});
		this.setVisible(true);
		setSize(900, 473);
		setLayout(null);

		// Functions that adds in the JComponments
		addPlayer();
		addvideoSlider();
		addTimeLabel();

		playButton();
		pauseButton();
		stopButton();

		backButton();
		forwardButton();

		muteButton();
		soundButton();
		volumeAdjuster();
		fileChooser();
		addCamera();

		// sets the speed to be Zero
		speed = 0;
		// Timer to start when fast forward or back buttons are pressed
		// Allows fast forward and rewind functionality
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (video.getTime() == 0
						|| video.getTime() == video.getLength()) {
					speed = 0;
					timer.stop();
				}
				// Based on how many times user presses the fast forward or
				// rewind button, the speed in which we se206_a03/icons fast
				// forward/rewind changes
				switch (speed) {
				case -3:
					video.skip(-5000);
					break;
				case -2:
					video.skip(-2000);
					break;
				case -1:
					video.skip(-1000);
					break;
				case 1:
					video.skip(1000);
					break;
				case 2:
					video.skip(2000);
					break;
				case 3:
					video.skip(5000);
					break;
				default:
					pause.setVisible(true);
					play.setVisible(false);
					timer.stop();
				}
			}
		});
		// This clock reports on the time in the video and updates the scrollBar
		videoSliderClock = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int time = (int) (video.getTime());
				videoSlider.setValue(time);
				videoSlider.setMaximum((int)video.getLength());
				
				//Stops the video once the video has finished.
				if (video.getTime() > video.getLength()- 50) {
					speed = 0;
					timer.stop();
					video.stop();
					pause.setVisible(false);
					play.setVisible(true);
					toggleStopButtons(false);
				}
			}
		});
	}

	// Adds in the media player
	private void addPlayer() {
		videoPanel = new JPanel(new BorderLayout());
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();

		videoPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		videoPanel.setLocation(0, 0);
		videoPanel.setVisible(true);

		add(videoPanel);
	}

	// Adds the video slider
	private void addvideoSlider() {
		// Timer to change the video slider
		videoSlider = new JSlider(JSlider.HORIZONTAL);
		//Allows the user to click on a part of a slider and go to the position in the video
		//Reference: http://stackoverflow.com/questions/7095428/jslider-clicking-makes-the-dot-go-towards-that-direction
		//^ that site helped me to get this to work =D
		videoSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point p = e.getPoint();
				double percent = p.x / ((double) videoSlider.getWidth());
				int range = videoSlider.getMaximum() - videoSlider.getMinimum();
				double newVal = range * percent;
				int result = (int) (videoSlider.getMinimum() + newVal);
				video.setTime(result);
			}
		});
		// Allows user to drag the slider to change the time of the video.
		videoSlider.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				videoSliderClock.stop();
				int time = videoSlider.getValue();
				video.setTime(time);
				videoSliderClock.start();
			}
		});

		videoSlider.setValue(0);
		add(videoSlider);
	}

	// Adds the time label
	private void addTimeLabel() {
		videoTimer = new JLabel("00:00:00");
		videoTimer.setSize(60, 25);
		add(videoTimer);

		// Timer used to count the where in the video we are
		Timer ticker = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int time = (int) (video.getTime() / 1000);
				if (time == 0) {
					videoTimer.setText("00:00:00");
				} else {
					// Math is done to display the time in appropriate format
					int second = time % 60;
					int min = time / 60;
					int minute = min % 60;
					int hour = min / 60;
					String h = String.format("%02d", hour);
					String m = String.format("%02d", minute);
					String s = String.format("%02d", second);
					videoTimer.setText(h + ":" + m + ":" + s);
				}
			}
		});
		ticker.start();
	}

	// Adds the play Button
	private void playButton() {
		play = new JButton();
		setIcon(play, Main.PLAYPIC);

		// plays the video depending on situation
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				// Checks if the video was fast forwarded.
				if (speed != 0) {
					speed = 0;
					pause.setVisible(true);
					play.setVisible(false);
					// Checks if the video video has been stopped
				} else if (!(stop.isEnabled())) {
					video.playMedia(Main.getInstance().original
							.getAbsolutePath());
					videoSliderClock.start();
					pause.setVisible(true);
					play.setVisible(false);
					toggleStopButtons(true);
					// Checks if the video has been paused.
				} else if (!(video.isPlaying())) {
					video.pause();
					pause.setVisible(true);
					play.setVisible(false);
				}
			}
		});
		
		play.setToolTipText("Plays the video");
		play.setEnabled(false);
		play.setSize(32, 32);
		add(play);
	}

	// Adds the pause button
	private void pauseButton() {
		pause = new JButton();
		setIcon(pause, Main.PAUSEPIC);

		// pauses the video when button pressed
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.pause();
				pause.setVisible(false);
				play.setVisible(true);
			}
		});
		
		pause.setToolTipText("Pauses the video");
		pause.setVisible(false);
		pause.setSize(32, 32);
		add(pause);
	}
 
	// Adds the stop button
	private void stopButton() {
		stop = new JButton();
		setIcon(stop, Main.STOPPIC);

		// Stops the video when button pressed, also toggles buttons
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 0;
				timer.stop();
				video.stop();
				pause.setVisible(false);
				play.setVisible(true);
				toggleStopButtons(false);
			}
		});
		
		stop.setToolTipText("Stops the video");
		stop.setEnabled(false);
		stop.setSize(32, 32);
		add(stop);
	}

	// Adds the rewind button
	private void backButton() {
		back = new JButton();
		setIcon(back, Main.BACKPIC);

		// Rewinds the video when the button is pressed
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (speed > -3) {
					speed--; // reduce the speed
				}
				play.setVisible(true);
				pause.setVisible(false);
				timer.start();
			}
		});
		
		back.setToolTipText("Rewinds the video");
		back.setEnabled(false);
		back.setSize(32, 32);
		add(back);
	}

	// Adds the fast forward button
	private void forwardButton() {
		forward = new JButton();
		setIcon(forward, Main.FORWARDPIC);

		// Fast forwards the video
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (speed < 3) {
					speed++; // Increases the speed
				}
				play.setVisible(true);
				pause.setVisible(false);
				timer.start();
			}
		});
		
		forward.setToolTipText("Fast forwards the video");
		forward.setEnabled(false);
		forward.setSize(32, 32);
		add(forward);
	}

	// Adds the mute button
	private void muteButton() {
		mute = new JButton();
		setIcon(mute, Main.MUTEPIC);

		mute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// unmutes the video if it is muted
				if (video.isMute()) {
					// If the video was "muted" via the volume slider, then
					// change the icon
					if (video.getVolume() == 0) {
						video.setVolume(100);
						setIcon(sound, Main.HIGHSOUNDPIC);
					}
					video.mute(false);
					setIcon(mute, Main.MUTEPIC);
					// Mutes the video
				} else {
					video.mute(true);
					setIcon(mute, Main.LOWSOUNDPIC);
				}
			}
		});
		
		mute.setToolTipText("Mutes/Unmutes the video");
		mute.setEnabled(false);
		mute.setSize(32, 32);
		add(mute);
	}

	// Adds the button that allows user to adjust the sound
	private void soundButton() {
		sound = new JButton();
		setIcon(sound, Main.HIGHSOUNDPIC);

		sound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Changes the icons of the mute button and when the mute
				// status when volume adjuster is being used
				if (volume.isVisible()) {
					if (video.getVolume() == 0) {
						video.mute(true);
						setIcon(mute, Main.LOWSOUNDPIC);
					} else {
						setIcon(mute, Main.MUTEPIC);
					}
					volume.setVisible(false);
					mute.setVisible(true);
					// Checks if the volume is muted, if it is then volume
					// becomes zero.
				} else {
					if (video.isMute()) {
						volume.setValue(0);
						video.mute(false);
						// Otherwise gets the volume of the video.
					} else {
						volume.setValue(video.getVolume());
					}
					volume.setVisible(true);
					mute.setVisible(false);
				}
			}
		});
		
		sound.setToolTipText("Adjust the volume");
		sound.setEnabled(false);
		sound.setSize(32, 32);
		add(sound);
	}

	// Adds the volume adjuster
	private void volumeAdjuster() {
		volume = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		volume.addChangeListener(new ChangeListener() {
			// Allows the user to adjust the volume
			public void stateChanged(ChangeEvent e) {
				int vol = volume.getValue();
				video.setVolume(vol);
				// Also changes the icon of the sound button
				if (vol == 0) {
					setIcon(sound, Main.MUTEPIC);
				} else {
					setIcon(sound, Main.HIGHSOUNDPIC);
				}
			}
		});
		volume.setSize(150, 32);
		volume.setVisible(false);
		add(volume);
	}

	private void fileChooser() {
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
					if (FileChecker.checkFile(jfile.getSelectedFile()
							.toString())) {
						Main.getInstance().original = jfile.getSelectedFile();
						video.playMedia(Main.getInstance().original
								.getAbsolutePath());
						pause.setVisible(true);
						play.setVisible(false);
						play.setEnabled(true);
						toggleStopButtons(true);
						videoSliderClock.start();
					} else {
						JOptionPane.showMessageDialog(null,
								"Please select a Video or Audio file",
								"Error!", JOptionPane.ERROR_MESSAGE);
					}
				}
				jfile.setVisible(true);
			}
		});
		chooser.setSize(116, 32);
		add(chooser);
	}

	// Adds the button that allows screen shot to be taken
	private void addCamera() {
		camera = new JButton();
		setIcon(camera, "/resource/camera.png");

		camera.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				screenShot = new ScreenShot(videoTimer.getText(), camera);
				screenShot.execute();
			}
		});
		
		camera.setToolTipText("<html>"+
				"Takes a screen shot of the video at the current time" +
				"<br>"+"Images are saved to where video is located!"+
				"</html>");
		camera.setEnabled(false);
		camera.setSize(32, 32);
		add(camera);
	}

	// Allows our JButtons to have an Picture.
	// Reference:
	// http://stackoverflow.com/questions/4801386/how-do-i-add-an-image-to-a-jbutton
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

	// Toggles buttons when the stopped button is pressed.
	private void toggleStopButtons(boolean b) {
		stop.setEnabled(b);
		back.setEnabled(b);
		forward.setEnabled(b);
		mute.setEnabled(b);
		sound.setEnabled(b);
		
		if(!FileChecker.isAudioFile(Main.getInstance().original.getAbsolutePath())){
			camera.setEnabled(true);
		} else {
			camera.setEnabled(false);
		}
	}

	// Allows resizing of the panel
	private void resize() {
		int x = Main.getInstance().getWidth();
		int y = Main.getInstance().getHeight();

		// Math used to find the location of the J Components
		play.setLocation((x / 2) - 34, y - 65);
		pause.setLocation((x / 2) - 34, y - 65);
		stop.setLocation((x / 2) + 2, y - 65);
		back.setLocation((x / 2) - 71, y - 65);
		forward.setLocation((x / 2) + 39, y - 65);
		chooser.setLocation(12, y - 65);
		sound.setLocation(x - 44, y - 65);
		mute.setLocation(x - 81, y - 65);
		volume.setLocation(x - 231, y - 65);
		videoPanel.setSize(x, y - 100);
		videoTimer.setLocation(x - 65, y - 100);
		videoSlider.setBounds(0, y - 100, x - 65, 25);
		camera.setLocation(133, y - 65);
	}

	// Allows user to play a downloaded file
	public void playDownloadedVideo(String downloadedFile) {
		Main.getInstance().original = new File(downloadedFile);
		video.stop();
		video.playMedia(Main.getInstance().original.getAbsolutePath());
		pause.setVisible(true);
		play.setVisible(false);
		play.setEnabled(true);
		toggleStopButtons(true);
		videoSliderClock.start();
	}

	// This allows the user to pause the video when they select away from the
	// playback tab
	public void pauseVideo() {
		if (video.isPlaying()) {
			video.pause();
			play.setVisible(true);
			pause.setVisible(false);
		}
	}

	// This toggles the play button on when the user selects a file not in the
	// playback tab
	public void enablePlay() {
		play.setEnabled(true);
		play.setVisible(true);
		pause.setVisible(false);
		toggleStopButtons(false);
		video.playMedia(Main.getInstance().original.getAbsolutePath());
		video.stop();
	}

}
