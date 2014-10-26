package functionality.subtitles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import vamix.Edit;
import vamix.Main;

/**
 * This class extracts the subtitles stream from a video (if there is a stream)
 * It also reads the .ass file (if it exists) and gives the information to the
 * subtitles GUI
 * 
 * @author ywu591
 * 
 */
public class SubtitlesReader extends SwingWorker<Integer, Void> {
	private Process process;
	
	private static List<String> startTime;
	private static List<String> endTime;
	private static List<String> text;
	

	@Override
	protected Integer doInBackground() throws Exception {
		// The commands
		ProcessBuilder builder;
		
		String mainFile = Main.getInstance().original.getAbsolutePath();
		String extensionless = mainFile.substring(0,
				mainFile.lastIndexOf('.') + 1);
		String assFile = extensionless + "ass";
		
		builder = new ProcessBuilder("avconv","-i",mainFile,"-vn","-an","-codec:s","ass","-y",assFile);

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
		Edit.getInstance().updateProgressBar(true, "Getting Subtitles");
	}
	
	//Once done
	protected void done(){
		Edit.getInstance().updateProgressBar(false, "Getting Subtitles");
	}

	public static void readSubtitlesFile() {
		BufferedReader reader;
		String mainFile = Main.getInstance().original.getAbsolutePath();
		String extensionless = mainFile.substring(0,
				mainFile.lastIndexOf('.') + 1);
		String assFile = extensionless + "ass";
		startTime = new ArrayList<String>();
		endTime = new ArrayList<String>();
		text = new ArrayList<String>();
		
		try {
			reader = new BufferedReader(new FileReader(assFile));
			String line = null;
			//Finds the write place to start reading for subtitles.
			while ((line = reader.readLine()) != null) {
				if(line.equals("[Events]")){
					break;
				}
				
			}
			String format = reader.readLine();
			String[] split = format.split(",");
			int start = 0;
			int end = 0;
			int word = 0;
			for (int i = 0; i<split.length; i++){
				if (split[i].contains("Start")){
					start = i;
				}
				if (split[i].contains("End")){
					end = i;
				}
				if (split[i].contains("Text")){
					word = i;
				}
			}
			
			while((line = reader.readLine()) != null){
				String[] divide = line.split(",");
				startTime.add(divide[start]);
				endTime.add(divide[end]);
				text.add(divide[word]);
			}
			
		} catch (IOException e) {
		}
	}
	
	/**
	 * returns a list of start times
	 */
	public static List<String> getStartTime(){
		return startTime;
	}
	
	/**
	 * returns a list of end times
	 */
	public static List<String> getEndTime(){
		return endTime;
	}
	
	/**
	 * returns a list of text
	 */
	public static List<String> getText(){
		return text;
	}
}
