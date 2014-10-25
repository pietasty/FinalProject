package functionality.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import functionality.helpers.DataCollector;

import vamix.Edit;
import vamix.Main;

/**
 * Class to execute the the fade commands.
 * 
 * @author ywu591
 * 
 */
public class FadeWorker extends SwingWorker<Integer, Void> {
	private Process process;
	private String output;
	private int start;
	private int end;

	private String fpsline;
	private String durationline;
	private int fps;
	private int hh;
	private int mm;
	private int ss;

	public FadeWorker(int start, int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		fps = DataCollector.getfps();
		String duration = DataCollector.getDuration();
		String[] split = duration.split(":");
		hh = Integer.parseInt(split[0]);
		mm = Integer.parseInt(split[1]);
		ss = Integer.parseInt(split[2]);
		
		// More manipulation to figure out the frames
		int startlength = start * fps;
		int lastframe = (ss * fps) + (mm * 60 * fps) + (hh * 60 * 60 * fps);
		int endlength = end * fps;
		int endframe = lastframe - endlength;

		if (endframe < 0) {
			endframe = 0;
		}

		if (startlength > lastframe) {
			startlength = lastframe;
		}

		if (endlength > lastframe) {
			endlength = lastframe;
		}

		// default output name
		String filename = Main.getInstance().original.getName();
		String[] find = filename.split("\\.");
		output = Main.getInstance().original.getParent() + "/" + find[0];

		// fade commands
		ProcessBuilder fadebuilder;
		if (start == 0) {
			output = output + "_fadeout" + end + ".mp4";

			fadebuilder = new ProcessBuilder("avconv", "-i",
					Main.getInstance().original.getAbsolutePath(), "-vf",
					"fade=out:" + endframe + ":" + endlength, "-strict",
					"experimental", "-y", output);
		} else if (end == 0) {
			output = output + "_fadein" + start + ".mp4";

			fadebuilder = new ProcessBuilder("avconv", "-i",
					Main.getInstance().original.getAbsolutePath(), "-vf",
					"fade=in:0:" + startlength, "-strict", "experimental",
					"-y", output);
		} else {
			output = output + "_fadein" + start + "_fadeout" + end + ".mp4";

			fadebuilder = new ProcessBuilder("avconv", "-i",
					Main.getInstance().original.getAbsolutePath(), "-vf",
					"fade=in:0:" + startlength + "," + "fade=out:" + endframe
							+ ":" + endlength, "-strict", "experimental", "-y",
					output);
		}
		fadebuilder.redirectErrorStream(true);

		// execute the commands
		try {
			process = fadebuilder.start();
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
		Edit.getInstance().updateProgressBar(true, "Applying Fade");
	}

	// Reports to the user if Fading of the video is done correctly or not
	protected void done() {
		try {
			if (get() == -1) {
				JOptionPane
						.showMessageDialog(null,
								"Was unable to obtain data from file to execute the commnad");
			} else if (get() == 0) {
				JOptionPane.showMessageDialog(null, "Fading was Successful!");
				Edit.getInstance().setOutputFile(output);
				Edit.getInstance().getVideo()
						.playMedia(Edit.getInstance().getOutputFile());
				Edit.getInstance().enableVideoButtons();
			} else if (get() > 0) {
				JOptionPane.showMessageDialog(null,
						"Could not apply fade to the video");
			}
		} catch (InterruptedException | ExecutionException e) {
			JOptionPane.showMessageDialog(null,
					"Could not apply fade to the the video");
		}
		Edit.getInstance().enableEditButtons(true);
		Edit.getInstance().updateProgressBar(false, "");
	}

}
