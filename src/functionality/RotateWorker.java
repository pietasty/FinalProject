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

/**
 * Class that does the rotation of the video
 * 
 * @author ywu591
 * 
 */
public class RotateWorker extends SwingWorker<Integer, Void> {
	private Process process;
	private String output;
	private int option;

	public RotateWorker(String output, int option) {
		this.output = output;
		this.option = option;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		ProcessBuilder builder;
		int number = 0;
		// based on what option they picked it does the command
		if (option == 0) {
			number = 1;
		} else if (option == 2) {
			number = 2;
		} else if (option == 3) {
			number = 3;
		} else if (option == 4) {
			number = 0;
		}

		// The commands
		if (option == 1) {
			builder = new ProcessBuilder("avconv", "-i",
					Main.getInstance().original.getAbsolutePath(), "-vf",
					"transpose=1,transpose=1", "-strict", "experimental", "-y",
					output);
		} else {
			builder = new ProcessBuilder("avconv", "-i",
					Main.getInstance().original.getAbsolutePath(), "-vf",
					"transpose=" + number, "-strict", "experimental", "-y",
					output);
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

	// Updates the progress bar so the user knows that processes are going on
	protected void process(List<Void> chunks) {
		Edit.getInstance().updateProgressBar(true, "Rotating");
	}

	// Reports to the user if the rotation of the video is done correctly or not
	protected void done() {
		try {
			if (get() == 0) {
				JOptionPane.showMessageDialog(null, "Rotation was Successful!");
				Edit.getInstance().setOutputFile(output);
				Edit.getInstance().getVideo()
						.playMedia(Edit.getInstance().getOutputFile());
				Edit.getInstance().enableVideoButtons();
			} else if (get() > 0) {
				JOptionPane.showMessageDialog(null,
						"Could not rotate the video");
			}
		} catch (InterruptedException | ExecutionException e) {
			JOptionPane.showMessageDialog(null, "Could not rotate the video");
		}
		Edit.getInstance().enableEditButtons(true);
		Edit.getInstance().updateProgressBar(false, "");
	}

}
