package functionality.subtitles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import editPanes.Subtitles;

import vamix.Main;

/**
 * This Class writes subtitles entered from the Subtitles class to a .ass file
 * 
 * @author ywu591
 * 
 */
public class SubtitlesWriter {
	private static File f;
	
	//This is the initial part of a .ass file
	private static final String[] assFormat = {
			"[Script Info]",
			"ScriptType: v4.00+",
			"",
			"[V4+ Styles]",
			"Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, AlphaLevel, Encoding",
			"Style: Default,Arial,16,&Hffffff,&Hffffff,&H0,&H0,0,0,0,1,1,0,2,10,10,10,0,0",
			"", "[Events]", "Format: Layer, Start, End, Text" };
	
	/**
	 * This method is called to write subtitles to a .ass file
	 */
	public static void writeSubtitles() {
		// Figure out if the subtitles file already exists or not
		String mainFile = Main.getInstance().original.getAbsolutePath();
		String extensionless = mainFile.substring(0,
				mainFile.lastIndexOf('.') + 1);
		String assFile = extensionless + "ass";
		f = new File(assFile);
		
		//Gets a list of start times, end times and text from the Jtable in Subtitles class.
		List<String> startTimes = Subtitles.getInstance().getStartTimes();
		List<String> endTimes = Subtitles.getInstance().getEndTimes();
		List<String> text = Subtitles.getInstance().getText();

		try {
			//First delete any existing .ass file
			deleteSubtitlesFile();
			
			//Then creates a new .ass file
			f.createNewFile();
			
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(f.getAbsolutePath(), true)));
			
			//Write the initial format of a .ass file
			for (String s : assFormat) {
				writer.println(s);
			}
			
			//Then append the Subtitles to the bottom
			for (int i = 0; i < startTimes.size(); i++) {
				String s = "Dialogue: 0,";
				s = s + startTimes.get(i) + ".00,";
				s = s + endTimes.get(i) + ".00,";
				s = s + text.get(i);
				writer.println(s);
			}

			writer.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * Deletes any existing .ass file associated with the video.
	 */
	public static void deleteSubtitlesFile(){
		String mainFile = Main.getInstance().original.getAbsolutePath();
		String extensionless = mainFile.substring(0,
				mainFile.lastIndexOf('.') + 1);
		String assFile = extensionless + "ass";
		
		File f = new File(assFile);
		if(f.exists()){
			f.delete();
		}
	}
}
