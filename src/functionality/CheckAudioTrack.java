package functionality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

/**
 * Class to check if the video has and Audio track or not
 * 
 * @author ywu591
 */
public class CheckAudioTrack extends SwingWorker<Integer, Void> {
	private static CheckAudioTrack check;
	private Process process;
	private String input;

	public CheckAudioTrack(String input) {
		this.input = input;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		int output = 0;
		ProcessBuilder builder;

		String cmd = "avprobe \"" + input + "\"";

		builder = new ProcessBuilder("/bin/bash", "-c", cmd);

		// Sets up the builder and process
		builder.redirectErrorStream(true);
		try {
			process = builder.start();

			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));

			String line = null;
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
				if (line.contains("Audio:")) {
					output++;
				}
			}

			stdoutBuffered.close();
		} catch (IOException e) {

		}

		process.waitFor();
		return output;
	}
	
	/**
	 * Method gets called when we want to check if the file has an audio track or not
	 */
	public static boolean checkAudioTrack(String s) {
		check = new CheckAudioTrack(s);
		check.execute();
		try {
			int result = check.get();
			if (result > 0) {
				return true;
			}
			return false;
		} catch (InterruptedException | ExecutionException e) {
			return false;
		}
	}

}
