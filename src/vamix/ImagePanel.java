package vamix;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * This class is not used anymore but if you like kpop you can add this picture
 * to a panel and view the picture with pleasure!
 * 
 * References: http://stackoverflow.com/questions/299495/how-to-add-an-image-to-a-jpanel
 * 
 * @author ywu591
 * 
 */
public class ImagePanel extends JPanel {

	private BufferedImage image;

	public ImagePanel() {
		try {
			File file = new File("src/resource/kpop.png");
			image = ImageIO.read(file);
		} catch (IOException ex) {
			// handle exception...
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null); // see javadoc for more info on the
										// parameters
	}

}