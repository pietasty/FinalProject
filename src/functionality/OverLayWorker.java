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
import editPanes.OverLay;

/**
 * Swing Worker for the Overlay Audio function
 */
public class OverLayWorker extends SwingWorker<Integer, Void> {
	private Process process;
	private String infile;
	private String outfile;

	public OverLayWorker(String in, String out) {
		infile = in;
		outfile = out;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		ProcessBuilder builder;
		// The command
		builder = new ProcessBuilder("avconv", "-i",
				Main.getInstance().original.getAbsolutePath(), "-i", infile,
				"-filter_complex", "amix=inputs=2", "-strict", "experimental",
				"-y", outfile);

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

	// Updates progress bar so the user knows that a process is running
	protected void process(List<Void> chunks) {
		Edit.getInstance().updateProgressBar(true, "Overlaying Audio");
	}

	// Reports to the user based on if the user overlaid the audio successfully
	// or not.
	protected void done() {
		try {
			if (get() == 0) {
				JOptionPane.showMessageDialog(null,
						"Audio Overlayed Successfully!");
				Edit.getInstance().setOutputFile(
						OverLay.getInstance().getOutputFile());
				Edit.getInstance().getVideo()
						.playMedia(Edit.getInstance().getOutputFile());
				Edit.getInstance().enableVideoButtons();
			} else if (get() > 0) {
				JOptionPane.showMessageDialog(null,
						"Error occurred in Overlaying of Audio!");
			}
		} catch (InterruptedException | ExecutionException e) {
			JOptionPane.showMessageDialog(null,
					"Error occurred in Overlaying of Audio!");
		}
		Edit.getInstance().enableEditButtons(true);
		Edit.getInstance().updateProgressBar(false, "");
	}
}