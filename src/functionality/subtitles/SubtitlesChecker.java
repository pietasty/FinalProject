package functionality.subtitles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import vamix.Main;

/**
 * This class checks if a video has subtitles or not. Has subtitles means that
 * it either has a subtitles stream or a .ass file associated with it.
 * 
 * @author ywu591
 * 
 */
public class SubtitlesChecker extends SwingWorker<Integer, Void> {
	private static SubtitlesChecker checker;
	
	private Process process;
	
	@Override
	protected Integer doInBackground() throws Exception {
		int output = 0;
		ProcessBuilder builder;
		
		String input = Main.getInstance().original.getAbsolutePath();
		
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
				if (line.contains("Subtitle")) {
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
	 * Checks if a video has a subtitle stream or not
	 */
	public static boolean hasSubtitleStream() {
		checker = new SubtitlesChecker();
		checker.execute();
		try {
			int result = checker.get();
			if (result > 0) {
				return true;
			}
		} catch (InterruptedException | ExecutionException e) {
		} 
		return false;
	}
	
	/**
	 * Checks if a video has a .ass file associated with it
	 */
	public static boolean hasSubtitlesFile(){
		String mainFile = Main.getInstance().original.getAbsolutePath();
		String extensionless = mainFile.substring(0,
				mainFile.lastIndexOf('.') + 1);
		String assFile = extensionless + "ass";
		File f = new File(assFile);
		if(f.exists()){
			return true;
		}
		return false;
	}
	
}
