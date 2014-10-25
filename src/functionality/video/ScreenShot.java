package functionality.video;

import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import vamix.Main;

/**
 * Class that allows the user to take screen shots as they view the view.
 * @author ywu591
 *
 */
public class ScreenShot extends SwingWorker<Integer,Void> {
	private String time;	
	private JButton camera;
	private Process process;
	
	public ScreenShot(String time, JButton camera){
		this.time = time;
		this.camera = camera;
		camera.setEnabled(false);
	}
					
			
	@Override
	protected Integer doInBackground() throws Exception {
		//Checks what file to create
		int number = 1;
		File f = new File(Main.getInstance().original.getParent()+"/image_001.png");
		while (f.exists()){
			number++;
			f = new File(String.format(Main.getInstance().original.getParent()+"/image_%03d.png", number));
		}
		
		ProcessBuilder builder;
		//The commands
		builder  = new ProcessBuilder("avconv","-i",Main.getInstance().original.getAbsolutePath(),"-ss",time,
				f.getAbsolutePath());
		// Sets up the builder and process
		builder.redirectErrorStream(true);
		try {
			process = builder.start();
		} catch (IOException e) {
		}
		return process.waitFor();
	}
	
	protected void done(){
		camera.setEnabled(true);
	}

}
