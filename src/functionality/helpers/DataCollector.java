package functionality.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import vamix.Main;

/**
 * This class gets information about a video file, mainly fps, number of frames, duration of the video
 * @author ywu591
 *
 */
public class DataCollector extends SwingWorker<Integer,Void> {
	private static DataCollector dataCollector;
	
	private Process process;
	
	private String fpsline;
	private String durationline;
	private static int fps;
	private static int hh;
	private static int mm;
	private static int ss;
	
	@Override
	protected Integer doInBackground() throws Exception {
		ProcessBuilder databuilder;

		// First it check for the fps and duration of the video
		databuilder = new ProcessBuilder("avprobe",
				Main.getInstance().original.getAbsolutePath());

		// Sets up the builder and process
		databuilder.redirectErrorStream(true);
		try {
			process = databuilder.start();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));
			String line = null;
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
				if (line.contains("fps")) {
					fpsline = line;
				}
				if (line.contains("Duration")) {
					durationline = line;
				}
				publish();
			}
			stdoutBuffered.close();
		} catch (IOException e) {
		}

		// If value not given assume fps is 24, the normal frames of a video
		if (fpsline == null) {
			fpsline = " 24 fps";
		}

		if (process.waitFor() > 0 || durationline == null) {
			return -1;
		}

		// String manipulation to find the fps if fps is given
		String[] findfps = fpsline.split(",");
		for (String s : findfps) {
			if (s.contains("fps")) {
				fpsline = s;
			}
		}
		findfps = fpsline.split(" ");
		fps = Integer.parseInt(findfps[1]);

		// String manipulation to find the duration of the video
		String[] findduration = durationline.split(",");
		for (String s : findduration) {
			if (s.contains("Duration")) {
				durationline = s;
			}
		}
		findduration = durationline.split(":");
		hh = Integer.parseInt(findduration[1].trim());
		mm = Integer.parseInt(findduration[2]);
		String[] sec = findduration[3].split("\\.");
		ss = Integer.parseInt(sec[0]) + 1;
		
		return process.waitFor();
	}
	
	public static int getfps(){
		dataCollector = new DataCollector();
		dataCollector.execute();
		try {
			if(dataCollector.get() == 0){
				return fps;
			} 
			return -1;
		} catch (InterruptedException | ExecutionException e) {
			return -1;
		}

	}
	
	public static String getDuration(){
		dataCollector = new DataCollector();
		dataCollector.execute();
		try {
			if(dataCollector.get() == 0){
				return String.format("%02d", hh)+":"+String.format("%02d", mm)+":"+String.format("%02d", hh);
			}
			return "";
		} catch (InterruptedException | ExecutionException e) {
			return "";
		}
	}
	
	public static int getFrames(){
		dataCollector = new DataCollector();
		dataCollector.execute();
		int length = hh*60*60 + mm*60 + ss;
		return length*fps;
	}
}
