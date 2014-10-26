package functionality.subtitles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import editPanes.Extract;
import editPanes.SubtitlesSave;

import vamix.Edit;
import vamix.Main;


/**
 * This class adds a subtitle stream to a video
 * @author ywu591
 *
 */
public class SubtitlesMerger extends SwingWorker<Integer,Void>{
	private Process process;
	

	@Override
	protected Integer doInBackground() throws Exception {
		String mainFile = Main.getInstance().original.getAbsolutePath();
		String extensionless = mainFile.substring(0,
				mainFile.lastIndexOf('.') + 1);
		String assFile = extensionless + "ass";
		String output = SubtitlesSave.getInstance().getOutputFile();
		
		// The commands
		ProcessBuilder builder;
		builder = new ProcessBuilder("avconv","-i",mainFile,"-i",assFile,"-y",output);


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
		Edit.getInstance().updateProgressBar(true, "Adding Subtitle Stream");
	}

	// Reports to the user if extraction was successful or not
	protected void done() {
		try {
			if (get() == 0) {
				JOptionPane.showMessageDialog(null,
						"Subtitles stream added successfully!");
				Edit.getInstance().setOutputFile(
						SubtitlesSave.getInstance().getOutputFile());
				Edit.getInstance().getVideo()
						.playMedia(Edit.getInstance().getOutputFile());
				Edit.getInstance().enableVideoButtons();
			} else if (get() > 0) {
				JOptionPane.showMessageDialog(null,
						"Error occurred in adding the subtitles stream");
			}
		} catch (InterruptedException | ExecutionException e) {
			JOptionPane.showMessageDialog(null, "Error occurred in adding the subtitles stream");
		}
		Edit.getInstance().enableEditButtons(true);
		Edit.getInstance().updateProgressBar(false, "Adding Subtitle Stream");
	}

}
