package functionality.subtitles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		
		List<String> startTimes = Subtitles.getInstance().getStartTimes();
		List<String> endTimes = Subtitles.getInstance().getEndTimes();
		List<String> text = Subtitles.getInstance().getText();

		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath(), true)));
			if (!f.exists()) {
				f.createNewFile();
			}
			if (!checkFile()){
				for(String s : assFormat){
					writer.println(s);
				}
			}
			
			for(int i = 0; i < startTimes.size(); i++){
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
	
	private static boolean checkFile(){
		BufferedReader br = null; 
		try {
			br = new BufferedReader(new FileReader(f.getAbsolutePath()));
			
			String s = br.readLine();
			if (s == null){
				return false;
			}
			if (s.equals(assFormat[0])){
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}
}
