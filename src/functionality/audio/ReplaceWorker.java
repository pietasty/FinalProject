package functionality.audio;

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
import editPanes.Replace;

/**
 * Swing Worker for the Replace Audio function
 */
public class ReplaceWorker extends SwingWorker<Integer, Void> {
	private Process process;
	private String infile;
	private String outfile;

	public ReplaceWorker(String in, String out) {
		infile = in;
		outfile = out;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		ProcessBuilder builder;
		// The commands
		builder = new ProcessBuilder("avconv", "-i",
				Main.getInstance().original.getAbsolutePath(), "-i", infile,
				"-vcodec", "copy", "-acodec", "copy", "-map", "0:0", "-map",
				"1:0", "-y", outfile);

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

	// Updates the progress bar so the user knows that processes are going on
	protected void process(List<Void> chunks) {
		Edit.getInstance().updateProgressBar(true, "Replacing Audio");
	}

	// Reports to the user if Replacement of audio is done correctly or not
	protected void done() {
		try {
			if (get() == 0) {
				JOptionPane.showMessageDialog(null,
						"Audio Replaced Successfully!");
				Edit.getInstance().setOutputFile(
						Replace.getInstance().getOutputFile());
				Edit.getInstance().getVideo()
						.playMedia(Edit.getInstance().getOutputFile());
				Edit.getInstance().enableVideoButtons();
			} else if (get() > 0) {
				JOptionPane
						.showMessageDialog(
								null,
								"Error occurred in Replacement of Audio!\n"
										+ "Most common error: output is not a video file");
			}
		} catch (InterruptedException | ExecutionException e) {
			JOptionPane.showMessageDialog(null,
					"Error occurred in Replacement of Audio!");
		}
		Edit.getInstance().enableEditButtons(true);
		Edit.getInstance().updateProgressBar(false, "");
	}
}
