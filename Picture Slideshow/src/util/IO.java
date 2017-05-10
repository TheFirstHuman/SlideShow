package util;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class IO {

	private static final String LOCAL_BASE = "resources/";
	private static final String DEFAULT_DIRECTORY = System.getProperty("user.home") + "/Desktop";
	private static final String EXTENSION = ".slider";
	
	private static final FileNameExtensionFilter IMAGE_FILTER = new FileNameExtensionFilter("Image Extensions", "jpg", "jpeg", "gif", "png");
	private static final FileNameExtensionFilter SLIDESHOW_FILTER = new FileNameExtensionFilter("Slideshow", EXTENSION.substring(1));
	
	private IO() {}
	
	/**
	 * Loads a BufferedImage from a URL
	 * @param url - The URL of the image to load
	 * @return The loaded image, null if the load attempt failed
	 */
	public static BufferedImage load(URL url) {
		try {
			return ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * Loads a BufferedImage from a File location
	 * @param file - A File location to read from
	 * @return The loaded BufferedImage, null if the load attempt failed
	 */
	public static BufferedImage load(File file) {
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Loads a BufferedImage from a location within the jar
	 * @param path - The name of the file to load
	 * @return A BufferedImage loaded from the resources file of the jar with the given name
	 */
	public static BufferedImage load(String path) {
		try {
			return ImageIO.read(ClassLoader.getSystemResourceAsStream(LOCAL_BASE + path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets URLs from the given file path
	 * @param path - The path to the URL text file
	 * @return An ArrayList containing all of the URLs
	 */
	public static ArrayList<URL> getURLs(String path) {
		ArrayList<URL> urls = new ArrayList<URL>();
		
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(LOCAL_BASE + path);
			Scanner scanner = new Scanner(is);
		
			while(scanner.hasNextLine())
				urls.add(new URL(scanner.nextLine()));
			
			scanner.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return urls;
	}
	
	/**
	 * Saves the slideshow to the file selected by the user
	 * @param component - The component the JFileChooser dialog will be attached to
	 * @param icons - The ImageIcons containing the images to save
	 */
	public static void saveSlideshow(JComponent component, ArrayList<ImageIcon> icons) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveDialog(component)));
			
			oos.writeObject(icons);
			oos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads a slideshow from a file selected by the user
	 * @param component - The component the JFileChooser dialog will be attached to
	 * @return An ArrayList containing all of the ImageIcons with all of the images in the slideshow
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<ImageIcon> loadSlideshow(JComponent component) {
		JFileChooser fc = new JFileChooser();
		
		fc.setCurrentDirectory(new File(DEFAULT_DIRECTORY));
		fc.setDialogTitle("Slideshow Imager Chooser");
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(false);
		fc.setFileFilter(SLIDESHOW_FILTER);
		fc.showOpenDialog(component);
		
		
		try {
			ArrayList<ImageIcon> icons = new ArrayList<ImageIcon>();
			ObjectInputStream ois =	new ObjectInputStream(new FileInputStream(fc.getSelectedFile().getAbsolutePath()));
			
			icons = (ArrayList)ois.readObject();
			ois.close();
			return icons;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Opens a dialog box for file selection
	 * @param component - The component which triggers the selection
	 * @return The file selected
	 */
	public static ArrayList<BufferedImage> loadImage(JComponent component) {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		
		for(File file: loadDialog(component, IMAGE_FILTER))
			images.add(load(file));
		
		return images;
	}
	
	/**
	 * Opens a locally stored web page with the input path in the system default browser
	 * @param path - The name of the web page file
	 */
	public static void openWebpage(String path) {
		try {
			Desktop.getDesktop().browse(ClassLoader.getSystemResource(LOCAL_BASE + path).toURI());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a JFileChooser save dialog and returns the name selection
	 * @param component - The JComponent the JFileChooser dialog will be attached to
	 * @return Returns the user name selection with the program file extension
	 */
	private static String saveDialog(JComponent component) {
		JFileChooser fc = new JFileChooser();
		
		fc.setDialogTitle("Slideshow Save File Selection");
		fc.setCurrentDirectory(new File(DEFAULT_DIRECTORY));
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setFileFilter(SLIDESHOW_FILTER);
		
		int selection = fc.showSaveDialog(component);
		
		if(selection == JFileChooser.APPROVE_OPTION) {
			File saveFile = fc.getSelectedFile();
			return saveFile.getAbsolutePath() + EXTENSION;
		}
		return null;
	}
	
	/**
	 * Opens a load dialog and returns all selected files
	 * @param component - The JComponent the JFileChooser dialog will be attached to
	 * @param filter - The file selection filter for the JFileChooser to apply
	 * @return An array containing all Files selected by the user
	 */
	private static File[] loadDialog(JComponent component, FileNameExtensionFilter filter) {
		JFileChooser fc = new JFileChooser();
		
		fc.setCurrentDirectory(new File(DEFAULT_DIRECTORY));
		fc.setDialogTitle("Slideshow Imager Chooser");
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(true);
		fc.setFileFilter(filter);
		fc.showOpenDialog(component);
		
		return fc.getSelectedFiles();
	}
	
}
