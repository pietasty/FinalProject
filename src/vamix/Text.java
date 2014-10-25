/**
 * @author lwwe379 & ywu591
 */
package vamix;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;

import functionality.helpers.FileChecker;

/**
 * Class for Text panel.
 * 
 * My partner did this so I have no idea what it does
 * and I did not touch it as I am scared it will break
 */
public class Text extends JPanel {

	private static Text instance;

	public static Text getInstance() {
		if (instance == null) {
			instance = new Text();
		}
		return instance;
	}

	private JButton play = new JButton();
	private JButton pause = new JButton();
	private JButton stop = new JButton();
	private JButton back = new JButton();
	private JButton forward = new JButton();
	private JButton mute = new JButton();
	private JButton chooser = new JButton("Choose file");
	private JButton addTxtButton = new JButton("Add text");
	private JButton saveButton = new JButton("Save changes");
	private JButton cancelText = new JButton("Cancel");

	/*
	 * These fields represent data about the media file represented as Strings
	 */
	private String width; // width of video resolution
	private String height; // height of video
							// "/se206_a03/icons/back.png"resolution
	private String framerate; // framerate of video
	private String duration; // length of video

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	private JEditorPane startText = new JEditorPane();
	private JEditorPane endText = new JEditorPane();
	private JProgressBar progress = new JProgressBar(); // initialize a progress
														// bar
	private File outputFile;
	private File settingsFile = new File(System.getProperty("user.home")
			+ "/.vamixsettings");
	protected JLabel filenameLabel = new JLabel("");
	private String fileDir; // absolute path (includes file name
	private String filepath; // path of file without file name
	private TextWorker addText;
	private JPanel progressPanel = new JPanel();
	private JEditorPane textPreview = new JEditorPane(); // text previewer
	private String currentFont;
	private String currentFontSize;
	private String fontColor;
	private String startDuration = "5"; // initialize duration as 5 seconds
	private String endDuration = "5";
	private Font font = new Font(Font.SANS_SERIF, 0, 12); // default font. gets
															// changed
	private JComboBox<String> fontsizeSelect = new JComboBox<String>();
	private JComboBox<String> fontSelect = new JComboBox<String>();
	private JComboBox<String> colorSelect = new JComboBox<String>();
	private JComboBox<String> startTimeSelect = new JComboBox<String>();
	private JComboBox<String> endTimeSelect = new JComboBox<String>();
	// define the length of time we want to allow
	private String[] times = { "05", "10", "15", "20", "30", "40", "50", "60" };
	// define font colors in a String array
	private String[] colors = { "black", "white", "red", "orange", "yellow",
			"green", "blue", "cyan", "magenta" };
	// define font sizes into a String array
	private String[] fontSizes = { "12", "13", "14", "15", "16", "17", "18",
			"20", "22", "24", "26", "28", "32", "36", "40", "44", "48", "54",
			"60", "66", "72" };

	// define all the fonts we'll support in an enum
	private enum Fonts {
		FreeSerif("/usr/share/fonts/truetype/freefont/FreeSerif.ttf"), FreeSans(
				"/usr/share/fonts/truetype/freefont/FreeSans.ttf"), FreeMono(
				"/usr/share/fonts/truetype/freefont/FreeMono.ttf"), Garuda(
				"/usr/share/fonts/truetype/tlwg/Garuda.ttf"), Kinnari(
				"/usr/share/fonts/truetype/tlwg/Kinnari.ttf"), LiberationMono(
				"/usr/share/fonts/truetype/liberation/LiberationMono-Regular.ttf"), Purisa(
				"/usr/share/fonts/truetype/tlwg/Purisa.ttf"), Sawasdee(
				"/usr/share/fonts/truetype/tlwg/Sawasdee.ttf"), Typo(
				"/usr/share/fonts/truetype/tlwg/TlwgTypo.ttf");
		// Fonts constructor
		Fonts(String s) {
			_path = s;
		}

		String _path; // argument is the .ttf file of the font
	}

	// create an ActionListener to respond to changes in text settings
	private ActionListener textSettingsListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// set the font and font size
			currentFont = (String) fontSelect.getSelectedItem();
			String fontPath = Fonts.valueOf(currentFont)._path;
			currentFontSize = ((String) fontsizeSelect.getSelectedItem());
			fontColor = (String) colorSelect.getSelectedItem();
			int fontSize = Integer.parseInt(currentFontSize);

			try {
				// change the font field to user selected font
				font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
			} catch (FontFormatException | IOException e1) {

			}
			// change font field to also be user selected size
			font = font.deriveFont((float) fontSize);
			Color color;
			try {
				// use reflection to access member of Color class
				Field colorField = Class.forName("java.awt.Color").getField(
						fontColor);
				color = (Color) colorField.get(null);
			} catch (Exception exception) {
				color = Color.BLACK; // catch and set to defaults
			}
			textPreview.setForeground(color);
			// set the preview area to the updated font
			textPreview.setFont(font);
		}

	};

	private ActionListener textTimeListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			startDuration = (String) startTimeSelect.getSelectedItem();
			endDuration = (String) endTimeSelect.getSelectedItem();
		}

	};

	private Text() {
		setLayout(new BorderLayout()); // set layout of entire frame
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2)); // all main components in
													// grid
		// video panel
		JPanel mediaPanel = new JPanel();
		mediaPanel.setLayout(new BoxLayout(mediaPanel, BoxLayout.Y_AXIS));
		mediaPanel.setMaximumSize(new Dimension(400, 200));
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();
		JPanel videoPanel = new JPanel();
		videoPanel.setLayout(new BorderLayout());
		videoPanel.add(mediaPlayerComponent);
		videoPanel.setMaximumSize(new Dimension(400, 200));
		mediaPanel.add(videoPanel);
		// add playback buttons
		JPanel playbackPanel = new JPanel();
		playbackPanel.setLayout(new BoxLayout(playbackPanel, BoxLayout.X_AXIS));
		playbackPanel.setMaximumSize(new Dimension(400, 50));
		// invoke methods to set up buttons and add to playbackPanel
		playbackPanel.add(new JLabel("        "));
		setupPlaybackButtons();
		addPlaybackButtons(playbackPanel);
		mediaPanel.add(playbackPanel);
		mediaPanel.add(new JLabel(" ")); // create a space between player and
											// file chooser
		// create file chooser panel
		JPanel choosePanel = new JPanel();
		choosePanel.setLayout(new BorderLayout());
		choosePanel.setMaximumSize(new Dimension(400, 30));
		choosePanel.add(chooser, BorderLayout.WEST); // add choose file
		// set action listener to open up file chooser
		chooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				// open file chooser in new window
				JFileChooser fileSelector = new JFileChooser();
				File f = Main.getInstance().original;
				if (f != null) {
					fileSelector.setCurrentDirectory(new File(f.getParent()));
				}
				fileSelector.showOpenDialog(Text.this);

				try {

					if (FileChecker.checkFile(fileSelector.getSelectedFile()
							.toString())) {
						// get File object of selected file
						Main.getInstance().original = fileSelector
								.getSelectedFile().getAbsoluteFile();
						// get absolute path (includes name of file) in fileDir
						fileDir = fileSelector.getSelectedFile()
								.getAbsolutePath();
						// set label GUI component
						filenameLabel.setText("Selected file: "
								+ Main.getInstance().original.getName());
						filenameLabel.setVisible(true);
						filenameLabel.setFont(new Font(Font.SANS_SERIF, 0, 10));
						// get path of file (excluding file name)
						filepath = fileDir.substring(0,
								fileDir.lastIndexOf(File.separator));
						// set the outputFile (unsaved file) as a dot file of
						// selectedFile
						outputFile = new File(filepath + "/."
								+ Main.getInstance().original.getName());
						addTxtButton.setEnabled(true);
						Playback.getInstance().enablePlay();
					} else {
						JOptionPane.showMessageDialog(null,
								"Please select a Video or Audio file",
								"Error!", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NullPointerException e) {
					return; // return since no file was selected
				}
			}
		});

		choosePanel.add(filenameLabel, BorderLayout.EAST);
		filenameLabel.setVisible(false);
		filenameLabel.setPreferredSize(new Dimension(250, 30));
		mediaPanel.add(choosePanel); // add file chooser panel to GUI
		// add preview for current font and font size
		JPanel previewPane = new JPanel();
		previewPane.setLayout(new BoxLayout(previewPane, BoxLayout.Y_AXIS));
		previewPane.setMaximumSize(new Dimension(400, 150));
		mediaPanel.add(previewPane); // add to textPanel
		JPanel previewLabel = new JPanel();
		previewLabel.setLayout(new BorderLayout());
		previewLabel.setMaximumSize(new Dimension(400, 15));
		previewLabel.add(new JLabel("Text preview:"), BorderLayout.WEST);
		previewPane.add(new JLabel(" ")); // leave a gap in GUI
		previewPane.add(previewLabel);
		// define the preview text area
		textPreview
				.setText("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789");
		textPreview.setEditable(false);
		textPreview.setSize(new Dimension(390, 150));
		textPreview.setFont(font);
		// define scroll bar for preview area
		JScrollPane previewScroll = new JScrollPane(textPreview,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		;
		// fix scroll panel size
		previewScroll.setPreferredSize(new Dimension(400, 165));
		previewScroll.setMaximumSize(new Dimension(400, 165));
		previewPane.add(previewScroll);
		mediaPanel.setVisible(true);
		mainPanel.add(mediaPanel); // add videoPanel to first column of grid

		// text components panel
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		JPanel topPanel = new JPanel();
		textPanel.add(topPanel);
		topPanel.setLayout(new BorderLayout());
		JLabel startLabel = new JLabel("Text at start of video:");
		topPanel.add(startLabel, BorderLayout.NORTH);
		// set document filter for start text
		startText.setDocument(new TextDocumentFilter());
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		startText.setBorder(border);
		topPanel.add(startText, BorderLayout.CENTER);
		startText.setMinimumSize(new Dimension(300, 105)); // set min size
		// add time selector for length of text duration
		JPanel startTimePanel = new JPanel();
		startTimePanel.setLayout(new BorderLayout());
		startTimePanel.setPreferredSize(new Dimension(300, 20));
		startTimePanel.add(new JLabel("Select duration (seconds): "),
				BorderLayout.WEST);
		// add the times to the start time combo box
		for (String t : times) {
			startTimeSelect.addItem(t);
		}
		// add time listener to update time fields
		startTimeSelect.addActionListener(textTimeListener);
		startTimePanel.add(startTimeSelect, BorderLayout.EAST); // add combo box
																// to time panel
		topPanel.add(startTimePanel, BorderLayout.SOUTH); // add to top panel
		// fix the top panel size
		topPanel.setMaximumSize(new Dimension(300, 120));
		topPanel.setPreferredSize(new Dimension(300, 120));
		textPanel.add(new JLabel(" "));
		JPanel bottomPanel = new JPanel();
		textPanel.add(bottomPanel);
		bottomPanel.setLayout(new BorderLayout());

		JLabel endLabel = new JLabel("Text at end of video:  ");
		bottomPanel.add(endLabel, BorderLayout.NORTH);

		// set document filter for end text
		endText.setDocument(new TextDocumentFilter());
		endText.setBorder(border);
		bottomPanel.add(endText, BorderLayout.CENTER);
		endText.setMinimumSize(new Dimension(300, 105)); // set min size
		// add end time duration selector
		JPanel endTimePanel = new JPanel();
		endTimePanel.setLayout(new BorderLayout());
		endTimePanel.setPreferredSize(new Dimension(300, 20));
		endTimePanel.add(new JLabel("Select duration (seconds): "),
				BorderLayout.WEST);
		// add the times to the end time combo box
		for (String t : times) {
			endTimeSelect.addItem(t);
		}
		// add time listener to update time fields
		endTimeSelect.addActionListener(textTimeListener);
		endTimePanel.add(endTimeSelect, BorderLayout.EAST); // add combo box to
															// time panel
		bottomPanel.add(endTimePanel, BorderLayout.SOUTH); // add to top panel
		// fix the bottom panel size
		bottomPanel.setMaximumSize(new Dimension(300, 120));
		bottomPanel.setPreferredSize(new Dimension(300, 120));
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// create output file selector panel
				JPanel outSelectPanel = new JPanel();
				outSelectPanel.setPreferredSize(new Dimension(420, 180));
				outSelectPanel.setLayout(new BoxLayout(outSelectPanel,
						BoxLayout.X_AXIS));
				outSelectPanel.add(new JLabel("Select output file: "));
				final JTextField outText = new JTextField();
				outText.setSize(250, 20);
				outText.setText(System.getProperty("user.home"));
				outText.setMaximumSize(new Dimension(150, 25));
				outSelectPanel.add(outText);
				outSelectPanel.add(new JLabel(" "));
				JButton browse = new JButton("Browse");
				browse.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// open file chooser in new window
						JFileChooser selector = new JFileChooser();
						selector.showOpenDialog(Text.this);
						try {
							outText.setText(selector.getSelectedFile()
									.getAbsolutePath());
						} catch (NullPointerException e1) {

						}
					}

				});
				outSelectPanel.add(browse);

				String[] options = { "Save", "Cancel" }; // options for option
															// pane
				// create an option pane to open up output file chooser window
				int n = JOptionPane.showOptionDialog(null, outSelectPanel,
						"Save Video", JOptionPane.YES_NO_OPTION,
						JOptionPane.NO_OPTION, null, options, options[0]);
				if (n == 0) {
					// check if file exists
					File dummy = new File(outText.getText());
					if (dummy.exists()) {
						JOptionPane.showMessageDialog(null,
								"Error: You cannot override an existing file",
								"Override error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					boolean worked = outputFile.renameTo(new File(outText
							.getText()));
					if (!worked) {
						JOptionPane.showMessageDialog(null,
								"Error: There was an error creating the new saved file. "
										+ "Save operation was not successful.",
								"Save error", JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"Save operation successful!", "Success!",
								JOptionPane.PLAIN_MESSAGE);
					}
				}
			}
		});
		saveButton.setEnabled(false); // initially off
		addTxtButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = startText.getText();
				String credits = endText.getText();
				if (Main.getInstance().original != null) {
					updateFileFields();
					addText = new TextWorker(title, credits);
					addText.execute(); // execute swing worker class
				} else {
					JOptionPane.showMessageDialog(null,
							"Error: Please select a valid file");
				}
			}
		});
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BorderLayout());
		btnPanel.setMaximumSize(new Dimension(300, 45));
		btnPanel.add(new JLabel(" "), BorderLayout.NORTH);
		btnPanel.add(saveButton, BorderLayout.WEST);
		btnPanel.add(addTxtButton, BorderLayout.EAST);
		textPanel.add(btnPanel, BorderLayout.SOUTH);

		// leave space between text options and button panel with JLabel
		textPanel.add(new JLabel(" "));
		// Panel for selecting font size
		JPanel fontSizePanel = new JPanel();
		fontSizePanel.setMaximumSize(new Dimension(300, 20));
		textPanel.add(fontSizePanel); // add into textPanel
		fontSizePanel.setLayout(new GridLayout(1, 2));
		fontSizePanel.add(new JLabel("Select font size: "));

		// add all the font size options from the fontSizes String array
		for (String f : fontSizes) {
			fontsizeSelect.addItem(f);
		}
		currentFontSize = (String) fontsizeSelect.getSelectedItem();
		// add action listener for preview pane update
		fontsizeSelect.addActionListener(textSettingsListener);
		fontSizePanel.add(fontsizeSelect); // add combo box into font size panel

		// panel for selecting font
		JPanel fontPanel = new JPanel();
		fontPanel.setLayout(new GridLayout(1, 2));
		textPanel.add(fontPanel); // add into textPanel
		fontPanel.setMaximumSize(new Dimension(300, 20));
		fontPanel.add(new JLabel("Select font: "));
		// add fonts into JComboBox
		for (Fonts f : Fonts.values()) {
			fontSelect.addItem(f.name());
		}
		currentFont = (String) fontSelect.getSelectedItem(); // set currentFont
		// set action listener to change preview box
		fontSelect.addActionListener(textSettingsListener);
		fontPanel.add(fontSelect); // add combo box into fontPanel

		// panel for selecting color
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(1, 2));
		textPanel.add(colorPanel);
		colorPanel.setMaximumSize(new Dimension(300, 20));
		colorPanel.add(new JLabel("Select font colour: "));
		// add colors into combo box
		for (String c : colors) {
			colorSelect.addItem(c);
		}
		fontColor = (String) colorSelect.getSelectedItem();
		// set action listener to update preview box
		colorSelect.addActionListener(textSettingsListener);
		colorPanel.add(colorSelect);

		// load button panel here
		JPanel loadPanel = new JPanel();
		loadPanel.setLayout(new BorderLayout());
		loadPanel.setVisible(true);
		loadPanel.setMaximumSize(new Dimension(300, 45));
		JButton loadProject = new JButton("Load project");
		// disable load button if no save file exists
		if (!settingsFile.exists()) {
			loadProject.setEnabled(false);
		}
		// add action listener for load button
		loadProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					StringBuffer saveBuffer = new StringBuffer(); // store file
																	// contents
																	// in buffer
					BufferedReader reader = new BufferedReader(new FileReader(
							settingsFile)); // read from a buffered reader
					String line;
					while ((line = reader.readLine()) != null) {
						saveBuffer.append(line + "\n"); // get content into
														// saveBuffer
					}
					// split string by tab character
					String[] saveArgs = saveBuffer.toString().split("\t");
					// set field variables based on string
					Main.getInstance().original = new File(saveArgs[0]); // set
																			// input
																			// file
					setFilenameLabel(); // set label showing file name
					startText.setText(saveArgs[1]); // set start text
					startTimeSelect.setSelectedItem(saveArgs[2]); // set start
																	// duration
					endText.setText(saveArgs[3]); // set end text
					endTimeSelect.setSelectedItem(saveArgs[4]); // set end
																// duration
					Fonts loadedFont = Fonts.valueOf(saveArgs[5]); // get font
					fontSelect.setSelectedItem(loadedFont.name()); // set font
					fontsizeSelect.setSelectedItem(saveArgs[6]); // set font
																	// size
					// argument 7 will have a newline appended so get rid of it
					// with trim
					colorSelect.setSelectedItem(saveArgs[7].trim()); // set font
																		// color
					// print success message
					JOptionPane.showMessageDialog(null,
							"Previous project loaded successfully!",
							"Load successful!", JOptionPane.PLAIN_MESSAGE);
					reader.close(); // close reader
					Playback.getInstance().enablePlay();
				} catch (Exception e) {
					// show an error for caught exception
					JOptionPane.showMessageDialog(null,
							"Error: There was an error reading the saved settings. Some "
									+ "settings may not be loaded.",
							"FileReader error", JOptionPane.ERROR_MESSAGE);
					return;
				}

			}

		});
		loadPanel.add(new JLabel(" "), BorderLayout.NORTH);
		loadPanel.add(loadProject, BorderLayout.EAST);
		JButton saveProject = new JButton("Save project");
		// add action listener for save project button
		saveProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					PrintWriter writeSave = new PrintWriter(settingsFile
							.getAbsolutePath(), "UTF-8");
					// write to file. insert tab character after each string to
					// allow easy separation
					writeSave.print(Main.getInstance().original
							.getAbsolutePath() + "\t"); // input file
					writeSave.print(startText.getText() + "\t"); // start text
					writeSave.print(startDuration + "\t"); // start length
					writeSave.print(endText.getText() + "\t"); // end text
					writeSave.print(endDuration + "\t"); // end length
					writeSave.print(currentFont + "\t"); // font
					writeSave.print(currentFontSize + "\t"); // font size
					writeSave.print(fontColor); // font color
					writeSave.close(); // close PrintWriter
					JOptionPane.showMessageDialog(null,
							"Project settings saved successfully!");
				} catch (FileNotFoundException | UnsupportedEncodingException e1) {
					// show error message for caught exception.
					JOptionPane.showMessageDialog(null,
							"There was an error saving to file.",
							"PrintWriter error", JOptionPane.ERROR_MESSAGE);
					return;
				} catch (NullPointerException e2) {
					JOptionPane
							.showMessageDialog(
									null,
									"Error: You need to select a file before saving a project.",
									"File not found error",
									JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		loadPanel.add(saveProject, BorderLayout.WEST);
		textPanel.add(loadPanel);

		// progress bar panel here
		textPanel.add(progressPanel);
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
		progressPanel.setMaximumSize(new Dimension(300, 40));
		progressPanel.setPreferredSize(new Dimension(300, 40));
		progressPanel.add(progress);
		progressPanel.add(new JLabel(" "));
		progress.setMaximumSize(new Dimension(300, 25));
		progress.setStringPainted(true);
		progressPanel.add(cancelText);
		// add action listener to cancel swing worker
		cancelText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addText.cancel(true);
			}
		});
		cancelText.setEnabled(false);
		progressPanel.setVisible(false);

		mainPanel.add(textPanel); // add textPanel into 2nd column of grid
		add(mainPanel, BorderLayout.CENTER); // add grid into center of GUI
	}

	/**
	 * This helper method is called when add text is pressed (just before swing
	 * worker) it updates the fileDir, filepath and outputFile fields to the
	 * currently selected files.
	 */
	private void updateFileFields() {
		fileDir = Main.getInstance().original.getAbsolutePath();
		// get path of file (excluding file name)
		filepath = fileDir.substring(0, fileDir.lastIndexOf(File.separator));
		// set the outputFile (unsaved file) as a dot file of selectedFile
		outputFile = new File(filepath + "/."
				+ Main.getInstance().original.getName());
	}

	/*
	 * Create a TextDocumentFilter class that extends PlainDocument to handle
	 * the text input
	 */
	class TextDocumentFilter extends PlainDocument {
		/**
		 * set the limit of characters to 160. If we assume the average word has
		 * ~5 characters. A 160 character limit would fall in the 20-40 word
		 * limit for the title/credits. Twitter uses 140 characters and that has
		 * a good size so our number is close enough to be a good estimate. Also
		 * we've limited input to 5 lines so text can't be entered off screen.
		 */
		private int charLimit = 160;
		private int lineLimit = 6;

		TextDocumentFilter() {
			this.setDocumentFilter(new DocumentFilter() {
				/**
				 * This helper method determines if a string has exceeded the
				 * character limit of 160 or the line limit of 6
				 * 
				 * @param text
				 * @return
				 */
				public boolean validText(String text) {
					// check character limit is exceeded
					if (text.length() > charLimit) {
						return false;
					}
					// check new line limit is exceeded
					String[] splitText = text.split("\n");
					if (splitText.length >= lineLimit) {
						return false;
					}
					// check for bad characters as they mess up bash command
					char[] badChars = { '\\', '\'', '\"', ':' }; // array of bad
																	// characters
					for (char c : badChars) {
						// check string for any and all bad chars
						if (text.contains(c + "")) {
							return false;
						}
					}
					return true; // return true otherwise
				}

				/**
				 * This helper method takes in a string and returns the number
				 * of newline characters in the string
				 * 
				 * @param text
				 * @return
				 */
				public int countLines(String text) {
					// counts the number of newline characters in the string
					return text.replaceAll("[^\n]", "").length();
				}

				@Override
				public void insertString(FilterBypass fb, int off, String str,
						AttributeSet attr) throws BadLocationException {
					StringBuilder sb = new StringBuilder();
					sb.append(fb.getDocument().getText(0,
							fb.getDocument().getLength()));
					sb.insert(off, str);
					String text = sb.toString();
					str = str.replaceAll("\\t", ""); // get rid of tabs
					// get rid of new line character that are after the line
					// limit
					if (countLines(text) >= lineLimit - 1) {
						str = str.replaceAll("\\n", "");
					}

					// check the string is valid before inserting
					if (validText(text)) {
						super.insertString(fb, off, str, attr);
					}
				}

				@Override
				public void replace(FilterBypass fb, int off, int len,
						String str, AttributeSet attr)
						throws BadLocationException {
					StringBuilder sb = new StringBuilder();
					sb.append(fb.getDocument().getText(0,
							fb.getDocument().getLength()));
					int end = off + len;
					sb.replace(off, end, str);
					String text = sb.toString();
					str = str.replaceAll("\\t", ""); // get rid of tabs
					// get rid of new line character that are after the line
					// limit
					if (countLines(text) >= lineLimit - 1) {
						str = str.replaceAll("\\n", "");
					}
					// replace string if replacement is valid
					if (validText(text)) {
						super.replace(fb, off, len, str, attr);
					}
				}

			});
		}

	}

	class TextWorker extends SwingWorker<Void, Integer> {

		TextWorker(String title, String credits) {
			_title = title;
			_credits = credits;
		}

		String _title;
		String _credits;
		Process avconv;

		// define output file for swingworker class instance

		@Override
		protected Void doInBackground() throws Exception {
			Files.deleteIfExists(outputFile.toPath()); // overwrite previous
														// text operation
			// get info needed from metadata
			getMetaData();
			// using some of the metadata check to see if there is any error
			// with input
			if (!isValidInput()) {
				// cancel swing worker if input is not valid
				// isValidInput method will create the popup error messages
				this.cancel(true);
			}
			// define progressbar between 0 and duration time
			progress.setMaximum(Integer.parseInt(duration));
			progressPanel.setVisible(true);
			cancelText.setEnabled(true); // enable cancel
			// disable add text button while running
			addTxtButton.setEnabled(false);
			// bash command to apply text
			/**
			 * Applying both text filters to the same video in one bash command
			 * took a long time to do. I learned how to use [in] and [out]
			 * filters from the answer provided here
			 * http://stackoverflow.com/questions
			 * /11138832/ffmpeg-multiple-text-in-one-command-drawtext and
			 * applied it to how we needed to use it
			 */
			String cmd = "avconv -threads 8 -i \"" + fileDir
					+ "\" -strict experimental " + "-vf \"[in]drawtext="
					+ "fontfile=\'" + Fonts.valueOf(currentFont)._path + "\':"
					+ "text=\'" + _title + "\':"
					+ "x=(main_w/2-text_w/2):y=(main_h/2-text_h/2):fontsize="
					+ font.getSize() + ":" + "fontcolor=" + fontColor + ":"
					+ "draw=\'lt(n,$((" + framerate + "*" + startDuration
					+ ")))\'," + "drawtext=fontfile=\'"
					+ Fonts.valueOf(currentFont)._path + "\':" + "text=\'"
					+ _credits
					+ "\':x=(main_w/2-text_w/2):y=(main_h/2-text_h/2):"
					+ "fontsize=" + font.getSize() + ":fontcolor=" + fontColor
					+ ":" + "draw=\'gt(n,$((" + framerate + "*(" + duration
					+ "-" + endDuration + "))))\'" + "[out]\" "
					+ outputFile.getAbsolutePath();
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			builder.redirectErrorStream(true); // redirect error to stdout
			avconv = builder.start();
			InputStream out = avconv.getInputStream();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					out));
			String line;
			Matcher matcher;
			while ((line = stdout.readLine()) != null && !isCancelled()) {
				//TODO why does text not work???? so the confused lol gg
				System.out.println(line);
				// match pattern time=000.00
				matcher = Pattern.compile("time=\\d+.\\d+").matcher(line);
				if (matcher.find()) {
					int start = matcher.start();
					int end = matcher.end() - 2; // minus 2 account for decimal
													// points
					String progressTime = line.substring(start, end)
							.replaceAll("[^0-9]", ""); // eliminate "time=" and
														// spaces
					int progressNum = Integer.parseInt(progressTime);
					publish(progressNum);
				}

			}
			// check for bad exit code
			int code;
			if ((code = avconv.waitFor()) != 0) {
				JOptionPane.showMessageDialog(null,
						"There was an error executing"
								+ " add text command. Exit code " + code,
						"Add text error", JOptionPane.ERROR_MESSAGE);
			}
			avconv.destroy();
			return null;
		}

		private boolean isValidInput() {
			// check selected start + end duration doesn't exceed the total
			// video length
			if (Integer.parseInt(startDuration) + Integer.parseInt(endDuration) > Integer
					.parseInt(duration)) {
				JOptionPane.showMessageDialog(null,
						"Error: Selected start and end"
								+ " time exceed file duration",
						"Error: Time input error", +JOptionPane.ERROR_MESSAGE,
						null);
				return false;
			}
			return true;
		}

		// update progress bar
		protected void process(List<Integer> chunks) {
			for (Integer i : chunks) {
				progress.setValue(i);
			}
		}

		protected void done() {
			if (!isCancelled()) {
				saveButton.setEnabled(true);
				video.playMedia(outputFile.toString());
				pause.setVisible(true);
				play.setVisible(false);
				play.setEnabled(true);
				toggleStopButtons(true);
			}

			progressPanel.setVisible(false);
			cancelText.setEnabled(false);
			addTxtButton.setEnabled(true);
		}

		/**
		 * This helper method gets the frame rate of the video file we are using
		 * by also being run in background with rest of SwingWorker.
		 * 
		 * @return
		 * @throws Exception
		 */
		private void getMetaData() throws Exception {
			String printCommand = "avconv -i \"" + fileDir + "\""; // prints
																	// data of
																	// file
			// usual process setting up and starting
			Process printMetadata;
			ProcessBuilder avconv = new ProcessBuilder("/bin/bash", "-c",
					printCommand);
			printMetadata = avconv.start();
			/*
			 * Read error stream into java input stream. This works because our
			 * printCommand doesn't specify and output file for avconv (since we
			 * just want to print the file details). This means the output
			 * generated will be error stream (took way too long to figure this
			 * out :P )
			 */
			InputStream output = printMetadata.getErrorStream();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					output));
			String line;
			// set boolean values to know when all variables have been read
			boolean frRead = false;
			boolean resRead = false;
			boolean timeRead = false;
			while ((line = stdout.readLine()) != null) {
				// check if line matches pattern
				// pattern is 1 or more digits followed by optional decimal
				// point numbers then SPACE fps
				Matcher fpsMatch = Pattern.compile("\\d+.?\\d+? fps").matcher(
						line);
				// check and set framerate
				if (fpsMatch.find()) {
					int start = fpsMatch.start();
					int end = fpsMatch.end();
					String substring = line.substring(start, end);
					frRead = true;
					// if there's decimal point in substring set new end point
					// at decimal
					// and set framerate to be from zero to decimal point
					if (substring.contains(".")) {
						end = substring.indexOf('.');
						framerate = substring.substring(0, end);
					} else {
						// create substring where match occurred and remove
						// non-digits (this is executed if
						// there is no decimal point
						framerate = line.substring(start, end).replaceAll(
								"[^0-9]", "");
					}
				}
				// check and set width and height
				Matcher resMatch = Pattern.compile("\\d+x\\d+").matcher(line);
				if (resMatch.find()) {
					int start = resMatch.start();
					int end = resMatch.end();
					// create substring where match occurred and split at "x"
					String[] res = line.substring(start, end).split("x");
					width = res[0].replaceAll("[^0-9]", ""); // set width
					height = res[1].replaceAll("[^0-9]", "");
					resRead = true;
				}
				// check and set duration in seconds
				// pattern is "Duration: 00:00:00.00" format
				Matcher timeMatch = Pattern.compile(
						"Duration: \\d+:\\d+:\\d+.\\d+").matcher(line);
				if (timeMatch.find()) {
					int start = timeMatch.start(); // numbers start 9 chars in
					int end = timeMatch.end();
					int time = 0; // store total time in seconds
					// create substring where numbers are and split at colon
					String[] split = line.substring(start + 9, end).split(":");
					time += Integer.parseInt(split[0].replaceAll("[^0-9]", "")) * 3600; // hour
																						// field
					time += Integer.parseInt(split[1].replaceAll("[^0-9]", "")) * 60; // minute
																						// field
					time += Integer.parseInt(split[2].trim().substring(0, 2)); // seconds
																				// field
																				// (first
																				// 2
																				// chars)
					duration = Integer.toString(time);
					timeRead = true;
				}
				// end method execution if all metadata has been found
				if (frRead && resRead && timeRead) {
					printMetadata.destroy();
					return;
				}
			}
			printMetadata.destroy(); // kill process
		}

	}

	/**
	 * Playback components are defined here. The Text preview is simpler than
	 * Playback since the preview doesn't require as many features. The fast
	 * forward and rewind only skips for a second, not continuously.
	 */
	private void playButton() {
		play = new JButton();

		setIcon(play, Main.PLAYPIC);

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (video.isPlayable()) {
					video.pause();
					pause.setVisible(true);
					play.setVisible(false);
				} else {
					video.playMedia(outputFile.toString());
					pause.setVisible(true);
					play.setVisible(false);
					toggleStopButtons(true);
				}
			}
		});

		play.setEnabled(false);
	}

	private void pauseButton() {
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
	}

	private void stopButton() {
		stop = new JButton();
		setIcon(stop, Main.STOPPIC);

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressStopButton();
			}
		});
		stop.setEnabled(false);
	}

	private void muteButton() {
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
	}

	private void forwardButton() {
		forward = new JButton();

		setIcon(forward, Main.FORWARDPIC);

		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.skip(10000);
			}
		});

		forward.setEnabled(false);
	}

	private void backButton() {
		back = new JButton();

		setIcon(back, Main.BACKPIC);

		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.skip(-1000);
			}
		});

		back.setEnabled(false);
	}

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
	 * Gets called when we want to stop the video!
	 */
	private void pressStopButton() {
		video.stop();
		pause.setVisible(false);
		play.setVisible(true);
		toggleStopButtons(false);
	}

	private void toggleStopButtons(boolean b) {
		stop.setEnabled(b);
		back.setEnabled(b);
		forward.setEnabled(b);
		mute.setEnabled(b);
	}

	/**
	 * This method takes a JPanel as input and adds all the buttons in the
	 * correct order to the panel.
	 * 
	 * @param panel
	 */
	public void addPlaybackButtons(JPanel panel) {
		panel.add(play);
		panel.add(pause);
		panel.add(new JLabel("       "));
		panel.add(stop);
		panel.add(new JLabel("       "));
		panel.add(back);
		panel.add(new JLabel("       "));
		panel.add(forward);
		panel.add(new JLabel("       "));
		panel.add(mute);
	}

	/**
	 * This helper method calls all the methods each button has to set up their
	 * action listeners and icons
	 */
	public void setupPlaybackButtons() {
		playButton();
		pauseButton();
		stopButton();
		muteButton();
		forwardButton();
		backButton();
	}

	/**
	 * This allows the program to automatically pause the video when they select
	 * away from the Text tab.
	 */
	public void pauseVideo() {
		if (video.isPlaying()) {
			video.pause();
			play.setVisible(true);
			pause.setVisible(false);
		}
	}

	/**
	 * This function updates the JLabel which displays the current selected file
	 * for Text.
	 */
	public void setFilenameLabel() {
		filenameLabel.setText("Selected file: "
				+ Main.getInstance().original.getName());
		filenameLabel.setVisible(true);
		filenameLabel.setFont(new Font(Font.SANS_SERIF, 0, 10));
	}

}
