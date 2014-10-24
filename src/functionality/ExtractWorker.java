package functionality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import vamix.Edit;
import vamix.Main;
import editPanes.Extract;

/**
 * Swing Worker Class for the extract function
 * 
 * @author ywu591
 */
public class ExtractWorker extends SwingWorker<Integer, Void> {
	private Process process;
	private String fullname;
	private String starttime;
	private String length;
	private boolean wholeFile;

	public ExtractWorker(String output, boolean check, String st, String len) {
		fullname = output;
		starttime = st;
		length = len;
		wholeFile = check;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		// The commands
		ProcessBuilder builder;
		if (wholeFile) {
			builder = new ProcessBuilder("avconv", "-i",
					Main.getInstance().original.getAbsolutePath(), "-ac", "2",
					"-vn", "-y", fullname);
		} else {
			builder = new ProcessBuilder("avconv", "-i",
					Main.getInstance().original.getAbsolutePath(), "-ss",
					starttime, "-t", length, "-ac", "2", "-vn", "-y", fullname);
		}

		// Sets up the builder and process
		builder.redirectErrorStream(true);
		try {
			process = builder.start();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));
			String line = null;
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
				publish();
			}
			stdoutBuffered.close();
		} catch (IOException e) {
		}
		return process.waitFor();
	}

	// Updates progressBar so user knows the process is going on
	protected void process(List<Void> chunks) {
		Edit.getInstance().updateProgressBar(true, "Extracting Audio");
	}

	// Reports to the user if extraction was successful or not
	protected void done() {
		try {
			if (get() == 0) {
				JOptionPane.showMessageDialog(null,
						"Audio Extracted Successfully!");
				Edit.getInstance().setOutputFile(
						Extract.getInstance().getOutputFile());
				Edit.getInstance().getVideo()
						.playMedia(Edit.getInstance().getOutputFile());
				Edit.getInstance().enableVideoButtons();
			} else if (get() > 0) {
				JOptionPane.showMessageDialog(null,
						"Error occurred in extraction");
			}
		} catch (InterruptedException | ExecutionException e) {
			JOptionPane.showMessageDialog(null, "Error occurred in extraction");
		}
		Edit.getInstance().enableEditButtons(true);
		Edit.getInstance().updateProgressBar(false, "Extracting Audio");
	}
}