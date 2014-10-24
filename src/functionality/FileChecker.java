package functionality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

/**
 * This class checks if the file is a correct video or audio file
 * 
 * @author ywu591
 * 
 */
public class FileChecker extends SwingWorker<Integer, Void> {
	private static FileChecker filechecker;
	private Process process;
	private String input;

	private FileChecker(String input) {
		this.input = input;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		int output = 0;
		ProcessBuilder builder;

		String cmd = "file -b \""
				+ input
				+ "\" | grep -c \"MPEG\\|Matroska\\|video\\|Video\\|audio\\|Audio\"";

		builder = new ProcessBuilder("/bin/bash", "-c", cmd);

		// Sets up the builder and process
		builder.redirectErrorStream(true);
		try {
			process = builder.start();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));

			String line = stdoutBuffered.readLine();
			output = Integer.parseInt(line);

			stdoutBuffered.close();
		} catch (IOException e) {

		}

		return output;
	}

	/**
	 * This method gets called when we want to check if the file is a valid
	 * video or audio file
	 */
	public static boolean checkFile(String s) {
		filechecker = new FileChecker(s);
		filechecker.execute();
		try {
			int result = filechecker.get();
			if (result > 0) {
				return true;
			}
			return false;
		} catch (InterruptedException | ExecutionException e) {
			return false;
		}
	}

}
