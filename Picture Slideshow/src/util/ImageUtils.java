package util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class ImageUtils {

	/**
	 * Private Constructor
	 */
	private ImageUtils() {}
	
	/**
	 * Resizes the image to the new size; ignores aspect ratio of original image
	 * @param image - The image to resize
	 * @param width - The new width of the image
	 * @param height - The new height of the image
	 * @return The image set to the new size
	 */
	public static BufferedImage force(BufferedImage image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D g2 = resizedImage.createGraphics();
		g2.drawImage(image, 0, 0, width, height, null);
		g2.dispose();
		return resizedImage;
	}
	
	/**
	 * Attempts to resize the image to within the given bounds while maintaining aspect ratio
	 * @param image - The image to be resized
	 * @param maxWidth - The maximum width bound
	 * @param maxHeight - The maximum height bound
	 * @return Returns either an image resized to fit within the bounds, or the same image if no resize was necessary
	 */
	public static BufferedImage resize(BufferedImage image, int maxWidth, int maxHeight) {
		int w = image.getWidth(), h = image.getHeight();
		float aspectRatio = (float)w / h;
		
		if(image.getWidth() > maxWidth || image.getHeight() > maxHeight) {
			if(image.getWidth() > maxWidth && !(image.getHeight() > maxHeight)) {
				w = maxWidth;
				h = (int) (w / aspectRatio);
			}
			else if(!(image.getWidth() > maxWidth) && image.getHeight() > maxHeight) {
				h = maxHeight;
				w = (int) (h * aspectRatio);
			}
			else if(image.getWidth() > maxWidth && image.getHeight() > maxHeight) {
				h = maxHeight;
				w = (int)(h * aspectRatio);
				
				if(w > maxWidth) {
					w = maxWidth;
					h = (int) (w / aspectRatio);
				}	
			}
			return force(image, w, h);
		}
		else {
			return image;
		}
	}
	
	/**
	 * Returns a Dimension representing the dimension of an image
	 * @param image - The image from which to get the dimensions
	 * @return A Dimension representing the size of the image
	 */
	public static Dimension getDimension(BufferedImage image) {
		return new Dimension(image.getWidth(), image.getHeight());
	}
}
