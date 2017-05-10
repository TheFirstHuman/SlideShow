package src;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import util.IO;

public class Displayable {

	private static final int MINIMUM_WIDTH = 600;
	private static final int MINIMUM_HEIGHT = 480;
	
	private static int WIDTH = 1280;
	private static int HEIGHT = 800;
	
	private static final int ICON_SIZE = 32;
	
	private static final String[] info = {
			"Slideshow",
			"Author: Kyle Askine",
			"Last Update: May 9, 2017",
			"Version: 0.9"};
	
	private static final String slideshowIcon = "slideshow_icon.png";
	private static final String thumbnailIcon = "grid.png";
	private static final String fullscreenIcon = "fullscreen.png";
	private static final String addIcon = "add.png";
	private static final String subIcon = "sub.png";
	private static final String firstIcon = "first.png";
	private static final String previousIcon = "previous.png";
	private static final String nextIcon = "next.png";
	private static final String lastIcon = "last.png";
	private static final String manual = "manual.html";
		
	private JFrame frame;
	private JPanel cardPanel, showPanel;
	private ArrayList<ImageIcon> icons;
	private CardLayout layout;
	
	/**
	 * Constructor
	 */
	public Displayable() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setup();
	}
	
	/**
	 * Sets up the slide show
	 */
	private void setup() {
		icons = new ArrayList<ImageIcon>();
		
		setupFrame();
		setupMenu();
		setupSlideshow();
		setupControls();
		
		layout.first(cardPanel);
		frame.setVisible(true);
	}
	
	/**
	 * Sets up the JFrame
	 */
	private void setupFrame() {
		frame = new JFrame(info[0]);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
		frame.setSize(WIDTH, HEIGHT);
	}
	
	/**
	 * Sets up the menu
	 */
	private void setupMenu() {
		JPanel northPanel = new JPanel(new GridLayout());
		JMenuBar menubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		
		JMenuItem newSlideshow = new JMenuItem("New");
		newSlideshow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		newSlideshow.setToolTipText("Create a new slide show.");
		newSlideshow.addActionListener(ae -> {
			icons.clear();
			setupSlideshow();
			frame.repaint();
		});
		fileMenu.add(newSlideshow);
		
		JMenuItem saveSlideshow = new JMenuItem("Save");
		saveSlideshow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		saveSlideshow.setToolTipText("Opens a dialog to save the current slide show.");
		saveSlideshow.addActionListener(ae -> {
			util.IO.saveSlideshow(saveSlideshow, icons);
		});
		fileMenu.add(saveSlideshow);
		
		JMenuItem loadSlideshow = new JMenuItem("Load");
		loadSlideshow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		loadSlideshow.setToolTipText("Opens a dialog to load a previously created slide show.");
		loadSlideshow.addActionListener(ae -> {
			icons = util.IO.loadSlideshow(loadSlideshow);
			
			setupSlideshow();
			
			for(ImageIcon icon: icons) 
				addSlide(icon);
		});
		fileMenu.add(loadSlideshow);
		
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(ae -> {
			System.exit(0);
		});
		fileMenu.add(exit);
		
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(ae -> {
			JDialog dialog = new JDialog();
			dialog.setTitle("About");
			dialog.setResizable(false);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			
			EmptyBorder border = new EmptyBorder(10, 10, 10, 10);
			
			for(String s: info) {
				JLabel label = new JLabel(s);
				label.setBorder(border);
				panel.add(label);
			}
			
			dialog.add(panel);
			dialog.pack();
			dialog.setVisible(true);
		});
		helpMenu.add(about);
		
		JMenuItem manMenu = new JMenuItem("Manual");
		manMenu.addActionListener(ae -> {
			IO.openWebpage(manual);
		});
		helpMenu.add(manMenu);
		
		menubar.add(fileMenu);
		menubar.add(helpMenu);
		
		northPanel.add(menubar);
		
		frame.add(northPanel, BorderLayout.NORTH);
		frame.setIconImage(util.IO.load(slideshowIcon));
	}
	
	/**
	 * Sets up the slide show
	 */
	private void setupSlideshow() {
		showPanel = new JPanel(new GridBagLayout());
		showPanel.setBackground(Color.BLACK);
		
		cardPanel = new JPanel();
		cardPanel.setBackground(Color.BLACK);
		
		layout = new CardLayout();
		cardPanel.setLayout(layout);
		
		showPanel.add(cardPanel);
		frame.add(showPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Sets up buttons
	 */
	private void setupControls() {
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		
		JToolBar plusBar = new JToolBar();
		plusBar.setFloatable(false);
		
		JButton btnAdd = new JButton();
		JButton btnSub = new JButton();
		
		addIcon(btnAdd, util.IO.load(addIcon));
		addIcon(btnSub, util.IO.load(subIcon));
		
		btnAdd.setToolTipText("Select an image to add to the slideshow.");
		btnSub.setToolTipText("Removes the currently displayed image from the slideshow.");
		
		btnAdd.addActionListener(ae -> {
			for(BufferedImage image: util.IO.loadImage(btnAdd))
				addSlide(image);
		});

		btnSub.addActionListener(ae -> {

		});
		
		plusBar.add(btnAdd);
		plusBar.add(btnSub);
		
		southPanel.add(plusBar, Container.LEFT_ALIGNMENT);
		southPanel.add(Box.createHorizontalGlue());
		
		JToolBar controlBar = new JToolBar();
		controlBar.setFloatable(false);
		
		JButton btnFirst = new JButton();
		JButton btnPrevious = new JButton();
		JButton btnNext = new JButton();
		JButton btnLast = new JButton();
		
		addIcon(btnFirst, util.IO.load(firstIcon));
		addIcon(btnPrevious, util.IO.load(previousIcon));
		addIcon(btnNext, util.IO.load(nextIcon));
		addIcon(btnLast, util.IO.load(lastIcon));
		
		btnFirst.setToolTipText("Displays the first image in the slide show.");
		btnPrevious.setToolTipText("Displays the previous image in the slide show.");
		btnNext.setToolTipText("Displays the next image in the slide show.");
		btnLast.setToolTipText("Displays the last image in the slide show.");
		
		btnFirst.setMnemonic(KeyEvent.VK_RIGHT);
		
		btnFirst.addActionListener(ae -> {			
			layout.first(cardPanel);
			frame.repaint();
		});
		
		btnPrevious.addActionListener(ae -> {
			layout.previous(cardPanel);
			frame.repaint();
		});
		
		btnNext.addActionListener(ae -> {
			layout.next(cardPanel);
			frame.repaint();
		});
		
		btnLast.addActionListener(ae -> {
			layout.last(cardPanel);
			frame.repaint();
		});
		
		
		
		controlBar.add(btnFirst);
		controlBar.add(btnPrevious);
		controlBar.add(btnNext);
		controlBar.add(btnLast);
		
		southPanel.add(controlBar, Container.CENTER_ALIGNMENT);
		southPanel.add(Box.createHorizontalGlue());
		
		JToolBar thumbnailBar = new JToolBar();
		thumbnailBar.setFloatable(false);
		
		JButton btnFullscreen = new JButton();
		JButton btnThumbnail = new JButton();
		
		addIcon(btnFullscreen, util.IO.load(fullscreenIcon));
		addIcon(btnThumbnail, IO.load(thumbnailIcon));
		
		btnFullscreen.setToolTipText("Toggles full screen.");
		btnThumbnail.setToolTipText("Toggles thumbnail overview of all slides in the slide show.");
		
		btnFullscreen.addActionListener(ae -> {
			if(frame.getExtendedState() == JFrame.NORMAL)
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			else
				frame.setExtendedState(JFrame.NORMAL);
		});
		thumbnailBar.add(btnFullscreen);
		
		btnThumbnail.addActionListener(ae -> {
			JDialog dlog = new JDialog();
			dlog.add(new JLabel("Ignite everything."));
			dlog.pack();
			dlog.setVisible(true);
			
		});
		thumbnailBar.add(btnThumbnail);
		
		southPanel.add(thumbnailBar, Container.RIGHT_ALIGNMENT);
		frame.add(southPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Adds a slide to the slide show from an icon
	 * @param icon - The ImageIcon containing the image to be added
	 */
	private void addSlide(ImageIcon icon) {
		Image image = icon.getImage();
		ImageObserver observer = icon.getImageObserver();
		
		BufferedImage bi = new BufferedImage(image.getWidth(observer), image.getHeight(observer), BufferedImage.OPAQUE);
		bi.getGraphics().drawImage(image, 0, 0, observer);
		
		cardPanel.add(new ImagePanel(bi, cardPanel, showPanel));
	}
	
	/**
	 * Adds a slide to the slide show from a BufferedImage
	 * @param image - The image to add
	 */
	private void addSlide(BufferedImage image) {
		cardPanel.add(new ImagePanel(image, cardPanel, showPanel));
		icons.add(new ImageIcon(image));
		
		layout.last(cardPanel);
	}
	
	/**
	 * Adds an image to a button as an icon
	 * @param button - The button to update
	 * @param image - The image to add
	 */
	private void addIcon(JButton button, BufferedImage image) {
		image = util.ImageUtils.resize(image, ICON_SIZE, ICON_SIZE);
		ImageIcon icon = new ImageIcon(image);
		button.setIcon(icon);
	}
	
}
