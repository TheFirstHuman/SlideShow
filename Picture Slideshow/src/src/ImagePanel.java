package src;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = -5137710974747714004L;
	
	private BufferedImage image;
	private JComponent parent, container;
	
	/**
	 * Constructor
	 * 
	 * ImagePanel automatically resizes based upon the size of the JComponent which is its container.
	 * 
	 * If the container is larger than the image, the image is presented at its maximum size
	 * If the container is smaller than the image, the image will resize within the container while attempting to maintain
	 * aspect ratio
	 * 
	 * @param image - The image to display
	 * @param parent - The JComponent which is the parent of this panel
	 * @param container - The JComponent which contains this panel
	 */
	public ImagePanel(BufferedImage image, JComponent parent, JComponent container) {
		this.image = image;
		this.parent = parent;
		this.container = container;
		
	}
	
	/**
	 * Sets the panel image
	 * @param image - The new image
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	/**
	 * Gets the image dimensions
	 * @return The image dimensions
	 */
	public Dimension getImageSize() {
		return util.ImageUtils.getDimension(image);
	}
	
	/**
	 * Paints the image to the screen
	 * @param g - The Graphics which will draw the image
	 */
	@Override
	public void paint(Graphics g) {
		BufferedImage newimage = util.ImageUtils.resize(image, container.getWidth(), container.getHeight());
		parent.setPreferredSize(new Dimension(newimage.getWidth(), newimage.getHeight()));
		g.drawImage(newimage, 0, 0, null);
		container.revalidate();
	}
	
}
